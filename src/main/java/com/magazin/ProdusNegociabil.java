package com.magazin;

public class ProdusNegociabil extends Produs {
    private double pretMinim;

    public ProdusNegociabil(String nume, double pret, String vanzatorEmail, double pretMinim) {
        super(nume, pret, vanzatorEmail);
        this.pretMinim = pretMinim;
    }

    public double getPretMinim() { return pretMinim; }
}