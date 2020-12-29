package ComunicationObjects;

import java.io.Serializable;
import java.util.Date;

public class RequestDownloadEmail extends Login implements Serializable {
    Date sinceDate;

    public RequestDownloadEmail(String userMail, String encryptedPassword, Date sinceDate){
        super(userMail, encryptedPassword);
        this.sinceDate = sinceDate;
    }

    public RequestDownloadEmail(String userMail, String encryptedPassword){
        super(userMail, encryptedPassword);
        this.sinceDate = null;
    }

    public Date getSinceDate(){
        return sinceDate;
    }

}
