package consumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.Scanner;

public class Consumer {
    private static final int SERVER_PORT = 8081;
    private static final int MAX_QUEUE_LENGTH = 5; // for testing
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

    private static void handleUpload(Socket socket) {
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {
            
            String fileName = dis.readUTF();
            File saveFile = new File(SAVE_FOLDER + fileName); 
            
            if (!videoQueue.offer(saveFile)) {
                System.out.println("Queue full. Dropping video: " + fileName);
                return;
            }
    
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Received and saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
