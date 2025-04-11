package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.ContratServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratServiceImplTest {

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ContratServiceImpl contratService;

    private Contrat contrat;
    private Etudiant etudiant;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        // Initialisation des objets
        contrat = new Contrat();
        contrat.setIdContrat(1); // ID défini manuellement pour les mocks
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)); // +30 jours
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(1000);

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1); // ID défini manuellement pour les mocks
        etudiant.setNomE("Oussema");
        etudiant.setPrenomE("Med");
        etudiant.setContrats(new HashSet<>());

        startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60); // -60 jours
        endDate = new Date();
    }

    @Test
    void testRetrieveAllContrats() {
        // Données simulées
        List<Contrat> contrats = Arrays.asList(contrat, new Contrat());
        when(contratRepository.findAll()).thenReturn(contrats);

        // Appel de la méthode
        List<Contrat> result = contratService.retrieveAllContrats();

        // Vérifications
        assertNotNull(result, "La liste des contrats ne devrait pas être null");
        assertEquals(2, result.size(), "La liste devrait contenir 2 contrats");
        verify(contratRepository, times(1)).findAll();
    }

    @Test
    void testAddContrat() {
        // Simuler le comportement du repository
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        // Appel de la méthode
        Contrat result = contratService.addContrat(contrat);

        // Vérifications
        assertNotNull(result, "Le contrat ajouté ne devrait pas être null");
        assertEquals(Specialite.IA, result.getSpecialite(), "La spécialité devrait être IA");
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testUpdateContrat() {
        // Simuler le comportement du repository
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        // Appel de la méthode
        Contrat result = contratService.updateContrat(contrat);

        // Vérifications
        assertNotNull(result, "Le contrat mis à jour ne devrait pas être null");
        assertEquals(1, result.getIdContrat(), "L'ID devrait être 1");
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testRetrieveContrat() {
        // Simuler le comportement du repository
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));

        // Appel de la méthode
        Contrat result = contratService.retrieveContrat(1);

        // Vérifications
        assertNotNull(result, "Le contrat récupéré ne devrait pas être null");
        assertEquals(Specialite.IA, result.getSpecialite(), "La spécialité devrait être IA");
        verify(contratRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveContratNotFound() {
        // Simuler un contrat non trouvé
        when(contratRepository.findById(999)).thenReturn(Optional.empty());

        // Appel de la méthode
        Contrat result = contratService.retrieveContrat(999);

        // Vérifications
        assertNull(result, "Le résultat devrait être null pour un contrat inexistant");
        verify(contratRepository, times(1)).findById(999);
    }

    @Test
    void testRemoveContrat() {
        // Simuler le comportement du repository
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        doNothing().when(contratRepository).delete(contrat);

        // Appel de la méthode
        contratService.removeContrat(1);

        // Vérifications
        verify(contratRepository, times(1)).findById(1);
        verify(contratRepository, times(1)).delete(contrat);
    }

    @Test
    void testAffectContratToEtudiantSuccess() {
        // Simuler le comportement des repositories
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
        when(etudiantRepository.findByNomEAndPrenomE("Oussema", "Med")).thenReturn(etudiant);
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        // Appel de la méthode
        Contrat result = contratService.affectContratToEtudiant(1, "Oussema", "Med");

        // Vérifications
        assertNotNull(result, "Le contrat affecté ne devrait pas être null");
        assertEquals(etudiant, result.getEtudiant(), "L'étudiant devrait être affecté au contrat");
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testAffectContratToEtudiantTooManyActiveContrats() {
        // Simuler un étudiant avec 5 contrats actifs
        for (int i = 0; i < 5; i++) {
            Contrat c = new Contrat();
            c.setIdContrat(i + 2);
            c.setArchive(false);
            etudiant.getContrats().add(c);
        }
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
        when(etudiantRepository.findByNomEAndPrenomE("Oussema", "Med")).thenReturn(etudiant);

        // Appel de la méthode
        Contrat result = contratService.affectContratToEtudiant(1, "Oussema", "Med");

        // Vérifications
        assertNotNull(result, "Le contrat ne devrait pas être null");
        assertNull(result.getEtudiant(), "L'étudiant ne devrait pas être affecté");
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testNbContratsValides() {
        // Simuler le comportement du repository
        when(contratRepository.getnbContratsValides(startDate, endDate)).thenReturn(5);

        // Appel de la méthode
        Integer result = contratService.nbContratsValides(startDate, endDate);

        // Vérifications
        assertNotNull(result, "Le résultat ne devrait pas être null");
        assertEquals(5, result, "Le nombre de contrats valides devrait être 5");
        verify(contratRepository, times(1)).getnbContratsValides(startDate, endDate);
    }

    @Test
    void testRetrieveAndUpdateStatusContrat() {
        // Simuler des contrats
        Contrat contratToArchive = new Contrat();
        contratToArchive.setIdContrat(2);
        contratToArchive.setDateDebutContrat(new Date());
        contratToArchive.setDateFinContrat(new Date()); // Date de fin = aujourd'hui
        contratToArchive.setArchive(false);

        List<Contrat> contrats = Arrays.asList(contrat, contratToArchive);
        when(contratRepository.findAll()).thenReturn(contrats);
        when(contratRepository.save(any(Contrat.class))).thenReturn(contratToArchive);

        // Appel de la méthode
        contratService.retrieveAndUpdateStatusContrat();

        // Vérifications
        verify(contratRepository, times(1)).findAll();
        verify(contratRepository, times(1)).save(contratToArchive);
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        // Simuler des contrats
        Contrat contratIA = new Contrat(1, new Date(), new Date(), Specialite.IA, false, 1000);
        Contrat contratCloud = new Contrat(2, new Date(), new Date(), Specialite.CLOUD, false, 2000);
        List<Contrat> contrats = Arrays.asList(contratIA, contratCloud);
        when(contratRepository.findAll()).thenReturn(contrats);

        // Dates : différence de 30 jours (~1 mois)
        float expectedChiffreAffaire = (1 * 300) + (1 * 400); // 300 pour IA + 400 pour CLOUD

        // Appel de la méthode
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Vérifications
        assertEquals(expectedChiffreAffaire, result, 0.01,
                "Le chiffre d'affaires calculé devrait correspondre à la valeur attendue");
        verify(contratRepository, times(1)).findAll();
    }
}