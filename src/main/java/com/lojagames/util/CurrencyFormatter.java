package com.lojagames.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitário para formatação de valores monetários.
 */
public class CurrencyFormatter {
    
    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(PT_BR);
    
    // Construtor privado para classe utilitária
    private CurrencyFormatter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Formata um valor BigDecimal como moeda brasileira.
     * 
     * @param value valor a ser formatado
     * @return string formatada como moeda (ex: "R$ 89,90")
     */
    public static String formatBRL(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }
        return CURRENCY_FORMAT.format(value);
    }
    
    /**
     * Formata um valor double como moeda brasileira.
     * 
     * @param value valor a ser formatado
     * @return string formatada como moeda
     */
    public static String formatBRL(double value) {
        return formatBRL(BigDecimal.valueOf(value));
    }
    
    /**
     * Remove formatação de moeda e converte para BigDecimal.
     * 
     * @param formattedValue valor formatado (ex: "R$ 89,90")
     * @return BigDecimal correspondente
     * @throws IllegalArgumentException se o formato for inválido
     */
    public static BigDecimal parseBRL(String formattedValue) {
        if (formattedValue == null || formattedValue.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Remove símbolos de moeda e espaços
            String cleanValue = formattedValue
                    .replace("R$", "")
                    .replace(" ", "")
                    .replace(".", "")  // Remove separador de milhares
                    .replace(",", "."); // Troca vírgula decimal por ponto
            
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid currency format: " + formattedValue, e);
        }
    }
}
