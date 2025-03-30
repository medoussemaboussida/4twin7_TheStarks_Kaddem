package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.spring.kaddem.entities.Departement;

import static org.junit.jupiter.api.Assertions.*;

 class DepartementJUnitTests {

    private Departement departement;

    @BeforeEach
     void setUp() {
        departement = new Departement("Informatique");
        departement.setIdDepart(1); // Initialisation manuelle
    }

    @Test
     void testGetIdDepart() {
        assertNotNull(departement.getIdDepart(), "L'ID ne devrait pas être null après initialisation");
        assertEquals(1, departement.getIdDepart(), "L'ID devrait être 1");
    }

    @Test
     void testGetNomDepart() {
        assertEquals("Informatique", departement.getNomDepart(), "Le nom du département devrait être 'Informatique'");
    }

    @Test
     void testSetNomDepart() {
        departement.setNomDepart("Mathematiques");
        assertEquals("Mathematiques", departement.getNomDepart(), "Le nom du département devrait être 'Mathematiques' après modification");
    }
}