package net.floodlightcontroller.eastwestcommunication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFEchoReply;
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
import net.floodlightcontroller.restserver.IRestApiService;

public class EastWestCommunication implements IFloodlightModule,IOFMessageListener{
	
	protected IFloodlightProviderService floodlightProvider;
	private IRestApiService restApi;
	
	protected static EastWestCommunicationSengProcess nmt;
	
	protected IOFSwitchService  switchService;
	protected ILinkDiscoveryService linkService;
	//real work
	protected SendPacketOutMessage packetOutMessage;
	
	@Override
	public String getName() {
		return EastWestCommunication.class.getSimpleName();
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

		switch(msg.getType()){
		case PACKET_IN:
			Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			if(packetOutMessage.isDoingSendPacket(eth)){
				System.out.println("receive eastwestcomm packet in...");
				DealPacketInMessage ns = DealPacketInMessage.getInstance();
				ns.handlePacketIn(eth.getPayload());
			}
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
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		linkService = context.getServiceImpl(ILinkDiscoveryService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		this.packetOutMessage = new SendPacketOutMessage();
		nmt = new EastWestCommunicationSengProcess(this);
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new EastWestCommunicationRoutable());
	}
	
	public IOFSwitchService getSwitchService(){
		return switchService;
	}
	
	public ILinkDiscoveryService getLinkService() {
		return linkService;
	}

	public SendPacketOutMessage getPacketOutMessage() {
		return packetOutMessage;
	}

	public static EastWestCommunicationSengProcess getNmt() {
		return nmt;
	}
	
}
