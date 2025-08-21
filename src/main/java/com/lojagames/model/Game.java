package com.lojagames.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade que representa um jogo na loja.
 */
public class Game {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private int ageRating;
    private String description;
    private boolean available;

    public Game() {}

    /**
     * Construtor principal para criação de um novo jogo.
     * 
     * @param name Nome do jogo (não pode ser nulo ou vazio)
     * @param price Preço do jogo (deve ser >= 0)
     * @param category Categoria do jogo (não pode ser nulo ou vazio)
     * @param ageRating Classificação etária (deve estar entre 0 e 18)
     */
    public Game(String name, BigDecimal price, String category, int ageRating) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.ageRating = ageRating;
        this.available = true;
        this.description = "";
    }

    public Game(Long id, String name, BigDecimal price, String category, 
                int ageRating, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.ageRating = ageRating;
        this.description = description;
        this.available = available;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public int getAgeRating() { return ageRating; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return available; }

    // Setters com validação
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (isValidString(name)) {
            this.name = name;
        }
    }

    public void setPrice(BigDecimal price) {
        if (isValidPrice(price)) {
            this.price = price;
        }
    }

    public void setCategory(String category) {
        if (isValidString(category)) {
            this.category = category;
        }
    }

    public void setAgeRating(int ageRating) {
        if (isValidAgeRating(ageRating)) {
            this.ageRating = ageRating;
        }
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Verifica se o jogo é gratuito.
     * 
     * @return true se o preço for zero
     */
    public boolean isFree() {
        return price != null && price.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Verifica se o jogo é apropriado para uma determinada idade.
     * 
     * @param customerAge idade do cliente
     * @return true se o cliente pode comprar o jogo
     */
    public boolean isAgeAppropriate(int customerAge) {
        return customerAge >= ageRating;
    }

    // Métodos de validação privados
    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isValidAgeRating(int ageRating) {
        return ageRating >= 0 && ageRating <= 18;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Game game = (Game) obj;
        return Objects.equals(name, game.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("Game{name='%s', price=%s, category='%s', ageRating=%d}", 
                           name, price, category, ageRating);
    }
}
