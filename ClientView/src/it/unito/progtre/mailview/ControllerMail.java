package it.unito.progtre.mailview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMail implements Initializable {

    @FXML
    private Button newMail;

    @FXML
    private ComboBox<String> mails;

    @FXML
    private Label mailText;

    @FXML
    private Button update;

    @FXML
    private Button delete;

    private Model model;

    public void handleButtonActionNewMail(ActionEvent actionEvent) {
        Stage stage;
        Parent root;

        try {
            stage = (Stage) newMail.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMail.fxml"));
            root = loader.load();
            ControllerNewMail controller = loader.getController();
            controller.setModel(model);

            Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
            URL url = this.getClass().getResource("NewMail.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleButtonActionMail(ActionEvent actionEvent) {
        if(actionEvent.getSource() == mails){
            if(model.size() > 0) //mailText.setText(model.getEmails().get(mails.getSelectionModel().getSelectedIndex()));
                mailText.setText(mails.getSelectionModel().getSelectedItem());
        }else if(actionEvent.getSource() == update){
            mails.getItems().clear();
            //  for(int i = 0; i < model.size(); i++)
            //mails.getItems().add(model.getEmails().get(i));
        }else if(actionEvent.getSource() == delete){
            model.delete(mails.getSelectionModel().getSelectedIndex());
            mails.getItems().remove(mails.getSelectionModel().getSelectedIndex());
            if(model.size() == 0) mailText.setText("");
        }
    }

    public void handleButtonReplyEmail(ActionEvent actionEvent) {
        //to be defined
    }

    public void setModel(Model m){
        this.model = m;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
