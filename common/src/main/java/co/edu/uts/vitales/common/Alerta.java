package co.edu.uts.vitales.common;

/** Evento de alerta (RF-14). tLecturaMs y tAlertaMs permiten calcular la latencia extremo a extremo. */
public record Alerta(
        String idAlerta,
        String idPaciente,
        String regla,
        String severidad,
        String signo,
        double valor,
        int puntaje,
        long tLecturaMs,
        long tAlertaMs,
        String idEscenario) {
}
