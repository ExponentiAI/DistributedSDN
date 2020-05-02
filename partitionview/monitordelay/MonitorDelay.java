package net.floodlightcontroller.monitordelay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.floodlightcontroller.linkdiscovery.Link;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.internal.LinkInfo;
import net.floodlightcontroller.threadpool.IThreadPoolService;

public class MonitorDelay implements IFloodlightModule, IOFMessageListener, IMonitorDelayService,IOFSwitchListener {
    private IFloodlightProviderService floodlightProviderService;
    private IOFSwitchService switchService;
    private ILinkDiscoveryService linkDiscoveryService;
    private IThreadPoolService threadPoolServcie;

    @Override
    public String getName() {

        return MonitorDelay.class.getSimpleName();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg,
                           FloodlightContext cntx) {

        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {

        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IMonitorDelayService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> l = new HashMap<>();
        l.put(IMonitorDelayService.class, this);
        return l;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IThreadPoolService.class);
        l.add(IOFSwitchService.class);
        l.add(ILinkDiscoveryService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        switchService = context.getServiceImpl(IOFSwitchService.class);
        linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
        threadPoolServcie = context.getServiceImpl(IThreadPoolService.class);
    }


    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        testFunc();
    }

    @Override
    public void switchAdded(DatapathId switchId) {

    }

    @Override
    public void switchRemoved(DatapathId switchId) {

    }

    @Override
    public void switchActivated(DatapathId switchId) {

    }

    @Override
    public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {

    }

    @Override
    public void switchChanged(DatapathId switchId) {

    }

    @Override
    public void switchDeactivated(DatapathId switchId) {

    }


    public void testFunc(){
        Map<Link,LinkInfo> linkInfo = linkDiscoveryService.getLinks();
        Iterator<Entry<Link, LinkInfo>> iter = linkInfo.entrySet().iterator();
        while(iter.hasNext()){
            Entry<Link, LinkInfo> node = iter.next();
            System.out.println("源交换机:"+node.getKey().getSrc().toString()+",源端口："+node.getKey().getSrcPort());
            System.out.println("目的交换机:"+node.getKey().getDst().toString()+",目的端口："+node.getKey().getDstPort());
            System.out.println("链路时延:"+node.getKey().getLatency());
            System.out.println("当前时延:"+node.getValue().getCurrentLatency());
        }
    }


}
