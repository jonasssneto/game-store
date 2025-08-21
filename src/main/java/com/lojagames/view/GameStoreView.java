package com.lojagames.view;

import com.lojagames.controller.GameStoreController;
import com.lojagames.model.Game;
import com.lojagames.service.PurchaseService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interface gráfica da loja de jogos.
 */
public class GameStoreView extends JFrame {
    
    private final GameStoreController controller;
    
    // Componentes da interface
    private JList<Game> availableGamesList;
    private JList<Game> purchasedGamesList;
    private JLabel customerInfoLabel;
    private JLabel balanceLabel;
    private JLabel statsLabel;
    private DefaultListModel<Game> availableGamesModel;
    private DefaultListModel<Game> purchasedGamesModel;
    
    // Botões
    private JButton buySelectedButton;
    private JButton buyMaximumButton;
    private JButton addBalanceButton;
    private JButton refreshButton;

    public GameStoreView(GameStoreController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        
        this.controller = controller;
        initializeComponents();
        setupEventListeners();
        refreshData();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Inicializa os componentes da interface.
     */
    private void initializeComponents() {
        // Configuração da janela
        setTitle("Loja de Games");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Painel superior - informações do cliente
        createCustomerInfoPanel();
        
        // Painel central - listas de jogos
        createGameListsPanel();
        
        // Painel inferior - botões de ação
        createActionButtonsPanel();
    }

    /**
     * Cria o painel de informações do cliente.
     */
    private void createCustomerInfoPanel() {
        JPanel customerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Informações do Cliente"));
        
        customerInfoLabel = new JLabel("Cliente: Carregando...", JLabel.CENTER);
        customerInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        balanceLabel = new JLabel("Saldo: R$ 0,00", JLabel.CENTER);
        balanceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        balanceLabel.setForeground(new Color(0, 120, 0));
        
        statsLabel = new JLabel("Estatísticas: Carregando...", JLabel.CENTER);
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        
        customerPanel.add(customerInfoLabel);
        customerPanel.add(balanceLabel);
        customerPanel.add(statsLabel);
        
        add(customerPanel, BorderLayout.NORTH);
    }

    /**
     * Cria o painel com as listas de jogos.
     */
    private void createGameListsPanel() {
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Lista de jogos disponíveis
        availableGamesModel = new DefaultListModel<>();
        availableGamesList = new JList<>(availableGamesModel);
        availableGamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableGamesList.setCellRenderer(new GameListCellRenderer());
        
        JScrollPane availableScrollPane = new JScrollPane(availableGamesList);
        availableScrollPane.setPreferredSize(new Dimension(450, 400));
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Jogos Disponíveis"));
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);
        
        // Lista de jogos comprados
        purchasedGamesModel = new DefaultListModel<>();
        purchasedGamesList = new JList<>(purchasedGamesModel);
        purchasedGamesList.setCellRenderer(new GameListCellRenderer());
        
        JScrollPane purchasedScrollPane = new JScrollPane(purchasedGamesList);
        purchasedScrollPane.setPreferredSize(new Dimension(450, 400));
        
        JPanel purchasedPanel = new JPanel(new BorderLayout());
        purchasedPanel.setBorder(BorderFactory.createTitledBorder("Jogos Comprados"));
        purchasedPanel.add(purchasedScrollPane, BorderLayout.CENTER);
        
        listsPanel.add(availablePanel);
        listsPanel.add(purchasedPanel);
        
        add(listsPanel, BorderLayout.CENTER);
    }

    /**
     * Cria o painel de botões de ação.
     */
    private void createActionButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        buySelectedButton = new JButton("Comprar Selecionado");
        buySelectedButton.setPreferredSize(new Dimension(180, 35));
        buySelectedButton.setBackground(new Color(52, 152, 219));
        buySelectedButton.setForeground(Color.WHITE);
        buySelectedButton.setFocusPainted(false);
        
        buyMaximumButton = new JButton("Comprar Máximo");
        buyMaximumButton.setPreferredSize(new Dimension(150, 35));
        buyMaximumButton.setBackground(new Color(46, 204, 113));
        buyMaximumButton.setForeground(Color.WHITE);
        buyMaximumButton.setFocusPainted(false);
        
        addBalanceButton = new JButton("Adicionar Saldo");
        addBalanceButton.setPreferredSize(new Dimension(150, 35));
        addBalanceButton.setBackground(new Color(155, 89, 182));
        addBalanceButton.setForeground(Color.WHITE);
        addBalanceButton.setFocusPainted(false);
        
