package com.magazin;

import java.util.ArrayList;
import java.util.List;

public class SistemManager {
    private static SistemManager instanta;
    private List<Utilizator> utilizatori;
    private List<Produs> produse;

    private SistemManager() {
        this.utilizatori = new ArrayList<>();
        this.produse = new ArrayList<>();
        // Administratorul default
        utilizatori.add(new Administrator("admin@email.com", "admin"));
    }

    public static SistemManager getInstanta() {
        if (instanta == null)
            instanta = new SistemManager();
        return instanta;
    }

    public Utilizator login(String email, String parola) {
        for (Utilizator u : utilizatori) {
            if (u.getEmail().equals(email) && u.getParola().equals(parola)) {
                // Verificare specială pentru vânzători (cerința i)
                if (u instanceof Vanzator) {
                    if (!((Vanzator) u).isContAprobat()) {
                        System.out.println("Contul vânzătorului nu este aprobat!");
                        return null;
                    }
                }
                return u;
            }
        }
        return null;
    }

    // În SistemManager.java

    public boolean proceseazaOferta(int idProdus, String emailCumparator, double pretPropus) {
        for (Produs p : produse) {
            if (p.getId() == idProdus && p instanceof ProdusNegociabil) {
                ProdusNegociabil pn = (ProdusNegociabil) p;

                // Logica: Dacă oferta e peste prețul minim, e acceptată (cerința d)
                if (pretPropus >= pn.getPretMinim()) {
                    System.out.println("Oferta acceptata!");
                    // Aici ar trebui să apelezi logica de cumpărare (cerința g)
                    finalizeazaVanzare(pn, emailCumparator, pretPropus);
                    return true;
                } else {
                    System.out.println("Oferta refuzata automat (sub pretul minim).");
                    return false;
                }
            }
        }
        return false;
    }

    private void finalizeazaVanzare(Produs p, String cumparator, double pret) {
        System.out.println("Produs vandut lui " + cumparator + " cu pretul " + pret);
        produse.remove(p); // Șterge din sistem (cerința g)
    }

    // Metodă pentru a adăuga un produs (cerința e)
    public void adaugaProdus(Produs p) {
        this.produse.add(p);
    }
}