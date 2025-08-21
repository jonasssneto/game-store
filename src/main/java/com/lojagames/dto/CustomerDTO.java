package com.lojagames.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para transferência de dados de clientes.
 */
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;
    private int age;
    private List<GameDTO> purchasedGames;

    public CustomerDTO() {}

    public CustomerDTO(String name, String email, BigDecimal balance, int age) {
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.age = age;
    }

    public CustomerDTO(Long id, String name, String email, BigDecimal balance, 
                       int age, List<GameDTO> purchasedGames) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.age = age;
        this.purchasedGames = purchasedGames;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public List<GameDTO> getPurchasedGames() { return purchasedGames; }
    public void setPurchasedGames(List<GameDTO> purchasedGames) { this.purchasedGames = purchasedGames; }

    @Override
    public String toString() {
        return String.format("CustomerDTO{id=%d, name='%s', email='%s', balance=%s, age=%d}",
                           id, name, email, balance, age);
    }
}
