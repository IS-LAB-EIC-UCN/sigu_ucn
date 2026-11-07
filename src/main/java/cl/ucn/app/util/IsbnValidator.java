package cl.ucn.app.util;

public final class IsbnValidator {

    private IsbnValidator() {
    }

    public static boolean esValido(String isbn) {
        if (isbn == null) {
            return false;
        }

        String limpio = isbn.trim().replace("-", "").replace(" ", "").toUpperCase();

        if (limpio.length() == 10) {
            return validarIsbn10(limpio);
        } else if (limpio.length() == 13) {
            return validarIsbn13(limpio);
        }
        return false;
    }

    private static boolean validarIsbn10(String isbn) {
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) return false;
            suma += (c - '0') * (10 - i);
        }
        char ultimo = isbn.charAt(9);
        if (ultimo == 'X') {
            suma += 10;
        } else if (Character.isDigit(ultimo)) {
            suma += (ultimo - '0');
        } else {
            return false;
        }
        return (suma % 11 == 0);
    }

    private static boolean validarIsbn13(String isbn) {
        int suma = 0;
        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) return false;
            int digito = c - '0';
            suma += (i % 2 == 0) ? digito : digito * 3;
        }
        return (suma % 10 == 0);
    }
}
