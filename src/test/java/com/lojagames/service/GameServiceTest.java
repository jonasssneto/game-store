package com.lojagames.service;

import com.lojagames.model.Game;
import com.lojagames.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Testes unitários para GameService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {
    
    @Mock
    private GameRepository gameRepository;
    
    private GameService gameService;
    private Game sampleGame;
    
    @BeforeEach
    void setUp() {
        gameService = new GameService(gameRepository);
        sampleGame = new Game("Test Game", new BigDecimal("59.90"), "Action", 16);
    }
    
    @Test
    @DisplayName("Should throw exception when repository is null")
    void should_ThrowException_When_RepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new GameService(null));
    }
    
    @Test
    @DisplayName("Should create game successfully when valid data provided")
    void should_CreateGame_When_ValidDataProvided() {
        // Arrange
        when(gameRepository.existsByName("Test Game")).thenReturn(false);
        when(gameRepository.save(any(Game.class))).thenReturn(sampleGame);
        
        // Act
        Game result = gameService.createGame("Test Game", new BigDecimal("59.90"), "Action", 16);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Game", result.getName());
        verify(gameRepository).existsByName("Test Game");
        verify(gameRepository).save(any(Game.class));
    }
    
    @Test
    @DisplayName("Should throw exception when game name already exists")
    void should_ThrowException_When_GameNameAlreadyExists() {
        // Arrange
        when(gameRepository.existsByName("Test Game")).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame("Test Game", new BigDecimal("59.90"), "Action", 16));
        
        verify(gameRepository).existsByName("Test Game");
        verify(gameRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when invalid game data provided")
    void should_ThrowException_When_InvalidGameDataProvided() {
        // Test null name
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame(null, new BigDecimal("59.90"), "Action", 16));
        
        // Test empty name
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame("", new BigDecimal("59.90"), "Action", 16));
        
        // Test negative price
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame("Test", new BigDecimal("-10.00"), "Action", 16));
        
        // Test invalid age rating
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame("Test", new BigDecimal("59.90"), "Action", 25));
    }
    
    @Test
    @DisplayName("Should find game by ID successfully")
    void should_FindGame_When_ValidIdProvided() {
        // Arrange
        Long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(sampleGame));
        
        // Act
        Game result = gameService.findGameById(gameId);
        
        // Assert
        assertNotNull(result);
        assertEquals(sampleGame, result);
        verify(gameRepository).findById(gameId);
    }
    
    @Test
    @DisplayName("Should throw exception when game not found by ID")
    void should_ThrowException_When_GameNotFoundById() {
        // Arrange
        Long gameId = 999L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> gameService.findGameById(gameId));
        verify(gameRepository).findById(gameId);
    }
    
    @Test
    @DisplayName("Should list available games")
    void should_ListAvailableGames_When_Called() {
        // Arrange
        List<Game> expectedGames = Arrays.asList(sampleGame);
        when(gameRepository.findAvailable()).thenReturn(expectedGames);
        
        // Act
        List<Game> result = gameService.listAvailableGames();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleGame, result.get(0));
        verify(gameRepository).findAvailable();
    }
    
    @Test
    @DisplayName("Should filter games by category")
    void should_FilterGamesByCategory_When_CategoryProvided() {
        // Arrange
        String category = "Action";
        List<Game> expectedGames = Arrays.asList(sampleGame);
        when(gameRepository.findByCategory(category)).thenReturn(expectedGames);
        
        // Act
        List<Game> result = gameService.filterGamesByCategory(category);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByCategory(category);
    }
    
    @Test
    @DisplayName("Should return all games when null category provided")
    void should_ReturnAllGames_When_NullCategoryProvided() {
        // Arrange
        List<Game> expectedGames = Arrays.asList(sampleGame);
        when(gameRepository.findAvailable()).thenReturn(expectedGames);
        
        // Act
        List<Game> result = gameService.filterGamesByCategory(null);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedGames, result);
        verify(gameRepository).findAvailable();
        verify(gameRepository, never()).findByCategory(any());
    }
    
    @Test
    @DisplayName("Should check if customer can buy game")
    void should_CheckCustomerCanBuyGame_When_ValidGameAndAge() {
        // Arrange
        sampleGame.setAvailable(true);
        
        // Act & Assert
        assertTrue(gameService.canCustomerBuyGame(sampleGame, 18));
        assertTrue(gameService.canCustomerBuyGame(sampleGame, 16));
        assertFalse(gameService.canCustomerBuyGame(sampleGame, 15));
        assertFalse(gameService.canCustomerBuyGame(null, 18));
    }
    
    @Test
    @DisplayName("Should delete game successfully")
    void should_DeleteGame_When_ValidIdProvided() {
        // Arrange
        Long gameId = 1L;
        when(gameRepository.deleteById(gameId)).thenReturn(true);
        
        // Act & Assert
        assertDoesNotThrow(() -> gameService.deleteGame(gameId));
        verify(gameRepository).deleteById(gameId);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent game")
    void should_ThrowException_When_DeletingNonExistentGame() {
        // Arrange
        Long gameId = 999L;
        when(gameRepository.deleteById(gameId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> gameService.deleteGame(gameId));
        verify(gameRepository).deleteById(gameId);
    }
}
