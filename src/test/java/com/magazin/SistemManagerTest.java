package com.magazin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SistemManagerTest {
    @Test
    public void testLoginAdmin() {
        SistemManager manager = SistemManager.getInstanta();
        Utilizator admin = manager.login("admin@email.com", "admin");
        assertNotNull(admin, "Login-ul adminului ar trebui să reușească");
        assertTrue(admin instanceof Administrator);
    }

    @Test
    public void testOfertaRefuzata() {
        SistemManager manager = SistemManager.getInstanta();
        ProdusNegociabil p = new ProdusNegociabil("Laptop", 1000, "Descriere", "v@email.com", 800);
        manager.adaugaProdus(p);

        boolean rezultat = manager.proceseazaOferta(p.getId(), "c@email.com", 700);
        assertFalse(rezultat, "Oferta sub pretul minim trebuie refuzată");
    }
}