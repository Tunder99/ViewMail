package it.unito.progtre.mailview;
import it.adz.prog3.mail.comunicationobjects.Email;
import java.util.ArrayList;
import java.util.Date;

public class Model {
    private ArrayList<Email> emails;
    private String password;
    private String mail;
    private Date date;
    private Boolean timer;
    private GenericController controller;
    private Boolean stopped;

    public Model(){
        emails = new ArrayList<>();
        timer = false;
        stopped = false;
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

    public void setDate(Date date){
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public Boolean getTimer() {
        return timer;
    }

    public void setTimer(Boolean timer) {
        this.timer = timer;
    }

    public GenericController getController() {
        return controller;
    }

    public void setController(GenericController controller) {
        this.controller = controller;
    }

    public Boolean getStopped() {
        return stopped;
    }

    public void setStopped() {
        this.stopped = true;
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "emails=" + emails +
                '}';
    }
}
