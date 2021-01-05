package sample;

import ComunicationObjects.Email;
import java.util.ArrayList;

public class DataModel {
    private ArrayList<Email> emails;

    public DataModel(){
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
