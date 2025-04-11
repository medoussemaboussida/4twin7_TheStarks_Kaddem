package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContratServiceImplTest {

    @Autowired
    private ContratServiceImpl contratService;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    private Contrat contrat;
    private Etudiant etudiant;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        // Clear repositories to ensure a clean state
        contratRepository.deleteAll();
        etudiantRepository.deleteAll();

        // Initialize test data
        contrat = new Contrat();
        contrat.setMontantContrat(1000);
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // 30 days later

        etudiant = new Etudiant();
        etudiant.setNomE("John");
        etudiant.setPrenomE("Doe");

        startDate = new Date(System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000); // 60 days ago
        endDate = new Date();
    }

    @Test
    @Order(1)
    void testAddContrat() {
        Contrat savedContrat = contratService.addContrat(contrat);

        assertNotNull(savedContrat);
        assertNotNull(savedContrat.getIdContrat());
        assertEquals(contrat.getMontantContrat(), savedContrat.getMontantContrat());
        assertEquals(1, contratRepository.count());
    }

    @Test
    @Order(2)
    void testRetrieveAllContrats() {
        contratService.addContrat(contrat);

        List<Contrat> contrats = contratService.retrieveAllContrats();

        assertNotNull(contrats);
        assertEquals(1, contrats.size());
        assertEquals(contrat.getMontantContrat(), contrats.get(0).getMontantContrat());
    }

    @Test
    @Order(3)
    void testUpdateContrat() {
        Contrat savedContrat = contratService.addContrat(contrat);
        savedContrat.setMontantContrat(2000);

        Contrat updatedContrat = contratService.updateContrat(savedContrat);

        assertNotNull(updatedContrat);
        assertEquals(savedContrat.getIdContrat(), updatedContrat.getIdContrat());
        assertEquals(2000, updatedContrat.getMontantContrat());
    }

    @Test
    @Order(4)
    void testRetrieveContrat() {
        Contrat savedContrat = contratService.addContrat(contrat);

        Contrat retrievedContrat = contratService.retrieveContrat(savedContrat.getIdContrat());

        assertNotNull(retrievedContrat);
        assertEquals(savedContrat.getIdContrat(), retrievedContrat.getIdContrat());
        assertEquals(contrat.getMontantContrat(), retrievedContrat.getMontantContrat());
    }

    @Test
    @Order(5)
    void testRetrieveContratNotFound() {
        Contrat retrievedContrat = contratService.retrieveContrat(999);

        assertNull(retrievedContrat);
    }

    @Test
    @Order(6)
    void testRemoveContrat() {
        Contrat savedContrat = contratService.addContrat(contrat);

        contratService.removeContrat(savedContrat.getIdContrat());

        assertEquals(0, contratRepository.count());
    }

    @Test
    @Order(7)
    void testAffectContratToEtudiant_Success() {
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        Contrat savedContrat = contratService.addContrat(contrat);

        Contrat assignedContrat = contratService.affectContratToEtudiant(
                savedContrat.getIdContrat(), "John", "Doe");

        assertNotNull(assignedContrat);
        assertEquals(savedEtudiant, assignedContrat.getEtudiant());
        assertTrue(savedEtudiant.getContrats().contains(assignedContrat));
    }

    @Test
    @Order(8)
    void testAffectContratToEtudiant_MaxContracts() {
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        // Add 5 contracts
        for (int i = 0; i < 5; i++) {
            Contrat c = new Contrat();
            c.setArchive(true); // Active contract
            c.setEtudiant(savedEtudiant);
            contratService.addContrat(c);
        }
        Contrat newContrat = contratService.addContrat(contrat);

        Contrat assignedContrat = contratService.affectContratToEtudiant(
                newContrat.getIdContrat(), "John", "Doe");

        assertNotNull(assignedContrat);
        assertNull(assignedContrat.getEtudiant()); // Should not assign due to max contracts
    }

    @Test
    @Order(9)
    void testNbContratsValides() {
        contrat.setDateDebutContrat(startDate);
        contrat.setDateFinContrat(endDate);
        contratService.addContrat(contrat);

        Integer count = contratService.nbContratsValides(startDate, endDate);

        assertNotNull(count);
        assertEquals(1, count);
    }

    @Test
    @Order(10)
    void testGetChiffreAffaireEntreDeuxDates_IA() {
        contrat.setSpecialite(Specialite.IA);
        contratService.addContrat(contrat);

        float chiffreAffaire = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // 60 days ≈ 2 months * 300 = 600
        assertEquals(600.0f, chiffreAffaire, 0.01);
    }

    @Test
    @Order(11)
    void testGetChiffreAffaireEntreDeuxDates_Cloud() {
        contrat.setSpecialite(Specialite.CLOUD);
        contratService.addContrat(contrat);

        float chiffreAffaire = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // 60 days ≈ 2 months * 400 = 800
        assertEquals(800.0f, chiffreAffaire, 0.01);
    }

    @Test
    @Order(12)
    void testRetrieveAndUpdateStatusContrat() {
        // This test is simplified; assumes contract is not yet expired
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 16L * 24 * 60 * 60 * 1000)); // 16 days from now
        contratService.addContrat(contrat);

        contratService.retrieveAndUpdateStatusContrat();

        Contrat updatedContrat = contratRepository.findById(contrat.getIdContrat()).orElse(null);
        assertNotNull(updatedContrat);
        assertFalse(updatedContrat.getArchive()); // Should not be archived yet
    }

    @AfterEach
    void tearDown() {
        contratRepository.deleteAll();
        etudiantRepository.deleteAll();
    }
}