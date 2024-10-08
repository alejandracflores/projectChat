/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectChat;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */
public class hilo extends Thread {
    DataInputStream netIn;
    // JTextArea para mostrar los mensajes
    JTextArea mostrarMensajes;
    // Referencia a la ventana principal del chat (para actualizar contactos)
    chat mainChat;

    // Constructor que recibe el flujo de entrada, el área de mensajes y la referencia a la ventana principal
    public hilo(DataInputStream netIn, JTextArea mostrarMensajes, chat mainChat) {
        this.netIn = netIn;
        this.mostrarMensajes = mostrarMensajes;
        this.mainChat = mainChat;
    }

    public void run() {
        try {
            while (true) {
                String mensaje = netIn.readUTF();

                // Verifica si el mensaje es la lista de usuarios
                if (mensaje.startsWith("Usuarios conectados:")) {
                    // Imprime en la consola del servidor en lugar de en el área de mensajes
                    System.out.println(mensaje); // Aquí lo imprimimos en la consola

                    // Procesa la lista de usuarios conectados
                    String[] partes = mensaje.replace("Usuarios conectados: [", "").replace("]", "").split(", ");
                    ArrayList<String> usuarios = new ArrayList<>();
                    for (String user : partes) {
                        usuarios.add(user);
                    }
                    // Actualiza la lista de contactos llamando al método de la ventana principal
                    mainChat.actualizarContactos(usuarios);
                } else {
                    // Si no es un mensaje de lista de usuarios, lo mostramos en el área de mensajes
                    mostrarMensajes.append(mensaje + "\n");
                }
            }
        } catch (IOException ioe) {
            System.out.println("Desconectado del servidor.");
        }
    }
}