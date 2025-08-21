package com.lojagames.controller;

import com.lojagames.model.Game;
import com.lojagames.model.Customer;
import com.lojagames.model.Cart;
import com.lojagames.service.GameService;
import com.lojagames.service.CustomerService;
import com.lojagames.service.PurchaseService;
import com.lojagames.util.CurrencyFormatter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller para a interface gráfica da loja de jogos.
 */
public class GameStoreController {
    
    private final GameService gameService;
    private final CustomerService customerService;
    private final PurchaseService purchaseService;
    private Customer currentCustomer;
    private Cart currentCart;

    /**
     * Construtor com injeção de dependência.
     * 
     * @param gameService serviço de jogos
     * @param customerService serviço de clientes
     * @param purchaseService serviço de compras
     */
    public GameStoreController(GameService gameService, 
                              CustomerService customerService,
                              PurchaseService purchaseService) {
        if (gameService == null) {
            throw new IllegalArgumentException("GameService cannot be null");
        }
        if (customerService == null) {
            throw new IllegalArgumentException("CustomerService cannot be null");
        }
        if (purchaseService == null) {
            throw new IllegalArgumentException("PurchaseService cannot be null");
        }
        
        this.gameService = gameService;
        this.customerService = customerService;
        this.purchaseService = purchaseService;
        this.currentCart = new Cart();
    }

    /**
     * Define o cliente atual da sessão.
     * 
     * @param customerId ID do cliente
     * @throws IllegalArgumentException se o cliente não for encontrado
     */
    public void setCurrentCustomer(Long customerId) {
        this.currentCustomer = customerService.findCustomerById(customerId);
        this.currentCart = new Cart(customerId);
    }

    /**
     * Cria um cliente padrão para demonstração.
     * 
     * @param name nome do cliente
     * @param email email do cliente
     * @param initialBalance saldo inicial
     * @param age idade do cliente
     * @return cliente criado
     */
    public Customer createDefaultCustomer(String name, String email, 
                                        BigDecimal initialBalance, int age) {
        Customer customer = customerService.createCustomer(name, email, initialBalance, age);
        setCurrentCustomer(customer.getId());
        return customer;
    }

    /**
     * Retorna o cliente atual.
     * 
     * @return cliente atual ou null se não houver
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Retorna o saldo formatado do cliente atual.
     * 
     * @return saldo formatado como moeda
     */
    public String getCurrentCustomerFormattedBalance() {
        if (currentCustomer == null) {
            return CurrencyFormatter.formatBRL(BigDecimal.ZERO);
        }
        return CurrencyFormatter.formatBRL(currentCustomer.getBalance());
    }

    /**
     * Lista todos os jogos disponíveis.
     * 
     * @return lista de jogos disponíveis
     */
    public List<Game> getAvailableGames() {
        return gameService.listAvailableGames();
    }

    /**
     * Lista jogos ordenados por preço.
     * 
     * @param ascending true para ordem crescente
     * @return lista ordenada de jogos
     */
    public List<Game> getGamesSortedByPrice(boolean ascending) {
        return gameService.listGamesSortedByPrice(ascending);
    }

    /**
     * Filtra jogos por categoria.
     * 
     * @param category categoria desejada
     * @return lista de jogos da categoria
     */
    public List<Game> filterGamesByCategory(String category) {
        return gameService.filterGamesByCategory(category);
    }

    /**
     * Filtra jogos apropriados para a idade do cliente atual.
     * 
     * @return lista de jogos apropriados
     */
    public List<Game> getAgeAppropriateGames() {
        if (currentCustomer == null) {
            return getAvailableGames();
        }
        return gameService.filterGamesForAge(currentCustomer.getAge());
    }

    /**
     * Adiciona um jogo ao carrinho.
     * 
     * @param game jogo a ser adicionado
     * @param quantity quantidade
     * @return true se adicionado com sucesso
     */
    public boolean addGameToCart(Game game, int quantity) {
        if (currentCustomer == null || game == null || quantity <= 0) {
            return false;
        }
        
        if (!gameService.canCustomerBuyGame(game, currentCustomer.getAge())) {
            return false;
        }
        
        if (currentCustomer.ownsGame(game)) {
            return false;
        }
        
        currentCart.addItem(game, quantity);
        return true;
    }

