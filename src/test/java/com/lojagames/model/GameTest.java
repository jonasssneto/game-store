package com.lojagames.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a classe Game.
 */
@DisplayName("Game Entity Tests")
class GameTest {
    
    private Game game;
    
    @BeforeEach
    void setUp() {
        game = new Game("Minecraft", new BigDecimal("89.90"), "Aventura", 10);
    }
    
    @Test
    @DisplayName("Should create game with valid data")
    void should_CreateGame_When_ValidDataProvided() {
        assertNotNull(game);
        assertEquals("Minecraft", game.getName());
        assertEquals(new BigDecimal("89.90"), game.getPrice());
        assertEquals("Aventura", game.getCategory());
        assertEquals(10, game.getAgeRating());
        assertTrue(game.isAvailable());
    }
    
    @Test
    @DisplayName("Should identify free games correctly")
    void should_IdentifyFreeGame_When_PriceIsZero() {
        Game freeGame = new Game("Fortnite", BigDecimal.ZERO, "Battle Royale", 12);
        assertTrue(freeGame.isFree());
        assertFalse(game.isFree());
    }
    
    @Test
    @DisplayName("Should validate age appropriateness correctly")
    void should_ValidateAge_When_CheckingAgeRating() {
        assertTrue(game.isAgeAppropriate(10));
        assertTrue(game.isAgeAppropriate(15));
        assertFalse(game.isAgeAppropriate(8));
    }
    
    @Test
    @DisplayName("Should not update price when negative value provided")
    void should_NotUpdatePrice_When_NegativeValueProvided() {
        BigDecimal originalPrice = game.getPrice();
        game.setPrice(new BigDecimal("-10.00"));
        assertEquals(originalPrice, game.getPrice());
    }
    
    @Test
    @DisplayName("Should not update age rating when invalid value provided")
    void should_NotUpdateAgeRating_When_InvalidValueProvided() {
        int originalRating = game.getAgeRating();
        game.setAgeRating(25); // Invalid rating
        assertEquals(originalRating, game.getAgeRating());
        
        game.setAgeRating(-5); // Invalid rating
        assertEquals(originalRating, game.getAgeRating());
    }
    
    @Test
    @DisplayName("Should not update name when null or empty provided")
    void should_NotUpdateName_When_NullOrEmptyProvided() {
        String originalName = game.getName();
        
        game.setName(null);
        assertEquals(originalName, game.getName());
        
        game.setName("");
        assertEquals(originalName, game.getName());
        
        game.setName("   ");
        assertEquals(originalName, game.getName());
    }
    
    @Test
    @DisplayName("Should compare games correctly using equals")
    void should_CompareGames_When_UsingEquals() {
        Game sameNameGame = new Game("Minecraft", new BigDecimal("99.90"), "Ação", 16);
        Game differentNameGame = new Game("FIFA", new BigDecimal("89.90"), "Aventura", 10);
        
        assertEquals(game, sameNameGame);
        assertNotEquals(game, differentNameGame);
        assertNotEquals(game, null);
        assertNotEquals(game, "Not a game");
    }
    
    @Test
    @DisplayName("Should generate consistent hash codes")
    void should_GenerateConsistentHashCodes_When_SameGameName() {
        Game sameNameGame = new Game("Minecraft", new BigDecimal("99.90"), "Ação", 16);
        assertEquals(game.hashCode(), sameNameGame.hashCode());
    }
    
    @Test
    @DisplayName("Should generate proper string representation")
    void should_GenerateProperString_When_ToStringCalled() {
        String gameString = game.toString();
        assertNotNull(gameString);
        assertTrue(gameString.contains("Minecraft"));
        assertTrue(gameString.contains("89.90"));
        assertTrue(gameString.contains("Aventura"));
        assertTrue(gameString.contains("10"));
    }
}
