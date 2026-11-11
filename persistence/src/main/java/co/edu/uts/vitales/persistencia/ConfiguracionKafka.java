package co.edu.uts.vitales.persistencia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
class ConfiguracionKafka {

    /**
     * Si TimescaleDB no responde, reintenta cada 5 segundos sin descartar nada (RNF-06):
     * los eventos siguen guardados en Kafka y se escriben cuando la base vuelve.
     */
    @Bean
    DefaultErrorHandler manejadorDeErrores() {
        return new DefaultErrorHandler(new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS));
    }
}
