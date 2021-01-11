package it.unito.progtre.mailview;

import it.adz.prog3.mail.comunicationobjects.Login;
import it.adz.prog3.mail.comunicationobjects.ReplySendEmail;
import it.adz.prog3.mail.comunicationobjects.RequestSendEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ControllerNewMail implements Initializable {

    @FXML
    private TextField toMails;

    @FXML
    private TextField subjectText;

    @FXML
    private TextArea text;

    @FXML
    private Button send;

    private Model model;

    public void setToMails(String toMails){
        this.toMails.setText(toMails);
    }

    public void setSubjectText(String subjectText){
        this.subjectText.setText(subjectText);
    }

    public void setMailText(String text){ this.text.setText(text); }

    public void handleButtonActionSend(ActionEvent actionEvent) {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        RequestSendEmail req = null;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        ArrayList<String> to = new ArrayList<>(Arrays.asList(toMails.getText().split(",")));
        for(int i = 0; i < to.size(); i++){
            String str = to.get(i);
            to.remove(i);
            to.add(i, str.trim());
        }
        for (int i = 0; i < to.size(); i++)
            to.get(i).trim();

        try {
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            if(to.size() > 0) {
                req = new RequestSendEmail(new Login(model.getMail(), digest("SHA-256", model.getPassword())), to,
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

    public void sceneChanger(Button bt1){
        Stage stage;
        Parent root = null;
        if (model.size() > 0) {
            stage = (Stage) bt1.getScene().getWindow();
            try {
                //root = FXMLLoader.load(getClass().getResource("Mail.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Mail.fxml"));
                root = loader.load();
                ControllerMail controller = loader.getController();
                controller.setModel(model);
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
                //root = FXMLLoader.load(getClass().getResource("Mail2.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Mail2.fxml"));
                root = loader.load();
                ControllerMail controller = loader.getController();
                controller.setModel(model);
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

    public void setModel(Model m){
        this.model = m;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
