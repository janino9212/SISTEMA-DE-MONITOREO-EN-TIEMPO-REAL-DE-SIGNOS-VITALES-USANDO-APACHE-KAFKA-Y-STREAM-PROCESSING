package co.edu.uts.vitales.common;

/**
 * Puntajes individuales (0 a 3) por signo vital, según la sección 2.4 del documento de requisitos (NEWS2).
 * Transcritos de memoria: verificar contra el documento oficial del Royal College of Physicians (2017).
 */
public final class Puntaje {

    private Puntaje() {
    }

    public static int frecuenciaCardiaca(int lpm) {
        if (lpm <= 40) return 3;
        if (lpm <= 50) return 1;
        if (lpm <= 90) return 0;
        if (lpm <= 110) return 1;
        if (lpm <= 130) return 2;
        return 3;
    }

    public static int presionSistolica(int mmHg) {
        if (mmHg <= 90) return 3;
        if (mmHg <= 100) return 2;
        if (mmHg <= 110) return 1;
        if (mmHg <= 219) return 0;
        return 3;
    }

    public static int temperatura(double celsius) {
        if (celsius <= 35.0) return 3;
        if (celsius <= 36.0) return 1;
        if (celsius <= 38.0) return 0;
        if (celsius <= 39.0) return 1;
        return 2;
    }

    /** Escala 1 de saturación de oxígeno. */
    public static int saturacion(int porcentaje) {
        if (porcentaje <= 91) return 3;
        if (porcentaje <= 93) return 2;
        if (porcentaje <= 95) return 1;
        return 0;
    }
}
