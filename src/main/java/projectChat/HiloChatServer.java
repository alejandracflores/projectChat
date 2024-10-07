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
                    // Encriptar el mensaje privado antes de enviarlo
                    String encryptedMsg = AESUtil.encrypt(privateMsg);
                    DataOutputStream out = new DataOutputStream(recipientSocket.getOutputStream());
                    out.writeUTF("Mensaje privado de " + sender + ": " + encryptedMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (Socket soc : vector) {
                netOut = new DataOutputStream(soc.getOutputStream());
                netOut.writeUTF(msg);
            }
        }
    }

    public void run() {
        try {
            initStreams();
            // Pide el nombre de usuario al cliente
            String username = netIn.readUTF();
            usuarios.put(username, socket); // Agrega el nombre de usuario y socket al hashmap

            // Notifica a todos que el usuario se ha unido
            String joinMsg = "m^Server@localhost^-^" + username + " se ha unido desde " + socket.getInetAddress() + "^";
            sendMsg(joinMsg);

            while (true) {
                String msg = netIn.readUTF();
                sendMsg(msg); // Envía el mensaje a todos los clientes
            }
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
    }
}