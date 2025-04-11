package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Option;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications après chaque test
class EtudiantServiceImplTest {

    @Autowired
    private EtudiantServiceImpl etudiantService;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    private Etudiant etudiant;
    private Departement departement;
    private Contrat contrat;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        etudiantRepository.deleteAll();
        departementRepository.deleteAll();
        contratRepository.deleteAll();
        equipeRepository.deleteAll();

        // Initialisation des objets
        etudiant = new Etudiant();
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");
        etudiant.setOp(Option.GAMIX);

        departement = new Departement();
        departement.setNomDepart("Informatique");

        contrat = new Contrat();
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);

        equipe = new Equipe();
        equipe.setNomEquipe("Equipe A");
    }

    @Test
    void testRetrieveAllEtudiants() {
        // Ajouter des étudiants
        etudiantRepository.save(etudiant);
        Etudiant etudiant2 = new Etudiant("Ali", "Ben", Option.SE);
        etudiantRepository.save(etudiant2);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Vérifications
        assertNotNull(result, "La liste des étudiants ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 étudiants");
        assertTrue(result.stream().anyMatch(e -> e.getNomE().equals("Oussema")),
                "La liste devrait contenir l'étudiant Oussema");
    }

    @Test
    void testAddEtudiant() {
        // Appel de la méthode
        Etudiant result = etudiantService.addEtudiant(etudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant ajouté ne devrait pas être null");
        assertNotNull(result.getIdEtudiant(), "L'ID de l'étudiant devrait être généré");
        assertEquals("Oussema", result.getNomE(), "Le nom devrait être 'Oussema'");
        assertEquals(Option.GAMIX, result.getOp(), "L'option devrait être 'GAMIX'");
        assertEquals(1, etudiantRepository.count(), "Il devrait y avoir 1 étudiant dans la base");
    }

    @Test
    void testUpdateEtudiant() {
        // Sauvegarder l'étudiant initial
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);

        // Modifier l'étudiant
        savedEtudiant.setNomE("Oussema Updated");
        savedEtudiant.setOp(Option.SE);

        // Appel de la méthode
        Etudiant result = etudiantService.updateEtudiant(savedEtudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant mis à jour ne devrait pas être null");
        assertEquals("Oussema Updated", result.getNomE(), "Le nom devrait être 'Oussema Updated'");
        assertEquals(Option.SE, result.getOp(), "L'option devrait être 'SE'");
        assertEquals(savedEtudiant.getIdEtudiant(), result.getIdEtudiant(), "L'ID ne devrait pas changer");
    }

    @Test
    void testRetrieveEtudiant() {
        // Sauvegarder l'étudiant
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.retrieveEtudiant(savedEtudiant.getIdEtudiant());

        // Vérifications
        assertNotNull(result, "L'étudiant récupéré ne devrait pas être null");
        assertEquals("Oussema", result.getNomE(), "Le nom devrait être 'Oussema'");
        assertEquals(savedEtudiant.getIdEtudiant(), result.getIdEtudiant(), "L'ID devrait correspondre");
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        // Appel de la méthode avec un ID inexistant
        assertThrows(Exception.class, () -> etudiantService.retrieveEtudiant(999),
                "Une exception devrait être levée pour un étudiant inexistant");
    }

    @Test
    void testRemoveEtudiant() {
        // Sauvegarder l'étudiant
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);

        // Appel de la méthode
        etudiantService.removeEtudiant(savedEtudiant.getIdEtudiant());

        // Vérifications
        assertEquals(0, etudiantRepository.count(), "Il ne devrait plus y avoir d'étudiant dans la base");
    }

    @Test
    void testAssignEtudiantToDepartement() {
        // Sauvegarder l'étudiant et le département
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        Departement savedDepartement = departementRepository.save(departement);

        // Appel de la méthode
        etudiantService.assignEtudiantToDepartement(savedEtudiant.getIdEtudiant(), savedDepartement.getIdDepart());

        // Vérifications
        Etudiant updatedEtudiant = etudiantRepository.findById(savedEtudiant.getIdEtudiant()).orElse(null);
        assertNotNull(updatedEtudiant, "L'étudiant ne devrait pas être null");
        assertNotNull(updatedEtudiant.getDepartement(), "Le département ne devrait pas être null");
        assertEquals("Informatique", updatedEtudiant.getDepartement().getNomDepart(),
                "Le département devrait être 'Informatique'");
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        // Sauvegarder le contrat et l'équipe
        Contrat savedContrat = contratRepository.save(contrat);
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant,
                savedContrat.getIdContrat(), savedEquipe.getIdEquipe());

        // Vérifications
        assertNotNull(result, "L'étudiant ne devrait pas être null");
        assertNotNull(result.getIdEtudiant(), "L'ID de l'étudiant devrait être généré");
        assertEquals(1, contratRepository.findById(savedContrat.getIdContrat()).get().getEtudiant().getIdEtudiant(),
                "Le contrat devrait être assigné à l'étudiant");
        assertEquals(1, equipeRepository.findById(savedEquipe.getIdEquipe()).get().getEtudiants().size(),
                "L'équipe devrait contenir 1 étudiant");
    }

    @Test
    void testGetEtudiantsByDepartement() {
        // Sauvegarder le département et les étudiants
        Departement savedDepartement = departementRepository.save(departement);
        etudiant.setDepartement(savedDepartement);
        Etudiant savedEtudiant1 = etudiantRepository.save(etudiant);
        Etudiant etudiant2 = new Etudiant("Ali", "Ben", Option.SE);
        etudiant2.setDepartement(savedDepartement);
        etudiantRepository.save(etudiant2);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(savedDepartement.getIdDepart());

        // Vérifications
        assertNotNull(result, "La liste des étudiants ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 étudiants");
        assertTrue(result.stream().anyMatch(e -> e.getNomE().equals("Oussema")),
                "La liste devrait contenir l'étudiant Oussema");
    }
}