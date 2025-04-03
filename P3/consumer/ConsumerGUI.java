package consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.PauseTransition;

public class ConsumerGUI extends Application {
    private static final int THUMBNAIL_WIDTH = 320;
    private static final int THUMBNAIL_HEIGHT = 180;
    private static final int PREVIEW_SECONDS = 10;
    private static final int CARDS_PER_ROW = 5;
    private int maxQueueSize = Consumer.MAX_QUEUE_LENGTH;

    private static ConsumerGUI instance;
    private static BlockingQueue<File> videoQueue;
    private static AtomicInteger droppedVideos;

    private Stage primaryStage;
    private ObservableList<File> videoList;
    private Label statusLabel;
    private Label queueStatusLabel;
    private Label droppedVideosLabel;
    private FlowPane videoGridPane;

    public static void launchGUI(BlockingQueue<File> queue, AtomicInteger droppedCounter) {
        videoQueue = queue;
        droppedVideos = droppedCounter;
        new Thread(() -> Application.launch(ConsumerGUI.class)).start();
    }

    public static ConsumerGUI getInstance() {
        while (instance == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        this.videoList = FXCollections.observableArrayList();

        createUI();
        
        primaryStage.setOnCloseRequest(event -> {
            handleExit();
        });
    }

    private void createUI() {
        primaryStage.setTitle("Media Upload Consumer");
    
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Configuration Panel
        HBox configPanel = new HBox(10);
        configPanel.setPadding(new Insets(0, 0, 10, 0));
        
        Label threadsLabel = new Label("Consumer Threads:");
        Spinner<Integer> threadsSpinner = new Spinner<>(1, 10, Consumer.CONSUMER_THREADS);
        
        Label queueLabel = new Label("Max Queue Size:");
        Spinner<Integer> queueSpinner = new Spinner<>(1, 100, Consumer.MAX_QUEUE_LENGTH);
        
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            Consumer.updateConfiguration(
                threadsSpinner.getValue(),
                queueSpinner.getValue()
            );
        });
        
        configPanel.getChildren().addAll(
            threadsLabel, threadsSpinner,
            queueLabel, queueSpinner,
            applyButton
        );
    
        // Top Header
        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready to receive videos");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        queueStatusLabel = new Label("Queue: 0/" + videoQueue.remainingCapacity());
        queueStatusLabel.setStyle("-fx-font-size: 14;");
        
