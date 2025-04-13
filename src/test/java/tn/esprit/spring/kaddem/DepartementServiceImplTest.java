package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.services.DepartementServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementServiceImplTest {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    private Departement departement;

    @BeforeEach
    void setUp() {
        // Initialisation d'un objet Departement pour les tests
        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");
    }

    @Test
    void testRetrieveAllDepartements() {
        // Données simulées
        List<Departement> departementList = Arrays.asList(departement, new Departement());
        when(departementRepository.findAll()).thenReturn(departementList);

        // Appel de la méthode
        List<Departement> result = departementService.retrieveAllDepartements();

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertEquals(2, result.size(), "La liste doit contenir 2 départements");
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllDepartementsEmptyList() {
        // Simuler une liste vide
        when(departementRepository.findAll()).thenReturn(Arrays.asList());

        // Appel de la méthode
        List<Departement> result = departementService.retrieveAllDepartements();

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertTrue(result.isEmpty(), "La liste doit être vide");
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void testAddDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Appel de la méthode
        Departement result = departementService.addDepartement(departement);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        assertEquals("Informatique", result.getNomDepart(), "Le nom du département doit correspondre");
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testAddDepartementWithNullName() {
        // Créer un département avec un nom null
        Departement invalidDepartement = new Departement();
        invalidDepartement.setIdDepart(2);
        invalidDepartement.setNomDepart(null);
        when(departementRepository.save(any(Departement.class))).thenReturn(invalidDepartement);

        // Appel de la méthode
        Departement result = departementService.addDepartement(invalidDepartement);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        assertNull(result.getNomDepart(), "Le nom du département doit être null");
        verify(departementRepository, times(1)).save(invalidDepartement);
    }

    @Test
    void testAddDepartementWithEmptyName() {
        // Créer un département avec un nom vide
        Departement invalidDepartement = new Departement();
        invalidDepartement.setIdDepart(3);
        invalidDepartement.setNomDepart("");
        when(departementRepository.save(any(Departement.class))).thenReturn(invalidDepartement);

        // Appel de la méthode
        Departement result = departementService.addDepartement(invalidDepartement);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        assertEquals("", result.getNomDepart(), "Le nom du département doit être vide");
        verify(departementRepository, times(1)).save(invalidDepartement);
    }

    @Test
    void testUpdateDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Appel de la méthode
        Departement result = departementService.updateDepartement(departement);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        assertEquals(1, result.getIdDepart(), "L'ID du département doit correspondre");
        assertEquals("Informatique", result.getNomDepart(), "Le nom du département doit correspondre");
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testUpdateDepartementNotFound() {
        // Simuler un département non existant
        when(departementRepository.findById(1)).thenReturn(Optional.empty());
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Appel de la méthode
        Departement result = departementService.updateDepartement(departement);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testRetrieveDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        // Appel de la méthode
        Departement result = departementService.retrieveDepartement(1);

        // Vérifications
        assertNotNull(result, "Le département ne doit pas être null");
        assertEquals("Informatique", result.getNomDepart(), "Le nom du département doit correspondre");
        verify(departementRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveDepartementInvalidId() {
        // Simuler un ID invalide
        when(departementRepository.findById(-1)).thenReturn(Optional.empty());

        // Vérification que la méthode lève NoSuchElementException
        assertThrows(java.util.NoSuchElementException.class,
                () -> departementService.retrieveDepartement(-1),
                "Récupérer un département avec un ID négatif doit lever NoSuchElementException");
        verify(departementRepository, times(1)).findById(-1);
    }

    @Test
    void testRetrieveDepartementNotFound() {
        // Simuler un département non trouvé
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        // Vérification que la méthode lève NoSuchElementException
        assertThrows(java.util.NoSuchElementException.class,
                () -> departementService.retrieveDepartement(999),
                "Récupérer un département inexistant doit lever NoSuchElementException");
        verify(departementRepository, times(1)).findById(999);
    }

    @Test
    void testDeleteDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        doNothing().when(departementRepository).delete(departement);

        // Appel de la méthode
        departementService.deleteDepartement(1);

        // Vérifications
        verify(departementRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).delete(departement);
    }

    @Test
    void testDeleteDepartementNotFound() {
        // Simuler un département non trouvé
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        // Vérifier que la suppression lève une exception
        assertThrows(javax.persistence.EntityNotFoundException.class,
                () -> departementService.deleteDepartement(999),
                "Supprimer un département non existant doit lever une exception");
        verify(departementRepository, times(1)).findById(999);
        verify(departementRepository, never()).delete(any(Departement.class));
    }

    @Test
    void testAddDepartementConcurrent() {
        // Simuler un conflit d'ajout
        Departement departement2 = new Departement();
        departement2.setIdDepart(1); // Même ID
        departement2.setNomDepart("Mathématiques");

        when(departementRepository.save(departement)).thenThrow(new javax.persistence.EntityExistsException("Département existe déjà"));

        // Vérifier que l'ajout lève une exception
        assertThrows(javax.persistence.EntityExistsException.class,
                () -> departementService.addDepartement(departement),
                "Ajouter un département avec un ID existant doit lever une exception");
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testRetrieveAllDepartementsLargeList() {
        // Simuler une grande liste de départements
        List<Departement> largeList = Arrays.asList(new Departement[1000]);
        for (int i = 0; i < 1000; i++) {
            Departement dep = new Departement();
            dep.setIdDepart(i + 1);
            dep.setNomDepart("Departement" + i);
            largeList.set(i, dep);
        }
        when(departementRepository.findAll()).thenReturn(largeList);

        // Appel de la méthode
        List<Departement> result = departementService.retrieveAllDepartements();

        // Vérifications
        assertNotNull(result, "La liste ne doit pas être null");
        assertEquals(1000, result.size(), "La liste doit contenir 1000 départements");
        verify(departementRepository, times(1)).findAll();
    }
}