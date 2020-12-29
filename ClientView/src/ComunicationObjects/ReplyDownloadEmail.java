package ComunicationObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class ReplyDownloadEmail implements Reply, Serializable {
    private int exitCode; //-1 invalid login //1 ok //-3 Error
    private ArrayList<Email> emails;

    public ReplyDownloadEmail(int exitCode, ArrayList<Email> emails) {
        this.exitCode = exitCode;
        this.emails = emails;
    }

    public int getExitCode() {
        return exitCode;
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }
}
