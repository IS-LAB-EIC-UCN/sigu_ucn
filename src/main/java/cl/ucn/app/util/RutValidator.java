package cl.ucn.app.util;

public final class RutValidator {

    private RutValidator() {
    }

    public static boolean esValido(String rutCompleto) {
        if (rutCompleto == null) {
            return false;
        }

        String limpio = rutCompleto.trim().replace(".", "").replace("-", "").toUpperCase();
        if (limpio.length() < 2 || limpio.length() > 9) {
            return false;
        }

        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);

        try {
            int rutAux = Integer.parseInt(cuerpo);
            if (rutAux <= 0) {
                return false;
            }
            return dv == calcularDigitoVerificador(rutAux);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static char calcularDigitoVerificador(int rut) {
        int m = 0;
        int s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return (char) (s > 0 ? s + 47 : 75);
    }

    public static String formatear(String rutCompleto) {
        if (rutCompleto == null) {
            return "";
        }
        String limpio = rutCompleto.trim().replace(".", "").replace("-", "").toUpperCase();
        if (limpio.length() < 2) {
            return rutCompleto.trim();
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);

        StringBuilder sb = new StringBuilder();
        int contador = 0;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            sb.insert(0, cuerpo.charAt(i));
            contador++;
            if (contador == 3 && i > 0) {
                sb.insert(0, '.');
                contador = 0;
            }
        }
        sb.append('-').append(dv);
        return sb.toString();
    }
}
