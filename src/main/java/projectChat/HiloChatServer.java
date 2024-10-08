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

    private void sendMsg(String msg) throws IOException {
        String[] tokens = msg.split("\\^");
        if (tokens[0].equals("p")) { // Es un mensaje privado
            String recipient = tokens[1];
            String privateMsg = tokens[2];
            String sender = tokens[3]; // El nombre del remitente
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
            // Enviar mensaje a todos los clientes
            for (Socket soc : vector) {
                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                out.writeUTF(msg);
            }

            // Actualizar la lista de usuarios conectados
            String userList = "Usuarios conectados: " + usuarios.keySet().toString();
            for (Socket soc : vector) {
                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                out.writeUTF(userList);
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

            // Notifica a todos que el usuario se ha unido
            String joinMsg = username + " se ha unido";
            sendMsg(joinMsg);

            while (true) {
                String msg = netIn.readUTF();
                // Envía el mensaje a todos los clientes
                sendMsg(msg);
            }
        } catch (IOException ioe) {
            System.out.println("Error en el hilo del usuario: " + ioe.toString());
        } finally {
            // Notificar a todos que el usuario se ha desconectado
            if (username != null) {
                usuarios.remove(username);
                // Remover el socket del vector de clientes
                vector.remove(socket);

                // Notificar a todos que el usuario se ha desconectado
                String leaveMsg = username + " ha salido de la conexión.";
                try {
                    sendMsg(leaveMsg);
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