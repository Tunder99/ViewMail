package it.unito.progtre.mailview;

import it.adz.prog3.mail.comunicationobjects.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ControllerView implements Initializable {

    @FXML
    private TextField TFemail;

    @FXML
    private PasswordField pwd;

    @FXML
    private Button bt1;

    private String mail, password;
    private Model model;

    public void handleButtonActionLogin(ActionEvent actionEvent) {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        mail = TFemail.getText();
        password = pwd.getText();
        model.setMail(mail);
        model.setPassword(password);

        try {
            socket = new Socket("poggivpn.ddns.net", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            if(isEmailValid(mail)) {
                Login login = new Login(mail, digest("SHA-256", password));
                out.writeObject(login);

                try {
                    Object obj = null;
                    obj = in.readObject();
                    if (obj instanceof ReplyLogin) {
                        ReplyLogin rep = (ReplyLogin) obj;
                        if(rep.getExitCode() != 1){
                            System.out.println("Login failed");
                            errorAlert.setHeaderText("Login failed");
                            errorAlert.setContentText("Wrong email or password, please try again");
                            errorAlert.showAndWait();
                        }else{
                            socket = new Socket("poggivpn.ddns.net", 8189);
                            out = new ObjectOutputStream(socket.getOutputStream());
                            in = new ObjectInputStream(socket.getInputStream());
                            model.setDate(new Date());
                            RequestDownloadEmail req = new RequestDownloadEmail(mail, digest("SHA-256", password));
                            out.writeObject(req);

                            try{
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
                                        model.setEmails(rep2.getEmails());
                                    }
                                }
                            }catch (ClassNotFoundException e){
                                System.out.println("Connection error");
                                errorAlert.setHeaderText("Wrong server reply");
                                errorAlert.setContentText("");
                                errorAlert.showAndWait();
                            }
                            sceneChanger(bt1);
                        }
                    }
                }catch (ClassNotFoundException e){
                    System.out.println("Connection error");
                    errorAlert.setHeaderText("Wrong server reply");
                    errorAlert.setContentText("");
                    errorAlert.showAndWait();
                }
            }else{
                System.out.println("Email not valid");
                errorAlert.setHeaderText("Email not valid");
                errorAlert.setContentText("Try again!");
                errorAlert.showAndWait();
            }
        }catch (IOException e){
            System.out.println("Connection error!");
            errorAlert.setHeaderText("Server offline");
            errorAlert.setContentText("");
            errorAlert.showAndWait();
            return;
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

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}