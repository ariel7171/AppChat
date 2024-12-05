package com.example.appchat.util;

public class Validaciones {

    // Método para validar que el texto no esté vacío
    public static boolean validarTexto(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Método para validar que un número en formato String sea válido
    public static int validarNumero(String numero) {
        try {
            return Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            return -1; // Retornar -1 si el número no es válido
        }
    }

    // Método para validar un correo electrónico (ejemplo básico)
    public static boolean validarMail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para validar una contraseña
    public static String validarPass(String pass, String pass1) {
        if (pass.equals(pass1)) {
            return null; // Retornar null si ambas coinciden
        } else {
            return "Error de contraseña"; // Retornar mensaje si no coinciden
        }
    }

    // Método para controlar la fortaleza de la contraseña
    public static boolean controlarPassword(String pass) {
        // Ejemplo simple: longitud mínima de 8 caracteres
        //return pass != null && pass.length() >= 8;
        return pass != null && pass.length() >= 4;
    }

}
