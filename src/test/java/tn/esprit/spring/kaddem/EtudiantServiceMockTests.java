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

import java.util.*;

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
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Doe"); // Utilisé dans tous les tests
        etudiant.setPrenomE("John");
        etudiant.setEquipes(new ArrayList<>());

        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");

        contrat = new Contrat();
        contrat.setIdContrat(1);

        equipe = new Equipe();
        equipe.setIdEquipe(1);
        equipe.setEtudiants(new HashSet<>());
    }

    @Test
    void testRetrieveAllEtudiants() {
        List<Etudiant> etudiantList = Arrays.asList(etudiant);
        when(etudiantRepository.findAll()).thenReturn(etudiantList);

        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getNomE());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllEtudiantsEmptyList() {
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList());

        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testAddEtudiant() {
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        Etudiant result = etudiantService.addEtudiant(etudiant);

        assertNotNull(result);
        assertEquals("Doe", result.getNomE());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testAddEtudiantWithNullName() {
        Etudiant invalidEtudiant = new Etudiant();
        invalidEtudiant.setIdEtudiant(2);
        invalidEtudiant.setNomE(null);
        invalidEtudiant.setPrenomE("Jane");
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(invalidEtudiant);

        Etudiant result = etudiantService.addEtudiant(invalidEtudiant);

        assertNotNull(result);
        assertNull(result.getNomE());
        verify(etudiantRepository, times(1)).save(invalidEtudiant);
    }

    @Test
    void testAddEtudiantWithEmptyName() {
        Etudiant invalidEtudiant = new Etudiant();
        invalidEtudiant.setIdEtudiant(3);
        invalidEtudiant.setNomE("");
        invalidEtudiant.setPrenomE("Jane");
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(invalidEtudiant);

        Etudiant result = etudiantService.addEtudiant(invalidEtudiant);

        assertNotNull(result);
        assertEquals("", result.getNomE());
        verify(etudiantRepository, times(1)).save(invalidEtudiant);
    }

    @Test
    void testRetrieveEtudiant() {
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        Etudiant result = etudiantService.retrieveEtudiant(1);

        assertNotNull(result);
        assertEquals("Doe", result.getNomE()); // Corrigé ici
        verify(etudiantRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> etudiantService.retrieveEtudiant(999));
        verify(etudiantRepository, times(1)).findById(999);
    }

    @Test
    void testRetrieveEtudiantInvalidId() {
        when(etudiantRepository.findById(-1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> etudiantService.retrieveEtudiant(-1));
        verify(etudiantRepository, times(1)).findById(-1);
    }

    @Test
    void testUpdateEtudiant() {
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        Etudiant result = etudiantService.updateEtudiant(etudiant);

        assertNotNull(result);
        assertEquals("Doe", result.getNomE()); // Corrigé ici aussi
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testUpdateEtudiantNotFound() {
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        Etudiant result = etudiantService.updateEtudiant(etudiant);

        assertNotNull(result);
        assertEquals("Doe", result.getNomE());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testRemoveEtudiant() {
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        doNothing().when(etudiantRepository).delete(etudiant);

        etudiantService.removeEtudiant(1);

        verify(etudiantRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).delete(etudiant);
    }

    @Test
    void testRemoveEtudiantNotFound() {
        when(etudiantRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> etudiantService.removeEtudiant(999));
        verify(etudiantRepository, times(1)).findById(999);
    }
}
