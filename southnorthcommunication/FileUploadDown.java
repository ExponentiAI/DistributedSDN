package net.floodlightcontroller.fileuploaddown;


import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.fileuploaddown.type.FileData;
import net.floodlightcontroller.packet.*;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileUploadDown implements IFloodlightModule, IOFMessageListener, IFileUploadDownService{
    protected IFloodlightProviderService floodlightProvider;
    protected ConcurrentCircularBuffer<FileData> buffer;
    protected IRestApiService restApi;
    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        switch (msg.getType()) {
            case PACKET_IN: {
                Ethernet eth =
                        IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);


                if (eth.getEtherType() == EthType.IPv4) {
                    IPv4 ipv4 = (IPv4) eth.getPayload();
                    byte[] ipOptions = ipv4.getOptions();

                    if (ipv4.getProtocol() == IpProtocol.TCP) {
                        TCP tcp = (TCP) ipv4.getPayload();


                        Data dataPktTcp = (Data) tcp.getPayload();
                        System.out.println(dataPktTcp.getData().length);
                        byte[] arrtcp = dataPktTcp.getData();
                        for (int i = 0; i < dataPktTcp.getData().length; i++) {
                            System.out.print((char) arrtcp[i]);
                        }
                        }else if(ipv4.getProtocol() == IpProtocol.UDP) {
                            UDP udp = (UDP) ipv4.getPayload();


                            Data dataPktUdp = (Data) udp.getPayload();
                            System.out.println(dataPktUdp.getData().length);
                            byte[] arrudp = dataPktUdp.getData();
                            for (int i = 0; i < dataPktUdp.getData().length; i++) {
                                System.out.print((char) arrudp[i]);
}
                        }
                                } else if (eth.getEtherType() == EthType.ARP) {
                                ARP arp = (ARP) eth.getPayload();
                                boolean gratuitous = arp.isGratuitous();
                                }
                                break;
                                }

        default:
        break;
        }
            return Command.CONTINUE;
        }


    @Override
    public String getName() {
        return FileUploadDown.class.getSimpleName();
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
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFileUploadDownService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(IFileUploadDownService.class, this);
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
        buffer = new ConcurrentCircularBuffer<FileData>(FileData.class, 100);
        restApi = context.getServiceImpl(IRestApiService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        restApi.addRestletRoutable(new FileUploadDownWebRoutable());
    }

    @Override
    public ConcurrentCircularBuffer<FileData> getBuffer() {
        return buffer;
    }
}
