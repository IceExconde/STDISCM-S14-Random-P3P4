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

public class ConsumerGUI extends javafx.application.Application {
    private static final int THUMBNAIL_WIDTH = 320;
    private static final int THUMBNAIL_HEIGHT = 180;
    private static final int PREVIEW_SECONDS = 10;
    private static final int CARDS_PER_ROW = 5;

    private static ConsumerGUI instance;
    private static BlockingQueue<File> videoQueue;

    private Stage primaryStage;
    private ObservableList<File> videoList;
    private Label statusLabel;
    private Label queueStatusLabel;
    private FlowPane videoGridPane;

    public static void launchGUI(BlockingQueue<File> queue) {
        videoQueue = queue;
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
    
        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready to receive videos");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        queueStatusLabel = new Label("Queue: 0/" + videoQueue.remainingCapacity());
        queueStatusLabel.setStyle("-fx-font-size: 14;");
        
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> handleExit());
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        headerBox.getChildren().addAll(statusLabel, spacer, queueStatusLabel, exitButton);
        root.setTop(headerBox);
    
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
        // Show confirmation dialog
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
            int currentSize = videoQueue.size();
            int maxSize = videoQueue.remainingCapacity() + currentSize;
            queueStatusLabel.setText("Queue: " + currentSize + "/" + maxSize);
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
        private final HBox buttonBox = new HBox(5, previewButton);
        private final ProgressIndicator progressIndicator = new ProgressIndicator();
        private Image thumbnailImage = null;
        private final File videoFile;

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
            
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            
            setSpacing(8);
            
            getChildren().addAll(imageView, nameLabel, buttonBox, progressIndicator);
            
            previewButton.setOnAction(e -> showPreview(videoFile));
            
            loadThumbnail();
        }
        
        private void loadThumbnail() {
            new Thread(() -> {
                Image thumbnail = extractFirstFrame(videoFile);
                Platform.runLater(() -> {
                    if (thumbnail != null) {
                        thumbnailImage = thumbnail;
                        imageView.setImage(thumbnail);
                        getChildren().remove(progressIndicator);
                    } else {
                        try {
                            thumbnailImage = new Image("file:resources/video-icon.png");
                            imageView.setImage(thumbnailImage);
                        } catch (Exception e) {
                            try {
                                thumbnailImage = new Image(getClass().getResourceAsStream("/video-icon.png"));
                                imageView.setImage(thumbnailImage);
                            } catch (Exception ex) {
                                imageView.setStyle("-fx-background-color: #cccccc;");
                            }
                        }
                        getChildren().remove(progressIndicator);
                    }
                });
            }).start();
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

            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            final Image[] thumbnail = new Image[1];
            
            mediaPlayer.setOnReady(() -> {
                try {
                    WritableImage image = new WritableImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                    
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setFitWidth(THUMBNAIL_WIDTH);
                    mediaView.setFitHeight(THUMBNAIL_HEIGHT);
                    
                    mediaPlayer.seek(Duration.seconds(5));
                    
                    Thread.sleep(100);
                    
                    mediaView.snapshot(new SnapshotParameters(), image);
                    thumbnail[0] = image;
                } catch (Exception e) {
                    System.err.println("Error capturing thumbnail: " + e.getMessage());
                } finally {
                    latch.countDown();
                    mediaPlayer.dispose();
                }
            });
            
            mediaPlayer.play();
            
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
            
            return thumbnail[0];
        } catch (Exception e) {
            System.err.println("Error extracting first frame: " + e.getMessage());
            return null;
        }
    }
}