package co.edu.uts.vitales.common;

/**
 * Evento de lectura de signos vitales (RF-05). La clave del mensaje en Kafka es idPaciente.
 * tEventoMs es el momento de la lectura (event time), en milisegundos epoch.
 */
public record LecturaVital(
        String idPaciente,
        long tEventoMs,
        int fc,
        int pas,
        int pad,
        double temp,
        int spo2,
        String idEscenario) {
}
