package net.floodlightcontroller.fileuploaddown.type;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.fileuploaddown.FileUploadDownSerializer;
import net.floodlightcontroller.packet.Data;
import org.projectfloodlight.openflow.protocol.OFMessage;

@JsonSerialize(using= FileUploadDownSerializer.class)
public class FileData {
    private int code;
    private final Data data;
    private final IOFSwitch sw;
    private final OFMessage msg;

    public FileData(IOFSwitch sw, OFMessage msg,Data data, int code) {
        this.sw = sw;
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public IOFSwitch getSwitch() {
        return this.sw;
    }

    public OFMessage getMessage() {
        return this.msg;
    }

    public Data getData() {
        return this.data;
    }

    public int getCode(){
        return this.code;
    }
}
