package it.adz.prog3.mail.comunicationobjects;

import java.io.Serializable;

public class ReplyLogin implements Reply, Serializable {
    private int exitCode;
    private String name;

    public ReplyLogin(String name) {
        this.exitCode = 1; // logged
        this.name = name;
    }

    public ReplyLogin(){
        this.exitCode = -1; // NOT logged
        this.name = null;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getName(){
        return name;
    }
}
