package com.akapps.puzka;

public class MainProfile {
    String name, password, email;
    int isEnabled;

    public MainProfile() {}

    public MainProfile(String name, String password, String email, int isEnabled) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.isEnabled = isEnabled;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getIsEnabled() {
        return isEnabled;
    }
}
