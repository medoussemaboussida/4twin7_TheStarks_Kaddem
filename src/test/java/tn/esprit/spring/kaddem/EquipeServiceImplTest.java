package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.*;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EquipeServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications après chaque test
class EquipeServiceImplTest {

    @Autowired
    private EquipeServiceImpl equipeService;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ContratRepository contratRepository;

    private Equipe equipe;
    private Etudiant etudiant;
    private Contrat contrat;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        equipeRepository.deleteAll();
        etudiantRepository.deleteAll();
        contratRepository.deleteAll();

        // Initialisation des objets
        equipe = new Equipe();
        equipe.setNomEquipe("Equipe A");
        equipe.setNiveau(Niveau.JUNIOR);

        etudiant = new Etudiant();
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");
        etudiant.setOp(Option.GAMIX);

        contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 2)); // -2 ans
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);
    }

    @Test
    void testRetrieveAllEquipes() {
        // Ajouter des équipes
        equipeRepository.save(equipe);
        Equipe equipe2 = new Equipe("Equipe B", Niveau.SENIOR);
        equipeRepository.save(equipe2);

        // Appel de la méthode
        List<Equipe> result = equipeService.retrieveAllEquipes();

        // Vérifications
        assertNotNull(result, "La liste des équipes ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 équipes");
        assertTrue(result.stream().anyMatch(e -> e.getNomEquipe().equals("Equipe A")),
                "La liste devrait contenir l'équipe 'Equipe A'");
    }

    @Test
    void testAddEquipe() {
        // Appel de la méthode
        Equipe result = equipeService.addEquipe(equipe);

        // Vérifications
        assertNotNull(result, "L'équipe ajoutée ne devrait pas être null");
        assertNotNull(result.getIdEquipe(), "L'ID de l'équipe devrait être généré");
        assertEquals("Equipe A", result.getNomEquipe(), "Le nom devrait être 'Equipe A'");
        assertEquals(Niveau.JUNIOR, result.getNiveau(), "Le niveau devrait être 'JUNIOR'");
        assertEquals(1, equipeRepository.count(), "Il devrait y avoir 1 équipe dans la base");
    }

    @Test
    void testUpdateEquipe() {
        // Sauvegarder l'équipe initiale
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Modifier l'équipe
        savedEquipe.setNomEquipe("Equipe A Updated");
        savedEquipe.setNiveau(Niveau.SENIOR);

        // Appel de la méthode
        Equipe result = equipeService.updateEquipe(savedEquipe);

        // Vérifications
        assertNotNull(result, "L'équipe mise à jour ne devrait pas être null");
        assertEquals("Equipe A Updated", result.getNomEquipe(), "Le nom devrait être 'Equipe A Updated'");
        assertEquals(Niveau.SENIOR, result.getNiveau(), "Le niveau devrait être 'SENIOR'");
        assertEquals(savedEquipe.getIdEquipe(), result.getIdEquipe(), "L'ID ne devrait pas changer");
    }

    @Test
    void testRetrieveEquipe() {
        // Sauvegarder l'équipe
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        Equipe result = equipeService.retrieveEquipe(savedEquipe.getIdEquipe());

        // Vérifications
        assertNotNull(result, "L'équipe récupérée ne devrait pas être null");
        assertEquals("Equipe A", result.getNomEquipe(), "Le nom devrait être 'Equipe A'");
        assertEquals(savedEquipe.getIdEquipe(), result.getIdEquipe(), "L'ID devrait correspondre");
    }

    @Test
    void testRetrieveEquipeNotFound() {
        // Appel de la méthode avec un ID inexistant
        assertThrows(Exception.class, () -> equipeService.retrieveEquipe(999),
                "Une exception devrait être levée pour une équipe inexistante");
    }

    @Test
    void testDeleteEquipe() {
        // Sauvegarder l'équipe
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        equipeService.deleteEquipe(savedEquipe.getIdEquipe());

        // Vérifications
        assertEquals(0, equipeRepository.count(), "Il ne devrait plus y avoir d'équipe dans la base");
    }

    @Test
    void testEvoluerEquipesJuniorToSenior() {
        // Préparer une équipe JUNIOR avec 3 étudiants ayant des contrats actifs
        equipe.setEtudiants(new HashSet<>());
        Etudiant etudiant2 = new Etudiant("Ali", "Ben", Option.SE);
        Etudiant etudiant3 = new Etudiant("Sara", "Ahmed", Option.SIM);
        Set<Etudiant> etudiants = new HashSet<>(Arrays.asList(etudiant, etudiant2, etudiant3));

        // Sauvegarder les étudiants et leurs contrats
        for (Etudiant e : etudiants) {
            e.setContrats(new HashSet<>());
            Contrat c = new Contrat();
            c.setDateDebutContrat(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 2)); // -2 ans
            c.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
            c.setArchive(false);
            c.setSpecialite(Specialite.CLOUD);
            c.setMontantContrat(2000);
            c.setEtudiant(e);
            e.getContrats().add(c);
            etudiantRepository.save(e);
            contratRepository.save(c);
        }

        equipe.setEtudiants(etudiants);
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        equipeService.evoluerEquipes();

        // Vérifications
        Equipe updatedEquipe = equipeRepository.findById(savedEquipe.getIdEquipe()).orElse(null);
        assertNotNull(updatedEquipe, "L'équipe ne devrait pas être null");
        assertEquals(Niveau.SENIOR, updatedEquipe.getNiveau(),
                "L'équipe devrait être passée de JUNIOR à SENIOR");
    }

    @Test
    void testEvoluerEquipesSeniorToExpert() {
        // Préparer une équipe SENIOR avec 3 étudiants ayant des contrats actifs
        equipe.setNiveau(Niveau.SENIOR);
        equipe.setEtudiants(new HashSet<>());
        Etudiant etudiant2 = new Etudiant("Ali", "Ben", Option.SE);
        Etudiant etudiant3 = new Etudiant("Sara", "Ahmed", Option.SIM);
        Set<Etudiant> etudiants = new HashSet<>(Arrays.asList(etudiant, etudiant2, etudiant3));

        // Sauvegarder les étudiants et leurs contrats
        for (Etudiant e : etudiants) {
            e.setContrats(new HashSet<>());
            Contrat c = new Contrat();
            c.setDateDebutContrat(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 2)); // -2 ans
            c.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
            c.setArchive(false);
            c.setSpecialite(Specialite.CLOUD);
            c.setMontantContrat(2000);
            c.setEtudiant(e);
            e.getContrats().add(c);
            etudiantRepository.save(e);
            contratRepository.save(c);
        }

        equipe.setEtudiants(etudiants);
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        equipeService.evoluerEquipes();

        // Vérifications
        Equipe updatedEquipe = equipeRepository.findById(savedEquipe.getIdEquipe()).orElse(null);
        assertNotNull(updatedEquipe, "L'équipe ne devrait pas être null");
        assertEquals(Niveau.EXPERT, updatedEquipe.getNiveau(),
                "L'équipe devrait être passée de SENIOR à EXPERT");
    }

    @Test
    void testEvoluerEquipesNoEvolution() {
        // Préparer une équipe JUNIOR avec moins de 3 étudiants ayant des contrats actifs
        equipe.setEtudiants(new HashSet<>());
        etudiant.setContrats(new HashSet<>());
        equipe.getEtudiants().add(etudiant);
        etudiantRepository.save(etudiant);
        Equipe savedEquipe = equipeRepository.save(equipe);

        // Appel de la méthode
        equipeService.evoluerEquipes();

        // Vérifications
        Equipe updatedEquipe = equipeRepository.findById(savedEquipe.getIdEquipe()).orElse(null);
        assertNotNull(updatedEquipe, "L'équipe ne devrait pas être null");
        assertEquals(Niveau.JUNIOR, updatedEquipe.getNiveau(),
                "L'équipe devrait rester JUNIOR");
    }
}