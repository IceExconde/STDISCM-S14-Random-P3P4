Extract the javafx-sdk-21.0.06.zip
place in your preferred directory (then update the module-path below)

To compile all the .java files:
javac --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics  consumer\*.java producer\*.java  

Test in different terminal
[Consumer]
java --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics consumer.Consumer

[Producer]
java producer.Producer