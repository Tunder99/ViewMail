package it.unito.progtre.mailview;
import it.adz.prog3.mail.comunicationobjects.Email;

import java.util.ArrayList;

public class Model {
    private ArrayList<Email> emails;
    private String password;
    private String mail;

    public Model(){
        emails = new ArrayList<>();
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }

    public int size(){
        return emails.size();
    }

    public void delete(int index){
        emails.remove(index);
    }

    public void setMail(String mail){
        this.mail = mail;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "emails=" + emails +
                '}';
    }
}
