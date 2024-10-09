package projectChat;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class hilo extends Thread {
    DataInputStream netIn;
    JTextArea mostrarMensajes; // JTextArea para mostrar los mensajes
    chat mainChat; // Referencia a la ventana principal del chat (para actualizar contactos)

    // Constructor que recibe el flujo de entrada, el área de mensajes y la referencia a la ventana principal
    public hilo(DataInputStream netIn, JTextArea mostrarMensajes, chat mainChat) {
        this.netIn = netIn;
        this.mostrarMensajes = mostrarMensajes;
        this.mainChat = mainChat;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = netIn.readUTF(); // Lee el mensaje desde el servidor

                if (mensaje.startsWith("Usuarios conectados:")) {
                    // Procesa la lista de usuarios conectados
                    String[] partes = mensaje.replace("Usuarios conectados: [", "").replace("]", "").split(", ");
                    ArrayList<String> usuarios = new ArrayList<>();
                    for (String user : partes) {
                        usuarios.add(user);
                    }
                    // Actualiza la lista de contactos llamando al método de la ventana principal
                    mainChat.actualizarContactos(usuarios);

                } else if (mensaje.startsWith("Mensaje privado de")) {
                    // Manejar mensaje privado
                    String[] partes = mensaje.split(": ", 2);
                    String remitente = partes[0].replace("Mensaje privado de ", "");
                    String contenido = partes[1];
                    mainChat.recibirMensajePrivado(remitente, contenido);

                } else if (mensaje.startsWith("/archivo ")) {
                    // Manejar recepción de archivo
                    String[] partes = mensaje.split(" ", 4);
                    String remitente = partes[1];
                    String nombreArchivo = partes[2];
                    long tamanoArchivo = Long.parseLong(partes[3]);
                    mainChat.recibirArchivoPrivado(remitente, nombreArchivo, tamanoArchivo);

                } else {
                    // Mensaje normal del chat (chat grupal)
                    mostrarMensajes.append(mensaje + "\n");
                }
            }
        } catch (IOException ioe) {
            System.out.println("Desconectado del servidor.");
        }
    }
}
