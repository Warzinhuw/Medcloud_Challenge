package lib.Models;

public class Patient {

    private String name;
    private String birthDate;
    private String email;
    private String address;

    public Patient(String name, String birthDate, String email, String address) {
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
