package it.unito.progtre.mailview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    private Model model;

    public void start(Stage primaryStage) {
         model = new Model();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1185, 750);
            ControllerView controller = loader.getController();
            controller.setModel(model);
            model.setController(controller);

            URL url = this.getClass().getResource("Login.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        model.setStopped();
    }

    public static void main(String[] args) {
        launch(args);
    }
}