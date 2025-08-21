package com.lojagames.repository;

import com.lojagames.model.Game;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementação em memória do GameRepository.
 */
public class InMemoryGameRepository implements GameRepository {
    
    private final Map<Long, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Game save(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        
        if (game.getId() == null) {
            game.setId(idGenerator.getAndIncrement());
        }
        
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public Optional<Game> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return games.values().stream()
                .filter(game -> name.equals(game.getName()))
                .findFirst();
    }

    @Override
    public List<Game> findAll() {
        return new ArrayList<>(games.values());
    }

    @Override
    public List<Game> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return games.values().stream()
                .filter(game -> category.equals(game.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            return new ArrayList<>();
        }
        
        return games.values().stream()
                .filter(game -> {
                    BigDecimal price = game.getPrice();
                    return price != null && 
                           price.compareTo(minPrice) >= 0 && 
                           price.compareTo(maxPrice) <= 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findByAgeRating(int maxAge) {
        return games.values().stream()
                .filter(game -> game.getAgeRating() <= maxAge)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findAvailable() {
        return games.values().stream()
                .filter(Game::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findFreeGames() {
        return games.values().stream()
                .filter(Game::isFree)
                .collect(Collectors.toList());
    }

    @Override
    public Game update(Game game) {
        if (game == null || game.getId() == null) {
            throw new IllegalArgumentException("Game and ID cannot be null");
        }
        
        if (!games.containsKey(game.getId())) {
            throw new IllegalArgumentException("Game not found with ID: " + game.getId());
        }
        
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return games.remove(id) != null;
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return games.values().stream()
                .anyMatch(game -> name.equals(game.getName()));
    }

    @Override
    public long count() {
        return games.size();
    }

    /**
     * Método utilitário para limpar todos os dados (útil para testes).
     */
    public void clear() {
        games.clear();
        idGenerator.set(1);
    }

    /**
     * Método utilitário para inicializar dados de exemplo.
     */
    public void initializeWithSampleData() {
        // Limpa dados existentes
        clear();
        
        // Adiciona jogos de exemplo
        save(new Game("Minecraft", new BigDecimal("89.90"), "Aventura", 10));
        save(new Game("FIFA 2023", new BigDecimal("199.90"), "Esporte", 0));
        save(new Game("The Sims 4", new BigDecimal("129.90"), "Simulação", 12));
        save(new Game("Grand Theft Auto V", new BigDecimal("149.90"), "Ação", 18));
        save(new Game("Fortnite", BigDecimal.ZERO, "Battle Royale", 12));
        save(new Game("Hades", new BigDecimal("79.90"), "Roguelike", 14));
        save(new Game("Stardew Valley", new BigDecimal("24.99"), "Simulação", 0));
        save(new Game("Celeste", new BigDecimal("36.90"), "Plataforma", 10));
        save(new Game("Among Us", new BigDecimal("19.99"), "Party", 7));
        save(new Game("Cyberpunk 2077", new BigDecimal("149.99"), "RPG", 18));
        save(new Game("Valorant", BigDecimal.ZERO, "FPS", 14));
        save(new Game("Rocket League", new BigDecimal("39.99"), "Esporte", 3));
    }
}
