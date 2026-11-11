package co.edu.uts.vitales.simulador;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuración del simulador (RF-02, RF-07). crisisDemoDespuesDeSegundos < 0 desactiva la crisis de demostración. */
@ConfigurationProperties(prefix = "simulador")
public record SimuladorProperties(
        int pacientes,
        long semilla,
        String idEscenario,
        long intervaloMs,
        int crisisDemoDespuesDeSegundos) {
}
