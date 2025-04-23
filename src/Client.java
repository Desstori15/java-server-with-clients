import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private String serverAddress;
    private int serverPort;
    private String username;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(input.readLine()); // Prompt for name
            username = scanner.nextLine();
            output.println(username);

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();

            while (true) {
                String message = scanner.nextLine();
                output.println(message);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 1515);
        client.start();
    }
}
