package co.edu.uts.vitales.procesador;

import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Conecta la topología con el StreamsBuilder que gestiona Spring Kafka. */
@Component
class ConfiguracionTopologia {

    @Autowired
    void configurar(StreamsBuilder builder) {
        TopologiaVitales.construir(builder);
    }
}
