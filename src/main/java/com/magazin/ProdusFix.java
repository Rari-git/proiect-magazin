package com.magazin;

public class ProdusFix extends Produs {
    // Constructor folosit pentru crearea manuală a produselor din meniu
    public ProdusFix(String nume, double pret, String descriere, String vanzatorEmail) {
        super(nume, pret, descriere, vanzatorEmail);
    }

    // Constructor gol obligatoriu pentru biblioteca Jackson
    public ProdusFix() {}
}