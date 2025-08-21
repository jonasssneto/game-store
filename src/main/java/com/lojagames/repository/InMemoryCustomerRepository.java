package com.lojagames.repository;

import com.lojagames.model.Customer;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementação em memória do CustomerRepository.
 */
public class InMemoryCustomerRepository implements CustomerRepository {
    
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Customer save(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        
        customers.put(customer.getId(), customer);
        return customer;
    }



    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<Customer> findByNameContaining(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerCaseName = name.toLowerCase();
        return customers.values().stream()
                .filter(customer -> customer.getName().toLowerCase().contains(lowerCaseName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByAgeRange(int minAge, int maxAge) {
        return customers.values().stream()
                .filter(customer -> {
                    int age = customer.getAge();
                    return age >= minAge && age <= maxAge;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByMinBalance(BigDecimal minBalance) {
        if (minBalance == null) {
            return new ArrayList<>();
        }
        
        return customers.values().stream()
                .filter(customer -> {
                    BigDecimal balance = customer.getBalance();
                    return balance != null && balance.compareTo(minBalance) >= 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Customer update(Customer customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer and ID cannot be null");
        }
        
        if (!customers.containsKey(customer.getId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + customer.getId());
        }
        
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return customers.remove(id) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return customers.values().stream()
                .anyMatch(customer -> email.equals(customer.getEmail()));
    }

    @Override
    public long count() {
        return customers.size();
    }

    /**
     * Método utilitário para limpar todos os dados (útil para testes).
     */
    public void clear() {
        customers.clear();
        idGenerator.set(1);
    }

    /**
     * Método utilitário para inicializar dados de exemplo.
     */
    public void initializeWithSampleData() {
        // Limpa dados existentes
        clear();
        
        // Adiciona clientes de exemplo
        save(new Customer("João Silva", "joao@email.com", new BigDecimal("500.00"), 25));
        save(new Customer("Maria Santos", "maria@email.com", new BigDecimal("750.00"), 30));
        save(new Customer("Pedro Oliveira", "pedro@email.com", new BigDecimal("300.00"), 18));
        save(new Customer("Ana Costa", "ana@email.com", new BigDecimal("1000.00"), 28));
        save(new Customer("Carlos Lima", "carlos@email.com", new BigDecimal("150.00"), 22));
    }
}
