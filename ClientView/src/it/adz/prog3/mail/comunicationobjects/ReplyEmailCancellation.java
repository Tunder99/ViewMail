package it.adz.prog3.mail.comunicationobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ReplyEmailCancellation implements Reply, Serializable {
    private int exitCode; //-1 invalid login //-2 some failed //-3 all failed //1- all deleted
    private ArrayList<UUID> deleted;

    public ReplyEmailCancellation(){
        this.exitCode = -1;
        deleted = null;
    }

    public ReplyEmailCancellation(int exitCode, ArrayList<UUID> deleted) {
        this.exitCode = exitCode;
        this.deleted = deleted;
    }

    public int getExitCode() {
        return exitCode;
    }

    public ArrayList<UUID> getDeleted() {
        return deleted;
    }
}
