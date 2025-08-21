package com.lojagames.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.lojagames.model.Game;

/**
 * Interface que define as operações de repositório para Game.
 */
public interface GameRepository {
    
    /**
     * Salva um jogo no repositório.
     * 
     * @param game jogo a ser salvo
     * @return jogo salvo com ID atualizado
     */
    Game save(Game game);
    
    /**
     * Busca um jogo por ID.
     * 
     * @param id ID do jogo
     * @return Optional contendo o jogo se encontrado
     */
    Optional<Game> findById(Long id);
    
    /**
     * Busca um jogo por nome.
     * 
     * @param name nome do jogo
     * @return Optional contendo o jogo se encontrado
     */
    Optional<Game> findByName(String name);
    
    /**
     * Busca todos os jogos.
     * 
     * @return lista de todos os jogos
     */
    List<Game> findAll();
    
    /**
     * Busca jogos por categoria.
     * 
     * @param category categoria dos jogos
     * @return lista de jogos da categoria
     */
    List<Game> findByCategory(String category);
    
    /**
     * Busca jogos por faixa de preço.
     * 
     * @param minPrice preço mínimo
     * @param maxPrice preço máximo
     * @return lista de jogos na faixa de preço
     */
    List<Game> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Busca jogos por classificação etária.
     * 
     * @param maxAge idade máxima permitida
     * @return lista de jogos apropriados para a idade
     */
    List<Game> findByAgeRating(int maxAge);
    
    /**
     * Busca jogos disponíveis.
     * 
     * @return lista de jogos disponíveis
     */
    List<Game> findAvailable();
    
    /**
     * Busca jogos gratuitos.
     * 
     * @return lista de jogos gratuitos
     */
    List<Game> findFreeGames();
    
    /**
     * Atualiza um jogo existente.
     * 
     * @param game jogo a ser atualizado
     * @return jogo atualizado
     */
    Game update(Game game);
    
    boolean existsByName(String name);
    
    /**
     * Conta o total de jogos no repositório.
     * 
     * @return número total de jogos
     */
    long count();
}
