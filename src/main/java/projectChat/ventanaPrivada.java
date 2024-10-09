package projectChat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ventanaPrivada extends JFrame {
    private String usuario;
    private JTextArea areaMensajes;
    private JTextField campoMensaje;
    private JButton botonEnviar;
    private chat mainChat;

    // Constructor
    public ventanaPrivada(String usuario, chat mainChat) {
        this.usuario = usuario;
        this.mainChat = mainChat;

        setTitle("Chat privado con " + usuario);
        setSize(400, 300);
        setLayout(new BorderLayout());

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        add(new JScrollPane(areaMensajes), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        // Acción del botón Enviar
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = campoMensaje.getText();
                if (!mensaje.isEmpty()) {
                    // Enviar el mensaje encriptado
                    mainChat.enviarMensajePrivado(usuario, mensaje);  // Aquí enviamos el mensaje al otro usuario
                    mostrarMensaje("Tú: " + mensaje);
                    campoMensaje.setText("");
                }
            }
        });

        setVisible(true);
    }

    // Método para mostrar mensajes
    public void mostrarMensaje(String mensaje) {
        areaMensajes.append(mensaje + "\n");
    }

    public String getUsuario() {
        return usuario;
    }

    // Método para recibir mensajes desencriptados
  public void recibirMensajePrivado(String remitente, String mensajeEncriptado) {
    try {
        // Desencriptar el mensaje
        String mensajeDesencriptado = AESUtil.decrypt(mensajeEncriptado);

        // Mostrar el mensaje en la ventana
        areaMensajes.append(remitente + ": " + mensajeDesencriptado + "\n");
    } catch (Exception e) {
        e.printStackTrace();
        areaMensajes.append("Error al desencriptar mensaje de " + remitente + "\n");
    }
}
}
