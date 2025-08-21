package com.lojagames.service;

import com.lojagames.model.Game;
import com.lojagames.model.Customer;
import com.lojagames.model.Cart;
import com.lojagames.model.CartItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Serviço responsável pela lógica de compras.
 */
public class PurchaseService {
    
    private final GameService gameService;
    private final CustomerService customerService;

    /**
     * Construtor com injeção de dependência.
     * 
     * @param gameService serviço de jogos
     * @param customerService serviço de clientes
     */
    public PurchaseService(GameService gameService, CustomerService customerService) {
        if (gameService == null) {
            throw new IllegalArgumentException("GameService cannot be null");
        }
        if (customerService == null) {
            throw new IllegalArgumentException("CustomerService cannot be null");
        }
        this.gameService = gameService;
        this.customerService = customerService;
    }

    /**
     * Processa a compra de um único jogo.
     * 
     * @param customerId ID do cliente
     * @param gameId ID do jogo
     * @return resultado da compra
     */
    public PurchaseResult purchaseGame(Long customerId, Long gameId) {
        Customer customer = customerService.findCustomerById(customerId);
        Game game = gameService.findGameById(gameId);
        
        return processSinglePurchase(customer, game);
    }

    /**
     * Processa a compra de múltiplos jogos de um carrinho.
     * 
     * @param customerId ID do cliente
     * @param cart carrinho de compras
     * @return resultado da compra
     */
    public PurchaseResult purchaseCart(Long customerId, Cart cart) {
        Customer customer = customerService.findCustomerById(customerId);
        
        if (cart == null || cart.isEmpty()) {
            return new PurchaseResult(false, "Carrinho está vazio", BigDecimal.ZERO, new ArrayList<>());
        }
        
        return processCartPurchase(customer, cart);
    }

        /**
     * Compra o máximo de jogos possível com o saldo disponível.
     * 
     * @param customerId ID do cliente
     * @param availableGames lista de jogos disponíveis
     * @return resultado da compra
     */
    public PurchaseResult purchaseMaximumGames(Long customerId, List<Game> availableGames) {
        Customer customer = customerService.findCustomerById(customerId);
        
        if (availableGames == null || availableGames.isEmpty()) {
            return new PurchaseResult(false, "Nenhum jogo disponível", BigDecimal.ZERO, new ArrayList<>());
        }
        
        // Filtra jogos apropriados para a idade e disponíveis
        List<Game> suitableGames = availableGames.stream()
                .filter(game -> gameService.canCustomerBuyGame(game, customer.getAge()))
                .filter(game -> !customer.ownsGame(game))
                .sorted((g1, g2) -> g1.getPrice().compareTo(g2.getPrice())) // Ordena por preço crescente
                .toList();
        
        return processGreedyPurchase(customer, suitableGames);
    }

