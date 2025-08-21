package com.lojagames.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa um carrinho de compras.
 */
public class Cart {
    private Long id;
    private Long customerId;
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    /**
     * Construtor com ID do cliente.
     * 
     * @param customerId ID do cliente proprietário do carrinho
     */
    public Cart(Long customerId) {
        this();
        this.customerId = customerId;
    }

    public Cart(Long id, Long customerId) {
        this(customerId);
        this.id = id;
    }

    // Getters
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    
    /**
     * Retorna uma lista imutável dos itens do carrinho.
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * Adiciona um item ao carrinho.
     * Se o jogo já existe, incrementa a quantidade.
     * 
     * @param game jogo a ser adicionado
     * @param quantity quantidade
     */
    public void addItem(Game game, int quantity) {
        if (game == null || quantity <= 0) {
            return;
        }

        CartItem existingItem = findItemByGame(game);
        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
        } else {
            items.add(new CartItem(game, quantity));
        }
    }

    /**
     * Remove um item do carrinho.
     * 
     * @param game jogo a ser removido
     */
    public void removeItem(Game game) {
        if (game == null) {
            return;
        }
        items.removeIf(item -> Objects.equals(item.getGame(), game));
    }

    /**
     * Remove uma quantidade específica de um item.
     * 
     * @param game jogo a ser removido
     * @param quantity quantidade a ser removida
     */
    public void removeItem(Game game, int quantity) {
        if (game == null || quantity <= 0) {
            return;
        }

        CartItem item = findItemByGame(game);
        if (item != null) {
            item.decreaseQuantity(quantity);
            if (item.getQuantity() == 0) {
                items.remove(item);
            }
        }
    }

    /**
     * Limpa todo o carrinho.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calcula o valor total do carrinho.
     * 
     * @return valor total
     */
    public BigDecimal getTotalValue() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica se o carrinho está vazio.
     * 
     * @return true se não há itens
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Retorna a quantidade total de itens no carrinho.
     * 
     * @return quantidade total
     */
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Busca um item pelo jogo.
     * 
     * @param game jogo a ser buscado
     * @return item encontrado ou null
     */
    private CartItem findItemByGame(Game game) {
        return items.stream()
                .filter(item -> Objects.equals(item.getGame(), game))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return String.format("Cart{id=%d, customerId=%d, items=%d, totalValue=%s}", 
                           id, customerId, items.size(), getTotalValue());
    }
}
