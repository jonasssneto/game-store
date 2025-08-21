package com.lojagames.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade que representa um item no carrinho de compras.
 */
public class CartItem {
    private Long id;
    private Game game;
    private int quantity;
    private BigDecimal unitPrice;

    public CartItem() {}

    /**
     * Construtor principal.
     * 
     * @param game Jogo a ser adicionado (não pode ser nulo)
     * @param quantity Quantidade (deve ser > 0)
     */
    public CartItem(Game game, int quantity) {
        this.game = game;
        this.quantity = quantity;
        this.unitPrice = game != null ? game.getPrice() : BigDecimal.ZERO;
    }

    public CartItem(Long id, Game game, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.game = game;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters
    public Long getId() { return id; }
    public Game getGame() { return game; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    // Setters com validação
    public void setId(Long id) {
        this.id = id;
    }

    public void setGame(Game game) {
        if (game != null) {
            this.game = game;
            this.unitPrice = game.getPrice();
        }
    }

    public void setQuantity(int quantity) {
        if (isValidQuantity(quantity)) {
            this.quantity = quantity;
        }
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (isValidPrice(unitPrice)) {
            this.unitPrice = unitPrice;
        }
    }

    /**
     * Calcula o valor total do item (quantity * unitPrice).
     * 
     * @return valor total do item
     */
    public BigDecimal getTotalPrice() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Incrementa a quantidade do item.
     * 
     * @param amount quantidade a ser adicionada
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Decrementa a quantidade do item.
     * 
     * @param amount quantidade a ser removida
     * @return nova quantidade (não pode ser menor que 0)
     */
    public void decreaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
    }

    // Métodos de validação privados
    private boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CartItem cartItem = (CartItem) obj;
        return Objects.equals(game, cartItem.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }

    @Override
    public String toString() {
        return String.format("CartItem{game=%s, quantity=%d, unitPrice=%s, totalPrice=%s}", 
                           game != null ? game.getName() : "null", quantity, unitPrice, getTotalPrice());
    }
}


