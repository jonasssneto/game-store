package com.lojagames.service;

import com.lojagames.model.Customer;
import com.lojagames.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada aos clientes.
 */
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    /**
     * Construtor com injeção de dependência.
     * 
     * @param customerRepository repositório de clientes
     */
    public CustomerService(CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new IllegalArgumentException("CustomerRepository cannot be null");
        }
        this.customerRepository = customerRepository;
    }

    /**
     * Cria um novo cliente.
     * 
     * @param name nome do cliente
     * @param email email do cliente
     * @param initialBalance saldo inicial
     * @param age idade do cliente
     * @return cliente criado
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public Customer createCustomer(String name, String email, BigDecimal initialBalance, int age) {
        validateCustomerData(name, email, initialBalance, age);
        
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Customer with email '" + email + "' already exists");
        }
        
        Customer customer = new Customer(name, email, initialBalance, age);
        return customerRepository.save(customer);
    }

    /**
     * Busca um cliente por ID.
     * 
     * @param id ID do cliente
     * @return cliente encontrado
     * @throws IllegalArgumentException se o cliente não for encontrado
     */
    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
    }

    /**
     * Busca um cliente por email.
     * 
     * @param email email do cliente
     * @return Optional contendo o cliente se encontrado
     */
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Lista todos os clientes.
     * 
     * @return lista de todos os clientes
     */
    public List<Customer> listAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Busca clientes por nome (busca parcial).
     * 
     * @param name nome a ser buscado
     * @return lista de clientes que contém o nome
     */
    public List<Customer> searchCustomersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return listAllCustomers();
        }
        return customerRepository.findByNameContaining(name);
    }

    /**
     * Adiciona saldo à conta de um cliente.
     * 
     * @param customerId ID do cliente
     * @param amount valor a ser adicionado
     * @return cliente atualizado
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public Customer addBalance(Long customerId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Customer customer = findCustomerById(customerId);
        customer.addBalance(amount);
        
        return customerRepository.update(customer);
    }

    /**
     * Atualiza informações de um cliente.
     * 
     * @param customer cliente a ser atualizado
     * @return cliente atualizado
     */
    public Customer updateCustomer(Customer customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer and ID cannot be null");
        }
        
        return customerRepository.update(customer);
    }

    /**
     * Atualiza informações específicas de um cliente.
     * 
     * @param customerId ID do cliente
     * @param name novo nome (opcional)
     * @param email novo email (opcional)
     * @param age nova idade (opcional)
     * @return cliente atualizado
     */
    public Customer updateCustomer(Long customerId, String name, String email, Integer age) {
        Customer existingCustomer = findCustomerById(customerId);
        
        if (name != null && !name.trim().isEmpty()) {
            existingCustomer.setName(name);
        }
        
        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(existingCustomer.getEmail()) && customerRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Customer with email '" + email + "' already exists");
            }
            existingCustomer.setEmail(email);
        }
        
        if (age != null) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Age must be between 0 and 150");
            }
            existingCustomer.setAge(age);
        }
        
        return customerRepository.update(existingCustomer);
    }

    /**
     * Remove um cliente.
     * 
     * @param customerId ID do cliente
     * @throws IllegalArgumentException se o cliente não for encontrado
     */
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.deleteById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
    }

    /**
     * Lista clientes por faixa etária.
     * 
     * @param minAge idade mínima
     * @param maxAge idade máxima
     * @return lista de clientes na faixa etária
     */
    public List<Customer> findCustomersByAgeRange(int minAge, int maxAge) {
        if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
            throw new IllegalArgumentException("Invalid age range");
        }
        
        return customerRepository.findByAgeRange(minAge, maxAge);
    }

    /**
     * Lista clientes com saldo mínimo.
     * 
     * @param minBalance saldo mínimo
     * @return lista de clientes com saldo >= minBalance
     */
    public List<Customer> findCustomersWithMinBalance(BigDecimal minBalance) {
        if (minBalance == null || minBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Min balance cannot be null or negative");
        }
        
        return customerRepository.findByMinBalance(minBalance);
    }

    /**
     * Conta o total de clientes.
     * 
     * @return número total de clientes
     */
    public long getTotalCustomersCount() {
        return customerRepository.count();
    }

    /**
     * Calcula o saldo total de todos os clientes.
     * 
     * @return saldo total
     */
    public BigDecimal getTotalBalance() {
        return customerRepository.findAll().stream()
                .map(Customer::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica se um cliente pode comprar um jogo com base no saldo e idade.
     * 
     * @param customerId ID do cliente
     * @param gamePrice preço do jogo
     * @param gameAgeRating classificação etária do jogo
     * @return true se pode comprar
     */
    public boolean canCustomerAffordGame(Long customerId, BigDecimal gamePrice, int gameAgeRating) {
        Customer customer = findCustomerById(customerId);
        
        return customer.hasSufficientBalance(gamePrice) && 
               customer.getAge() >= gameAgeRating;
    }

    /**
     * Valida os dados básicos de um cliente.
     * 
     * @param name nome do cliente
     * @param email email do cliente
     * @param initialBalance saldo inicial
     * @param age idade do cliente
     * @throws IllegalArgumentException se algum dado for inválido
     */
    private void validateCustomerData(String name, String email, BigDecimal initialBalance, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be null or empty");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be null or negative");
        }
        
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }

    /**
     * Valida formato de email básico.
     * 
     * @param email email a ser validado
     * @return true se o formato é válido
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".");
    }
}
