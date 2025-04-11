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
    private Etudiant etudiant;
    private Contrat contrat;

    @BeforeEach
    void setUp() {
        // Initialisation des objets
        equipe = new Equipe();
        equipe.setIdEquipe(1); // ID défini manuellement pour les mocks
        equipe.setNomEquipe("Equipe A");
        equipe.setNiveau(Niveau.JUNIOR);
        equipe.setEtudiants(new HashSet<>());

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1); // ID défini manuellement pour les mocks
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");
        etudiant.setOp(Option.GAMIX);
        etudiant.setContrats(new HashSet<>());

        contrat = new Contrat();
        contrat.setIdContrat(1); // ID défini manuellement pour les mocks
        contrat.setDateDebutContrat(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 2)); // -2 ans
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);
    }

    @Test
    void testRetrieveAllEquipes() {
        // Données simulées
        List<Equipe> equipes = Arrays.asList(equipe, new Equipe("Equipe B", Niveau.SENIOR));
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


}