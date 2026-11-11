package co.edu.uts.vitales.procesador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.uts.vitales.common.Alerta;
import co.edu.uts.vitales.common.LecturaVital;
import co.edu.uts.vitales.common.Topicos;
import co.edu.uts.vitales.common.serde.JsonSerdes;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.Test;

class TopologiaVitalesTest {

    private static LecturaVital lectura(int fc, int pas, double temp, int spo2) {
        return new LecturaVital("P-0001", 1_000L, fc, pas, 80, temp, spo2, "test");
    }

    @Test
    void reglaA_soloAlertaConPuntajeTres() {
        StreamsBuilder builder = new StreamsBuilder();
        TopologiaVitales.construir(builder);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

        try (TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            TestInputTopic<String, LecturaVital> entrada = driver.createInputTopic(
                    Topicos.LECTURAS,
                    Serdes.String().serializer(),
                    JsonSerdes.de(LecturaVital.class).serializer());
            TestOutputTopic<String, Alerta> salida = driver.createOutputTopic(
                    Topicos.ALERTAS,
                    Serdes.String().deserializer(),
                    JsonSerdes.de(Alerta.class).deserializer());

            entrada.pipeInput("P-0001", lectura(75, 120, 36.8, 98));   // normal
            entrada.pipeInput("P-0001", lectura(75, 120, 36.8, 92));   // spo2 con puntaje 2: no alerta
            assertTrue(salida.isEmpty());

            entrada.pipeInput("P-0001", lectura(75, 120, 36.8, 88));   // spo2 con puntaje 3
            List<Alerta> alertas = salida.readValuesToList();
            assertEquals(1, alertas.size());
            assertEquals("spo2", alertas.get(0).signo());
            assertEquals(ReglaUmbralCritico.ID, alertas.get(0).regla());
        }
    }
}
