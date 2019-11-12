package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class memory_repo_gui extends Application
{

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/fx.fxml"));
        primaryStage.setTitle("memory_repo");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 640, 400));
        primaryStage.show();
        Controller.populateRunOptions();
    }
}
