package com.lojagames.service;

import com.lojagames.model.Game;
import com.lojagames.repository.GameRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio relacionada aos jogos.
 */
public class GameService {
    
    private final GameRepository gameRepository;

    /**
     * Construtor com injeção de dependência.
     * 
     * @param gameRepository repositório de jogos
     */
    public GameService(GameRepository gameRepository) {
        if (gameRepository == null) {
            throw new IllegalArgumentException("GameRepository cannot be null");
        }
        this.gameRepository = gameRepository;
    }

    /**
     * Cria um novo jogo.
     * 
     * @param name nome do jogo
     * @param price preço do jogo
     * @param category categoria do jogo
     * @param ageRating classificação etária
     * @return jogo criado
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public Game createGame(String name, BigDecimal price, String category, int ageRating) {
        validateGameData(name, price, category, ageRating);
        
        if (gameRepository.existsByName(name)) {
            throw new IllegalArgumentException("Game with name '" + name + "' already exists");
        }
        
        Game game = new Game(name, price, category, ageRating);
        return gameRepository.save(game);
    }

    /**
     * Busca um jogo por ID.
     * 
     * @param id ID do jogo
     * @return jogo encontrado
     * @throws IllegalArgumentException se o jogo não for encontrado
     */
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + id));
    }

    /**
     * Busca um jogo por nome.
     * 
     * @param name nome do jogo
     * @return Optional contendo o jogo se encontrado
     */
    public Optional<Game> findGameByName(String name) {
        return gameRepository.findByName(name);
    }

    /**
     * Lista todos os jogos disponíveis.
     * 
     * @return lista de jogos disponíveis
     */
    public List<Game> listAvailableGames() {
        return gameRepository.findAvailable();
    }

    /**
     * Lista todos os jogos ordenados por preço.
     * 
     * @param ascending true para ordem crescente, false para decrescente
     * @return lista de jogos ordenados por preço
     */
    public List<Game> listGamesSortedByPrice(boolean ascending) {
        List<Game> games = gameRepository.findAvailable();
        Comparator<Game> comparator = Comparator.comparing(Game::getPrice);
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return games.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Filtra jogos por categoria.
     * 
     * @param category categoria dos jogos
     * @return lista de jogos da categoria
     */
    public List<Game> filterGamesByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return listAvailableGames();
        }
        return gameRepository.findByCategory(category);
    }

    /**
     * Filtra jogos por faixa de preço.
     * 
     * @param minPrice preço mínimo
     * @param maxPrice preço máximo
     * @return lista de jogos na faixa de preço
     */
    public List<Game> filterGamesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("9999.99");
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        
        return gameRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Filtra jogos apropriados para uma idade.
     * 
     * @param customerAge idade do cliente
     * @return lista de jogos apropriados
     */
    public List<Game> filterGamesForAge(int customerAge) {
        if (customerAge < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        
        return gameRepository.findByAgeRating(customerAge);
    }

    /**
     * Lista jogos gratuitos.
     * 
     * @return lista de jogos gratuitos
     */
    public List<Game> listFreeGames() {
        return gameRepository.findFreeGames();
    }

    /**
     * Atualiza informações de um jogo.
     * 
     * @param gameId ID do jogo
     * @param name novo nome (opcional)
     * @param price novo preço (opcional)
     * @param category nova categoria (opcional)
     * @param ageRating nova classificação (opcional)
     * @return jogo atualizado
     */
    public Game updateGame(Long gameId, String name, BigDecimal price, 
                          String category, Integer ageRating) {
        Game existingGame = findGameById(gameId);
        
        if (name != null && !name.trim().isEmpty()) {
            if (!name.equals(existingGame.getName()) && gameRepository.existsByName(name)) {
                throw new IllegalArgumentException("Game with name '" + name + "' already exists");
            }
            existingGame.setName(name);
        }
        
        if (price != null) {
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            existingGame.setPrice(price);
        }
        
        if (category != null && !category.trim().isEmpty()) {
            existingGame.setCategory(category);
        }
        
        if (ageRating != null) {
            if (ageRating < 0 || ageRating > 18) {
                throw new IllegalArgumentException("Age rating must be between 0 and 18");
            }
            existingGame.setAgeRating(ageRating);
        }
        
        return gameRepository.update(existingGame);
    }

    /**
     * Remove um jogo.
     * 
     * @param gameId ID do jogo
     * @throws IllegalArgumentException se o jogo não for encontrado
     */
    public void deleteGame(Long gameId) {
        if (!gameRepository.deleteById(gameId)) {
            throw new IllegalArgumentException("Game not found with ID: " + gameId);
        }
    }

    /**
     * Conta o total de jogos.
     * 
     * @return número total de jogos
     */
    public long getTotalGamesCount() {
        return gameRepository.count();
    }

    /**
     * Verifica se um jogo pode ser comprado por um cliente de determinada idade.
     * 
     * @param game jogo a ser verificado
     * @param customerAge idade do cliente
     * @return true se pode comprar
     */
    public boolean canCustomerBuyGame(Game game, int customerAge) {
        if (game == null) {
            return false;
        }
        
        return game.isAvailable() && game.isAgeAppropriate(customerAge);
    }

    /**
     * Valida os dados básicos de um jogo.
     * 
     * @param name nome do jogo
     * @param price preço do jogo
     * @param category categoria do jogo
     * @param ageRating classificação etária
     * @throws IllegalArgumentException se algum dado for inválido
     */
    private void validateGameData(String name, BigDecimal price, String category, int ageRating) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Game price cannot be null or negative");
        }
        
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Game category cannot be null or empty");
        }
        
        if (ageRating < 0 || ageRating > 18) {
            throw new IllegalArgumentException("Age rating must be between 0 and 18");
        }
    }
}
