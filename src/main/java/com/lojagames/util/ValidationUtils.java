package com.lojagames.util;

/**
 * Validador para diferentes tipos de dados.
 */
public class ValidationUtils {
    
    // Construtor privado para classe utilitária
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Valida se uma string não é nula ou vazia.
     * 
     * @param value string a ser validada
     * @return true se é válida
     */
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Valida formato básico de email.
     * 
     * @param email email a ser validado
     * @return true se o formato é válido
     */
    public static boolean isValidEmail(String email) {
        if (!isValidString(email)) {
            return false;
        }
        
        return email.contains("@") && 
               email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".") &&
               email.indexOf("@") > 0 &&
               email.lastIndexOf(".") < email.length() - 1;
    }
    
    /**
     * Valida se uma idade está dentro de limites razoáveis.
     * 
     * @param age idade a ser validada
     * @return true se é válida
     */
    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 150;
    }
    
    /**
     * Valida se uma classificação etária é válida.
     * 
     * @param ageRating classificação etária
     * @return true se é válida
     */
    public static boolean isValidAgeRating(int ageRating) {
        return ageRating >= 0 && ageRating <= 18;
    }
    
    /**
     * Valida se uma string tem comprimento mínimo.
     * 
     * @param value string a ser validada
     * @param minLength comprimento mínimo
     * @return true se atende ao critério
     */
    public static boolean hasMinimumLength(String value, int minLength) {
        return isValidString(value) && value.trim().length() >= minLength;
    }
    
    /**
     * Valida se uma string tem comprimento máximo.
     * 
     * @param value string a ser validada
     * @param maxLength comprimento máximo
     * @return true se atende ao critério
     */
    public static boolean hasMaximumLength(String value, int maxLength) {
        return value != null && value.length() <= maxLength;
    }
    
    /**
     * Valida se um número está dentro de um intervalo.
     * 
     * @param value valor a ser validado
     * @param min valor mínimo (inclusivo)
     * @param max valor máximo (inclusivo)
     * @return true se está no intervalo
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Valida se um ID é válido (não nulo e positivo).
     * 
     * @param id ID a ser validado
     * @return true se é válido
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}
