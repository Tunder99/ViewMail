package ComunicationObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class RequestEmailCancellation extends Login implements Serializable {
    ArrayList<UUID> idMail;

    public RequestEmailCancellation(String userMail, String encryptedPassword, ArrayList<UUID> idMail){
        super(userMail, encryptedPassword);
        this.idMail = idMail;
    }

    public ArrayList<UUID> getIdMail() {
        return idMail;
    }
}
