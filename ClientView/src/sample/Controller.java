package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button bt1;

    private DataModel model;

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;

        if(true) { //al posti di true controllo sul getSize() dell'arraylist di mail per controllare se la casella è vuota
            stage = (Stage) bt1.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("mail.fxml"));

            Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
            URL url = this.getClass().getResource("Mail.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        }else{
            stage = (Stage) bt1.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("mail2.fxml"));

            Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
            URL url = this.getClass().getResource("Mail2.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        }

    }

    public void initialize(URL location, ResourceBundle resources) {
        if (model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        model = new DataModel();
    }
}
