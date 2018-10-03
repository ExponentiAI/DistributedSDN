package net.floodlightcontroller.flow;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EntryListSerializer extends JsonSerializer<FlowEntryList>{

	@Override
	public void serialize(FlowEntryList fel, JsonGenerator jGen, SerializerProvider serializer)
			throws IOException, JsonProcessingException {
		
		jGen.configure(Feature.WRITE_NUMBERS_AS_STRINGS, true);
		
		if(fel == null){
			jGen.writeStartObject();
			jGen.writeString("Sorry, no flow entry to show");
			jGen.writeEndObject();
			return;
		}
		
		List<EntryInfo> theList = fel.getList();
		
		if(theList.isEmpty()){
			jGen.writeStartObject();
			jGen.writeString("Enpty List...");
			jGen.writeEndObject();
			return;
		}
		
		jGen.writeStartArray();
		for(EntryInfo e : theList){
			
			jGen.writeStartObject();
			jGen.writeFieldName(e.getSrcMac().toString());
			jGen.writeStartObject();
			
			//jGen.writeStringField("switchId", e.getToSw().getId().toString());
			jGen.writeNumberField("inPort", e.getInPort().getPortNumber());
			jGen.writeStringField("ethSrc", e.getSrcMac().toString());
			jGen.writeStringField("ethDst", e.getDstMac().toString());
			jGen.writeStringField("ipv4Src", e.getSrcIp().toString());
			jGen.writeStringField("ipv4Dst", e.getDstIp().toString());
			
			jGen.writeEndObject();
			jGen.writeEndObject();
		}
		jGen.writeEndArray();
	}
}
