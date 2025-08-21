package com.lojagames.repository;

import com.lojagames.model.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define as operações de repositório para Customer.
 */
public interface CustomerRepository {
    
    /**
     * Salva um cliente no repositório.
     * 
     * @param customer cliente a ser salvo
     * @return cliente salvo com ID atualizado
     */
    Customer save(Customer customer);
    
    /**
     * Busca um cliente por ID.
     * 
     * @param id ID do cliente
     * @return Optional contendo o cliente se encontrado
     */
    Optional<Customer> findById(Long id);
    
    /**
     * Busca um cliente por email.
     * 
     * @param email email do cliente
     * @return Optional contendo o cliente se encontrado
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Busca todos os clientes.
     * 
     * @return lista de todos os clientes
     */
    List<Customer> findAll();
    
    /**
     * Busca clientes por nome (busca parcial).
     * 
     * @param name nome a ser buscado
     * @return lista de clientes que contém o nome
     */
    List<Customer> findByNameContaining(String name);
    
    /**
     * Busca clientes por faixa de idade.
     * 
     * @param minAge idade mínima
     * @param maxAge idade máxima
     * @return lista de clientes na faixa etária
     */
    List<Customer> findByAgeRange(int minAge, int maxAge);
    
    /**
     * Busca clientes com saldo mínimo.
     * 
     * @param minBalance saldo mínimo
     * @return lista de clientes com saldo >= minBalance
     */
    List<Customer> findByMinBalance(BigDecimal minBalance);
    
    /**
     * Atualiza um cliente existente.
     * 
     * @param customer cliente a ser atualizado
     * @return cliente atualizado
     */
    Customer update(Customer customer);
    
    boolean existsByEmail(String email);
    
    /**
     * Conta o total de clientes no repositório.
     * 
     * @return número total de clientes
     */
    long count();
}
