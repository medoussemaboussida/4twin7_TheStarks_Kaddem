package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.services.EquipeServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplMockTest {

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;

    @BeforeEach
    void initData() {
        equipe = new Equipe();
        equipe.setIdEquipe(100);
        equipe.setNomEquipe("The Starks");
        equipe.setNiveau(Niveau.JUNIOR);
    }

    @Test
    void shouldReturnEmptyList_WhenNoEquipeExists() {
        when(equipeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Equipe> result = equipeService.retrieveAllEquipes();

        assertTrue(result.isEmpty(), "La liste des équipes doit être vide.");
        verify(equipeRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEquipeList_WhenEquipesExist() {
        when(equipeRepository.findAll()).thenReturn(Arrays.asList(equipe));

        List<Equipe> result = equipeService.retrieveAllEquipes();

        assertEquals(1, result.size());
        assertEquals("The Starks", result.get(0).getNomEquipe());
        verify(equipeRepository).findAll();
    }

    @Test
    void shouldAddNewEquipeSuccessfully() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.addEquipe(equipe);

        assertNotNull(result);
        assertEquals(Niveau.JUNIOR, result.getNiveau());
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void shouldDeleteEquipe_WhenEquipeExists() {
        when(equipeRepository.findById(100)).thenReturn(Optional.of(equipe));
        doNothing().when(equipeRepository).delete(equipe);

        equipeService.deleteEquipe(100);

        verify(equipeRepository).findById(100);
        verify(equipeRepository).delete(equipe);
    }

    @Test
    void shouldThrowException_WhenEquipeNotFoundForDelete() {
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> equipeService.deleteEquipe(999));
    }

    @Test
    void shouldRetrieveEquipeById() {
        when(equipeRepository.findById(100)).thenReturn(Optional.of(equipe));

        Equipe result = equipeService.retrieveEquipe(100);

        assertNotNull(result);
        assertEquals("The Starks", result.getNomEquipe());
    }

    @Test
    void shouldUpdateEquipe_WhenValidData() {
        equipe.setNomEquipe("Updated Starks");
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        Equipe result = equipeService.updateEquipe(equipe);

        assertEquals("Updated Starks", result.getNomEquipe());
        verify(equipeRepository).save(equipe);
    }
}
