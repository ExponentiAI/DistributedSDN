package net.floodlightcontroller.fileuploaddown;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.floodlightcontroller.fileuploaddown.type.FileData;
import net.floodlightcontroller.packet.Data;

import java.io.IOException;

public class FileUploadDownSerializer extends JsonSerializer<FileData> {
    @Override
    public void serialize(FileData m, JsonGenerator jGen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeObject(m.getData());
        jGen.writeNumber(m.getCode());
        jGen.writeEndObject();
    }
}
