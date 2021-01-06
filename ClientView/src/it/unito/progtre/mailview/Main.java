package it.unito.progtre.mailview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Model model = new Model();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
            Controller controller = loader.getController();
            Scene scene = new Scene(root);
            controller.setModel(model);

            URL url = this.getClass().getResource("Login.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}