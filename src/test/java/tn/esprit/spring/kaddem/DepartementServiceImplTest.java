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
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void testAddDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Appel de la méthode
        Departement result = departementService.addDepartement(departement);

        // Vérifications
        assertNotNull(result);
        assertEquals("Informatique", result.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testUpdateDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Appel de la méthode
        Departement result = departementService.updateDepartement(departement);

        // Vérifications
        assertNotNull(result);
        assertEquals(1, result.getIdDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testRetrieveDepartement() {
        // Simuler le comportement du repository
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        // Appel de la méthode
        Departement result = departementService.retrieveDepartement(1);

        // Vérifications
        assertNotNull(result);
        assertEquals("Informatique", result.getNomDepart());
        verify(departementRepository, times(1)).findById(1);
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
    void testRetrieveDepartementNotFound() {
        // Simuler un département non trouvé
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        // Vérification que la méthode lève une exception
        assertThrows(Exception.class, () -> departementService.retrieveDepartement(999));
        verify(departementRepository, times(1)).findById(999);
    }
}