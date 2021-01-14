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
import java.util.*;

public class ControllerMail extends GenericController implements Initializable{

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
    private Label date;

    @FXML
    private Button update;

    @FXML
    private Button reply;

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
            model.setController(controller);
            Scene scene = new Scene(root, 1185, 750);

            URL url = this.getClass().getResource("NewMail.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("New Mail");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleButtonActionCombobox(ActionEvent actionEvent) {
        if(actionEvent.getSource() == mails && model.size() > 0){
            if(mails.getSelectionModel().getSelectedIndex() >= 0) {
                mailText.setText(mails.getSelectionModel().getSelectedItem().getText());
                mailSubject.setText(mails.getSelectionModel().getSelectedItem().getSubject());
                fromMail.setText(mails.getSelectionModel().getSelectedItem().getFrom());
                toMail.setText(("" + mails.getSelectionModel().getSelectedItem().getTo()).replace("[", "").replace("]", ""));
                date.setText("" + mails.getSelectionModel().getSelectedItem().getDate());
            }else{
                mailText.setText(mails.getItems().get(model.size()-1).getText());
                mailSubject.setText(mails.getItems().get(model.size()-1).getSubject());
                fromMail.setText(mails.getItems().get(model.size()-1).getFrom());
                toMail.setText(("" + mails.getItems().get(model.size()-1).getTo()).replace("[", "").replace("]", ""));
                date.setText("" + mails.getItems().get(model.size() - 1).getDate());
                mails.getSelectionModel().selectLast();
            }
        }
    }

