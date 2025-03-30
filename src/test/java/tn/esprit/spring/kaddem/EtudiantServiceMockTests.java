package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class EtudiantServiceMockTests {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @BeforeEach
    void setUp() {
        // Plus besoin de MockitoAnnotations.initMocks(this)
    }

    @Test
    void testAddEtudiant() {
        Etudiant sampleEtudiant = new Etudiant();
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(sampleEtudiant);
        Etudiant savedEtudiant = etudiantService.addEtudiant(sampleEtudiant);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
        assertEquals(sampleEtudiant, savedEtudiant);
    }

    @Test
    void testRetrieveEtudiant() {
        Integer etudiantId = 1;
        Etudiant sampleEtudiant = new Etudiant();
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(sampleEtudiant));
        Etudiant retrievedEtudiant = etudiantService.retrieveEtudiant(etudiantId);
        verify(etudiantRepository, times(1)).findById(etudiantId);
        assertEquals(sampleEtudiant, retrievedEtudiant);
    }

    @Test
    void testUpdateEtudiant() {
        Etudiant sampleEtudiant = new Etudiant();
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(sampleEtudiant);
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(sampleEtudiant);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
        assertEquals(sampleEtudiant, updatedEtudiant);
    }

    @Test
    void testRemoveEtudiant() {
        Integer etudiantId = 1;
        Etudiant sampleEtudiant = new Etudiant();
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(sampleEtudiant));
        etudiantService.removeEtudiant(etudiantId);
        verify(etudiantRepository, times(1)).delete(sampleEtudiant);
    }
}