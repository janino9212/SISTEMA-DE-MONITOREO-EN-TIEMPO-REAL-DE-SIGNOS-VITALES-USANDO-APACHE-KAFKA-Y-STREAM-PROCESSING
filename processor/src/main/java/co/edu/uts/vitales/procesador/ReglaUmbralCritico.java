package co.edu.uts.vitales.procesador;

import co.edu.uts.vitales.common.Alerta;
import co.edu.uts.vitales.common.LecturaVital;
import co.edu.uts.vitales.common.Puntaje;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Regla A (RF-11): alerta inmediata cuando un signo tiene puntaje 3. */
final class ReglaUmbralCritico {

    static final String ID = "A_UMBRAL_CRITICO";

    private ReglaUmbralCritico() {
    }

    static List<Alerta> evaluar(LecturaVital l) {
        List<Alerta> alertas = new ArrayList<>();
        agregar(alertas, l, "fc", l.fc(), Puntaje.frecuenciaCardiaca(l.fc()));
        agregar(alertas, l, "pas", l.pas(), Puntaje.presionSistolica(l.pas()));
        agregar(alertas, l, "temp", l.temp(), Puntaje.temperatura(l.temp()));
        agregar(alertas, l, "spo2", l.spo2(), Puntaje.saturacion(l.spo2()));
        return alertas;
    }

    private static void agregar(List<Alerta> salida, LecturaVital l, String signo, double valor, int puntaje) {
        if (puntaje == 3) {
            salida.add(new Alerta(
                    UUID.randomUUID().toString(),
                    l.idPaciente(),
                    ID,
                    "CRITICA",
                    signo,
                    valor,
                    puntaje,
                    l.tEventoMs(),
                    System.currentTimeMillis(),
                    l.idEscenario()));
        }
    }
}
