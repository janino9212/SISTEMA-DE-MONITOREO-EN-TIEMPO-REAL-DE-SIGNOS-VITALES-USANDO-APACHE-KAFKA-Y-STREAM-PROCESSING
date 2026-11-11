package co.edu.uts.vitales.procesador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class ProcesadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcesadorApplication.class, args);
    }
}
