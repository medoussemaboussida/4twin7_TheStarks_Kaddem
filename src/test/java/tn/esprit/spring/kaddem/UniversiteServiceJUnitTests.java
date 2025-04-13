package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversiteServiceJUnitTests {

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService; // Remplacez par votre implémentation réelle si différente

    private Universite universite;
    private Departement departement;

    @BeforeEach
    void setUp() {
        universite = new Universite();
        universite.setIdUniv(1);
        universite.setNomUniv("Test Universite");
        universite.setDepartements(new HashSet<>());

        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");
    }

    @Test
    void testRetrieveAllUniversites() {
        // Arrange
        List<Universite> universites = Arrays.asList(universite);
        when(universiteRepository.findAll()).thenReturn(universites);

        // Act
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Universite", result.get(0).getNomUniv());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveUniversite() {
        // Arrange
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Act
        Universite result = universiteService.retrieveUniversite(1);

        // Assert
        assertNotNull(result);
        assertEquals("Test Universite", result.getNomUniv());
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveUniversiteNotFound() {
        // Arrange
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> universiteService.retrieveUniversite(999));
        verify(universiteRepository, times(1)).findById(999);
    }

    @Test
    void testAddUniversite() {
        // Arrange
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Act
        Universite result = universiteService.addUniversite(universite);

        // Assert
        assertNotNull(result);
        assertEquals("Test Universite", result.getNomUniv());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testDeleteUniversite() {
        // Arrange
        doNothing().when(universiteRepository).deleteById(anyInt());

        // Act
        universiteService.deleteUniversite(1);

        // Assert
        verify(universiteRepository, times(1)).deleteById(1);
    }

    @Test
    void testUpdateUniversite() {
        // Arrange
        Universite updatedUniversite = new Universite();
        updatedUniversite.setIdUniv(1);
        updatedUniversite.setNomUniv("Updated Universite");
        when(universiteRepository.save(any(Universite.class))).thenReturn(updatedUniversite);

        // Act
        Universite result = universiteService.updateUniversite(updatedUniversite);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Universite", result.getNomUniv());
        verify(universiteRepository, times(1)).save(updatedUniversite);
    }

    @Test
    void testAssignUniversiteToDepartement() {
        // Arrange
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Act
        universiteService.assignUniversiteToDepartement(1, 1);

        // Assert
        verify(universiteRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(universiteRepository, times(1)).save(universite);
        assertTrue(universite.getDepartements().contains(departement));
    }

    @Test
    void testAssignUniversiteToDepartementUniversiteNotFound() {
        // Arrange
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> universiteService.assignUniversiteToDepartement(999, 1));
        verify(universiteRepository, times(1)).findById(999);
        verify(departementRepository, never()).findById(anyInt());
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        // Arrange
        universite.getDepartements().add(departement);
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Act
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Informatique", result.iterator().next().getNomDepart());
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveDepartementsByUniversiteNotFound() {
        // Arrange
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> universiteService.retrieveDepartementsByUniversite(999));
        verify(universiteRepository, times(1)).findById(999);
    }
}