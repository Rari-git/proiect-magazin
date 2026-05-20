package com.magazin;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SistemManager manager = SistemManager.getInstanta();
        
        try {
            List<Produs> produseIncarcate = DataService.incarcaProduse();
            if (produseIncarcate != null) manager.setProduse(produseIncarcate);
            
            List<Utilizator> uInc = DataService.incarcaUtilizatori();
            if (uInc != null) manager.setUtilizatori(uInc);

            List<Oferta> oInc = DataService.incarcaOferte();
            if (oInc != null) manager.setOferteActive(oInc);

            List<String> iInc = DataService.incarcaIstoric();
            if (iInc != null) manager.setIstoricVanzari(iInc);

        } catch (IOException e) {
            System.out.println("Eroare la incarcarea datelor: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Magazin Online ---");
            System.out.println("1. Login");
            System.out.println("2. Inregistrare Cumparator");
            System.out.println("3. Inregistrare Vanzator");
            System.out.println("0. Iesire");
            
            int opt = scanner.nextInt(); scanner.nextLine();
            switch (opt) {
                case 1:
                    System.out.print("Email: "); String e = scanner.nextLine();
                    System.out.print("Parola: "); String p = scanner.nextLine();
                    Utilizator u = manager.login(e, p);
                    if (u != null) meniuUtilizator(u, scanner, manager);
                    else System.out.println("Eroare login.");
                    break;
                case 2:
                    System.out.print("Email: "); String ec = scanner.nextLine();
                    System.out.print("Parola: "); String pc = scanner.nextLine();
                    manager.inregistrare(new Cumparator(ec, pc));
                    break;
                case 3:
                    System.out.print("Email: "); String ev = scanner.nextLine();
                    System.out.print("Parola: "); String pv = scanner.nextLine();
                    manager.inregistrare(new Vanzator(ev, pv));
                    break;
                case 0: 
                    running = false;
                    try {
                        DataService.salveazaProduse(manager.getProduse());
                        DataService.salveazaUtilizatori(manager.getUtilizatori());
                        DataService.salveazaOferte(manager.getOferteActive());
                        DataService.salveazaIstoric(manager.getIstoricVanzari());
                        System.out.println("Date salvate cu succes!");
                    } catch (IOException ex) {
                        System.out.println("Eroare la salvare: " + ex.getMessage());
                    }
                    break;
            }
        }
    }

    private static void meniuUtilizator(Utilizator u, Scanner sc, SistemManager sm) {
        System.out.println("Sunteti logat ca: " + u.getEmail());
        if (u instanceof Administrator) {
            System.out.println("1. Aproba vanzator, 2. Dezactiveaza vanzator (Anulare cont)");
            int opt = sc.nextInt(); sc.nextLine();
            if (opt == 1) {
                sm.getVanzatoriNeaprobati().forEach(v -> System.out.println(v.getEmail()));
                System.out.print("Email de aprobat: ");
                sm.setStatusVanzator(sc.nextLine(), true);
            } else if (opt == 2) {
                System.out.print("Email vanzator de dezactivat: ");
                sm.setStatusVanzator(sc.nextLine(), false);
            }
        } else if (u instanceof Cumparator) {
            System.out.println("1. Vizualizeaza produse, 2. Cumpara pret fix, 3. Fa oferta negociabil");
            int opt = sc.nextInt(); sc.nextLine();
            if (opt == 1) {
                System.out.println("\n--- Lista Produse ---");
                sm.getProduse().forEach(p -> System.out.println("Produs: " + p.getNume() + " | Pret: " + p.getPret() + " EUR | Vanzator: " + p.getVanzatorEmail() + "\n   Descriere: " + p.getDescriere() + "\n   [Selectati folosind ID: " + p.getId() + "]"));
            } else if (opt == 2) {
                System.out.print("ID Produs: "); int id = sc.nextInt();
                sm.cumparaProdusFix(id, u.getEmail());
            } else if (opt == 3) {
                System.out.print("ID Produs: "); int id = sc.nextInt();
                System.out.print("Pret propus: "); double pret = sc.nextDouble();
                sm.proceseazaOferta(id, u.getEmail(), pret);
            }
        } else if (u instanceof Vanzator) {
            System.out.println("1. Pune produs (Fix), 2. Pune produs (Negociabil), 3. Gestionare oferte, 4. Anuleaza vanzare");
            int opt = sc.nextInt(); sc.nextLine();
            switch (opt) {
                case 1:
                    System.out.print("Nume: "); String n1 = sc.nextLine();
                    System.out.print("Pret: "); double p1 = sc.nextDouble(); sc.nextLine();
                    System.out.print("Descriere: "); String d1 = sc.nextLine();
                    sm.adaugaProdus(new ProdusFix(n1, p1, d1, u.getEmail()));
                    break;
                case 2:
                    System.out.print("Nume: "); String n2 = sc.nextLine();
                    System.out.print("Pret afisat: "); double p2 = sc.nextDouble();
                    System.out.print("Pret minim (secret): "); double pm = sc.nextDouble(); sc.nextLine();
                    System.out.print("Descriere: "); String d2 = sc.nextLine();
                    sm.adaugaProdus(new ProdusNegociabil(n2, p2, d2, u.getEmail(), pm));
                    break;
                case 3:
                    List<Oferta> oferte = sm.getOfertePentruVanzator(u.getEmail());
                    for (int i = 0; i < oferte.size(); i++) System.out.println(i + ". " + oferte.get(i));
                    System.out.print("Selectati index oferta (sau -1): ");
                    int idx = sc.nextInt();
                    if (idx >= 0 && idx < oferte.size()) {
                        System.out.println("1. Aproba, 2. Respinge/Anula");
                        int decizie = sc.nextInt();
                        if (decizie == 1) 
                            sm.aprobaOferta(oferte.get(idx));
                        else if (decizie == 2)
                            sm.respingeOferta(oferte.get(idx));
                    }
                    break;
                case 4:
                    System.out.print("ID Produs de anulat: ");
                    sm.anuleazaVanzare(sc.nextInt(), u.getEmail());
                    break;
            }
        }
    }
}