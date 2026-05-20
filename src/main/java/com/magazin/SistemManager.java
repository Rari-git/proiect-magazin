package com.magazin;

import java.util.ArrayList;
import java.util.List;

public class SistemManager {
    private static SistemManager instanta;
    private List<Utilizator> utilizatori;
    private List<Produs> produse;
    private List<String> istoricVanzari;
    private List<Oferta> oferteActive;

    private SistemManager() {
        this.utilizatori = new ArrayList<>();
        this.produse = new ArrayList<>();
        this.istoricVanzari = new ArrayList<>();
        this.oferteActive = new ArrayList<>();
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
                        System.out.println("Contul vanzatorului este inactiv sau neaprobat!");
                        return null;
                    }
                }
                return u;
            }
        }
        return null;
    }

    public void inregistrare(Utilizator u) {
        this.utilizatori.add(u);
        if (u instanceof Cumparator) {
            System.out.println("Cont cumparator creat cu succes.");
        } else {
            System.out.println("Cerere trimisa. Contul de vanzator asteapta aprobarea adminului.");
        }
    }

    public List<Vanzator> getVanzatoriNeaprobati() {
        List<Vanzator> lista = new ArrayList<>();
        for (Utilizator u : utilizatori) {
            if (u instanceof Vanzator && !((Vanzator) u).isContAprobat()) lista.add((Vanzator) u);
        }
        return lista;
    }

    public void setStatusVanzator(String email, boolean status) {
        for (Utilizator u : utilizatori) {
            if (u instanceof Vanzator && u.getEmail().equals(email)) {
                ((Vanzator) u).setContAprobat(status);
            }
        }
    }

    public boolean proceseazaOferta(int idProdus, String emailCumparator, double pretPropus) {
        for (Produs p : produse) {
            if (p.getId() == idProdus && p instanceof ProdusNegociabil) {
                ProdusNegociabil pn = (ProdusNegociabil) p;
                if (pretPropus >= pn.getPretMinim()) {
                    oferteActive.add(new Oferta(idProdus, emailCumparator, pretPropus));
                    System.out.println("Oferta a fost trimisa vanzatorului.");
                    return true;
                } else {
                    System.out.println("Oferta refuzata automat (sub pretul minim setat de vanzator).");
                    return false;
                }
            }
        }
        return false;
    }
    
    public void aprobaOferta(Oferta o) {
        Produs produsul = null;
        for(Produs p : produse) {
            if(p.getId() == o.getIdProdus()) {
                produsul = p;
                break;
            }
        }
        if(produsul != null) {
            finalizeazaVanzare(produsul, o.getEmailCumparator(), o.getPretPropus());
            oferteActive.removeIf(of -> of.getIdProdus() == o.getIdProdus());
        }
    }

    public void cumparaProdusFix(int idProdus, String emailCumparator) {
        Produs gasit = null;
        for (Produs p : produse) {
            if (p.getId() == idProdus && p instanceof ProdusFix) {
                gasit = p;
                break;
            }
        }
        if (gasit != null) {
            finalizeazaVanzare(gasit, emailCumparator, gasit.getPret());
        } else {
            System.out.println("Produsul nu a fost gasit sau nu este cu pret fix.");
        }
    }

    public void anuleazaVanzare(int idProdus, String emailVanzator) {
        produse.removeIf(p -> p.getId() == idProdus && p.getVanzatorEmail().equals(emailVanzator));
        oferteActive.removeIf(o -> o.getIdProdus() == idProdus);
        System.out.println("Vanzarea a fost anulata.");
    }

    private void finalizeazaVanzare(Produs p, String cumparator, double pret) {
        String record = "Produs: " + p.getNume() + " | Cumparator: " + cumparator + " | Pret: " + pret;
        istoricVanzari.add(record); // Cerinta g
        oferteActive.removeIf(o -> o.getIdProdus() == p.getId()); // Sterge ofertele (Cerinta g)
        produse.remove(p); // Șterge din sistem (cerința g)
        System.out.println("Tranzactie finalizata!");
    }

    public void adaugaProdus(Produs p) { if (p != null) this.produse.add(p); }
    public void setProduse(List<Produs> produse) { if (produse != null) this.produse = produse; }
    public List<Produs> getProduse() { return produse; }

    public List<Oferta> getOferteActive() { return oferteActive; }
    public void setOferteActive(List<Oferta> oferte) { if (oferte != null) this.oferteActive = oferte; }
    public List<String> getIstoricVanzari() { return istoricVanzari; }
    public void setIstoricVanzari(List<String> istoric) { if (istoric != null) this.istoricVanzari = istoric; }

    public List<Utilizator> getUtilizatori() { return utilizatori; }
    public void setUtilizatori(List<Utilizator> utilizatori) {
        if (utilizatori != null && !utilizatori.isEmpty()) {
            this.utilizatori = utilizatori;
            // Ne asigurăm că administratorul default există mereu dacă lista e nouă
            if (utilizatori.stream().noneMatch(u -> u.getEmail().equals("admin@email.com"))) {
                utilizatori.add(new Administrator("admin@email.com", "admin"));
            }
        }
    }

    public List<Oferta> getOfertePentruVanzator(String emailVanzator) {
        List<Oferta> filtrate = new ArrayList<>();
        for (Oferta o : oferteActive) {
            for (Produs p : produse) {
                if (p.getId() == o.getIdProdus() && p.getVanzatorEmail().equals(emailVanzator)) {
                    filtrate.add(o);
                }
            }
        }
        return filtrate;
    }
}