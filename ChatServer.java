package projectChat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class ChatServer extends Thread {
    private final Vector<Socket> vector = new Vector<>();
    private final HashMap<String, Socket> usuarios = new HashMap<>();
    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    private ServerSocket connect() {
        try {
            return new ServerSocket(port);
        } catch (IOException ioe) {
            System.out.println("No se pudo realizar la conexión en el puerto " + port);
            return null;
        }
    }

    public void principal() {
        ServerSocket sSocket = connect();
        if (sSocket != null) {
            // Hilo para actualizar la lista de usuarios cada 30 segundos
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String userList = "Usuarios conectados: " + usuarios.keySet().toString();
                    for (Socket socket : vector) {
                        try {
                            DataOutputStream netOut = new DataOutputStream(socket.getOutputStream());
                            netOut.writeUTF(userList);
                        } catch (IOException ioe) {
                            System.out.println("Error al enviar lista de usuarios: " + ioe.getMessage());
                        }
                    }
                }
            }, 0, 30000); // Cada 30 segundos

            while (true) {
                try {
                    System.out.println("ChatServer abierto y esperando conexiones en puerto " + port);
                    Socket socket = sSocket.accept();
                    vector.add(socket);
                    Thread hilo = new Thread(new HiloChatServer(socket, vector, usuarios));
                    hilo.start();
                } catch (IOException ioe) {
                    System.out.println("Error al aceptar la conexión: " + ioe.getMessage());
                }
            }
        } else {
            System.err.println("No se pudo abrir el puerto");
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ChatServer <puerto>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            ChatServer chat = new ChatServer(port);
            chat.principal();
        } catch (NumberFormatException e) {
            System.out.println("El puerto debe ser un número válido.");
        }
    }
}