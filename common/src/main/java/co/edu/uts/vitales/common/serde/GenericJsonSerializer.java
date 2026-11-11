package co.edu.uts.vitales.common.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/** Serializador JSON con constructor vacío, para configurarlo por nombre en application.yml. */
public class GenericJsonSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(String topic, T dato) {
        if (dato == null) {
            return null;
        }
        try {
            return JsonSerdes.MAPPER.writeValueAsBytes(dato);
        } catch (JsonProcessingException e) {
            throw new SerializationException("No se pudo serializar el evento", e);
        }
    }
}
