package consumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Consumer {
    public static final int SERVER_PORT = 8081;
    public static final int MAX_QUEUE_LENGTH = 3; // for testing
    public static final String SAVE_FOLDER = "P3/consumer/videos/";

    public static BlockingQueue<File> videoQueue;
    private static ConsumerGUI gui;

    public static void main(String[] args) {
        new Thread(() -> {
            startConsumerServer();
        }).start();
    }
    
    public static void startConsumerServer() {
        videoQueue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);
        
        // Initialize JavaFX GUI
        ConsumerGUI.launchGUI(videoQueue);
        gui = ConsumerGUI.getInstance();

        File saveDir = new File(SAVE_FOLDER);
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            gui.showError("Failed to create directory: " + saveDir.getAbsolutePath());
            return;
        }

        ExecutorService consumerPool = Executors.newFixedThreadPool(3);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            gui.updateStatus("Consumer is listening on port " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                consumerPool.execute(() -> handleUpload(socket));
            }
        } catch (IOException e) {
            gui.showError("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleUpload(Socket socket) {
        gui.updateQueueStatus();

        if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
            try {
                gui.updateStatus("Queue full. Rejecting connection.");
                socket.close();
                return;
            } catch (IOException e) {
                gui.showError("Error closing socket: " + e.getMessage());
            }
        }

        File saveFile = null;
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {

            String fileName = dis.readUTF();
            saveFile = new File(SAVE_FOLDER + fileName);
            videoQueue.put(saveFile);
            gui.updateQueueStatus();

            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                gui.addVideo(saveFile);
                gui.updateStatus("Received and saved: " + fileName);
            }
        } catch (IOException | InterruptedException e) {
            gui.showError("Error handling upload: " + e.getMessage());
        } finally {
            if (saveFile != null) {
                videoQueue.remove(saveFile);
                gui.updateQueueStatus();
            }
        }
    }
}