package net.floodlightcontroller.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchBackend;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

public class NetworkFlow implements IFloodlightModule, IOFMessageListener, INetworkFlowService{
	
	protected IFloodlightProviderService floodlightProvider; 
	protected IOFSwitchService switchService;
	protected NetworkFlowThread nct;
	protected IRestApiService restApi;
	
	protected SendEntry sendEntry;

	@Override
	public String getName() {
		return NetworkFlow.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		//System.out.println("received packet_in...");
		return net.floodlightcontroller.core.IListener.Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(INetworkFlowService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(INetworkFlowService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		
		this.sendEntry = new SendEntry();
		
		nct = new NetworkFlowThread(this);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new NetworkFlowWebRoutable());
		nct.start();
	}

	public IOFSwitchService getSwitchService() {
		return switchService;
	}

	public SendEntry getSendEntry() {
		return sendEntry;
	}
	
	public static void handleFlowStatsReply(OFFlowStatsReply reply, IOFSwitchBackend sw){
		PacketProcess pp = PacketProcess.getInstance();
		pp.handleFlowStatsReply(reply, sw);
	}

	@Override
	public List<EntryInfo> getEntries() {
		PacketProcess pp = PacketProcess.getInstance();
		return pp.storeInfos;
	}

}
