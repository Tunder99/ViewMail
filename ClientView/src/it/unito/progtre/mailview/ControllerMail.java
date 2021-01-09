package it.unito.progtre.mailview;

import it.adz.prog3.mail.comunicationobjects.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

public class ControllerMail implements Initializable {

    @FXML
    private Button newMail;

    @FXML
    private ComboBox<Email> mails;

    @FXML
    private Label mailText;

    @FXML
    private Label mailSubject;

    @FXML
    private Label fromMail;

    @FXML
    private Label toMail;

    @FXML
    private Button update;

    @FXML
    private Button reply;

    @FXML
    private Button replyAll;

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
            stage.setTitle("New Mail");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleButtonActionMail(ActionEvent actionEvent) {
        if(actionEvent.getSource() == mails){
            if(model.size() > 0) {
                mailText.setText(mails.getSelectionModel().getSelectedItem().getText());
                mailSubject.setText(mails.getSelectionModel().getSelectedItem().getSubject());
                fromMail.setText(mails.getSelectionModel().getSelectedItem().getFrom());
                toMail.setText(("" + mails.getSelectionModel().getSelectedItem().getTo()).replace("[", "").replace("]", ""));
            }
        }else if(actionEvent.getSource() == update){
            mails.getItems().clear();
            ////////////////////////
            updateEmail();
            sceneChanger(update);
            //Check/////////////////
        }else if(actionEvent.getSource() == delete){
            model.delete(mails.getSelectionModel().getSelectedIndex());
            mails.getItems().remove(mails.getSelectionModel().getSelectedIndex());
            if(model.size() == 0) mailText.setText("");
        }
    }

    public void handleButtonReplyEmail(ActionEvent actionEvent) {
        Stage stage = (Stage) update.getScene().getWindow();
        Parent root = null;
        try {
            if (actionEvent.getSource() == reply && mails.getSelectionModel().getSelectedItem() != null) {
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
                stage.setTitle("New Mail");
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();
                
                controller.setToMails(mails.getSelectionModel().getSelectedItem().getFrom());
                controller.setSubjectText("Replying to " + mails.getSelectionModel().getSelectedItem().getSubject());
            } else {
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
                stage.setTitle("New Mail");
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();

                mails.getSelectionModel().getSelectedItem().getTo().removeIf(e -> e.trim().equals(model.getMail().trim()));
                controller.setToMails(mails.getSelectionModel().getSelectedItem().getFrom() + ","
                   + ("" + mails.getSelectionModel().getSelectedItem().getTo()).replace("[", "").replace("]", ""));
                controller.setSubjectText("Replying to " + mails.getSelectionModel().getSelectedItem().getSubject());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleButtonForwardEmail(ActionEvent actionEvent){
        Stage stage = (Stage) update.getScene().getWindow();
        Parent root = null;

        try {
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
            stage.setTitle("New Mail");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();

            controller.setSubjectText("Forwarding " + mails.getSelectionModel().getSelectedItem().getSubject());
            controller.setMailText(mails.getSelectionModel().getSelectedItem().getText());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setModel(Model m){
        this.model = m;
        for (Email e : model.getEmails()) {
            mails.getItems().add(e);
        }
    }

    public void updateEmail(){      //Check update///////////////////
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        try{
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            model.setDate(new Date());
            RequestDownloadEmail req = new RequestDownloadEmail(model.getMail(), digest("SHA-256", model.getPassword()), model.getDate());
            out.writeObject(req);

            Object obj2 = null;
            obj2 = in.readObject();
            if(obj2 instanceof ReplyDownloadEmail){
                ReplyDownloadEmail rep2 = (ReplyDownloadEmail) obj2;
                if(rep2.getExitCode() != 1){
                    System.out.println("Download failed");
                    errorAlert.setHeaderText("Download failed");
                    errorAlert.setContentText("");
                    errorAlert.showAndWait();
                }else{
                    model.getEmails().addAll(rep2.getEmails());
                    Collections.sort(model.getEmails());
                }
            }
        }catch (ClassNotFoundException | IOException e){
            System.out.println("Connection error");
            errorAlert.setHeaderText("Wrong server reply");
            errorAlert.setContentText("");
            errorAlert.showAndWait();
        }
    }

    public void sceneChanger(Button bt1){
        Stage stage;
        Parent root = null;
        if (model.size() > 0) { //al posti di true controllo sulla lunghezza dell'arraylist di mail per controllare se la casella è vuota
            stage = (Stage) bt1.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Mail.fxml"));
                root = loader.load();
                Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
                ControllerMail controller = loader.getController();
                controller.setModel(model);

                URL url = this.getClass().getResource("Mail.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("Mail box");
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stage = (Stage) bt1.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Mail2.fxml"));
                root = loader.load();
                Scene scene = new Scene(root, stage.getMaxWidth(), stage.getMaxHeight());
                ControllerMail controller = loader.getController();
                controller.setModel(model);

                URL url = this.getClass().getResource("Mail2.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("Mail box");
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //I seguenti due metodi criptano le pass (quando si genera un nuovo utente)
    public static String digest(String alg, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();
            return encodeHex(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static String encodeHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