        refreshButton = new JButton("Atualizar");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setBackground(new Color(52, 73, 94));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        buttonsPanel.add(buySelectedButton);
        buttonsPanel.add(buyMaximumButton);
        buttonsPanel.add(addBalanceButton);
        buttonsPanel.add(refreshButton);
        
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Configura os listeners de eventos.
     */
    private void setupEventListeners() {
        // Duplo clique para comprar jogo
        availableGamesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    buySelectedGame();
                }
            }
        });
        
        // Botão comprar selecionado
        buySelectedButton.addActionListener(e -> buySelectedGame());
        
        // Botão comprar máximo
        buyMaximumButton.addActionListener(e -> buyMaximumGames());
        
        // Botão adicionar saldo
        addBalanceButton.addActionListener(e -> showAddBalanceDialog());
        
        // Botão atualizar
        refreshButton.addActionListener(e -> refreshData());
    }

    /**
     * Compra o jogo selecionado.
     */
    private void buySelectedGame() {
        Game selectedGame = availableGamesList.getSelectedValue();
        if (selectedGame == null) {
            showWarningMessage("Selecione um jogo para comprar.");
            return;
        }
        
        if (controller.currentCustomerOwnsGame(selectedGame)) {
            showWarningMessage("Você já possui este jogo.");
            return;
        }
        
        PurchaseService.PurchaseResult result = controller.purchaseGame(selectedGame);
        
        if (result.isSuccess()) {
            showSuccessMessage("Compra realizada com sucesso!\n" + 
                             "Jogo: " + selectedGame.getName());
            refreshData();
        } else {
            showErrorMessage("Erro na compra: " + result.getMessage());
        }
    }

    /**
     * Compra o máximo de jogos possível.
     */
    private void buyMaximumGames() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Deseja comprar o máximo de jogos possível com seu saldo atual?",
            "Confirmar Compra Máxima",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            PurchaseService.PurchaseResult result = controller.purchaseMaximumGames();
            
            if (result.isSuccess()) {
                showSuccessMessage(String.format(
                    "Compra realizada com sucesso!\n" +
                    "Jogos comprados: %d\n" +
                    "Valor total: %s",
                    result.getPurchasedGames().size(),
                    result.getTotalAmount()
                ));
                refreshData();
            } else {
                showErrorMessage("Erro na compra: " + result.getMessage());
            }
        }
    }

    /**
     * Mostra dialog para adicionar saldo.
     */
    private void showAddBalanceDialog() {
        String input = JOptionPane.showInputDialog(
            this,
            "Digite o valor a ser adicionado:",
            "Adicionar Saldo",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(input.replace(",", "."));
                
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    showErrorMessage("O valor deve ser positivo.");
                    return;
                }
                
                boolean success = controller.addBalanceToCurrentCustomer(amount);
                
                if (success) {
                    showSuccessMessage("Saldo adicionado com sucesso!");
                    refreshData();
                } else {
                    showErrorMessage("Erro ao adicionar saldo.");
                }
                
            } catch (NumberFormatException e) {
                showErrorMessage("Valor inválido. Use apenas números.");
            }
        }
    }

    /**
     * Atualiza todos os dados da interface.
     */
    private void refreshData() {
        updateCustomerInfo();
        updateGameLists();
    }

    /**
     * Atualiza informações do cliente.
     */
    private void updateCustomerInfo() {
        if (controller.getCurrentCustomer() != null) {
            customerInfoLabel.setText("Cliente: " + controller.getCurrentCustomer().getName());
            balanceLabel.setText("Saldo: " + controller.getCurrentCustomerFormattedBalance());
            statsLabel.setText(controller.getCurrentCustomerStats());
        } else {
            customerInfoLabel.setText("Nenhum cliente selecionado");
            balanceLabel.setText("Saldo: R$ 0,00");
            statsLabel.setText("Estatísticas: N/A");
        }
    }

    /**
     * Atualiza as listas de jogos.
     */
    private void updateGameLists() {
        // Atualiza jogos disponíveis
        availableGamesModel.clear();
        List<Game> availableGames = controller.getAgeAppropriateGames();
        for (Game game : availableGames) {
            if (!controller.currentCustomerOwnsGame(game)) {
                availableGamesModel.addElement(game);
            }
        }
        
        // Atualiza jogos comprados
        purchasedGamesModel.clear();
        List<Game> purchasedGames = controller.getPurchasedGames();
        for (Game game : purchasedGames) {
            purchasedGamesModel.addElement(game);
        }
    }

    /**
     * Mostra mensagem de sucesso.
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostra mensagem de aviso.
     */
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Mostra mensagem de erro.
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Renderer customizado para a lista de jogos.
     */
    private static class GameListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Game) {
                Game game = (Game) value;
                setText(String.format("<html><b>%s</b><br>%s - %s<br><font color='green'>%s</font></html>",
                       game.getName(),
                       game.getCategory(),
                       game.getAgeRating() + "+",
                       "R$ " + game.getPrice()));
                setToolTipText(game.getDescription());
            }
            
            return this;
        }
    }
}
