package consumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer {
    public static final int SERVER_PORT = 8081;
    public static final int MAX_QUEUE_LENGTH = 2;
    public static final int PROCESSING_RATE_MS = 2000; // Process one video every 2 seconds
    public static final String SAVE_FOLDER = "P3/consumer/videos/";

    public static BlockingQueue<File> videoQueue;
    public static AtomicInteger droppedVideos = new AtomicInteger(0);
    private static ConsumerGUI gui;
    private static ScheduledExecutorService leakyBucket;
    private static ServerSocket serverSocket;
    private static volatile boolean acceptConnections = true;

    public static void main(String[] args) {
        new Thread(() -> {
            startConsumerServer();
        }).start();
    }
    
    public static void startConsumerServer() {
        videoQueue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);
        
        // Initialize JavaFX GUI
        ConsumerGUI.launchGUI(videoQueue, droppedVideos);
        gui = ConsumerGUI.getInstance();

        File saveDir = new File(SAVE_FOLDER);
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            gui.showError("Failed to create directory: " + saveDir.getAbsolutePath());
            return;
        }

        // Leaky bucket processing at fixed rate
        leakyBucket = Executors.newScheduledThreadPool(1);
        leakyBucket.scheduleAtFixedRate(() -> {
            try {
                if (!videoQueue.isEmpty()) {
                    File video = videoQueue.take();
                    processVideo(video);
                    gui.updateQueueStatus();
                    
                    // Check if we can start accepting connections again
                    synchronized (Consumer.class) {
                        if (!acceptConnections && videoQueue.size() < MAX_QUEUE_LENGTH) {
                            acceptConnections = true;
                            gui.updateStatus("Ready to accept new videos");
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                gui.showError("Processing interrupted: " + e.getMessage());
            }
        }, 0, PROCESSING_RATE_MS, TimeUnit.MILLISECONDS);

        // Connection handling thread pool
        ExecutorService connectionPool = Executors.newFixedThreadPool(10);

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            gui.updateStatus("Consumer is listening on port " + SERVER_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                synchronized (Consumer.class) {
                    if (!acceptConnections) {
                        Thread.sleep(100);
                        continue;
                    }
                }

                // Check queue before accepting
                if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
                    synchronized (Consumer.class) {
                        acceptConnections = false;
                    }
                    continue;
                }

                Socket socket = serverSocket.accept();
                connectionPool.execute(() -> handleUpload(socket));
            }
        } catch (IOException | InterruptedException e) {
            gui.showError("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            leakyBucket.shutdown();
            connectionPool.shutdown();
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                gui.showError("Error closing server socket: " + e.getMessage());
            }
        }
    }

    private static void handleUpload(Socket socket) {
        File tempFile = null;
        try {
            // Immediately check queue status again (in case it changed)
            if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
                droppedVideos.incrementAndGet();
                gui.updateDroppedCount();
                socket.close();
                gui.updateStatus("Dropped video - queue full");
                return;
            }

            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            String fileName = dis.readUTF();
            
            // Check queue status again after reading filename
            if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
                droppedVideos.incrementAndGet();
                gui.updateDroppedCount();
                socket.close();
                gui.updateStatus("Dropped video - queue full: " + fileName);
                return;
            }

            // Create temp file in system temp directory instead of SAVE_FOLDER
            tempFile = File.createTempFile("upload_", ".tmp");
            
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    
                    // Check queue status periodically during upload
                    if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
                        droppedVideos.incrementAndGet();
                        gui.updateDroppedCount();
                        socket.close();
                        tempFile.delete();
                        gui.updateStatus("Dropped video during upload: " + fileName);
                        return;
                    }
                }
            }
            
            // Only move to save folder if queue has space
            File saveFile = new File(SAVE_FOLDER + fileName);
            if (tempFile.renameTo(saveFile)) {
                videoQueue.put(saveFile);
                gui.updateQueueStatus();
                gui.updateStatus("Received: " + fileName);
                
                // Check if queue is now full
                if (videoQueue.size() >= MAX_QUEUE_LENGTH) {
                    synchronized (Consumer.class) {
                        acceptConnections = false;
                    }
                }
            } else {
                droppedVideos.incrementAndGet();
                gui.updateDroppedCount();
                tempFile.delete();
                gui.updateStatus("Failed to save: " + fileName);
            }
        } catch (IOException | InterruptedException e) {
            droppedVideos.incrementAndGet();
            gui.updateDroppedCount();
            gui.showError("Error handling upload: " + e.getMessage());
        } finally {
            try {
                socket.close();
                // Ensure temp file is deleted if something went wrong
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            } catch (IOException e) {
                // Ignore close errors
            }
        }
    }

    private static void processVideo(File video) {
        // Simulate video processing
        gui.updateStatus("Processing: " + video.getName());
        try {
            Thread.sleep(1000); // Simulate processing time
            gui.addVideo(video);
            gui.updateStatus("Completed processing: " + video.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            gui.showError("Processing interrupted for: " + video.getName());
        }
    }
}