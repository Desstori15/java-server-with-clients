import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private String serverName;
    private List<String> bannedPhrases;
    private Map<String, ClientHandler> clients = new HashMap<>();

    public Server(String configFile) throws IOException {
        loadConfiguration(configFile);
    }

    private void loadConfiguration(String configFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));

        this.port = Integer.parseInt(properties.getProperty("port"));
        this.serverName = properties.getProperty("serverName");
        this.bannedPhrases = Arrays.asList(properties.getProperty("bannedPhrases").split(","));
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println(serverName + " started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private String clientName;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                output.println("Enter your nickname:");
                clientName = input.readLine();
                synchronized (clients) {
                    clients.put(clientName, this);
                    broadcast(clientName + " has joined the chat.");
                    sendClientList();
                }

                String message;
                while ((message = input.readLine()) != null) {
                    if (message.startsWith("/banned")) {
                        output.println("Banned phrases: " + String.join(", ", bannedPhrases));
                    } else if (message.startsWith("/to ")) {
                        sendPrivateMessage(message);
                    } else if (message.startsWith("/exclude ")) {
                        sendMessageExcluding(message);
                    } else if (isBannedMessage(message)) {
                        output.println("Message contains banned phrases. Cannot be sent.");
                    } else {
                        broadcast(clientName + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private boolean isBannedMessage(String message) {
            for (String phrase : bannedPhrases) {
                if (message.contains(phrase)) {
                    return true;
                }
            }
            return false;
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients.values()) {
                    client.output.println(message);
                }
            }
        }

        private void sendClientList() {
            output.println("Connected clients: " + String.join(", ", clients.keySet()));
        }

        private void sendPrivateMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                output.println("Usage: /to <username> <message>");
                return;
            }
            String targetName = parts[1];
            String privateMessage = parts[2];
            synchronized (clients) {
                ClientHandler target = clients.get(targetName);
                if (target != null) {
                    target.output.println("(Private) " + clientName + ": " + privateMessage);
                } else {
                    output.println("User not found: " + targetName);
                }
            }
        }

        private void sendMessageExcluding(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                output.println("Usage: /exclude <username> <message>");
                return;
            }
            String excludeName = parts[1];
            String broadcastMessage = parts[2];
            synchronized (clients) {
                for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                    if (!entry.getKey().equals(excludeName)) {
                        entry.getValue().output.println(clientName + ": " + broadcastMessage);
                    }
                }
            }
        }

        private void disconnect() {
            synchronized (clients) {
                clients.remove(clientName);
                broadcast(clientName + " has left the chat.");
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server("server_config.txt");
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}


