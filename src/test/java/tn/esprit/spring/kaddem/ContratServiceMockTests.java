package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.ContratServiceImpl;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ContratServiceMockTests {

    @InjectMocks
    ContratServiceImpl contratService;

    ContratRepository contratRepository = Mockito.mock(ContratRepository.class);
    EtudiantRepository etudiantRepository = Mockito.mock(EtudiantRepository.class);


    Contrat contrat;

    @BeforeEach
    void setUp() {
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(new Date());
        contrat.setDateFinContrat(new Date());
    }

    @Test
    void retrieveContrat() {
        when(contratRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(contrat));
        Contrat retrievedContrat = contratService.retrieveContrat(1);
        Assertions.assertNotNull(retrievedContrat);
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
        Assertions.assertNotNull(updatedContrat);
    }

    @Test
    void addContrat() {
        when(contratRepository.save(contrat)).thenReturn(contrat);
        Contrat addedContrat = contratService.addContrat(contrat);
        Assertions.assertNotNull(addedContrat);
    }

    @Test
    void deleteContrat() {
        Integer contratId = 1;

        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));
        Mockito.doNothing().when(contratRepository).delete(contrat);

        contratService.removeContrat(contratId);
    }




}