        droppedVideosLabel = new Label("Dropped: 0");
        droppedVideosLabel.setStyle("-fx-font-size: 14; -fx-text-fill: red;");
        
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> handleExit());
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        headerBox.getChildren().addAll(
            statusLabel, 
            new Label(" | "), 
            queueStatusLabel,
            new Label(" | "),
            droppedVideosLabel,
            spacer,         
            exitButton
        );
        VBox topContainer = new VBox(configPanel, headerBox);
        root.setTop(topContainer);

        Button clearButton = new Button("Clear All Videos");
        clearButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        clearButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Clear");
            confirm.setHeaderText("Clear All Videos");
            confirm.setContentText("Are you sure you want to delete all saved videos?");
    
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Consumer.clearAllVideos();
                }
            });
        });

        configPanel.getChildren().add(clearButton);
    
        // Video Grid
        videoGridPane = new FlowPane();
        videoGridPane.setHgap(15);
        videoGridPane.setVgap(15);
        videoGridPane.setPadding(new Insets(10));
        videoGridPane.setPrefWrapLength(CARDS_PER_ROW * (THUMBNAIL_WIDTH + 50)); 
        
        ScrollPane scrollPane = new ScrollPane(videoGridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        
        root.setCenter(scrollPane);
    
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Exit Application");
        alert.setContentText("Are you sure you want to exit the Consumer server?");
        
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        
        alert.showAndWait().ifPresent(type -> {
            if (type == buttonTypeYes) {
                updateStatus("Shutting down...");
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void addVideo(File videoFile) {
        Platform.runLater(() -> {
            if (!videoList.contains(videoFile)) {
                videoList.add(videoFile);
                VideoCard videoCard = new VideoCard(videoFile);
                videoGridPane.getChildren().add(videoCard);
            }
        });
    }

    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    public void updateQueueStatus() {
        Platform.runLater(() -> {
            int currentSize = Consumer.getQueueSize();
            queueStatusLabel.setText("Queue: " + currentSize + "/" + maxQueueSize);
        });
    }

    public void updateDroppedCount() {
        Platform.runLater(() -> {
            droppedVideosLabel.setText("Dropped: " + droppedVideos.get());
        });
    }

    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private class VideoCard extends VBox {
        private final ImageView imageView = new ImageView();
        private final Label nameLabel = new Label();
        private final Button previewButton = new Button("Play Preview");
        private final Button fullButton = new Button("Play Full");
        private final HBox buttonBox = new HBox(5, previewButton, fullButton);
        private final ProgressIndicator progressIndicator = new ProgressIndicator();
        private Image thumbnailImage = null;
        private final File videoFile;
        private boolean thumbnailLoaded = false;
    
        public VideoCard(File videoFile) {
            this.videoFile = videoFile;
            
            setPadding(new Insets(10));
            setPrefWidth(THUMBNAIL_WIDTH + 20);
            setMaxWidth(THUMBNAIL_WIDTH + 20);
            setStyle("-fx-background-color: white; -fx-border-color: #dddddd; " +
                     "-fx-border-radius: 5; -fx-background-radius: 5; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 5);");
            
            imageView.setFitWidth(THUMBNAIL_WIDTH);
            imageView.setFitHeight(THUMBNAIL_HEIGHT);
            imageView.setPreserveRatio(true);
    
            nameLabel.setText(videoFile.getName());
            nameLabel.setWrapText(true);
            nameLabel.setPadding(new Insets(5, 0, 5, 0));
            nameLabel.setStyle("-fx-font-weight: bold;");
            
            buttonBox.setAlignment(Pos.CENTER);
            setSpacing(8);
            
            getChildren().addAll(imageView, nameLabel, buttonBox, progressIndicator);
            
            previewButton.setOnAction(e -> showPreview(videoFile));
            fullButton.setOnAction(e -> playFullVideo(videoFile));
            
            loadThumbnailWithRetry(3); // Try 3 times to load thumbnail
        }
        
        private void loadThumbnailWithRetry(int remainingAttempts) {
            if (thumbnailLoaded || remainingAttempts <= 0) {
                Platform.runLater(() -> {
                    if (!thumbnailLoaded) {
                        setFallbackThumbnail();
                    }
                    getChildren().remove(progressIndicator);
                });
                return;
            }
    
            new Thread(() -> {
                Image thumbnail = extractFirstFrame(videoFile);
                Platform.runLater(() -> {
                    if (thumbnail != null) {
                        thumbnailImage = thumbnail;
                        imageView.setImage(thumbnail);
                        thumbnailLoaded = true;
                        getChildren().remove(progressIndicator);
                    } else {
                        // Retry after a delay if attempts remain
                        if (remainingAttempts > 1) {
                            PauseTransition delay = new PauseTransition(Duration.seconds(1));
                            delay.setOnFinished(event -> loadThumbnailWithRetry(remainingAttempts - 1));
                            delay.play();
                        } else {
                            setFallbackThumbnail();
                            getChildren().remove(progressIndicator);
                        }
                    }
                });
            }).start();
        }
    
        private void setFallbackThumbnail() {
            try {
                // Try to load from resources folder first
                thumbnailImage = new Image(getClass().getResourceAsStream("/video-icon.png"));
                imageView.setImage(thumbnailImage);
            } catch (Exception e) {
                try {
                    // Try to load from file system
                    thumbnailImage = new Image("file:resources/video-icon.png");
                    imageView.setImage(thumbnailImage);
                } catch (Exception ex) {
                    // Final fallback - just show a colored background
                    imageView.setStyle("-fx-background-color: #cccccc;");
                }
            }
        }
    }

    private void playFullVideo(File videoFile) {
        try {
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            
            Stage playerStage = new Stage();
            playerStage.initOwner(primaryStage);
            playerStage.setTitle("Playing: " + videoFile.getName());
            
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(450);
            StackPane root = new StackPane(mediaView);
            Scene scene = new Scene(root);
            
            playerStage.setScene(scene);
            playerStage.show();
            
            mediaPlayer.play();
            
            mediaPlayer.setOnEndOfMedia(() -> {
                Platform.runLater(playerStage::close);
            });
        } catch (Exception e) {
            showError("Error playing video: " + e.getMessage());
        }
    }

    private void showPreview(File videoFile) {
        try {
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            
            Stage previewStage = new Stage();
            previewStage.initOwner(primaryStage);
            previewStage.setTitle("Preview: " + videoFile.getName());
            
            mediaView.setFitWidth(640);
            mediaView.setFitHeight(360);
            StackPane root = new StackPane(mediaView);
            Scene scene = new Scene(root);
            
            previewStage.setScene(scene);
            
            previewStage.setOnCloseRequest(event -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });
            
            previewStage.show();
            
            mediaPlayer.play();
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.seek(Duration.ZERO);
                
                java.util.Timer timer = new java.util.Timer();
                timer.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                mediaPlayer.stop();
                                mediaPlayer.dispose(); 
                                previewStage.close();
                            });
                        }
                    },
                    PREVIEW_SECONDS * 1000
                );
            });
        } catch (Exception e) {
            System.err.println("Error showing preview: " + e.getMessage());
        }
    }

    private Image extractFirstFrame(File videoFile) {
        try {
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setMute(true);

            CountDownLatch latch = new CountDownLatch(1);
            final Image[] thumbnail = new Image[1];
            
            mediaPlayer.setOnReady(() -> {
                try {
                    // Try multiple positions if first attempt fails
                    for (int i = 0; i < 3; i++) {
                        mediaPlayer.seek(Duration.seconds(10));
                        
                        // Give it time to seek
                        Thread.sleep(500);
                        
                        WritableImage image = new WritableImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                        MediaView mediaView = new MediaView(mediaPlayer);
                        mediaView.setFitWidth(THUMBNAIL_WIDTH);
                        mediaView.setFitHeight(THUMBNAIL_HEIGHT);
                        
                        if (mediaView.snapshot(new SnapshotParameters(), image) != null) {
                            thumbnail[0] = image;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error capturing thumbnail: " + e.getMessage());
                } finally {
                    latch.countDown();
                    mediaPlayer.dispose();
                }
            });
            
            mediaPlayer.play();
            latch.await(15, TimeUnit.SECONDS); // Longer timeout for multiple attempts
            return thumbnail[0];
        } catch (Exception e) {
            System.err.println("Error extracting frame: " + e.getMessage());
            return null;
        }
    }

    public void clearVideoDisplay() {
        Platform.runLater(() -> {
            videoList.clear();
            videoGridPane.getChildren().clear();
        });
    }

    public void updateQueueMaxSize(int newMaxSize) {
        Platform.runLater(() -> {
            maxQueueSize = newMaxSize;
        });
    }
}