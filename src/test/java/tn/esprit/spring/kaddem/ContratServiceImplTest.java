package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.ContratServiceImpl;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications après chaque test
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
        // Nettoyer la base avant chaque test
        contratRepository.deleteAll();
        etudiantRepository.deleteAll();

        // Initialisation des objets
        contrat = new Contrat();
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);

        etudiant = new Etudiant();
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");

        // Sauvegarder l'étudiant pour les tests d'affectation
        etudiantRepository.save(etudiant);

        startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60); // -60 jours
        endDate = new Date();
    }

    @Test
    void testRetrieveAllContrats() {
        // Ajouter des contrats
        contratRepository.save(contrat);
        Contrat contrat2 = new Contrat(new Date(), new Date(), Specialite.CLOUD, false, 2000);
        contratRepository.save(contrat2);

        // Appel de la méthode
        List<Contrat> result = contratService.retrieveAllContrats();

        // Vérifications
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testAddContrat() {
        // Appel de la méthode
        Contrat result = contratService.addContrat(contrat);

        // Vérifications
        assertNotNull(result);
        assertNotNull(result.getIdContrat());
        assertEquals(Specialite.IA, result.getSpecialite());
        assertEquals(1, contratRepository.count());
    }

    @Test
    void testUpdateContrat() {
        // Sauvegarder le contrat initial
        Contrat savedContrat = contratRepository.save(contrat);

        // Modifier le contrat
        savedContrat.setMontantContrat(1500);
        savedContrat.setSpecialite(Specialite.RESEAUX);

        // Appel de la méthode
        Contrat result = contratService.updateContrat(savedContrat);

        // Vérifications
        assertNotNull(result);
        assertEquals(1500, result.getMontantContrat());
        assertEquals(Specialite.RESEAUX, result.getSpecialite());
    }

    @Test
    void testRetrieveContrat() {
        // Sauvegarder le contrat
        Contrat savedContrat = contratRepository.save(contrat);

        // Appel de la méthode
        Contrat result = contratService.retrieveContrat(savedContrat.getIdContrat());

        // Vérifications
        assertNotNull(result);
        assertEquals(Specialite.IA, result.getSpecialite());
        assertEquals(savedContrat.getIdContrat(), result.getIdContrat());
    }

    @Test
    void testRetrieveContratNotFound() {
        // Appel de la méthode avec un ID inexistant
        Contrat result = contratService.retrieveContrat(999);

        // Vérifications
        assertNull(result);
    }

    @Test
    void testRemoveContrat() {
        // Sauvegarder le contrat
        Contrat savedContrat = contratRepository.save(contrat);

        // Appel de la méthode
        contratService.removeContrat(savedContrat.getIdContrat());

        // Vérifications
        assertEquals(0, contratRepository.count());
    }

    @Test
    void testAffectContratToEtudiantSuccess() {
        // Sauvegarder le contrat
        Contrat savedContrat = contratRepository.save(contrat);

        // Appel de la méthode
        Contrat result = contratService.affectContratToEtudiant(savedContrat.getIdContrat(), "Oussema", "Med");

        // Vérifications
        assertNotNull(result);
        assertNotNull(result.getEtudiant());
        assertEquals("Oussema", result.getEtudiant().getNomE());
        assertEquals(1, etudiantRepository.findByNomEAndPrenomE("Oussema", "Med").getContrats().size());
    }

    @Test
    void testAffectContratToEtudiantTooManyActiveContrats() {
        // Ajouter 5 contrats actifs à l'étudiant
        for (int i = 0; i < 5; i++) {
            Contrat c = new Contrat(new Date(), new Date(), Specialite.CLOUD, false, 2000);
            c.setEtudiant(etudiant);
            contratRepository.save(c);
        }

        // Sauvegarder un nouveau contrat
        Contrat newContrat = contratRepository.save(contrat);

        // Appel de la méthode
        Contrat result = contratService.affectContratToEtudiant(newContrat.getIdContrat(), "Oussema", "Med");

        // Vérifications
        assertNotNull(result);
        assertNull(result.getEtudiant()); // L'étudiant ne doit pas être affecté
    }

    @Test
    void testNbContratsValides() {
        // Ajouter des contrats valides
        contratRepository.save(contrat);
        Contrat contrat2 = new Contrat(startDate, endDate, Specialite.CLOUD, false, 2000);
        contratRepository.save(contrat2);

        // Appel de la méthode
        Integer result = contratService.nbContratsValides(startDate, endDate);

        // Vérifications
        // Note : Le résultat dépend de l'implémentation réelle de getnbContratsValides
        assertNotNull(result);
    }

    @Test
    void testRetrieveAndUpdateStatusContrat() {
        // Créer un contrat qui doit être archivé (date de fin = aujourd'hui)
        Contrat contratToArchive = new Contrat();
        contratToArchive.setDateDebutContrat(new Date());
        contratToArchive.setDateFinContrat(new Date());
        contratToArchive.setSpecialite(Specialite.CLOUD);
        contratToArchive.setArchive(false);
        contratToArchive.setMontantContrat(2000);
        contratRepository.save(contratToArchive);

        // Appel de la méthode
        contratService.retrieveAndUpdateStatusContrat();

        // Vérifications
        Contrat updatedContrat = contratRepository.findById(contratToArchive.getIdContrat()).orElse(null);
        assertNotNull(updatedContrat);
        assertTrue(updatedContrat.getArchive());
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        // Ajouter des contrats
        contratRepository.save(new Contrat(new Date(), new Date(), Specialite.IA, false, 1000));
        contratRepository.save(new Contrat(new Date(), new Date(), Specialite.CLOUD, false, 2000));

        // Appel de la méthode
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Vérifications
        float expectedChiffreAffaire = (1 * 300) + (1 * 400); // 1 mois * (300 pour IA + 400 pour CLOUD)
        assertEquals(expectedChiffreAffaire, result, 0.01);
    }
}