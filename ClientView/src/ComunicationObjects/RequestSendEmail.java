package ComunicationObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestSendEmail extends Login implements Serializable {
    private ArrayList<String> to;
    private String subject;
    private String text;

    public RequestSendEmail(Login l, ArrayList<String> to, String subject, String text){
        super(l.getUserMail(), l.getEncryptedPassword());
        //this.to = to;
        this.subject = subject;
        this.text = text;
        this.to = to;
    }

    public ArrayList<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String toString(){
        return getUserMail()+" "+getEncryptedPassword()+" "+subject;
    }
}
