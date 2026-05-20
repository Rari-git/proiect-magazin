package com.magazin;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SistemManager manager = SistemManager.getInstanta();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Bine ai venit in Magazinul Online ---");
        System.out.println("1. Login");
        System.out.println("2. Iesire");

        int optiune = scanner.nextInt();
        scanner.nextLine(); // Consumă newline-ul

        if (optiune == 1) {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Parola: ");
            String parola = scanner.nextLine();

            Utilizator u = manager.login(email, parola);
            if (u != null) {
                if (u instanceof Administrator) {
                    System.out.println("Meniu Admin: 1. Aproba Vanzator, 2. Sterge Cont Vanzator");
                    // Aici adaugi logica specifică (ex: manager.aprobaVanzator(email))
                } else if (u instanceof Vanzator) {
                    System.out.println("Meniu Vanzator: 1. Pune produs la vanzare, 2. Anuleaza vanzare");
                } else if (u instanceof Cumparator) {
                    System.out.println("Meniu Cumparator: 1. Vizualizeaza produse, 2. Fa o oferta");
                }
            } else {
                System.out.println("Date invalide sau cont neaprobat.");
            }
        }
        scanner.close();
    }
}