package projectChat;

public class ChatApp { // Cambia el nombre de la clase a ChatApp

    public static void main(String[] args) {
        // Verifica si se pasó el puerto como argumento
        if (args.length != 1) {
            System.err.println("Por favor, proporciona un número de puerto.");
            System.exit(1);
        }
        
        try {
            int port = Integer.parseInt(args[0]); // Convierte el argumento a int
            ChatServer chatServer = new ChatServer(port); // Crea una instancia del servidor
            chatServer.principal(); // Inicia el servidor
        } catch (NumberFormatException e) {
            System.err.println("El puerto debe ser un número válido.");
        }
    }
}
