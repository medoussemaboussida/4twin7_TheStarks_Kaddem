package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.*;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EquipeServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ContratRepository contratRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;
    private Etudiant etudiant1, etudiant2, etudiant3;
    private Contrat contrat;

    @BeforeEach
    void setUp() {
        // Initialisation de l'équipe
        equipe = new Equipe();
        equipe.setIdEquipe(1);
        equipe.setNomEquipe("Equipe A");
        equipe.setNiveau(Niveau.JUNIOR);
        equipe.setEtudiants(new HashSet<>());

        // Initialisation des étudiants
        etudiant1 = new Etudiant();
        etudiant1.setIdEtudiant(1);
        etudiant1.setNomE("Oussema");
        etudiant1.setPrenomE("Med");
        etudiant1.setOp(Option.GAMIX);
        etudiant1.setContrats(new HashSet<>());

        etudiant2 = new Etudiant();
        etudiant2.setIdEtudiant(2);
        etudiant2.setNomE("Ahmed");
        etudiant2.setPrenomE("Ben");
        etudiant2.setOp(Option.GAMIX);
        etudiant2.setContrats(new HashSet<>());

        etudiant3 = new Etudiant();
        etudiant3.setIdEtudiant(3);
        etudiant3.setNomE("Sara");
        etudiant3.setPrenomE("Ali");
        etudiant3.setOp(Option.GAMIX);
        etudiant3.setContrats(new HashSet<>());

        // Initialisation du contrat
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 2)); // -2 ans
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);
    }

    @Test
    void testRetrieveAllEquipes() {
        // Données simulées
        List<Equipe> equipes = Arrays.asList(equipe, new Equipe());
        equipes.get(1).setIdEquipe(2);
        equipes.get(1).setNomEquipe("Equipe B");
        equipes.get(1).setNiveau(Niveau.SENIOR);
        when(equipeRepository.findAll()).thenReturn(equipes);

        // Appel de la méthode
        List<Equipe> result = equipeService.retrieveAllEquipes();

        // Vérifications
        assertNotNull(result, "La liste des équipes ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 équipes");
        assertTrue(result.stream().anyMatch(e -> e.getNomEquipe().equals("Equipe A")),
                "La liste devrait contenir l'équipe 'Equipe A'");
        verify(equipeRepository, times(1)).findAll();
    }

    @Test
    void testAddEquipe() {
        // Simuler le comportement du repository
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        // Appel de la méthode
        Equipe result = equipeService.addEquipe(equipe);

        // Vérifications
        assertNotNull(result, "L'équipe ajoutée ne devrait pas être null");
        assertEquals("Equipe A", result.getNomEquipe(), "Le nom devrait être 'Equipe A'");
        assertEquals(Niveau.JUNIOR, result.getNiveau(), "Le niveau devrait être 'JUNIOR'");
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void testUpdateEquipe() {
        // Simuler le comportement du repository
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        // Appel de la méthode
        Equipe result = equipeService.updateEquipe(equipe);

        // Vérifications
        assertNotNull(result, "L'équipe mise à jour ne devrait pas être null");
        assertEquals("Equipe A", result.getNomEquipe(), "Le nom devrait être 'Equipe A'");
        assertEquals(Niveau.JUNIOR, result.getNiveau(), "Le niveau devrait être 'JUNIOR'");
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void testRetrieveEquipe() {
        // Simuler le comportement du repository
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));

        // Appel de la méthode
        Equipe result = equipeService.retrieveEquipe(1);

        // Vérifications
        assertNotNull(result, "L'équipe récupérée ne devrait pas être null");
        assertEquals("Equipe A", result.getNomEquipe(), "Le nom devrait être 'Equipe A'");
        assertEquals(1, result.getIdEquipe(), "L'ID devrait être 1");
        verify(equipeRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveEquipeNotFound() {
        // Simuler une équipe non trouvée
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());

        // Appel de la méthode avec un ID inexistant
        assertThrows(NoSuchElementException.class, () -> equipeService.retrieveEquipe(999),
                "Une exception devrait être levée pour une équipe inexistante");
        verify(equipeRepository, times(1)).findById(999);
    }

    @Test
    void testDeleteEquipe() {
        // Simuler le comportement du repository
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        doNothing().when(equipeRepository).delete(equipe);

        // Appel de la méthode
        equipeService.deleteEquipe(1);

        // Vérifications
        verify(equipeRepository, times(1)).findById(1);
        verify(equipeRepository, times(1)).delete(equipe);
    }

    @Test
    void testEvoluerEquipesJuniorToSenior() {
        // Configurer une équipe JUNIOR avec 3 étudiants ayant des contrats actifs
        equipe.setNiveau(Niveau.JUNIOR);
        equipe.getEtudiants().add(etudiant1);
        equipe.getEtudiants().add(etudiant2);
        equipe.getEtudiants().add(etudiant3);

        // Configurer des contrats actifs (plus d’un an, non archivés)
        etudiant1.getContrats().add(contrat);
        etudiant2.getContrats().add(contrat);
        etudiant3.getContrats().add(contrat);

        // Simuler le repository
        List<Equipe> equipes = Collections.singletonList(equipe);
        when(equipeRepository.findAll()).thenReturn(equipes);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        // Appel de la méthode
        equipeService.evoluerEquipes();

        // Vérifications
        assertEquals(Niveau.SENIOR, equipe.getNiveau(), "L'équipe devrait passer à SENIOR");
        verify(equipeRepository, times(1)).findAll();
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void testEvoluerEquipesNoEvolution() {
        // Configurer une équipe JUNIOR avec 1 étudiant ayant un contrat actif
        equipe.setNiveau(Niveau.JUNIOR);
        equipe.getEtudiants().add(etudiant1);

        // Configurer un contrat actif pour un seul étudiant
        etudiant1.getContrats().add(contrat);

        // Simuler le repository
        List<Equipe> equipes = Collections.singletonList(equipe);
        when(equipeRepository.findAll()).thenReturn(equipes);

        // Appel de la méthode
        equipeService.evoluerEquipes();

        // Vérifications
        assertEquals(Niveau.JUNIOR, equipe.getNiveau(), "L'équipe devrait rester JUNIOR");
        verify(equipeRepository, times(1)).findAll();
        verify(equipeRepository, never()).save(any(Equipe.class));
    }
}