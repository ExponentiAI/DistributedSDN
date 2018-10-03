package net.floodlightcontroller.flow;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=EntryListSerializer.class)
public class FlowEntryList {
	
	private List<EntryInfo> theList;
	
	public FlowEntryList(List<EntryInfo> theList){
		this.theList = theList;
	}
	
	public List<EntryInfo> getList(){
		return theList;
	}
}
