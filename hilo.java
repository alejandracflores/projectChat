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
    JTextArea mostrarMensajes;
    public hilo (DataInputStream netIn, JTextArea mostrarMensajes) {
        this.netIn = netIn;
        this.mostrarMensajes = mostrarMensajes;
    }
    
    public void run() {
        // Thread para recibir mensajes del servidor
        try {
            while (true) {
                String mensaje = netIn.readUTF();
                // Desencriptar si es un mensaje privado
                if (mensaje.startsWith("Mensaje privado de")) {
                    String[] tokens = mensaje.split(": ");
                    String sender = tokens[0];
                    String encryptedMsg = tokens[1];
                    try {
                        // Desencriptar el mensaje
                        String decryptedMsg = AESUtil.decrypt(encryptedMsg);
                        System.out.println(sender + ": " + decryptedMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mostrarMensajes.append(mensaje);
                }
            }
        } catch (IOException ioe) {
            System.out.println("Desconectado del servidor.");
        }
    }
}
