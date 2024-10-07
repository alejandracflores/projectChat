/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */
public class hilo extends Thread {
    DataInputStream netIn;
    // JTextArea para mostrar los mensajes
    JTextArea mostrarMensajes;
    // JTextArea para mostrar la lista de usuarios
    JTextArea mostrarContactos;
    
    
    // Constructor de mostrarContactos y mostrarMensajes
    public hilo(DataInputStream netIn, JTextArea mostrarMensajes, JTextArea mostrarContactos) {
        this.netIn = netIn;
        // Asignar el área de texto de mensajes
        this.mostrarMensajes = mostrarMensajes;
        // Asignar el área de texto de contactos
        this.mostrarContactos = mostrarContactos;
    }

    public void run() {
        try {
            while (true) {
                String mensaje = netIn.readUTF();
                
                // Verifica si el mensaje es la lista de usuarios
                if (mensaje.startsWith("Usuarios conectados: ")) {
                    // Actualizar la lista de contactos
                    mostrarContactos.setText(mensaje);
                } else {
                    // Mostrar mensajes regulares
                    mostrarMensajes.append(mensaje + "\n");
                }
            }
        } catch (IOException ioe) {
            System.out.println("Desconectado del servidor.");
        }
    }
}
