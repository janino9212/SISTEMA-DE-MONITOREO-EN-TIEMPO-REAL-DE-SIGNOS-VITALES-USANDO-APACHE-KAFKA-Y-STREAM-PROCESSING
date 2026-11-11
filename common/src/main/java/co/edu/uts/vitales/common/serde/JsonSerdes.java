package co.edu.uts.vitales.common.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

/** Serdes JSON para Kafka Streams, sin cabeceras de tipo (el tipo se fija en el código). */
public final class JsonSerdes {

    static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonSerdes() {
    }

    public static <T> Serde<T> de(Class<T> tipo) {
        Serializer<T> serializador = (topic, dato) -> {
            if (dato == null) {
                return null;
            }
            try {
                return MAPPER.writeValueAsBytes(dato);
            } catch (JsonProcessingException e) {
                throw new SerializationException("No se pudo serializar " + tipo.getSimpleName(), e);
            }
        };
        Deserializer<T> deserializador = (topic, bytes) -> {
            if (bytes == null) {
                return null;
            }
            try {
                return MAPPER.readValue(bytes, tipo);
            } catch (IOException e) {
                throw new SerializationException("No se pudo deserializar " + tipo.getSimpleName(), e);
            }
        };
        return Serdes.serdeFrom(serializador, deserializador);
    }
}
