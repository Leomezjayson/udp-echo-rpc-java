import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * EchoServer
 * ----------
 * A simple UDP server that:
 *   1. Waits for a message from any client.
 *   2. Prints the client's IP address to the console.
 *   3. Sends the message back with each letter's case inverted
 *      (e.g. "Hello" -> "hELLO").
 *
 * This demonstrates raw socket programming (java.net.DatagramSocket)
 * with no external networking libraries.
 */
public class EchoServer {

    private static final int PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        // DatagramSocket bound to PORT — this is the server's "mailbox"
        // for incoming UDP packets. UDP is connectionless: there is no
        // handshake, the server just listens for packets to arrive.
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("========================================");
            System.out.println("  UDP Echo Server");
            System.out.println("  Listening on port " + PORT);
            System.out.println("========================================");

            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            // Server runs forever, handling one packet at a time.
            while (true) {
                // An empty packet, ready to be filled in by receive().
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Blocks here until a packet arrives from any client.
                serverSocket.receive(receivePacket);

                // Extract exactly the bytes that were actually sent
                // (receivePacket.getLength(), not the full buffer size).
                String message = new String(
                        receivePacket.getData(), 0, receivePacket.getLength());

                // The client's address and port are attached to the
                // packet automatically by UDP — no need to ask for it.
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Received from " + clientAddress.getHostAddress()
                        + ":" + clientPort + " -> \"" + message + "\"");

                // Build the case-inverted reply.
                String reply = invertCase(message);

                byte[] replyBytes = reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(
                        replyBytes, replyBytes.length, clientAddress, clientPort);

                serverSocket.send(replyPacket);
                System.out.println("Replied with -> \"" + reply + "\"");
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Inverts the case of every letter in the input string.
     * "Hello" -> "hELLO"
     */
    private static String invertCase(String input) {
        StringBuilder result = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(c); // non-letters unchanged
            }
        }
        return result.toString();
    }
}