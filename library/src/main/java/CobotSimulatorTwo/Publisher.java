package CobotSimulatorTwo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * The Publisher class generates and sends random angles to a connected client. It listens for
 * a client connection on a specified port and continuously sends a set of six random angles every
 * 30 seconds until the client disconnects.
 *
 * @author Reza Mousakhani
 * @author Damian Dhesi
 * @author Shiv Panchal
 * @version 2.0
 */
public class Publisher implements Runnable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Publisher.class);
    private final int PORT;

    //Wait time can be adjusted as necessary, just to stop from spamming client
    private final static int WAIT_TIME = 30000;

    /**
     * Constructs a new Publisher with the specified port.
     *
     * @param port the port number to listen for client connections
     */
    public Publisher(int port) {
        this.PORT = port;
    }

    /**
     * Runs the Publisher, accepting client connections, generating random angles, and sending them
     * to the connected client. Waits for a new client if the current one disconnects.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Publisher started. Ready for clients to connect");
            Socket client = serverSocket.accept();
            LOGGER.info("Client connected: " + client.toString());
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            Random rand = new Random();

            while (true) {
                LOGGER.info("Randomly generating angles");
                StringBuilder angles = new StringBuilder();
                for (int i = 0; i < 5; i++) {
                    angles.append(rand.nextInt(360)).append(",");
                }
                angles.append(rand.nextInt(360));

                LOGGER.info("Sending 6 angles: " + angles);
                out.println(angles);

                if (out.checkError()) {
                    LOGGER.info("Client closed. Waiting for new one");
                    client = serverSocket.accept();
                    LOGGER.info("New client found: " + client.toString());
                    out = new PrintWriter(client.getOutputStream(), true);
                }

                LOGGER.info("Wait 30 seconds");
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException ignored) {}
                LOGGER.info(("Wait finished"));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
