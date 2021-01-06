package it.adz.prog3.mail.comunicationobjects;
import java.io.Serializable;

public class Login implements Serializable {
    private String userMail;
    private String encryptedPassword;

    public Login(String userMail, String encryptedPassword) {
        this.userMail = userMail;
        this.encryptedPassword = encryptedPassword;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}
