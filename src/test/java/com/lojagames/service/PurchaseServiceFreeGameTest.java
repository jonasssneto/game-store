package com.lojagames.service;

import com.lojagames.model.Game;
import com.lojagames.model.Customer;
import com.lojagames.repository.GameRepository;
import com.lojagames.repository.CustomerRepository;
import com.lojagames.repository.InMemoryGameRepository;
import com.lojagames.repository.InMemoryCustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes específicos para compra de jogos gratuitos no PurchaseService.
 */
class PurchaseServiceFreeGameTest {

    private PurchaseService purchaseService;
    private GameService gameService;
    private CustomerService customerService;
    private GameRepository gameRepository;
    private CustomerRepository customerRepository;
    
    private Customer testCustomer;
    private Game freeGame;
    private Game paidGame;

    @BeforeEach
    void setUp() {
        // Configuração dos repositórios
        gameRepository = new InMemoryGameRepository();
        customerRepository = new InMemoryCustomerRepository();
        
        // Configuração dos serviços
        gameService = new GameService(gameRepository);
        customerService = new CustomerService(customerRepository);
        purchaseService = new PurchaseService(gameService, customerService);
        
        // Criação de jogos de teste
        freeGame = new Game("Valorant", BigDecimal.ZERO, "FPS", 16);
        freeGame.setId(1L);
        freeGame.setDescription("Jogo gratuito de tiro");
        
        paidGame = new Game("Cyberpunk 2077", new BigDecimal("199.90"), "RPG", 18);
        paidGame.setId(2L);
        paidGame.setDescription("Jogo pago");
        
        // Adiciona jogos ao repositório
        gameRepository.save(freeGame);
        gameRepository.save(paidGame);
        
        // Criação de cliente de teste
        testCustomer = new Customer("João Silva", "joao@test.com", new BigDecimal("100.00"), 20);
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    @DisplayName("Deve permitir comprar jogo gratuito com qualquer saldo")
    void shouldAllowPurchaseOfFreeGameWithAnySaldo() {
        // Act
        PurchaseService.PurchaseResult result = purchaseService.purchaseGame(
            testCustomer.getId(), freeGame.getId());
        
        // Assert
        assertTrue(result.isSuccess(), "Compra de jogo gratuito deve ser permitida");
        assertEquals("Compra realizada com sucesso", result.getMessage());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(1, result.getPurchasedGames().size());
        assertEquals(freeGame.getName(), result.getPurchasedGames().get(0).getName());
        
        // Verifica que o saldo não foi alterado
        Customer updatedCustomer = customerService.findCustomerById(testCustomer.getId());
        assertEquals(new BigDecimal("100.00"), updatedCustomer.getBalance());
        assertTrue(updatedCustomer.ownsGame(freeGame));
    }

    @Test
    @DisplayName("Deve permitir comprar jogo gratuito mesmo com saldo zero")
    void shouldAllowPurchaseOfFreeGameWithZeroBalance() {
        // Arrange - cliente com saldo zero
        Customer poorCustomer = new Customer("Maria Pobre", "maria@test.com", BigDecimal.ZERO, 20);
        poorCustomer = customerRepository.save(poorCustomer);
        
        // Act
        PurchaseService.PurchaseResult result = purchaseService.purchaseGame(
            poorCustomer.getId(), freeGame.getId());
        
        // Assert
        assertTrue(result.isSuccess(), "Compra de jogo gratuito deve ser permitida mesmo com saldo zero");
        assertEquals("Compra realizada com sucesso", result.getMessage());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        
        // Verifica que o cliente possui o jogo
        Customer updatedCustomer = customerService.findCustomerById(poorCustomer.getId());
        assertTrue(updatedCustomer.ownsGame(freeGame));
        assertEquals(BigDecimal.ZERO, updatedCustomer.getBalance());
    }

    @Test
    @DisplayName("Deve incluir jogos gratuitos na compra máxima")
    void shouldIncludeFreeGamesInMaximumPurchase() {
        // Arrange - cliente com pouco saldo
        Customer customerWithLowBalance = new Customer("Pedro Economia", "pedro@test.com", new BigDecimal("50.00"), 20);
        customerWithLowBalance = customerRepository.save(customerWithLowBalance);
        
        List<Game> availableGames = List.of(freeGame, paidGame);
        
        // Act
        PurchaseService.PurchaseResult result = purchaseService.purchaseMaximumGames(
            customerWithLowBalance.getId(), availableGames);
        
        // Assert
        assertTrue(result.isSuccess(), "Compra máxima deve incluir jogos gratuitos");
        assertTrue(result.getMessage().contains("Comprou"), "Mensagem deve estar em português");
        assertEquals(1, result.getPurchasedGames().size(), "Deve comprar apenas o jogo gratuito");
        assertEquals(freeGame.getName(), result.getPurchasedGames().get(0).getName());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        
        // Verifica que o saldo não foi alterado
        Customer updatedCustomer = customerService.findCustomerById(customerWithLowBalance.getId());
        assertEquals(new BigDecimal("50.00"), updatedCustomer.getBalance());
        assertTrue(updatedCustomer.ownsGame(freeGame));
        assertFalse(updatedCustomer.ownsGame(paidGame));
    }

    @Test
    @DisplayName("Deve comprar jogos gratuitos e pagos na compra máxima")
    void shouldPurchaseBothFreeAndPaidGamesInMaximumPurchase() {
        // Arrange - cliente com saldo suficiente
        Customer richCustomer = new Customer("Ana Rica", "ana@test.com", new BigDecimal("500.00"), 20);
        richCustomer = customerRepository.save(richCustomer);
        
        List<Game> availableGames = List.of(freeGame, paidGame);
        
        // Act
        PurchaseService.PurchaseResult result = purchaseService.purchaseMaximumGames(
            richCustomer.getId(), availableGames);
        
        // Assert
        assertTrue(result.isSuccess(), "Compra máxima deve funcionar com saldo suficiente");
        assertEquals(2, result.getPurchasedGames().size(), "Deve comprar ambos os jogos");
        assertEquals(new BigDecimal("199.90"), result.getTotalAmount(), "Valor deve ser apenas do jogo pago");
        
        // Verifica que possui ambos os jogos
        Customer updatedCustomer = customerService.findCustomerById(richCustomer.getId());
        assertTrue(updatedCustomer.ownsGame(freeGame));
        assertTrue(updatedCustomer.ownsGame(paidGame));
        assertEquals(new BigDecimal("300.10"), updatedCustomer.getBalance());
    }

    @Test
    @DisplayName("Não deve comprar jogo gratuito se já possuir")
    void shouldNotPurchaseFreeGameIfAlreadyOwned() {
        // Arrange - cliente já possui o jogo gratuito
        testCustomer.addPurchasedGame(freeGame);
        customerRepository.update(testCustomer);
        
        // Act
        PurchaseService.PurchaseResult result = purchaseService.purchaseGame(
            testCustomer.getId(), freeGame.getId());
        
        // Assert
        assertFalse(result.isSuccess(), "Não deve permitir comprar jogo que já possui");
        assertTrue(result.getMessage().contains("já possui"), "Mensagem deve estar em português");
    }
}
