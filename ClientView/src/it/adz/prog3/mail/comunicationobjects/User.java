package it.adz.prog3.mail.comunicationobjects;

public class User {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String file;


    public User(String name, String surname, String email, String password, String file) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public boolean testPassword(String password) {
        return this.password.equals(password);
    }

    public String getFile() {
        return file;
    }
}
