package projectChat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.lang.*;

public class ChatServer extends Thread {
    private Vector<Socket> vector = new Vector<Socket>();
    private HashMap<String, Socket> usuarios = new HashMap<>();
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    private ServerSocket connect() {
        try {
            ServerSocket sSocket = new ServerSocket(port);
            return sSocket;
        } catch (IOException ioe) {
            System.out.println("No se pudo realizar la conexión");
        }
        return null;
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
                            ioe.printStackTrace();
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
                    ioe.printStackTrace();
                }
            }
        } else {
            System.err.println("No se pudo abrir el puerto");
        }
    }

    public static void main(String[] args) {
        ChatServer chat = new ChatServer(Integer.parseInt(args[0]));
        chat.principal();
    }
}