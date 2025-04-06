// STDISCM S14 Exconde, Gomez, Maristela, Rejano
package producer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * * Producer class that sends video files to a server.
 * It creates a specified number of threads, each responsible for sending video files from a designated folder to the consumer.
 */
public class Producer {
    //private static final String SERVER_HOST = "10.255.234.230";
    private static final String SERVER_HOST = "localhost"; // For local testing
    private static final int SERVER_PORT = 8081;
    private static final int CONNECTION_TIMEOUT_MS = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int p = getValidThreadCount(scanner);
        scanner.close();

        for (int i = 1; i <= p; i++) {
            String folderPath = "P3/producer/videos" + i;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            new Thread(() -> startProducer(folderPath)).start();
        }
    }

    /**
     * * Validates the number of producer threads.
     * If the input is not a valid positive integer, it prompts the user to enter a valid number.
     * @param scanner
     * @return the number of producer threads
     */
    private static int getValidThreadCount(Scanner scanner) {
        while (true) {
            System.out.print("Enter number of producer threads: ");
            String input = scanner.nextLine();
            
            if (IntegerValidator.exceedsIntegerLimit(input)) {
                System.out.println("Error: Input exceeds maximum value for an integer (" + Integer.MAX_VALUE + ").");
            } else if (IntegerValidator.isValidPositiveInteger(input)) {
                return Integer.parseInt(input);
            } else {
                System.out.println("Error: Invalid input. Please enter a valid integer >= 1.");
            }
        }
    }

    /**
     * * Starts the producer thread that sends video files to the server.
     * It retrieves the list of video files from the specified folder and sends them one by one.
     * @param folderPath
     */
    private static void startProducer(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp4"));
        
        if (files != null) {
            for (File file : files) {
                sendVideoWithRetry(file, 3);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * * Sends a video file to the server with retry logic.
     * If the upload fails due to a connection issue, it retries up to maxRetries times.
     * @param file
     * @param maxRetries
     */
    private static void sendVideoWithRetry(File file, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                sendVideo(file);
                return;
            } catch (SocketException e) {
                // Connection rejected (queue full)
                System.out.println("Video rejected (queue full): " + file.getName());
                return;
            } catch (IOException e) {
                attempts++;
                System.err.println("Attempt " + attempts + " failed for " + file.getName() + ": " + e.getMessage());
                if (attempts < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        System.err.println("Failed to upload " + file.getName() + " after " + maxRetries + " attempts");
    }

    /**
     * * Sends a video file to the server.
     * It opens a socket connection, sends the file name, and streams the file data to the server.
     * @param file
     * @throws IOException
     */
    private static void sendVideo(File file) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = socket.getOutputStream()) {
            
            socket.setSoTimeout(CONNECTION_TIMEOUT_MS);
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(file.getName());
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                
                // Check if connection was closed by server (queue full)
                if (socket.isClosed() || socket.isOutputShutdown()) {
                    throw new SocketException("Connection closed by server");
                }
            }
            System.out.println(Thread.currentThread().getName() + " uploaded: " + file.getName());
        }
    }
}