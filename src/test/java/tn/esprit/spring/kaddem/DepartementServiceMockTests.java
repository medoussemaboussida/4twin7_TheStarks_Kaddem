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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementServiceMockTests {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    private Departement sampleDepartement;

    @BeforeEach
    void setUp() {
        // Plus besoin de MockitoAnnotations.initMocks(this)
        sampleDepartement = new Departement();
        sampleDepartement.setIdDepart(1);
        sampleDepartement.setNomDepart("Informatique");
    }

    @Test
    void testAddDepartement() {
        when(departementRepository.save(any(Departement.class))).thenReturn(sampleDepartement);
        Departement savedDepartement = departementService.addDepartement(sampleDepartement);
        verify(departementRepository, times(1)).save(any(Departement.class));
        assertNotNull(savedDepartement, "Le département sauvegardé ne devrait pas être null");
        assertEquals(sampleDepartement.getIdDepart(), savedDepartement.getIdDepart(), "L'ID du département devrait correspondre");
        assertEquals(sampleDepartement.getNomDepart(), savedDepartement.getNomDepart(), "Le nom du département devrait correspondre");
    }

    @Test
    void testRetrieveDepartement() {
        Integer departementId = 1;
        when(departementRepository.findById(departementId)).thenReturn(Optional.of(sampleDepartement));
        Departement retrievedDepartement = departementService.retrieveDepartement(departementId);
        verify(departementRepository, times(1)).findById(departementId);
        assertNotNull(retrievedDepartement, "Le département récupéré ne devrait pas être null");
        assertEquals(sampleDepartement.getIdDepart(), retrievedDepartement.getIdDepart(), "L'ID du département devrait correspondre");
        assertEquals(sampleDepartement.getNomDepart(), retrievedDepartement.getNomDepart(), "Le nom du département devrait correspondre");
    }

    @Test
    void testRetrieveDepartementNotFound() {
        Integer departementId = 999;
        when(departementRepository.findById(departementId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> departementService.retrieveDepartement(departementId),
                "Une exception devrait être levée si le département n'est pas trouvé");
        verify(departementRepository, times(1)).findById(departementId);
    }

    @Test
    void testUpdateDepartement() {
        when(departementRepository.save(any(Departement.class))).thenReturn(sampleDepartement);
        Departement updatedDepartement = departementService.updateDepartement(sampleDepartement);
        verify(departementRepository, times(1)).save(any(Departement.class));
        assertNotNull(updatedDepartement, "Le département mis à jour ne devrait pas être null");
        assertEquals(sampleDepartement.getIdDepart(), updatedDepartement.getIdDepart(), "L'ID du département devrait correspondre");
        assertEquals(sampleDepartement.getNomDepart(), updatedDepartement.getNomDepart(), "Le nom du département devrait correspondre");
    }

    @Test
    void testDeleteDepartement() {
        Integer departementId = 1;
        when(departementRepository.findById(departementId)).thenReturn(Optional.of(sampleDepartement));
        departementService.deleteDepartement(departementId);
        verify(departementRepository, times(1)).findById(departementId);
        verify(departementRepository, times(1)).delete(sampleDepartement);
    }

    @Test
    void testDeleteDepartementNotFound() {
        Integer departementId = 999;
        when(departementRepository.findById(departementId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> departementService.deleteDepartement(departementId),
                "Une exception devrait être levée si le département n'est pas trouvé");
        verify(departementRepository, times(1)).findById(departementId);
        verify(departementRepository, never()).delete(any(Departement.class));
    }
}