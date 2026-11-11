package co.edu.uts.vitales.persistencia;

import co.edu.uts.vitales.common.Alerta;
import co.edu.uts.vitales.common.LecturaVital;
import co.edu.uts.vitales.common.Topicos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Escribe lecturas y alertas en TimescaleDB, por lotes (RF-19, RF-20).
 * La escritura es idempotente: ON CONFLICT DO NOTHING evita duplicados si un evento se reprocesa (RF-23).
 */
@Component
class ConsumidorEventos {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorEventos.class);

    private static final String SQL_LECTURA =
            "INSERT INTO lecturas (t, id_paciente, fc, pas, pad, temp, spo2, id_escenario) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id_paciente, t) DO NOTHING";

    private static final String SQL_ALERTA =
            "INSERT INTO alertas (id_alerta, t_alerta, t_lectura, id_paciente, regla, severidad, signo, valor, puntaje, id_escenario) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id_alerta, t_alerta) DO NOTHING";

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private long totalLecturas;
    private long totalAlertas;

    ConsumidorEventos(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @KafkaListener(topics = Topicos.LECTURAS, groupId = "persistencia-lecturas", batch = "true")
    void alRecibirLecturas(List<String> mensajes) {
        List<LecturaVital> lecturas = interpretar(mensajes, LecturaVital.class);
        if (lecturas.isEmpty()) {
            return;
        }
        jdbc.batchUpdate(SQL_LECTURA, lecturas, lecturas.size(), (ps, l) -> {
            ps.setTimestamp(1, Timestamp.from(Instant.ofEpochMilli(l.tEventoMs())));
            ps.setString(2, l.idPaciente());
            ps.setInt(3, l.fc());
            ps.setInt(4, l.pas());
            ps.setInt(5, l.pad());
            ps.setDouble(6, l.temp());
            ps.setInt(7, l.spo2());
            ps.setString(8, l.idEscenario());
        });
        long antes = totalLecturas;
        totalLecturas += lecturas.size();
        if (totalLecturas / 1000 > antes / 1000) {
            log.info("Lecturas procesadas: {}", totalLecturas);
        }
    }

    @KafkaListener(topics = Topicos.ALERTAS, groupId = "persistencia-alertas", batch = "true")
    void alRecibirAlertas(List<String> mensajes) {
        List<Alerta> alertas = interpretar(mensajes, Alerta.class);
        if (alertas.isEmpty()) {
            return;
        }
        jdbc.batchUpdate(SQL_ALERTA, alertas, alertas.size(), (ps, a) -> {
            ps.setString(1, a.idAlerta());
            ps.setTimestamp(2, Timestamp.from(Instant.ofEpochMilli(a.tAlertaMs())));
            ps.setTimestamp(3, Timestamp.from(Instant.ofEpochMilli(a.tLecturaMs())));
            ps.setString(4, a.idPaciente());
            ps.setString(5, a.regla());
            ps.setString(6, a.severidad());
            ps.setString(7, a.signo());
            ps.setDouble(8, a.valor());
            ps.setInt(9, a.puntaje());
            ps.setString(10, a.idEscenario());
        });
        totalAlertas += alertas.size();
        log.info("Alertas guardadas: {} (total {})", alertas.size(), totalAlertas);
    }

    /** Los mensajes que no se pueden leer se omiten con una advertencia, para no bloquear el flujo. */
    private <T> List<T> interpretar(List<String> mensajes, Class<T> tipo) {
        List<T> resultado = new ArrayList<>(mensajes.size());
        for (String json : mensajes) {
            try {
                resultado.add(mapper.readValue(json, tipo));
            } catch (JsonProcessingException e) {
                log.warn("Mensaje omitido, no es un {} válido: {}", tipo.getSimpleName(), e.getOriginalMessage());
            }
        }
        return resultado;
    }
}
