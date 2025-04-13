package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.ContratServiceImpl;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratServiceMockTests {

    @InjectMocks
    ContratServiceImpl contratService;

    @Mock
    ContratRepository contratRepository;

    @Mock
    EtudiantRepository etudiantRepository;

    Contrat contrat;
    Etudiant etudiant;

    @BeforeEach
    void setUp() {
        // Initialisation du contrat
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date());
        contrat.setArchive(false);

        // Initialisation de l'étudiant
        etudiant = new Etudiant();
        etudiant.setNomE("Nom");
        etudiant.setPrenomE("Prenom");
        etudiant.setContrats(new HashSet<>());
    }

    // Tests existants
    @Test
    void retrieveContrat() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        Contrat retrievedContrat = contratService.retrieveContrat(1);
        assertNotNull(retrievedContrat);
        assertEquals(1, retrievedContrat.getIdContrat());
    }

    @Test
    void retrieveAllContrats() {
        List<Contrat> contratList = Collections.singletonList(contrat);
        when(contratRepository.findAll()).thenReturn(contratList);
        List<Contrat> allContrats = contratService.retrieveAllContrats();
        assertEquals(1, allContrats.size());
    }

    @Test
    void updateContrat() {
        when(contratRepository.save(contrat)).thenReturn(contrat);
        Contrat updatedContrat = contratService.updateContrat(contrat);
        assertNotNull(updatedContrat);
        assertEquals(1, updatedContrat.getIdContrat());
    }

    @Test
    void addContrat() {
        when(contratRepository.save(contrat)).thenReturn(contrat);
        Contrat addedContrat = contratService.addContrat(contrat);
        assertNotNull(addedContrat);
        assertEquals(1, addedContrat.getIdContrat());
    }

    @Test
    void deleteContrat() {
        Integer contratId = 1;
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));
        doNothing().when(contratRepository).delete(contrat);
        contratService.removeContrat(contratId);
        verify(contratRepository, times(1)).delete(contrat);
    }

    // Nouveaux tests pour les méthodes non couvertes

    @Test
    void affectContratToEtudiant_success() {
        // Préparer les mocks
        when(etudiantRepository.findByNomEAndPrenomE("Nom", "Prenom")).thenReturn(etudiant);
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
        when(contratRepository.save(contrat)).thenReturn(contrat);

        // Appeler la méthode
        Contrat result = contratService.affectContratToEtudiant(1, "Nom", "Prenom");

        // Vérifications
        assertNotNull(result);
        assertEquals(etudiant, result.getEtudiant());
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void affectContratToEtudiant_maxContratsReached() {
        // Simuler un étudiant avec 5 contrats actifs
        Contrat contratActif = new Contrat();
        contratActif.setArchive(true); // Considéré comme actif dans la logique (à corriger dans le code)
        etudiant.getContrats().add(contratActif);
        etudiant.getContrats().add(contratActif);
        etudiant.getContrats().add(contratActif);
        etudiant.getContrats().add(contratActif);
        etudiant.getContrats().add(contratActif);

        // Préparer les mocks
        when(etudiantRepository.findByNomEAndPrenomE("Nom", "Prenom")).thenReturn(etudiant);
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);

        // Appeler la méthode
        Contrat result = contratService.affectContratToEtudiant(1, "Nom", "Prenom");

        // Vérifications
        assertNotNull(result);
        assertNull(result.getEtudiant()); // L'étudiant ne doit pas être affecté
        verify(contratRepository, never()).save(contrat);
    }

    @Test
    void nbContratsValides() {
        Date startDate = new Date();
        Date endDate = new Date();
        when(contratRepository.getnbContratsValides(startDate, endDate)).thenReturn(5);

        Integer result = contratService.nbContratsValides(startDate, endDate);

        assertEquals(5, result);
        verify(contratRepository, times(1)).getnbContratsValides(startDate, endDate);
    }

    @Test
    void retrieveAndUpdateStatusContrat_expiringIn15Days() {
        // Simuler un contrat qui expire dans 15 jours
        Date dateFin = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15));
        contrat.setDateFinContrat(dateFin);
        List<Contrat> contrats = Collections.singletonList(contrat);

        when(contratRepository.findAll()).thenReturn(contrats);

        // Appeler la méthode
        contratService.retrieveAndUpdateStatusContrat();

        // Vérifications
        verify(contratRepository, never()).save(contrat); // Pas d'archivage
    }

    @Test
    void retrieveAndUpdateStatusContrat_expired() {
        // Simuler un contrat qui expire aujourd'hui
        Date dateFin = new Date();
        contrat.setDateFinContrat(dateFin);
        List<Contrat> contrats = Collections.singletonList(contrat);

        when(contratRepository.findAll()).thenReturn(contrats);
        when(contratRepository.save(contrat)).thenReturn(contrat);

        // Appeler la méthode
        contratService.retrieveAndUpdateStatusContrat();

        // Vérifications
        assertTrue(contrat.getArchive());
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void getChiffreAffaireEntreDeuxDates() {
        // Simuler des contrats
        Contrat contratIA = new Contrat();
        contratIA.setSpecialite(Specialite.IA);
        Contrat contratCloud = new Contrat();
        contratCloud.setSpecialite(Specialite.CLOUD);
        List<Contrat> contrats = Arrays.asList(contratIA, contratCloud);

        when(contratRepository.findAll()).thenReturn(contrats);

        // Simuler une période de 30 jours (1 mois)
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + TimeUnit.DAYS.toMillis(30));

        // Appeler la méthode
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Vérifications (1 mois * (300 pour IA + 400 pour CLOUD))
        assertEquals(700.0f, result, 0.01f);
    }
}