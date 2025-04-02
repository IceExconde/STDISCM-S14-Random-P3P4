package producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Producer{
    private static final String SERVER_HOST = "localhost"; //change for testing
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

        String[] videoFolders = {"videos1", "videos2", "videos3"};

        //verify logic on this i think if there are more threads than folders other threads will upload on the same folder
        for (int i = 0; i < p && i < videoFolders.length; i++) {
            //idk if theres a better way to make folder path's relative i just based this on running in intellij
            String folderPath = "P3/producer/" + videoFolders[i];
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
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            System.out.println("Uploaded: " + file.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
