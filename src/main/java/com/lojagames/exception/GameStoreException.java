package com.lojagames.exception;

/**
 * Exceção base para todas as exceções da loja de jogos.
 */
public class GameStoreException extends RuntimeException {
    
    public GameStoreException(String message) {
        super(message);
    }

    public GameStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exceção lançada quando um jogo não é encontrado.
 */
class GameNotFoundException extends GameStoreException {
    
    public GameNotFoundException(String message) {
        super(message);
    }
    
    public GameNotFoundException(Long gameId) {
        super("Game not found with ID: " + gameId);
    }
}

/**
 * Exceção lançada quando um cliente não é encontrado.
 */
class CustomerNotFoundException extends GameStoreException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with ID: " + customerId);
    }
}

/**
 * Exceção lançada quando há problemas relacionados a saldo.
 */
class InsufficientBalanceException extends GameStoreException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

/**
 * Exceção lançada quando há problemas de validação.
 */
class ValidationException extends GameStoreException {
    
    public ValidationException(String message) {
        super(message);
    }
}

/**
 * Exceção lançada quando uma operação de compra falha.
 */
class PurchaseException extends GameStoreException {
    
    public PurchaseException(String message) {
        super(message);
    }
    
    public PurchaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
