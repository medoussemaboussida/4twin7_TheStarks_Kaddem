package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.services.DepartementServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications après chaque test
class DepartementServiceImplTest {

    @Autowired
    private DepartementServiceImpl departementService;

    @Autowired
    private DepartementRepository departementRepository;

    private Departement departement;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        departementRepository.deleteAll();

        // Initialisation d'un département
        departement = new Departement();
        departement.setNomDepart("Informatique");
    }

    @Test
    void testRetrieveAllDepartements() {
        // Ajouter des départements
        departementRepository.save(departement);
        Departement departement2 = new Departement();
        departement2.setNomDepart("Mathematiques");
        departementRepository.save(departement2);

        // Appel de la méthode
        List<Departement> result = departementService.retrieveAllDepartements();

        // Vérifications
        assertNotNull(result, "La liste des départements ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 départements");
        assertTrue(result.stream().anyMatch(d -> d.getNomDepart().equals("Informatique")),
                "La liste devrait contenir le département Informatique");
    }

    @Test
    void testAddDepartement() {
        // Appel de la méthode
        Departement result = departementService.addDepartement(departement);

        // Vérifications
        assertNotNull(result, "Le département ajouté ne devrait pas être null");
        assertNotNull(result.getIdDepart(), "L'ID du département devrait être généré");
        assertEquals("Informatique", result.getNomDepart(), "Le nom devrait être 'Informatique'");
        assertEquals(1, departementRepository.count(), "Il devrait y avoir 1 département dans la base");
    }

    @Test
    void testUpdateDepartement() {
        // Sauvegarder le département initial
        Departement savedDepartement = departementRepository.save(departement);

        // Modifier le département
        savedDepartement.setNomDepart("Physique");

        // Appel de la méthode
        Departement result = departementService.updateDepartement(savedDepartement);

        // Vérifications
        assertNotNull(result, "Le département mis à jour ne devrait pas être null");
        assertEquals("Physique", result.getNomDepart(), "Le nom devrait être 'Physique'");
        assertEquals(savedDepartement.getIdDepart(), result.getIdDepart(),
                "L'ID ne devrait pas changer");
    }

    @Test
    void testRetrieveDepartement() {
        // Sauvegarder le département
        Departement savedDepartement = departementRepository.save(departement);

        // Appel de la méthode
        Departement result = departementService.retrieveDepartement(savedDepartement.getIdDepart());

        // Vérifications
        assertNotNull(result, "Le département récupéré ne devrait pas être null");
        assertEquals("Informatique", result.getNomDepart(), "Le nom devrait être 'Informatique'");
        assertEquals(savedDepartement.getIdDepart(), result.getIdDepart(),
                "L'ID devrait correspondre");
    }

    @Test
    void testRetrieveDepartementNotFound() {
        // Appel de la méthode avec un ID inexistant
        assertThrows(Exception.class, () -> departementService.retrieveDepartement(999),
                "Une exception devrait être levée pour un département inexistant");
    }

    @Test
    void testDeleteDepartement() {
        // Sauvegarder le département
        Departement savedDepartement = departementRepository.save(departement);

        // Appel de la méthode
        departementService.deleteDepartement(savedDepartement.getIdDepart());

        // Vérifications
        assertEquals(0, departementRepository.count(), "Il ne devrait plus y avoir de département dans la base");
    }
}