package consumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.Scanner;

public class Consumer {
    private static final int SERVER_PORT = 8081;
    private static final int MAX_QUEUE_LENGTH = 3; // for testing
    private static final String SAVE_FOLDER = "P3/consumer/videos/";

    private static BlockingQueue<File> videoQueue;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int c;

        while (true) {
            System.out.print("Enter number of consumer threads: ");
            String input = scanner.nextLine();
            if (IntegerValidator.isValidPositiveInteger(input)) {
                c = Integer.parseInt(input);
                break;
            } else {
                System.out.println("Error: Invalid input. Please enter a valid integer >= 1.");
            }
        }

        videoQueue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);
        ExecutorService consumerPool = Executors.newFixedThreadPool(c);

        File saveDir = new File(SAVE_FOLDER);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                System.err.println("Failed to create directory: " + saveDir.getAbsolutePath());
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Consumer is listening on port " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                consumerPool.execute(() -> handleUpload(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    //private static final Semaphore processingSlots = new Semaphore(MAX_QUEUE_LENGTH);

    private static void handleUpload(Socket socket) {
        // Log initial queue status
        System.out.println("[Queue Status] Current size: " + videoQueue.size() + "/" + MAX_QUEUE_LENGTH);
        
        // Check queue capacity FIRST (before reading data)
        if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
            try {
                System.out.println("Queue full. Rejecting connection.");
                socket.close(); // Immediately reject
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        File saveFile = null;
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {
            
            String fileName = dis.readUTF();
            saveFile = new File(SAVE_FOLDER + fileName);
    
            // Add to queue (now safe since we pre-checked)
            videoQueue.put(saveFile);
            System.out.println("[Queue Status] After adding: " + videoQueue.size() + "/" + MAX_QUEUE_LENGTH);
    
            // Save the file
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("Received and saved: " + fileName);
            }
    
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Remove from queue when done
            if (saveFile != null) {
                videoQueue.remove(saveFile);
                System.out.println("[Queue Status] After processing: " + videoQueue.size() + "/" + MAX_QUEUE_LENGTH);
            }
        }
    }
}
