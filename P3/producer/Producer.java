package producer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Producer {
    private static final String SERVER_HOST = "localhost"; // Change for testing
    private static final int SERVER_PORT = 8081;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        int p;

        while (true) {
            System.out.print("Enter number of producer threads: ");
            input = scanner.nextLine();  // Read input as a string

            if (IntegerValidator.isValidPositiveInteger(input)) {
                p = Integer.parseInt(input);
                break;
            } else {
                System.out.println("Error: Invalid input. Please enter a valid integer >= 1.");
            }
        }

        // // download videos for each producer thread
        // System.out.println("Fetching music videos...");
        // for (int i = 1; i <= p; i++) {
        //     String folderPath = "P3/producer/videos" + i;
        //     YouTubeDownloader.downloadYouTubeVideos(folderPath);
        // }
        scanner.close(); // Close scanner to avoid resource leak

         // dynamically create folders based on the number of producer threads
         for (int i = 1; i <= p; i++) {
            String folderPath = "P3/producer/videos" + i;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Create folder if it does not exist
            }
            new Thread(() -> startProducer(folderPath)).start();
        }
    }

    private static void startProducer(String folderPath) {
        System.out.println("Attempting to read from: " + new File(folderPath).getAbsolutePath());
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp4"));
        if (files != null) {
            for (File file : files) {
                sendVideo(file);
            }
        }
    }

    private static void sendVideo(File file) {
        try {
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                FileInputStream fis = new FileInputStream(file);
                OutputStream os = socket.getOutputStream()) {
                
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF(file.getName());
                System.out.println(Thread.currentThread().getName() + " uploading: " + file.getName());
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                System.out.println("Uploaded: " + file.getName());
            }
        } catch (IOException e) {
            System.err.println("Error uploading " + file.getName() + ": " + e.getMessage());
        }
    }
}
