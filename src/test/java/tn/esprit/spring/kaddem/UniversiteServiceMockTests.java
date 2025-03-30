package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversiteServiceMockTests {

    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    @BeforeEach
    void setUp() {
        // Plus besoin de MockitoAnnotations.initMocks(this)
    }

    @Test
    void testAddUniversite() {
        Universite sampleUniversite = new Universite();
        when(universiteRepository.save(any(Universite.class))).thenReturn(sampleUniversite);
        Universite savedUniversite = universiteService.addUniversite(sampleUniversite);
        verify(universiteRepository, times(1)).save(any(Universite.class));
        assertEquals(sampleUniversite, savedUniversite);
    }

    @Test
    void testRetrieveUniversite() {
        Integer universiteId = 1;
        Universite sampleUniversite = new Universite();
        when(universiteRepository.findById(universiteId)).thenReturn(Optional.of(sampleUniversite));
        Universite retrievedUniversite = universiteService.retrieveUniversite(universiteId);
        verify(universiteRepository, times(1)).findById(universiteId);
        assertEquals(sampleUniversite, retrievedUniversite);
    }

    @Test
    void testDeleteUniversite() {
        Integer universiteId = 1;
        Universite sampleUniversite = new Universite();
        when(universiteRepository.findById(universiteId)).thenReturn(Optional.of(sampleUniversite));
        universiteService.deleteUniversite(universiteId);
        verify(universiteRepository, times(1)).delete(sampleUniversite);
    }
}