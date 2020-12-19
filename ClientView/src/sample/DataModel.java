package sample;

import java.util.ArrayList;

public class DataModel {
    private ArrayList<String> emails;

    public DataModel(){
        emails = new ArrayList<>();
        emails.add("Test1");
        emails.add("Test2");
        emails.add("Test3");
        emails.add("Test4");
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public int size(){
        return emails.size();
    }

    public void delete(int index){
        emails.remove(index);
    }

    public int last(){
        return emails.size() - 1;
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "emails=" + emails +
                '}';
    }
}
