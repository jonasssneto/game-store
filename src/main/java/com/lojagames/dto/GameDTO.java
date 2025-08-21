package com.lojagames.dto;

import java.math.BigDecimal;

/**
 * DTO para transferência de dados de jogos.
 */
public class GameDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private int ageRating;
    private String description;
    private boolean available;

    public GameDTO() {}

    public GameDTO(String name, BigDecimal price, String category, int ageRating) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.ageRating = ageRating;
        this.available = true;
        this.description = "";
    }

    public GameDTO(Long id, String name, BigDecimal price, String category, 
                   int ageRating, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.ageRating = ageRating;
        this.description = description;
        this.available = available;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getAgeRating() { return ageRating; }
    public void setAgeRating(int ageRating) { this.ageRating = ageRating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("GameDTO{id=%d, name='%s', price=%s, category='%s', ageRating=%d, available=%s}",
                           id, name, price, category, ageRating, available);
    }
}
