package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications après chaque test
class UniversiteServiceImplTest {

    @Autowired
    private UniversiteServiceImpl universiteService;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private DepartementRepository departementRepository;

    private Universite universite;
    private Departement departement;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        universiteRepository.deleteAll();
        departementRepository.deleteAll();

        // Initialisation des objets
        universite = new Universite();
        universite.setNomUniv("Université de Tunis");
        universite.setDepartements(new HashSet<>());

        departement = new Departement();
        departement.setNomDepart("Informatique");
    }

    @Test
    void testRetrieveAllUniversites() {
        // Ajouter des universités
        universiteRepository.save(universite);
        Universite universite2 = new Universite("Université de Carthage");
        universiteRepository.save(universite2);

        // Appel de la méthode
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Vérifications
        assertNotNull(result, "La liste des universités ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 universités");
        assertTrue(result.stream().anyMatch(u -> u.getNomUniv().equals("Université de Tunis")),
                "La liste devrait contenir l'université 'Université de Tunis'");
    }

    @Test
    void testAddUniversite() {
        // Appel de la méthode
        Universite result = universiteService.addUniversite(universite);

        // Vérifications
        assertNotNull(result, "L'université ajoutée ne devrait pas être null");
        assertNotNull(result.getIdUniv(), "L'ID de l'université devrait être généré");
        assertEquals("Université de Tunis", result.getNomUniv(), "Le nom devrait être 'Université de Tunis'");
        assertEquals(1, universiteRepository.count(), "Il devrait y avoir 1 université dans la base");
    }

    @Test
    void testUpdateUniversite() {
        // Sauvegarder l'université initiale
        Universite savedUniversite = universiteRepository.save(universite);

        // Modifier l'université
        savedUniversite.setNomUniv("Université de Tunis Updated");

        // Appel de la méthode
        Universite result = universiteService.updateUniversite(savedUniversite);

        // Vérifications
        assertNotNull(result, "L'université mise à jour ne devrait pas être null");
        assertEquals("Université de Tunis Updated", result.getNomUniv(),
                "Le nom devrait être 'Université de Tunis Updated'");
        assertEquals(savedUniversite.getIdUniv(), result.getIdUniv(), "L'ID ne devrait pas changer");
    }

    @Test
    void testRetrieveUniversite() {
        // Sauvegarder l'université
        Universite savedUniversite = universiteRepository.save(universite);

        // Appel de la méthode
        Universite result = universiteService.retrieveUniversite(savedUniversite.getIdUniv());

        // Vérifications
        assertNotNull(result, "L'université récupérée ne devrait pas être null");
        assertEquals("Université de Tunis", result.getNomUniv(), "Le nom devrait être 'Université de Tunis'");
        assertEquals(savedUniversite.getIdUniv(), result.getIdUniv(), "L'ID devrait correspondre");
    }

    @Test
    void testRetrieveUniversiteNotFound() {
        // Appel de la méthode avec un ID inexistant
        assertThrows(Exception.class, () -> universiteService.retrieveUniversite(999),
                "Une exception devrait être levée pour une université inexistante");
    }

    @Test
    void testDeleteUniversite() {
        // Sauvegarder l'université
        Universite savedUniversite = universiteRepository.save(universite);

        // Appel de la méthode
        universiteService.deleteUniversite(savedUniversite.getIdUniv());

        // Vérifications
        assertEquals(0, universiteRepository.count(), "Il ne devrait plus y avoir d'université dans la base");
    }

    @Test
    void testAssignUniversiteToDepartement() {
        // Sauvegarder l'université et le département
        Universite savedUniversite = universiteRepository.save(universite);
        Departement savedDepartement = departementRepository.save(departement);

        // Appel de la méthode
        universiteService.assignUniversiteToDepartement(savedUniversite.getIdUniv(), savedDepartement.getIdDepart());

        // Vérifications
        Universite updatedUniversite = universiteRepository.findById(savedUniversite.getIdUniv()).orElse(null);
        assertNotNull(updatedUniversite, "L'université ne devrait pas être null");
        assertNotNull(updatedUniversite.getDepartements(), "La liste des départements ne devrait pas être null");
        assertEquals(1, updatedUniversite.getDepartements().size(), "L'université devrait avoir 1 département");
        assertTrue(updatedUniversite.getDepartements().stream()
                        .anyMatch(d -> d.getNomDepart().equals("Informatique")),
                "Le département devrait être 'Informatique'");
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        // Sauvegarder l'université et le département
        Universite savedUniversite = universiteRepository.save(universite);
        Departement savedDepartement = departementRepository.save(departement);
        savedUniversite.getDepartements().add(savedDepartement);
        universiteRepository.save(savedUniversite);

        // Appel de la méthode
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(savedUniversite.getIdUniv());

        // Vérifications
        assertNotNull(result, "La liste des départements ne devrait pas être null");
        assertEquals(1, result.size(), "Il devrait y avoir 1 département");
        assertTrue(result.stream().anyMatch(d -> d.getNomDepart().equals("Informatique")),
                "Le département devrait être 'Informatique'");
    }
}