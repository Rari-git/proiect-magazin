package com.magazin;

public abstract class Utilizator {
    private String email;
    private String parola;

    public Utilizator(String email, String parola) {
        this.email = email;
        this.parola = parola;
    }

    public String getEmail() { return email; }
    public String getParola() { return parola; } // Adăugat getter-ul
}