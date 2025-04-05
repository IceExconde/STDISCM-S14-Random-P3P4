package consumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer {
    public static final int SERVER_PORT = 8081;
    public static int PROCESSING_RATE_MS = 500; 
    public static String SAVE_FOLDER = "P3/consumer/videos/";

    private static volatile BlockingQueue<File> videoQueue;
    private static volatile ScheduledExecutorService leakyBucket;
    public static AtomicInteger droppedVideos = new AtomicInteger(0);
    private static ConsumerGUI gui;
    private static ServerSocket serverSocket;
    private static final ReentrantLock queueLock = new ReentrantLock();
    public static int CONSUMER_THREADS;
    public static int MAX_QUEUE_LENGTH;
    private static Semaphore queueSlots;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
    
        int threads = getValidThreadCount(scanner);
        int queueSize = getValidQueueSize(scanner);
        scanner.close();
    
        CONSUMER_THREADS = threads;
        MAX_QUEUE_LENGTH = queueSize;
        queueSlots = new Semaphore(MAX_QUEUE_LENGTH);
    
        new Thread(() -> startConsumerServer()).start();
    }

    private static int getValidThreadCount(Scanner scanner) {
        while (true) {
            System.out.print("Enter number of consumer threads: ");
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
    
    private static int getValidQueueSize(Scanner scanner) {
        while (true) {
            System.out.print("Enter max queue length: ");
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
    
    
    public static void startConsumerServer() {
        videoQueue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);
        
        ConsumerGUI.launchGUI(videoQueue, droppedVideos);
        gui = ConsumerGUI.getInstance();

        File saveDir = new File(SAVE_FOLDER);
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            gui.showError("Failed to create directory: " + saveDir.getAbsolutePath());
            return;
        }

        // Leaky bucket processing with configurable threads
        leakyBucket = Executors.newScheduledThreadPool(CONSUMER_THREADS);
        for (int i = 0; i < CONSUMER_THREADS; i++) {
            leakyBucket.scheduleAtFixedRate(() -> {
                try {
                    File video = videoQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (video != null) {
                        processVideo(video);
                        queueSlots.release();
                        gui.updateQueueStatus();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    gui.showError("Processing interrupted: " + e.getMessage());
                }
            }, 0, PROCESSING_RATE_MS, TimeUnit.MILLISECONDS);
        }

        // Connection handling thread pool
        ExecutorService connectionPool = Executors.newFixedThreadPool(10);

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            gui.updateStatus(String.format(
                "Consumer is listening on port %d with %d threads (Queue size: %d)", 
                SERVER_PORT, CONSUMER_THREADS, MAX_QUEUE_LENGTH
            ));

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                connectionPool.execute(() -> handleUpload(socket));
            }
        } catch (IOException e) {
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

    public static void updateConfiguration(int threads, int queueSize) {
        queueLock.lock();
        try {
            CONSUMER_THREADS = threads;
            MAX_QUEUE_LENGTH = queueSize;
    
            // Reinitialize queue and semaphore with new size
            BlockingQueue<File> newQueue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);
            videoQueue.drainTo(newQueue);
            videoQueue = newQueue;
            queueSlots = new Semaphore(MAX_QUEUE_LENGTH);
    
            // Restart consumer threads
            if (leakyBucket != null && !leakyBucket.isShutdown()) {
                leakyBucket.shutdownNow();
                try {
                    leakyBucket.awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    gui.showError("Failed to terminate old consumer threads: " + e.getMessage());
                }
            }
    
            leakyBucket = Executors.newScheduledThreadPool(CONSUMER_THREADS);
            for (int i = 0; i < CONSUMER_THREADS; i++) {
                leakyBucket.scheduleAtFixedRate(() -> {
                    try {
                        File video = videoQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (video != null) {
                            processVideo(video);
                            queueSlots.release();
                            gui.updateQueueStatus();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        gui.showError("Processing interrupted: " + e.getMessage());
                    }
                }, 0, PROCESSING_RATE_MS, TimeUnit.MILLISECONDS);
            }
    
            gui.updateStatus(String.format(
                "Configuration updated: %d threads, queue size %d",
                CONSUMER_THREADS, MAX_QUEUE_LENGTH
            ));
            gui.updateQueueMaxSize(MAX_QUEUE_LENGTH);
            gui.updateQueueStatus();
        } finally {
            queueLock.unlock();
        }
    }

    private static void handleUpload(Socket socket) {
        File tempFile = null;
        String fileName = "";
        
        try {
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            // First get the filename
            fileName = dis.readUTF();
            
            // Try to acquire a queue slot (non-blocking)
            if (!queueSlots.tryAcquire()) {
                droppedVideos.incrementAndGet();
                gui.updateDroppedCount();
                socket.close();
                String reason = "Queue full (Max: " + MAX_QUEUE_LENGTH + ")";
                System.out.println("[DROP] " + fileName + " - Reason: " + reason);
                gui.updateStatus("Dropped video - queue full: " + fileName);
                return;
            }

            // Create temp file
            tempFile = File.createTempFile("upload_", ".tmp");
            
            // Read the file data
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            // Final processing
            File saveFile = new File(SAVE_FOLDER + fileName);
            if (tempFile.renameTo(saveFile)) {
                queueLock.lock();
                try {
                    videoQueue.put(saveFile);
                    gui.updateQueueStatus();
                    gui.updateStatus("Received: " + fileName);
                } finally {
                    queueLock.unlock();
                }
            } else {
                queueSlots.release(); // Release the slot if we failed to save
                droppedVideos.incrementAndGet();
                gui.updateDroppedCount();
                String reason = "Failed to save file (rename operation failed)";
                System.out.println("[DROP] " + fileName + " - Reason: " + reason);
                tempFile.delete();
                gui.updateStatus("Failed to save: " + fileName);
            }
        } catch (SocketTimeoutException e) {
            queueSlots.release();
            droppedVideos.incrementAndGet();
            gui.updateDroppedCount();
            String reason = "Socket timeout during upload";
            System.out.println("[DROP] " + fileName + " - Reason: " + reason);
        } catch (IOException e) {
            if (queueSlots != null) queueSlots.release();
            droppedVideos.incrementAndGet();
            gui.updateDroppedCount();
            String reason = "I/O Error: " + e.getClass().getSimpleName();
            System.out.println("[DROP] " + fileName + " - Reason: " + reason);
        } catch (Exception e) {
            if (queueSlots != null) queueSlots.release();
            droppedVideos.incrementAndGet();
            gui.updateDroppedCount();
            String reason = "Unexpected error: " + e.getClass().getSimpleName();
            System.out.println("[DROP] " + fileName + " - Reason: " + reason);
        } finally {
            try {
                socket.close();
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            } catch (IOException e) {
                System.out.println("[WARNING] Error cleaning up resources: " + e.getMessage());
            }
        }
    }

    private static void processVideo(File video) {
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

    public static void clearAllVideos() {
        queueLock.lock();
        try {
            // Clear the queue and release all slots
            int released = videoQueue.size();
            videoQueue.clear();
            queueSlots.release(released);
            
            // Delete all files in the save folder
            File saveDir = new File(SAVE_FOLDER);
            if (saveDir.exists()) {
                File[] files = saveDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.delete()) {
                            System.err.println("Failed to delete: " + file.getAbsolutePath());
                        }
                    }
                }
            }
            
            // Update GUI
            gui.clearVideoDisplay();
            gui.updateQueueStatus();
            gui.updateStatus("All videos cleared");
        } finally {
            queueLock.unlock();
        }
    }

    public static int getQueueSize() {
        return videoQueue.size();
    }
}