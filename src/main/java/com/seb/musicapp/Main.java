package com.seb.musicapp;

import Discord.DiscordActivity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static boolean DEBUG = true;
    public Connector connector;
    public Stage stage;
    public Scene scene;
    public Stage queueStage;
    public QueueController queueController;
    public MainWindowController mainWindowController;
    public ConnectController connectController;
    public DiscordActivity discordActivity;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        this.stage = stage;
        initialise();
        DiscordActivity.create().ifPresent(this::setDiscordActivity);
    }

    private void setDiscordActivity(DiscordActivity discordActivity) {
        this.discordActivity = discordActivity;
    }

    public void initialise() throws IOException {
        this.connector = new Connector(this);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Connect.fxml"));
        scene = new Scene(fxmlLoader.load(), 320, 150);
        stage.setTitle("Connect");
        stage.setScene(scene);
        stage.show();
        connectController = fxmlLoader.getController();
        connectController.setApplication(this);
    }

    public static void main(String[] args) {
        launch();
    }

    public void connect(String id) {
        try {
            connector.connect(id);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        try {
            scene.setRoot(fxmlLoader.load());
            mainWindowController = fxmlLoader.getController();
            mainWindowController.setApplication(this);
            stage.setTitle("Music Bot App");
            stage.setScene(scene);
            mainWindowController.init();
            stage.setOnCloseRequest(event -> {
                queueStage.close();
                System.exit(0);
            });
            queueStage = new Stage();
            queueStage.setTitle("Queue");
            fxmlLoader = new FXMLLoader(Main.class.getResource("Queue.fxml"));
            Scene queueScene = new Scene(fxmlLoader.load(), 320, 800);
            queueStage.setTitle("Queue");
            queueStage.setScene(queueScene);
            queueStage.setOnCloseRequest(event -> {
                event.consume();
                queueStage.hide();
            });
            FXMLLoader finalFxmlLoader = fxmlLoader;
            queueController = finalFxmlLoader.getController();
            queueController.setApp(this);
            new Thread(() -> ((QueueController) finalFxmlLoader.getController()).setItems()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}