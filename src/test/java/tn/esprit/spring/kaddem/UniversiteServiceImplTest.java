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
class UniversiteServiceImplTest {

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
        // Initialisation des objets
        universite = new Universite();
        universite.setIdUniv(1); // ID défini manuellement pour les mocks
        universite.setNomUniv("Université de Tunis");
        universite.setDepartements(new HashSet<>());

        departement = new Departement();
        departement.setIdDepart(1); // ID défini manuellement pour les mocks
        departement.setNomDepart("Informatique");
    }

    @Test
    void testRetrieveAllUniversites() {
        // Données simulées
        List<Universite> universites = Arrays.asList(universite, new Universite("Université de Carthage"));
        when(universiteRepository.findAll()).thenReturn(universites);

        // Appel de la méthode
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Vérifications
        assertNotNull(result, "La liste des universités ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 universités");
        assertTrue(result.stream().anyMatch(u -> u.getNomUniv().equals("Université de Tunis")),
                "La liste devrait contenir l'université 'Université de Tunis'");
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testAddUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Appel de la méthode
        Universite result = universiteService.addUniversite(universite);

        // Vérifications
        assertNotNull(result, "L'université ajoutée ne devrait pas être null");
        assertEquals("Université de Tunis", result.getNomUniv(), "Le nom devrait être 'Université de Tunis'");
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testUpdateUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        // Appel de la méthode
        Universite result = universiteService.updateUniversite(universite);

        // Vérifications
        assertNotNull(result, "L'université mise à jour ne devrait pas être null");
        assertEquals("Université de Tunis", result.getNomUniv(), "Le nom devrait être 'Université de Tunis'");
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveUniversite() {
        // Simuler le comportement du repository
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Appel de la méthode
        Universite result = universiteService.retrieveUniversite(1);

        // Vérifications
        assertNotNull(result, "L'université récupérée ne devrait pas être null");
        assertEquals("Université de Tunis", result.getNomUniv(), "Le nom devrait être 'Université de Tunis'");
        assertEquals(1, result.getIdUniv(), "L'ID devrait être 1");
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveUniversiteNotFound() {
        // Simuler une université non trouvée
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());

        // Appel de la méthode avec un ID inexistant
        assertThrows(NoSuchElementException.class, () -> universiteService.retrieveUniversite(999),
                "Une exception devrait être levée pour une université inexistante");
        verify(universiteRepository, times(1)).findById(999);
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
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        // Préparer des données simulées
        universite.getDepartements().add(departement);
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        // Appel de la méthode
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        // Vérifications
        assertNotNull(result, "La liste des départements ne devrait pas être null");
        assertEquals(1, result.size(), "Il devrait y avoir 1 département");
        assertTrue(result.stream().anyMatch(d -> d.getNomDepart().equals("Informatique")),
                "Le département devrait être 'Informatique'");
        verify(universiteRepository, times(1)).findById(1);
    }
}