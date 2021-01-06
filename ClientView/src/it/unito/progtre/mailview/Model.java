package it.unito.progtre.mailview;
import it.adz.prog3.mail.comunicationobjects.Email;

import java.util.ArrayList;

public class Model {
    private ArrayList<Email> emails;

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

    @Override
    public String toString() {
        return "DataModel{" +
                "emails=" + emails +
                '}';
    }
}