    /**
     * Remove um jogo do carrinho.
     * 
     * @param game jogo a ser removido
     */
    public void removeGameFromCart(Game game) {
        if (currentCart != null) {
            currentCart.removeItem(game);
        }
    }

    /**
     * Limpa o carrinho.
     */
    public void clearCart() {
        if (currentCart != null) {
            currentCart.clear();
        }
    }

    /**
     * Retorna o valor total do carrinho formatado.
     * 
     * @return valor total formatado
     */
    public String getFormattedCartTotal() {
        if (currentCart == null) {
            return CurrencyFormatter.formatBRL(BigDecimal.ZERO);
        }
        return CurrencyFormatter.formatBRL(currentCart.getTotalValue());
    }

    /**
     * Compra um jogo específico.
     * 
     * @param game jogo a ser comprado
     * @return resultado da compra
     */
    public PurchaseService.PurchaseResult purchaseGame(Game game) {
        if (currentCustomer == null) {
            return new PurchaseService.PurchaseResult(false, "No customer selected", 
                                                    BigDecimal.ZERO, List.of());
        }
        
        PurchaseService.PurchaseResult result = purchaseService.purchaseGame(currentCustomer.getId(), game.getId());
        
        if (result.isSuccess()) {
            // Atualiza o cliente atual
            currentCustomer = customerService.findCustomerById(currentCustomer.getId());
        }
        
        return result;
    }

    /**
     * Compra todos os itens do carrinho.
     * 
     * @return resultado da compra
     */
    public PurchaseService.PurchaseResult purchaseCart() {
        if (currentCustomer == null) {
            return new PurchaseService.PurchaseResult(false, "No customer selected", 
                                                    BigDecimal.ZERO, List.of());
        }
        
        if (currentCart == null || currentCart.isEmpty()) {
            return new PurchaseService.PurchaseResult(false, "Cart is empty", 
                                                    BigDecimal.ZERO, List.of());
        }
        
        PurchaseService.PurchaseResult result = purchaseService.purchaseCart(currentCustomer.getId(), currentCart);
        
        if (result.isSuccess()) {
            // Atualiza o cliente atual e limpa o carrinho
            currentCustomer = customerService.findCustomerById(currentCustomer.getId());
            clearCart();
        }
        
        return result;
    }

    /**
     * Compra o máximo de jogos possível com o saldo disponível.
     * 
     * @return resultado da compra
     */
    public PurchaseService.PurchaseResult purchaseMaximumGames() {
        if (currentCustomer == null) {
            return new PurchaseService.PurchaseResult(false, "No customer selected", 
                                                    BigDecimal.ZERO, List.of());
        }
        
        List<Game> availableGames = getAgeAppropriateGames();
        PurchaseService.PurchaseResult result = purchaseService.purchaseMaximumGames(
                currentCustomer.getId(), availableGames);
        
        if (result.isSuccess()) {
            // Atualiza o cliente atual
            currentCustomer = customerService.findCustomerById(currentCustomer.getId());
        }
        
        return result;
    }

    /**
     * Adiciona saldo ao cliente atual.
     * 
     * @param amount valor a ser adicionado
     * @return true se adicionado com sucesso
     */
    public boolean addBalanceToCurrentCustomer(BigDecimal amount) {
        if (currentCustomer == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        try {
            currentCustomer = customerService.addBalance(currentCustomer.getId(), amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna os jogos comprados pelo cliente atual.
     * 
     * @return lista de jogos comprados
     */
    public List<Game> getPurchasedGames() {
        if (currentCustomer == null) {
            return List.of();
        }
        return currentCustomer.getPurchasedGames();
    }

    /**
     * Verifica se o cliente atual possui um jogo específico.
     * 
     * @param game jogo a ser verificado
     * @return true se possui o jogo
     */
    public boolean currentCustomerOwnsGame(Game game) {
        return currentCustomer != null && currentCustomer.ownsGame(game);
    }

    /**
     * Retorna estatísticas do cliente atual.
     * 
     * @return string com estatísticas formatadas
     */
    public String getCurrentCustomerStats() {
        if (currentCustomer == null) {
            return "Nenhum cliente selecionado";
        }
        
        int gamesOwned = currentCustomer.getPurchasedGames().size();
        BigDecimal totalSpent = currentCustomer.getTotalSpent();
        
        return String.format("Jogos: %d | Gasto total: %s", 
                           gamesOwned, CurrencyFormatter.formatBRL(totalSpent));
    }
}
