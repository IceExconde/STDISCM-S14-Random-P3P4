Extract the javafx-sdk-21.0.06.zip
place in your preferred directory (then update the module-path below)

To compile all the .java files:
javac --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media  consumer\Consumer.java consumer\ConsumerGUI.java consumer\IntegerValidator.java producer\Producer.java producer\IntegerValidator.java  

Test in different terminal
[Consumer]
java --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media consumer.Consumer

[Producer]
java producer.Producer