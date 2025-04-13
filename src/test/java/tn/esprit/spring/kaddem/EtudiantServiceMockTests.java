package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceMockTests {

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
        // Initialisation des objets pour les tests
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");

        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");

        contrat = new Contrat();
        contrat.setIdContrat(1);

        equipe = new Equipe();
        equipe.setIdEquipe(1);
    }

    // Tests pour retrieveAllEtudiants
    @Test
    void testRetrieveAllEtudiants() {
        // Données simulées
        List<Etudiant> etudiantList = Arrays.asList(etudiant, new Etudiant());
        when(etudiantRepository.findAll()).thenReturn(etudiantList);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertEquals(2, result.size(), "La liste doit contenir 2 étudiants");
        assertEquals("Doe", result.get(0).getNomE(), "Le nom du premier étudiant doit correspondre");
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllEtudiantsEmptyList() {
        // Simuler une liste vide
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList());

        // Appel de la méthode
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertTrue(result.isEmpty(), "La liste doit être vide");
        verify(etudiantRepository, times(1)).findAll();
    }

    // Tests pour addEtudiant
    @Test
    void testAddEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.addEtudiant(etudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant ne doit pas être null");
        assertEquals("Doe", result.getNomE(), "Le nom de l'étudiant doit correspondre");
        assertEquals("John", result.getPrenomE(), "Le prénom de l'étudiant doit correspondre");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testAddEtudiantWithNullName() {
        // Créer un étudiant avec un nom null
        Etudiant invalidEtudiant = new Etudiant();
        invalidEtudiant.setIdEtudiant(2);
        invalidEtudiant.setNomE(null);
        invalidEtudiant.setPrenomE("Jane");

        // Vérifier que l'ajout lève une exception
        assertThrows(IllegalArgumentException.class,
                () -> etudiantService.addEtudiant(invalidEtudiant),
                "Ajouter un étudiant avec un nom null doit lever une exception");
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void testAddEtudiantWithEmptyName() {
        // Créer un étudiant avec un nom vide
        Etudiant invalidEtudiant = new Etudiant();
        invalidEtudiant.setIdEtudiant(3);
        invalidEtudiant.setNomE("");
        invalidEtudiant.setPrenomE("Jane");

        // Vérifier que l'ajout lève une exception
        assertThrows(IllegalArgumentException.class,
                () -> etudiantService.addEtudiant(invalidEtudiant),
                "Ajouter un étudiant avec un nom vide doit lever une exception");
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    // Tests pour retrieveEtudiant
    @Test
    void testRetrieveEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        // Appel de la méthode
        Etudiant result = etudiantService.retrieveEtudiant(1);

        // Vérifications
        assertNotNull(result, "L'étudiant ne doit pas être null");
        assertEquals("Doe", result.getNomE(), "Le nom de l'étudiant doit correspondre");
        verify(etudiantRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        // Simuler un étudiant non trouvé
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que la récupération lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.retrieveEtudiant(999),
                "Récupérer un étudiant inexistant doit lever EntityNotFoundException");
        verify(etudiantRepository, times(1)).findById(999);
    }

    @Test
    void testRetrieveEtudiantInvalidId() {
        // Vérifier que la récupération avec un ID négatif lève une exception
        assertThrows(IllegalArgumentException.class,
                () -> etudiantService.retrieveEtudiant(-1),
                "Récupérer un étudiant avec un ID négatif doit lever une exception");
        verify(etudiantRepository, never()).findById(-1);
    }

    // Tests pour updateEtudiant
    @Test
    void testUpdateEtudiant() {
        // Simuler le comportement du repository
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.updateEtudiant(etudiant);

        // Vérifications
        assertNotNull(result, "L'étudiant ne doit pas être null");
        assertEquals(1, result.getIdEtudiant(), "L'ID de l'étudiant doit correspondre");
        assertEquals("Doe", result.getNomE(), "Le nom de l'étudiant doit correspondre");
        verify(etudiantRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testUpdateEtudiantNotFound() {
        // Simuler un étudiant non existant
        when(etudiantRepository.findById(1)).thenReturn(Optional.empty());

        // Vérifier que la mise à jour lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.updateEtudiant(etudiant),
                "Mettre à jour un étudiant inexistant doit lever une exception");
        verify(etudiantRepository, times(1)).findById(1);
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    // Tests pour removeEtudiant
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
    void testRemoveEtudiantNotFound() {
        // Simuler un étudiant non trouvé
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que la suppression lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.removeEtudiant(999),
                "Supprimer un étudiant inexistant doit lever une exception");
        verify(etudiantRepository, times(1)).findById(999);
        verify(etudiantRepository, never()).delete(any(Etudiant.class));
    }

    // Tests pour assignEtudiantToDepartement
    @Test
    void testAssignEtudiantToDepartement() {
        // Simuler le comportement des repositories
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        etudiantService.assignEtudiantToDepartement(1, 1);

        // Vérifications
        verify(etudiantRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).save(etudiant);
        assertEquals(departement, etudiant.getDepartement(), "L'étudiant doit être assigné au département");
    }

    @Test
    void testAssignEtudiantToDepartementEtudiantNotFound() {
        // Simuler un étudiant non trouvé
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que l'assignation lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.assignEtudiantToDepartement(999, 1),
                "Assigner un étudiant inexistant doit lever une exception");
        verify(etudiantRepository, times(1)).findById(999);
        verify(departementRepository, never()).findById(anyInt());
    }

    @Test
    void testAssignEtudiantToDepartementDepartementNotFound() {
        // Simuler un département non trouvé
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que l'assignation lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.assignEtudiantToDepartement(1, 999),
                "Assigner un étudiant à un département inexistant doit lever une exception");
        verify(etudiantRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(999);
    }

    // Tests pour addAndAssignEtudiantToEquipeAndContract
    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        // Simuler le comportement des repositories
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Appel de la méthode
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 1);

        // Vérifications
        assertNotNull(result, "L'étudiant ne doit pas être null");
        assertEquals("Doe", result.getNomE(), "Le nom de l'étudiant doit correspondre");
        verify(contratRepository, times(1)).findById(1);
        verify(equipeRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContractContratNotFound() {
        // Simuler un contrat non trouvé
        when(contratRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que l'ajout lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 999, 1),
                "Ajouter un étudiant avec un contrat inexistant doit lever une exception");
        verify(contratRepository, times(1)).findById(999);
        verify(equipeRepository, never()).findById(anyInt());
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContractEquipeNotFound() {
        // Simuler une équipe non trouvée
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que l'ajout lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 999),
                "Ajouter un étudiant avec une équipe inexistante doit lever une exception");
        verify(contratRepository, times(1)).findById(1);
        verify(equipeRepository, times(1)).findById(999);
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContractWithNullName() {
        // Créer un étudiant avec un nom null
        Etudiant invalidEtudiant = new Etudiant();
        invalidEtudiant.setIdEtudiant(2);
        invalidEtudiant.setNomE(null);
        invalidEtudiant.setPrenomE("Jane");

        // Vérifier que l'ajout lève une exception
        assertThrows(IllegalArgumentException.class,
                () -> etudiantService.addAndAssignEtudiantToEquipeAndContract(invalidEtudiant, 1, 1),
                "Ajouter un étudiant avec un nom null doit lever une exception");
        verify(contratRepository, never()).findById(anyInt());
        verify(equipeRepository, never()).findById(anyInt());
        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    // Tests pour getEtudiantsByDepartement
    @Test
    void testGetEtudiantsByDepartement() {
        // Données simulées
        List<Etudiant> etudiantList = Arrays.asList(etudiant);
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.findByDepartementIdDepart(1)).thenReturn(etudiantList);

        // Appel de la méthode
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(1);

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertEquals(1, result.size(), "La liste doit contenir 1 étudiant");
        assertEquals("Doe", result.get(0).getNomE(), "Le nom de l'étudiant doit correspondre");
        verify(departementRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).findByDepartementIdDepart(1);
    }

    @Test
    void testGetEtudiantsByDepartementNotFound() {
        // Simuler un département non trouvé
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que la récupération lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> etudiantService.getEtudiantsByDepartement(999),
                "Récupérer les étudiants d'un département inexistant doit lever une exception");
        verify(departementRepository, times(1)).findById(999);
        verify(etudiantRepository, never()).findByDepartementIdDepart(anyInt());
    }

    @Test
    void testGetEtudiantsByDepartementEmptyList() {
        // Simuler une liste vide
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.findByDepartementIdDepart(1)).thenReturn(Arrays.asList());

        // Appel de la méthode
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(1);

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertTrue(result.isEmpty(), "La liste doit être vide");
        verify(departementRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).findByDepartementIdDepart(1);
    }
}