    /**
     * Processa a compra de um único jogo.
     * 
     * @param customer cliente
     * @param game jogo
     * @return resultado da compra
     */
    private PurchaseResult processSinglePurchase(Customer customer, Game game) {
        List<String> errors = validatePurchase(customer, game);
        
        if (!errors.isEmpty()) {
            return new PurchaseResult(false, String.join("; ", errors), 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
        
        try {
            customer.deductBalance(game.getPrice());
            customer.addPurchasedGame(game);
            customerService.updateCustomer(customer);
            
            List<Game> purchasedGames = List.of(game);
            return new PurchaseResult(true, "Compra realizada com sucesso", 
                                    game.getPrice(), purchasedGames);
            
        } catch (Exception e) {
            return new PurchaseResult(false, "Erro ao processar compra do carrinho: " + e.getMessage(), 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
    }

    /**
     * Processa a compra de um carrinho.
     * 
     * @param customer cliente
     * @param cart carrinho
     * @return resultado da compra
     */
    private PurchaseResult processCartPurchase(Customer customer, Cart cart) {
        BigDecimal totalCost = cart.getTotalValue();
        List<String> errors = new ArrayList<>();
        
        // Validações gerais
        if (!customer.hasSufficientBalance(totalCost)) {
            errors.add("Saldo insuficiente para comprar o carrinho");
        }
        
        // Valida cada item
        for (CartItem item : cart.getItems()) {
            Game game = item.getGame();
            if (!gameService.canCustomerBuyGame(game, customer.getAge())) {
                errors.add("Não é possível comprar o jogo: " + game.getName());
            }
            if (customer.ownsGame(game)) {
                errors.add("Já possui o jogo: " + game.getName());
            }
        }
        
        if (!errors.isEmpty()) {
            return new PurchaseResult(false, String.join("; ", errors), 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
        
        try {
            customer.deductBalance(totalCost);
            List<Game> purchasedGames = new ArrayList<>();
            
            for (CartItem item : cart.getItems()) {
                customer.addPurchasedGame(item.getGame());
                purchasedGames.add(item.getGame());
            }
            
            customerService.updateCustomer(customer);
            
            return new PurchaseResult(true, "Compra do carrinho realizada com sucesso", 
                                    totalCost, purchasedGames);
            
        } catch (Exception e) {
            return new PurchaseResult(false, "Erro ao processar compra do carrinho: " + e.getMessage(), 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
    }

    /**
     * Processa compra gulosa (máximo de jogos possível).
     * 
     * @param customer cliente
     * @param games jogos disponíveis (já ordenados por preço)
     * @return resultado da compra
     */
    private PurchaseResult processGreedyPurchase(Customer customer, List<Game> games) {
        List<Game> purchasedGames = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal currentBalance = customer.getBalance();
        
        for (Game game : games) {
            // Verifica se o saldo atual é suficiente para comprar o jogo
            if (currentBalance.compareTo(game.getPrice()) >= 0) {
                try {
                    // Atualiza o saldo local para próximas verificações
                    currentBalance = currentBalance.subtract(game.getPrice());
                    customer.deductBalance(game.getPrice());
                    customer.addPurchasedGame(game);
                    purchasedGames.add(game);
                    totalSpent = totalSpent.add(game.getPrice());
                } catch (Exception e) {
                    // Se houve erro, para a compra
                    break;
                }
            }
        }
        
        if (purchasedGames.isEmpty()) {
            return new PurchaseResult(false, "Nenhum jogo pôde ser comprado com o saldo disponível", 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
        
        try {
            customerService.updateCustomer(customer);
            return new PurchaseResult(true, 
                                    String.format("Comprou %d jogos com sucesso", purchasedGames.size()), 
                                    totalSpent, purchasedGames);
        } catch (Exception e) {
            return new PurchaseResult(false, "Erro ao salvar dados do cliente: " + e.getMessage(), 
                                    BigDecimal.ZERO, new ArrayList<>());
        }
    }

        /**
     * Valida se uma compra pode ser realizada.
     * 
     * @param customer cliente
     * @param game jogo
     * @return lista de erros de validação
     */
    private List<String> validatePurchase(Customer customer, Game game) {
        List<String> errors = new ArrayList<>();
        
        if (!game.isAvailable()) {
            errors.add("Jogo não está disponível");
        }
        
        if (!game.isAgeAppropriate(customer.getAge())) {
            errors.add("Jogo não é apropriado para a idade");
        }
        
        if (customer.ownsGame(game)) {
            errors.add("Cliente já possui este jogo");
        }
        
        if (!customer.hasSufficientBalance(game.getPrice())) {
            errors.add("Saldo insuficiente");
        }
        
        return errors;
    }

    /**
     * Classe que representa o resultado de uma compra.
     */
    public static class PurchaseResult {
        private final boolean success;
        private final String message;
        private final BigDecimal totalAmount;
        private final List<Game> purchasedGames;

        public PurchaseResult(boolean success, String message, BigDecimal totalAmount, List<Game> purchasedGames) {
            this.success = success;
            this.message = message;
            this.totalAmount = totalAmount;
            this.purchasedGames = new ArrayList<>(purchasedGames);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public List<Game> getPurchasedGames() { return List.copyOf(purchasedGames); }

        @Override
        public String toString() {
            return String.format("PurchaseResult{success=%s, message='%s', totalAmount=%s, gamesCount=%d}", 
                               success, message, totalAmount, purchasedGames.size());
        }
    }
}
