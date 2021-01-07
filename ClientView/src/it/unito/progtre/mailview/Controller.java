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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML
    private TextField TFemail;

    @FXML
    private PasswordField pwd;

    @FXML
    private Button bt1;

    @FXML
    private Label mailText;

    @FXML
    private Button update;

    @FXML
    private Button delete;

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

    @FXML
    private Button reply, replyAll;

    private Date lastUpdateDate;
    private Email email;
    private String mail, password;
    private Model model;

    public void handleButtonActionLogin(ActionEvent actionEvent) {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        mail = TFemail.getText();       
        password = pwd.getText();

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
                            RequestDownloadEmail req = new RequestDownloadEmail(mail, digest("SHA-256", password));
                            lastUpdateDate = new Date();    //current date
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
                req = new RequestSendEmail(new Login(mail, digest("SHA-256", password)), to,
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

    public void handleButtonActionNewMail(ActionEvent actionEvent) {
        Stage stage;
        Parent root;

        try {
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

    public void sceneChanger(Button bt1){
        Stage stage;
        Parent root = null;
        if (model.size() > 0) { //al posti di true controllo sulla lunghezza dell'arraylist di mail per controllare se la casella è vuota
            stage = (Stage) bt1.getScene().getWindow();
            try {
                root = FXMLLoader.load(getClass().getResource("Mail.fxml"));
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
                root = FXMLLoader.load(getClass().getResource("Mail2.fxml"));
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
    public void initialize(URL location, ResourceBundle resources) {
        /*if (model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        model = new Model();*/

        if(location.toExternalForm().contains("Mail.fxml")){
            for (Email e : model.getEmails()) {
                System.out.println(e.getSubject());
                mails.getItems().add(e.getSubject());
            }
        }
    }
}