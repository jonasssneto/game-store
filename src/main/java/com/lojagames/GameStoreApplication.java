package com.lojagames;

import com.lojagames.controller.GameStoreController;
import com.lojagames.view.GameStoreView;
import com.lojagames.service.GameService;
import com.lojagames.service.CustomerService;
import com.lojagames.service.PurchaseService;
import com.lojagames.repository.GameRepository;
import com.lojagames.repository.CustomerRepository;
import com.lojagames.repository.InMemoryGameRepository;
import com.lojagames.repository.InMemoryCustomerRepository;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.math.BigDecimal;

/**
 * Classe principal da aplicação Game Store.
 * Responsável por configurar a injeção de dependências e inicializar a aplicação.
 */
public class GameStoreApplication {

    /**
     * Método principal da aplicação.
     * 
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        // Configura o Look and Feel
        configureLookAndFeel();
        
        // Executa na thread do Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Cria as dependências (Injeção manual de dependências)
                GameRepository gameRepository = createAndInitializeGameRepository();
                CustomerRepository customerRepository = createAndInitializeCustomerRepository();
                
                // Cria os serviços
                GameService gameService = new GameService(gameRepository);
                CustomerService customerService = new CustomerService(customerRepository);
                PurchaseService purchaseService = new PurchaseService(gameService, customerService);
                
                // Cria o controller
                GameStoreController controller = new GameStoreController(
                    gameService, customerService, purchaseService);
                
                // Cria um cliente padrão para demonstração
                createDefaultCustomer(controller);
                
                // Cria e exibe a interface gráfica
                new GameStoreView(controller);
                
                System.out.println("Game Store Application iniciada com sucesso!");
                
            } catch (Exception e) {
                System.err.println("Erro ao inicializar a aplicação: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    /**
     * Configura o Look and Feel da aplicação.
     */
    private static void configureLookAndFeel() {
        try {
            // Tenta usar o Look and Feel do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Não foi possível definir o Look and Feel do sistema. Usando padrão.");
        }
    }

    /**
     * Cria e inicializa o repositório de jogos com dados de exemplo.
     * 
     * @return repositório de jogos inicializado
     */
    private static GameRepository createAndInitializeGameRepository() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        repository.initializeWithSampleData();
        
        System.out.println("Repositório de jogos inicializado com " + 
                         repository.count() + " jogos.");
        
        return repository;
    }

    /**
     * Cria e inicializa o repositório de clientes com dados de exemplo.
     * 
     * @return repositório de clientes inicializado
     */
    private static CustomerRepository createAndInitializeCustomerRepository() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.initializeWithSampleData();
        
        System.out.println("Repositório de clientes inicializado com " + 
                         repository.count() + " clientes.");
        
        return repository;
    }

    /**
     * Cria um cliente padrão para demonstração.
     * 
     * @param controller controller da aplicação
     */
    private static void createDefaultCustomer(GameStoreController controller) {
        try {
            // Cria um cliente padrão para a demonstração
            controller.createDefaultCustomer(
                "Jonas Neto", 
                "jonas@gmail.com", 
                new BigDecimal("250.00"), 
                20
            );
            
            System.out.println("Cliente padrão criado para demonstração.");
            
        } catch (Exception e) {
            System.err.println("Erro ao criar cliente padrão: " + e.getMessage());
            
            // Tenta usar um cliente existente se houver
            try {
                controller.setCurrentCustomer(1L);
                System.out.println("Usando cliente existente com ID 1.");
            } catch (Exception ex) {
                System.err.println("Não foi possível definir cliente padrão.");
            }
        }
    }

    /**
     * Método para encerrar a aplicação graciosamente.
     */
    public static void shutdown() {
        System.out.println("Encerrando Game Store Application...");
        System.exit(0);
    }
}
