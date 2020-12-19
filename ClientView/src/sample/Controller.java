package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button bt1;

    @FXML
    private Label mailText;

    @FXML
    private Button update;

    @FXML
    private Button delete;

    @FXML
    private Button lastMail;

    @FXML
    private ComboBox<String> mails;

    private DataModel model;

    @FXML
    private void handleButtonActionLogin(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;

        if (true) { //al posti di true controllo sul getSize() dell'arraylist di mail per controllare se la casella è vuota
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
        } else {
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

        if(location.toExternalForm().contains("mail.fxml")){
            for(int i = 0; i < model.size(); i++) {
                mails.getItems().add(model.getEmails().get(i));
            }
        }
    }

    @FXML
    public void handleButtonActionMail(ActionEvent actionEvent) {
        if(actionEvent.getSource() == lastMail){
            mailText.setText(model.getEmails().get(model.last()));
        }else if(actionEvent.getSource() == mails){
                    mailText.setText(model.getEmails().get(mails.getSelectionModel().getSelectedIndex()));
        }else if(actionEvent.getSource() == update){
            for(int i = 0; i < model.size(); i++)
                mails.getItems().add(model.getEmails().get(i));
        }else if(actionEvent.getSource() == delete){
            if(actionEvent.getSource() != lastMail) {
                /*System.out.println("index " + mails.getSelectionModel().getSelectedIndex());
                System.out.println("model " + model.getEmails().get(mails.getSelectionModel().getSelectedIndex()));*/
                model.delete(mails.getSelectionModel().getSelectedIndex());
                mails.getItems().remove(mails.getSelectionModel().getSelectedIndex());
                //System.out.println("model:: " + model.toString());
            }
        }
    }
}