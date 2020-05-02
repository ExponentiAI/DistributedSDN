package net.floodlightcontroller.authenticater;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.floodlightcontroller.authenticater.types.AuthenticateMessage;


import java.io.IOException;

public class AuthenticateSerializer extends JsonSerializer<AuthenticateMessage> {

    @Override
    public void serialize(AuthenticateMessage m, JsonGenerator jGen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeNumberField("code", m.getCode());
        jGen.writeEndObject();
    }
}