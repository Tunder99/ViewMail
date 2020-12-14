package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
//import java.awt.*;
//import java.awt.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /*@FXML
    private Label accedi;

    @FXML
    private TextField email;

    @FXML
    private TextField border;

    @FXML
    private PasswordField pwd;*/

    @FXML
    private Button bt1;

    private DataModel model;

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;

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
    }

    public void initialize(URL location, ResourceBundle resources) {
        if (model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        model = new DataModel();
    }
}
