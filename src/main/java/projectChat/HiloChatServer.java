package projectChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

public class HiloChatServer implements Runnable {

    private Socket socket;
    private Vector<Socket> vector;
    private HashMap<String, Socket> usuarios;
    private DataInputStream netIn;
    private DataOutputStream netOut;

    public HiloChatServer(Socket socket, Vector<Socket> vector, HashMap<String, Socket> usuarios) {
        this.socket = socket;
        this.vector = vector;
        this.usuarios = usuarios;
    }

    private void initStreams() throws IOException {
        netIn = new DataInputStream(socket.getInputStream());
        netOut = new DataOutputStream(socket.getOutputStream());
    }

    private void sendMsg(String msg, boolean isServerMessage) throws IOException {
        // Si es un mensaje privado
        String[] tokens = msg.split("\\^");
        if (tokens[0].equals("p")) {
            String recipient = tokens[1];
            String privateMsg = tokens[2];
            String sender = tokens[3];
            Socket recipientSocket = usuarios.get(recipient);

            if (recipientSocket != null) {
                try {
                    String encryptedMsg = AESUtil.encrypt(privateMsg);
                    DataOutputStream out = new DataOutputStream(recipientSocket.getOutputStream());
                    out.writeUTF("Mensaje privado de " + sender + ": " + encryptedMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                netOut.writeUTF("Error: Usuario " + recipient + " no está conectado.");
            }
        } else {
            // Si el mensaje no es del servidor, agregar el formato del remitente
            if (!isServerMessage) {
                String sender = null;
                for (String username : usuarios.keySet()) {
                    if (usuarios.get(username).equals(socket)) {
                        sender = username;
                        break;
                    }
                }

                if (sender != null) {
                    // Agregar el remitente al mensaje si es un mensaje grupal
                    msg = sender + ": " + msg;
                }
            }

            // Enviar el mensaje a todos los clientes (con o sin remitente)
            for (Socket soc : vector) {
                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                out.writeUTF(msg);
            }
        }
    }


    public void run() {
        String username = null;
        try {
            initStreams();

            // Pide el nombre de usuario al cliente
            username = netIn.readUTF();
            // Agrega el nombre de usuario y socket al hashmap
            usuarios.put(username, socket);

            // Notifica a todos que el usuario se ha unido (mensaje del servidor)
            String joinMsg = username + " se ha unido";
            sendMsg(joinMsg, true);  // true indica que es un mensaje del servidor

            while (true) {
                String msg = netIn.readUTF();
                // Envía el mensaje a todos los clientes (mensaje normal)
                sendMsg(msg, false);  // false indica que no es un mensaje del servidor
            }
        } catch (IOException ioe) {
            System.out.println("Error en el hilo del usuario: " + ioe.toString());
        } finally {
            // Notificar a todos que el usuario se ha desconectado (mensaje del servidor)
            if (username != null) {
                usuarios.remove(username);
                vector.remove(socket);

                String leaveMsg = username + " ha salido de la conexión.";
                try {
                    // true indica que es un mensaje del servidor
                    sendMsg(leaveMsg, true);
                } catch (IOException e) {
                    System.out.println("Error al enviar el mensaje de desconexión.");
                }
            }

            // Cerrar recursos
            try {
                if (socket != null) {
                    socket.close();
                }
                if (netIn != null) {
                    netIn.close();
                }
                if (netOut != null) {
                    netOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}