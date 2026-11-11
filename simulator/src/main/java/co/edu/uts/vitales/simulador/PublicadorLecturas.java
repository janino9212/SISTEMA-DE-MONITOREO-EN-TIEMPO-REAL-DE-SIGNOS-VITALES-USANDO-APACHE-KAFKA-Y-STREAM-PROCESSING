package co.edu.uts.vitales.simulador;

import co.edu.uts.vitales.common.LecturaVital;
import co.edu.uts.vitales.common.Topicos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Publica un lote de lecturas en el topic "lecturas" en cada intervalo (RF-06). La clave es el paciente. */
@Component
class PublicadorLecturas {

    private static final Logger log = LoggerFactory.getLogger(PublicadorLecturas.class);

    private final KafkaTemplate<String, LecturaVital> plantilla;
    private final GeneradorLecturas generador;
    private final long inicioMs = System.currentTimeMillis();
    private final long crisisDespuesMs;
    private boolean crisisAnunciada;

    PublicadorLecturas(KafkaTemplate<String, LecturaVital> plantilla, SimuladorProperties props) {
        this.plantilla = plantilla;
        this.generador = new GeneradorLecturas(props.pacientes(), props.semilla(), props.idEscenario());
        this.crisisDespuesMs = props.crisisDemoDespuesDeSegundos() < 0
                ? -1
                : props.crisisDemoDespuesDeSegundos() * 1000L;
        log.info("Simulador listo: {} pacientes, escenario {}", props.pacientes(), props.idEscenario());
    }

    @Scheduled(fixedRateString = "${simulador.intervalo-ms}")
    void publicar() {
        long ahora = System.currentTimeMillis();
        boolean crisis = crisisDespuesMs >= 0 && ahora - inicioMs >= crisisDespuesMs;
        if (crisis && !crisisAnunciada) {
            log.info("Demostración: el paciente P-0001 entra en crisis (hipoxemia con taquicardia)");
            crisisAnunciada = true;
        }
        for (LecturaVital lectura : generador.siguienteLote(ahora, crisis)) {
            plantilla.send(Topicos.LECTURAS, lectura.idPaciente(), lectura);
        }
    }
}
