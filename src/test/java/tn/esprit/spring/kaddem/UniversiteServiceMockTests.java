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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversiteServiceMockTests {

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    private Universite universite;
    private Departement departement;

    @BeforeEach
    void setUp() {
        // Initialisation de l'université
        universite = new Universite();
        universite.setIdUniversite(1);
        universite.setNomUniversite("Université Test");
        universite.setDepartements(new HashSet<>());

        // Initialisation du département
        departement = new Departement();
        departement.setIdDepartement(1);
        departement.setNomDepartement("Informatique");
    }

    @Test
    void testAddUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Appel de la méthode
        Universite savedUniversite = universiteService.addUniversite(universite);

        // Vérifications
        assertNotNull(savedUniversite, "L'université ajoutée ne devrait pas être null");
        assertEquals("Université Test", savedUniversite.getNomUniversite(), "Le nom devrait être 'Université Test'");
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Appel de la méthode
        Universite retrievedUniversite = universiteService.retrieveUniversite(1);

        // Vérifications
        assertNotNull(retrievedUniversite, "L'université récupérée ne devrait pas être null");
        assertEquals("Université Test", retrievedUniversite.getNomUniversite(), "Le nom devrait être 'Université Test'");
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        doNothing().when(universiteRepository).delete(universite);

        // Appel de la méthode
        universiteService.deleteUniversite(1);

        // Vérifications
        verify(universiteRepository, times(1)).findById(1);
        verify(universiteRepository, times(1)).delete(universite);
    }

    @Test
    void testRetrieveAllUniversites() {
        // Données simulées
        Universite universite2 = new Universite();
        universite2.setIdUniversite(2);
        universite2.setNomUniversite("Université B");
        universite2.setDepartements(new HashSet<>());
        List<Universite> universites = Arrays.asList(universite, universite2);
        when(universiteRepository.findAll()).thenReturn(universites);

        // Appel de la méthode
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Vérifications
        assertNotNull(result, "La liste des universités ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 universités");
        assertTrue(result.stream().anyMatch(u -> u.getNomUniversite().equals("Université Test")),
                "La liste devrait contenir 'Université Test'");
        assertTrue(result.stream().anyMatch(u -> u.getNomUniversite().equals("Université B")),
                "La liste devrait contenir 'Université B'");
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Appel de la méthode
        Universite updatedUniversite = universiteService.updateUniversite(universite);

        // Vérifications
        assertNotNull(updatedUniversite, "L'université mise à jour ne devrait pas être null");
        assertEquals("Université Test", updatedUniversite.getNomUniversite(), "Le nom devrait être 'Université Test'");
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAssignUniversiteToDepartement() {
        // Simuler le comportement des repositories
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Appel de la méthode
        universiteService.assignUniversiteToDepartement(1, 1);

        // Vérifications
        assertTrue(universite.getDepartements().contains(departement),
                "Le département devrait être assigné à l'université");
        verify(universiteRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAssignUniversiteToDepartementUniversiteNotFound() {
        // Simuler une université non trouvée
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        // Appel de la méthode
        universiteService.assignUniversiteToDepartement(999, 1);

        // Vérifications
        verify(universiteRepository, times(1)).findById(999);
        verify(departementRepository, times(1)).findById(1);
        verify(universiteRepository, never()).save(any(Universite.class));
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        // Configurer l'université avec un département
        universite.getDepartements().add(departement);
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Appel de la méthode
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        // Vérifications
        assertNotNull(result, "L'ensemble des départements ne devrait pas être null");
        assertEquals(1, result.size(), "L'ensemble devrait contenir 1 département");
        assertTrue(result.contains(departement), "L'ensemble devrait contenir le département 'Informatique'");
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveDepartementsByUniversiteNotFound() {
        // Simuler une université non trouvée
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());

        // Appel de la méthode
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(999);

        // Vérifications
        assertNull(result, "L'ensemble devrait être null si l'université n'existe pas");
        verify(universiteRepository, times(1)).findById(999);
    }
}