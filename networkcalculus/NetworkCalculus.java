package net.floodlightcontroller.networkcalculus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFEchoReply;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;
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
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.packet.Ethernet;

public class NetworkCalculus implements IFloodlightModule,IOFMessageListener{
	
	protected IFloodlightProviderService floodlightProvider;
	
	protected NetworkCalculusThread nmt;
	
	protected IOFSwitchService  switchService;
	protected ILinkDiscoveryService linkService;
	//real work
	protected SendPortRequest sendPortRequest;
	
	@Override
	public String getName() {
		return NetworkCalculus.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return (type.equals(OFType.PACKET_IN)&&(name.equals("linkdiscovery")));
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		System.out.println("receive packet in...");
		switch(msg.getType()){
		case PACKET_IN:
			Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
				NetworkStore ns = NetworkStore.getInstance();
			break;
		}
		return net.floodlightcontroller.core.IListener.Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		linkService = context.getServiceImpl(ILinkDiscoveryService.class);
		this.sendPortRequest = new SendPortRequest();

		nmt = new NetworkCalculusThread(this);
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		nmt.start();
	}
	
	public IOFSwitchService getSwitchService(){
		return switchService;
	}

	public SendPortRequest getPacketLossMeter() {
		return sendPortRequest;
	}

	public ILinkDiscoveryService getLinkService() {
		return linkService;
	}

	public static void handlePortStatsReply(OFPortStatsReply reply,IOFSwitchBackend sw){
		NetworkStore ns = NetworkStore.getInstance();
		ns.handlePortStatsReply(reply, sw);
	}
}
