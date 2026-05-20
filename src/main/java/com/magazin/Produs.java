package com.magazin;

public abstract class Produs {
    private static int idCounter = 0;
    private final int id;
    private String nume;
    private double pret;
    private String vanzatorEmail;

    public Produs(String nume, double pret, String vanzatorEmail) {
        this.id = ++idCounter;
        this.nume = nume;
        this.pret = pret;
        this.vanzatorEmail = vanzatorEmail;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public String getVanzatorEmail() { return vanzatorEmail; }
}