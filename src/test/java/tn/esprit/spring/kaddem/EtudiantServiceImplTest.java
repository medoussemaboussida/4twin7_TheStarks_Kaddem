package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.*;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    private Etudiant etudiant;
    private Departement departement;
    private Contrat contrat;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        // Initialisation des objets
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1); // ID défini manuellement pour les mocks
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");
        etudiant.setOp(Option.GAMIX);
        etudiant.setContrats(new HashSet<>());
        etudiant.setEquipes(new ArrayList<>());

        departement = new Departement();
        departement.setIdDepart(1); // ID défini manuellement pour les mocks
        departement.setNomDepart("Informatique");

        contrat = new Contrat();
        contrat.setIdContrat(1); // ID défini manuellement pour les mocks
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);

        equipe = new Equipe();
        equipe.setIdEquipe(1); // ID défini manuellement pour les mocks
        equipe.setNomEquipe("Equipe A");
        equipe.setEtudiants(new HashSet<>());
    }

    @Test
    void testRetrieveAllEtudiants() {
        // Données simulées
        List<Etudiant> etudiants = Arrays.asList(etudiant, new Etudiant("Ali", "Ben", Option.SE));
        when(etudiantRepository.findAll()).thenReturn(etudiants);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Vérifications
        assertNotNull(result, "La liste des étudiants ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 étudiants");
        assertTrue(result.stream().anyMatch(e -> e.getNomE().equals("Oussema")),
                "La liste devrait contenir l'étudiant Oussema");
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testAddEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.addEtudiant(etudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant ajouté ne devrait pas être null");
        assertEquals("Oussema", result.getNomE(), "Le nom devrait être 'Oussema'");
        assertEquals(Option.GAMIX, result.getOp(), "L'option devrait être 'GAMIX'");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testUpdateEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.updateEtudiant(etudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant mis à jour ne devrait pas être null");
        assertEquals("Oussema", result.getNomE(), "Le nom devrait être 'Oussema'");
        assertEquals(Option.GAMIX, result.getOp(), "L'option devrait être 'GAMIX'");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testRetrieveEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        // Appel de la méthode
        Etudiant result = etudiantService.retrieveEtudiant(1);

        // Vérifications
        assertNotNull(result, "L'étudiant récupéré ne devrait pas être null");
        assertEquals("Oussema", result.getNomE(), "Le nom devrait être 'Oussema'");
        assertEquals(1, result.getIdEtudiant(), "L'ID devrait être 1");
        verify(etudiantRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        // Simuler un étudiant non trouvé
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        // Appel de la méthode avec un ID inexistant
        assertThrows(NoSuchElementException.class, () -> etudiantService.retrieveEtudiant(999),
                "Une exception devrait être levée pour un étudiant inexistant");
        verify(etudiantRepository, times(1)).findById(999);
    }

    @Test
    void testRemoveEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        doNothing().when(etudiantRepository).delete(etudiant);

        // Appel de la méthode
        etudiantService.removeEtudiant(1);

        // Vérifications
        verify(etudiantRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).delete(etudiant);
    }

    @Test
    void testAssignEtudiantToDepartement() {
        // Simuler le comportement des repositories
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        etudiantService.assignEtudiantToDepartement(1, 1);

        // Vérifications
        assertEquals(departement, etudiant.getDepartement(),
                "Le département devrait être assigné à l'étudiant");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        // Simuler uniquement ce qui est strictement nécessaire
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));

        // Appel de la méthode
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 1);

        // Vérifications
        assertNotNull(result, "L'étudiant ne devrait pas être null");
        assertEquals(etudiant, contrat.getEtudiant(), "Le contrat devrait être assigné à l'étudiant");
        assertTrue(equipe.getEtudiants().contains(etudiant), "L'étudiant devrait être dans l'équipe");
        verify(contratRepository, times(1)).findById(1);
        verify(equipeRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class)); // Ajouté car la méthode devrait sauvegarder
    }

    @Test
    void testGetEtudiantsByDepartement() {
        // Données simulées
        Etudiant etudiant2 = new Etudiant("Ali", "Ben", Option.SE);
        etudiant2.setIdEtudiant(2);
        etudiant.setDepartement(departement);
        etudiant2.setDepartement(departement);
        List<Etudiant> etudiants = Arrays.asList(etudiant, etudiant2);
        when(etudiantRepository.findEtudiantsByDepartement_IdDepart(1)).thenReturn(etudiants);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(1);

        // Vérifications
        assertNotNull(result, "La liste des étudiants ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 étudiants");
        assertTrue(result.stream().anyMatch(e -> e.getNomE().equals("Oussema")),
                "La liste devrait contenir l'étudiant Oussema");
        verify(etudiantRepository, times(1)).findEtudiantsByDepartement_IdDepart(1);
    }
}