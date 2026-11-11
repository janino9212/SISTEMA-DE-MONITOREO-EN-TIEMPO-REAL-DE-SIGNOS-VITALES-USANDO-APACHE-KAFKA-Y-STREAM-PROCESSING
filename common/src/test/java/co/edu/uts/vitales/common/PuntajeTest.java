package co.edu.uts.vitales.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PuntajeTest {

    @Test
    void frecuenciaCardiacaEnLosLimites() {
        int[][] casos = {{40, 3}, {41, 1}, {50, 1}, {51, 0}, {90, 0}, {91, 1}, {110, 1}, {111, 2}, {130, 2}, {131, 3}};
        for (int[] c : casos) {
            assertEquals(c[1], Puntaje.frecuenciaCardiaca(c[0]), "fc=" + c[0]);
        }
    }

    @Test
    void presionSistolicaEnLosLimites() {
        int[][] casos = {{90, 3}, {91, 2}, {100, 2}, {101, 1}, {110, 1}, {111, 0}, {219, 0}, {220, 3}};
        for (int[] c : casos) {
            assertEquals(c[1], Puntaje.presionSistolica(c[0]), "pas=" + c[0]);
        }
    }

    @Test
    void temperaturaEnLosLimites() {
        double[][] casos = {{35.0, 3}, {35.1, 1}, {36.0, 1}, {36.1, 0}, {38.0, 0}, {38.1, 1}, {39.0, 1}, {39.1, 2}};
        for (double[] c : casos) {
            assertEquals((int) c[1], Puntaje.temperatura(c[0]), "temp=" + c[0]);
        }
    }

    @Test
    void saturacionEnLosLimites() {
        int[][] casos = {{91, 3}, {92, 2}, {93, 2}, {94, 1}, {95, 1}, {96, 0}};
        for (int[] c : casos) {
            assertEquals(c[1], Puntaje.saturacion(c[0]), "spo2=" + c[0]);
        }
    }
}
