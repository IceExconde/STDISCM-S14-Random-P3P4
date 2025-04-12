# STDISCM-S14-Random-P3P4

## P3
### Build/Compilation Steps
#### Using Visual Studio Code:
- Extract the javafx-sdk-21.0.06.zip and place in your preferred directory (then update the module-path below)
- To compile all the .java files:
```javac --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics  consumer\*.java producer\*.java```

Execute in different terminal

[Consumer]

```java --module-path "D:\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics consumer.Consumer```

[Producer]

```java producer.Producer```
