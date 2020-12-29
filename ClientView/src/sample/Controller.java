package sample;

import ComunicationObjects.Login;
import ComunicationObjects.ReplySendEmail;
import ComunicationObjects.RequestSendEmail;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    @FXML
    private Button newMail;

    @FXML
    private TextField toMails;

    @FXML
    private TextField subjectText;

    @FXML
    private TextArea text;

    @FXML
    private Button send;

    private DataModel model;

    @FXML
    private void handleButtonActionLogin(ActionEvent actionEvent) throws IOException {
        sceneChanger(bt1);
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
            if(model.size() > 0) //mailText.setText(model.getEmails().get(mails.getSelectionModel().getSelectedIndex()));
                mailText.setText(mails.getSelectionModel().getSelectedItem());
        }else if(actionEvent.getSource() == update){
                mails.getItems().clear();
            for(int i = 0; i < model.size(); i++)
                mails.getItems().add(model.getEmails().get(i));
        }else if(actionEvent.getSource() == delete){
            model.delete(mails.getSelectionModel().getSelectedIndex());
            mails.getItems().remove(mails.getSelectionModel().getSelectedIndex());
            if(model.size() == 0) mailText.setText("");
        }
    }

    @FXML
    public void handleButtonActionNewMail(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) newMail.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("newMail.fxml"));

        Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
        URL url = this.getClass().getResource("newMail.css");
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

    @FXML
    public void handleButtonActionSend(ActionEvent actionEvent) {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        RequestSendEmail req = null;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        ArrayList<String> to = new ArrayList<>(Arrays.asList(toMails.getText().split(",")));

        for (int i = 0; i < to.size(); i++)
            to.get(i).trim();

        try {
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            if(to.size() > 0) {
                req = new RequestSendEmail(new Login("michelefiorelli99@progtre.it", "michele1"), to,
                        subjectText.getText().trim(), text.getText());
                out.writeObject(req);
            }else System.out.println("Missing emails");
        }catch (IOException e){
            System.out.println("Connection error");
            errorAlert.setHeaderText("Server offline");
            errorAlert.setContentText("");
            errorAlert.showAndWait();
            return;
        }

        Object obj = null;
        try{
            obj = in.readObject();
            if(obj instanceof ReplySendEmail){
                ReplySendEmail rep = (ReplySendEmail) obj;
                switch (rep.getExitCode()){
                    case -1:
                        System.out.println("Login failed");
                        errorAlert.setHeaderText("Mail not sent");
                        errorAlert.setContentText("Ops, an error occurred.");
                        errorAlert.showAndWait();
                        break;

                    case -2:
                        System.out.println("Some address haven't been found: " + rep.getNotDelivered().toString());
                        errorAlert.setHeaderText("Mail partially sent");
                        errorAlert.setContentText("Some address haven't been found: " + rep.getNotDelivered().toString());
                        errorAlert.showAndWait();
                        sceneChanger(send);
                        break;

                    case -3:
                        System.out.println("Sending failed");
                        errorAlert.setHeaderText("Sending failed");
                        errorAlert.setContentText("Ops, an error occurred.");
                        errorAlert.showAndWait();
                        break;

                    case 1:
                        System.out.println("Sending complete");
                        sceneChanger(send);
                        break;

                    default:
                        System.out.println("Error");
                }
            }else{
                System.out.println("Wrong type");
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sceneChanger(Button bt1){
        System.out.println("test commit");
        Stage stage;
        Parent root = null;

        if (model.size() > 0) { //al posti di true controllo sul getSize() dell'arraylist di mail per controllare se la casella è vuota
            stage = (Stage) bt1.getScene().getWindow();
            try {
                root = FXMLLoader.load(getClass().getResource("mail.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                root = FXMLLoader.load(getClass().getResource("mail2.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

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
}