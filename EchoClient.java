import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * EchoClient
 * ----------
 * Sends a message typed by the user to EchoServer over UDP,
 * then waits for and prints the case-inverted reply.
 *
 * Usage: run EchoServer first, then run this client.
 * Type messages; type "exit" to quit.
 */
public class EchoClient {

    private static final int SERVER_PORT = 9876;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.println("Connected to server at " + SERVER_HOST
                    + ":" + SERVER_PORT + " (type 'exit' to quit)");

            while (true) {
                System.out.print("You: ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Closing client.");
                    break;
                }

                // Marshal the string into raw bytes to send over the network.
                byte[] sendBytes = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                        sendBytes, sendBytes.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);

                // Prepare an empty packet to receive the server's reply into.
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Blocks until the server replies.
                clientSocket.receive(receivePacket);

                String reply = new String(
                        receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Server: " + reply);
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}