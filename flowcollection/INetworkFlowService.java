package net.floodlightcontroller.flow;

import java.util.List;
import net.floodlightcontroller.core.module.IFloodlightService;

public interface INetworkFlowService extends IFloodlightService {
	public List<EntryInfo> getEntries();
}
