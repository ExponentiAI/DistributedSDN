package net.floodlightcontroller.flow;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class NetworkFlowResource extends ServerResource{
	@Get("json")
	public FlowEntryList retrieve(){
		INetworkFlowService ncs = (INetworkFlowService) getContext().getAttributes().get(INetworkFlowService.class.getCanonicalName());
		if(ncs.getEntries().size() != 0){
			return new FlowEntryList(ncs.getEntries());
		}
		return null;		
	}
}
