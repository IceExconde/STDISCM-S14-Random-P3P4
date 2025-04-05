package producer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

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

    private static void startProducer(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp4"));
        
        if (files != null) {
            for (File file : files) {
                sendVideoWithRetry(file, 3);
                try {
                    // Add delay between videos to simulate real-world scenario
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

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