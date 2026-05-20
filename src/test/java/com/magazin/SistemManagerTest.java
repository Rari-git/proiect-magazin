package com.magazin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SistemManagerTest {
    private SistemManager manager;

    @BeforeEach
    public void setUp() {
        manager = SistemManager.getInstanta();
        manager.reset();
    }

    @Test
    public void testRequirementB_AdminLogin() {
        Utilizator admin = manager.login("admin@email.com", "admin");
        assertNotNull(admin, "Administratorul trebuie să se poată loga cu datele default.");
        assertTrue(admin instanceof Administrator);
    }

    @Test
    public void testRequirementHI_SellerIdentityApprovalAndCancellation() {
        String email = "vanzator_nou@test.com";
        String pass = "parola123";
        Vanzator v = new Vanzator(email, pass);

        // h) Identificare prin email și parolă
        manager.inregistrare(v);

        // i) Managerul poate anula/nu aproba logarea (Vânzătorul nu se poate loga
        // inițial)
        assertNull(manager.login(email, pass), "Vânzătorul neaprobat nu ar trebui să se poată loga.");

        // b) Adminul aprobă vânzătorul
        manager.setStatusVanzator(email, true);
        assertNotNull(manager.login(email, pass), "Vânzătorul aprobat trebuie să se poată loga.");

        // i) Managerul anulează contul (Rămâne în DB, dar fără login)
        manager.setStatusVanzator(email, false);
        assertNull(manager.login(email, pass), "Vânzătorul dezactivat nu ar trebui să se poată loga.");
        assertTrue(manager.getUtilizatori().stream().anyMatch(u -> u.getEmail().equals(email)),
                "Utilizatorul trebuie să existe în continuare în baza de date.");
    }

    @Test
    public void testRequirementF_BuyerRegistration() {
        String email = "cumparator_nou@test.com";
        // f) Creare cont cumpărător (nu necesită aprobare)
        manager.inregistrare(new Cumparator(email, "parola"));
        assertNotNull(manager.login(email, "parola"), "Cumpărătorul ar trebui să se poată loga imediat.");
    }

    @Test
    public void testRequirementCD_OfferValidation() {
        String seller = "seller_ofertare@test.com";
        manager.inregistrare(new Vanzator(seller, "pass"));
        manager.setStatusVanzator(seller, true);

        // c) Definiție produs negociabil
        ProdusNegociabil p = new ProdusNegociabil("Monitor", 1200, "4K Ultra", seller, 1000);
        manager.adaugaProdus(p);

        // d) Ofertă sub prețul minim - refuzată automat
        boolean refuzata = manager.proceseazaOferta(p.getId(), "client@test.com", 900);
        assertFalse(refuzata, "Oferta sub prețul minim trebuie refuzată.");

        // d) Ofertă acceptabilă - intră în sistem
        boolean acceptata = manager.proceseazaOferta(p.getId(), "client@test.com", 1100);
        assertTrue(acceptata, "Oferta validă trebuie să intre în sistem.");

        List<Oferta> oferte = manager.getOfertePentruVanzator(seller);
        assertEquals(1, oferte.size(), "Ar trebui să existe o ofertă înregistrată.");
    }

    @Test
    public void testRequirementG_SaleAndCleanup() {
        String seller = "seller_cleanup@test.com";
        String buyer = "buyer@test.com";
        manager.inregistrare(new Vanzator(seller, "pass"));
        manager.setStatusVanzator(seller, true);

        ProdusFix p = new ProdusFix("Mouse Gaming", 250, "RGB", seller);
        manager.adaugaProdus(p);
        int id = p.getId();

        // g) Când un produs este cumpărat
        manager.cumparaProdusFix(id, buyer);

        // g) Introdus în istoric și șters din sistem (produs + oferte)
        assertFalse(manager.getProduse().stream().anyMatch(prod -> prod.getId() == id), "Produsul trebuie șters.");
        assertTrue(manager.getIstoricVanzari().stream().anyMatch(h -> h.contains("Mouse Gaming") && h.contains(buyer)),
                "Vânzarea trebuie să apară în istoric.");
    }

    @Test
    public void testRequirementE_SellerNegotiationManagement() {
        String seller = "seller_neg@test.com";
        manager.inregistrare(new Vanzator(seller, "pass"));
        manager.setStatusVanzator(seller, true);

        ProdusNegociabil p = new ProdusNegociabil("Cameră", 1500, "Mirrorless", seller, 1300);
        manager.adaugaProdus(p);
        manager.proceseazaOferta(p.getId(), "client@test.com", 1400);

        Oferta o = manager.getOfertePentruVanzator(seller).get(0);

        // e) Vânzătorul poate anula (respinge) o ofertă
        manager.respingeOferta(o);
        assertEquals(0, manager.getOfertePentruVanzator(seller).size(), "Oferta trebuie respinsă.");
        assertTrue(manager.getProduse().contains(p), "Produsul trebuie să rămână dacă oferta e doar respinsă.");
    }
}