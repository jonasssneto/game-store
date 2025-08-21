package com.lojagames.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa um cliente da loja de jogos.
 */
public class Customer {
    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;
    private int age;
    private List<Game> purchasedGames;

    public Customer() {
        this.purchasedGames = new ArrayList<>();
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Construtor principal.
     * 
     * @param name Nome do cliente (não pode ser nulo ou vazio)
     * @param email Email do cliente (não pode ser nulo ou vazio)
     * @param initialBalance Saldo inicial (deve ser >= 0)
     * @param age Idade do cliente (deve ser >= 0)
     */
    public Customer(String name, String email, BigDecimal initialBalance, int age) {
        this();
        this.name = name;
        this.email = email;
        this.balance = initialBalance;
        this.age = age;
    }

    public Customer(Long id, String name, String email, BigDecimal balance, int age) {
        this(name, email, balance, age);
        this.id = id;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getBalance() { return balance; }
    public int getAge() { return age; }
    
    public List<Game> getPurchasedGames() {
        return Collections.unmodifiableList(purchasedGames);
    }

    // Setters com validação
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (isValidString(name)) {
            this.name = name;
        }
    }

    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        }
    }

    public void setBalance(BigDecimal balance) {
        if (isValidBalance(balance)) {
            this.balance = balance;
        }
    }

    public void setAge(int age) {
        if (isValidAge(age)) {
            this.age = age;
        }
    }

    /**
     * Verifica se o cliente tem saldo suficiente para uma compra.
     * 
     * @param amount valor da compra
     * @return true se o saldo for suficiente
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance != null && amount != null && 
               balance.compareTo(amount) >= 0;
    }

    /**
     * Deduz um valor do saldo do cliente.
     * 
     * @param amount valor a ser deduzido
     * @throws IllegalArgumentException se o valor for inválido ou insuficiente
     */
    public void deductBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // Para valores zero (jogos gratuitos), não precisa deduzir nem validar saldo
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        if (!hasSufficientBalance(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Adiciona um valor ao saldo do cliente.
     * 
     * @param amount valor a ser adicionado
     * @throws IllegalArgumentException se o valor for inválido
     */
    public void addBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Adiciona um jogo à lista de jogos comprados.
     * 
     * @param game jogo comprado
     */
    public void addPurchasedGame(Game game) {
        if (game != null && !purchasedGames.contains(game)) {
            purchasedGames.add(game);
        }
    }

    /**
     * Verifica se o cliente já possui um determinado jogo.
     * 
     * @param game jogo a ser verificado
     * @return true se o cliente já possui o jogo
     */
    public boolean ownsGame(Game game) {
        return game != null && purchasedGames.contains(game);
    }

    /**
     * Calcula o valor total gasto pelo cliente.
     * 
     * @return valor total das compras
     */
    public BigDecimal getTotalSpent() {
        return purchasedGames.stream()
                .map(Game::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Métodos de validação privados
    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return isValidString(email) && email.contains("@") && email.contains(".");
    }

    private boolean isValidBalance(BigDecimal balance) {
        return balance != null && balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isValidAge(int age) {
        return age >= 0 && age <= 150; // Limite razoável para idade
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("Customer{name='%s', email='%s', balance=%s, age=%d}", 
                           name, email, balance, age);
    }
}