    public void handleButtonReplyEmail(ActionEvent actionEvent) {
        if(mails.getSelectionModel().getSelectedItem() == null) return;

        Stage stage = (Stage) update.getScene().getWindow();
        Parent root = null;
        try {
            if (actionEvent.getSource() == reply) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMail.fxml"));
                root = loader.load();
                ControllerNewMail controller = loader.getController();
                controller.setModel(model);
                model.setController(controller);
                Scene scene = new Scene(root, 1185, 750);

                URL url = this.getClass().getResource("NewMail.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("New Mail");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
                
                controller.setToMails(mails.getSelectionModel().getSelectedItem().getFrom());
                controller.setSubjectText("Replying to " + mails.getSelectionModel().getSelectedItem().getSubject());
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMail.fxml"));
                root = loader.load();
                ControllerNewMail controller = loader.getController();
                controller.setModel(model);
                model.setController(controller);
                Scene scene = new Scene(root, 1185, 750);

                URL url = this.getClass().getResource("NewMail.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("New Mail");
                stage.setResizable(false);
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

    public void handleButtonActionUpdate(ActionEvent actionEvent) {
        if(mails != null) mails.getItems().clear();
        updateEmail(true);
        sceneChanger(update);
    }

    public void handleButtonActionDeleteMail(ActionEvent actionEvent) {
        if(mails.getSelectionModel().getSelectedItem() == null) return;

        delete.setDisable(true);
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        try{
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            ArrayList<UUID> remove = new ArrayList<>();
            remove.add(UUID.fromString(mails.getSelectionModel().getSelectedItem().getUuid()));
            RequestEmailCancellation req = new RequestEmailCancellation(model.getMail(), digest("SHA-256", model.getPassword()), remove);
            out.writeObject(req);

            Object obj2 = null;
            obj2 = in.readObject();
            if(obj2 instanceof ReplyEmailCancellation){
                ReplyEmailCancellation rep2 = (ReplyEmailCancellation) obj2;
                if(rep2.getExitCode() == -1){
                    System.out.println("Cancellation failed");
                    errorAlert.setHeaderText("Cancellation failed, invalid login");
                    errorAlert.setContentText("");
                    errorAlert.showAndWait();
                }else if(rep2.getExitCode() == -2){
                    System.out.println("Emails cancellation partially failed");
                    errorAlert.setHeaderText("Cancellation partially failed");
                    errorAlert.setContentText("");
                    errorAlert.showAndWait();
                }else if(rep2.getExitCode() == -3){
                    System.out.println("Impossible to cancel emails");
                    errorAlert.setHeaderText("Impossible to cancel emails");
                    errorAlert.setContentText("");
                    errorAlert.showAndWait();
                }else{
                    for(int i = 0; i < rep2.getDeleted().size(); i++){
                        for(int j = 0; j < model.size(); j++){
                            if(model.getEmails().get(j).getUuid().equals("" + rep2.getDeleted().get(i))){
                                model.delete(j);
                            }
                        }
                    }
                    mails.getItems().remove(mails.getSelectionModel().getSelectedIndex());
                }
            }
        }catch (ClassNotFoundException | IOException e){
            System.out.println("Connection error");
            errorAlert.setHeaderText("Wrong server reply");
            errorAlert.setContentText("");
            errorAlert.showAndWait();
        }

        if(model.size() == 0) {
            mailText.setText("");
            mailSubject.setText("");
            fromMail.setText("");
            toMail.setText("");
        }

        if(model.size() == 0) sceneChanger(delete);
        delete.setDisable(false);
    }

    public void handleButtonForwardEmail(ActionEvent actionEvent){
        if(mails.getSelectionModel().getSelectedItem() == null) return;

        Stage stage = (Stage) update.getScene().getWindow();
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMail.fxml"));
            root = loader.load();
            ControllerNewMail controller = loader.getController();
            controller.setModel(model);
            model.setController(controller);
            Scene scene = new Scene(root, 1185, 750);

            URL url = this.getClass().getResource("NewMail.css");
            if (url == null) {
                System.out.println("Resource not found. Aborting.");
                System.exit(-1);
            }
            String css = url.toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("New Mail");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            controller.setSubjectText("Forwarding " + mails.getSelectionModel().getSelectedItem().getSubject());
            controller.setMailText(mails.getSelectionModel().getSelectedItem().getText());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updateEmail(Boolean flag){
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        try{
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            RequestDownloadEmail req = new RequestDownloadEmail(model.getMail(), digest("SHA-256", model.getPassword()), model.getDate());
            model.setDate(new Date());
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
                    if(rep2 != null && rep2.getEmails() != null && rep2.getEmails().size() > 0) {
                        Alert popup = new Alert(Alert.AlertType.INFORMATION);
                        int c = 0;
                        a: for(int i = 0; i < rep2.getEmails().size(); i++){
                            for(int j = 0; j < model.size(); j++){
                                if(rep2.getEmails().get(i).getUuid().equals(model.getEmails().get(j).getUuid())){
                                    continue a;
                                }
                            }
                            c++;
                            model.getEmails().add(rep2.getEmails().get(i));
                            if(!flag) {
                                if(mails != null)
                                    mails.getItems().add(model.getEmails().get(model.size()-1));
                                else sceneChanger(update);
                            }
                        }
                        Collections.sort(model.getEmails());

                        if(c > 0) {
                            popup.setHeaderText("New mail");
                            popup.setContentText("You've received " + c + " mail(s)!");
                            popup.show();
                            try {
                                Thread.sleep(1000);
                                popup.hide();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }catch (ClassNotFoundException | IOException e){
            if(flag) {
                System.out.println("Connection error");
                errorAlert.setHeaderText("Wrong server reply");
                errorAlert.setContentText("Server might be offline.");
                errorAlert.showAndWait();
            }
        }
    }

    public void sceneChanger(Button bt1){
        Stage stage;
        Parent root = null;
        if (model.size() > 0) {
            stage = (Stage) bt1.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Mail.fxml"));
                root = loader.load();
                Scene scene = new Scene(root, 1185, 750);
                ControllerMail controller = loader.getController();
                controller.setModel(model);
                model.setController(controller);

                URL url = this.getClass().getResource("Mail.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("Mail box");
                stage.setResizable(false);
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
                Scene scene = new Scene(root, 1185, 750);
                ControllerMail controller = loader.getController();
                controller.setModel(model);
                model.setController(controller);

                URL url = this.getClass().getResource("Mail2.css");
                if (url == null) {
                    System.out.println("Resource not found. Aborting.");
                    System.exit(-1);
                }
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
                stage.setTitle("Mail box");
                stage.setResizable(false);
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

    public void setModel(Model m){
        this.model = m;
        for (Email e : model.getEmails()) {
            mails.getItems().add(e);
        }

        if(!model.getTimer()) {
            TimerUpdate timer = new TimerUpdate(model);
            new Timer().scheduleAtFixedRate((timer), 5000, 5000);
            model.setTimer(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}