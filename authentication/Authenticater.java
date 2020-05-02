package net.floodlightcontroller.authenticater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.authenticater.types.AuthenticateMessage;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.IpProtocol;

import net.floodlightcontroller.authenticater.utils.MD5Utils;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;

public class Authenticater implements IFloodlightModule, IAuthenticaterService, IOFMessageListener {
    private static final int g=2;
    private static final int p=10001;
    private static final double X=123456;
    private static final String Y= MD5Utils.stringToMD5(Math.pow(g,X)%p+"");//md5加密后的k值

	protected IFloodlightProviderService floodlightProvider;
	protected ConcurrentCircularBuffer<AuthenticateMessage> buffer;
	protected IRestApiService restApi;
    private Set<Long> macAddress;

    @Override
    public ConcurrentCircularBuffer<AuthenticateMessage> getBuffer() {
        return buffer;
    }
    @Override
    public String getName() {
    	 return "Authenticater";
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
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        if (msg.getType() != OFType.PACKET_IN) {
            return Command.CONTINUE;
        }
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        Long sourceMACHash = eth.getSourceMACAddress().getLong();
        /** 返回消息:code (1=认证成功，0=认证失败) **/
        if (!macAddress.contains(sourceMACHash)) {//未登录状态
	        IPacket p = eth.getPayload();
	        if (!(p instanceof IPv4)) {
	            return Command.CONTINUE;
	        }
	        
	        IPv4 iPv4=(IPv4)p;
	        if (iPv4.getProtocol()!=IpProtocol.TCP) {
	        	return Command.CONTINUE;
			}
	        
	        TCP tcp=(TCP)iPv4.getPayload();
	        Data data=(Data)tcp.getPayload();
	        
	        //输入其他网址，未附带密码
	        if (data==null) {
	        	//TODO 重定向到http://192.168.1.1:8080/ui/pages/authenticate.html
			}
	        byte[] data2 = data.getData();
	        String ymd = new String(data2);
            if (Y.equals(ymd)){
            	//提示前端当前已登录成功
                buffer.add(new AuthenticateMessage(sw, msg,1));
                //将该主机mac加入集合，表示已认证
                macAddress.add(sourceMACHash);
                return Command.CONTINUE;
            }else {
            	//提示前端当前已登录失败
                buffer.add(new AuthenticateMessage(sw, msg,0));
                return Command.STOP;
            }
	    }else {//已登录状态
			return Command.CONTINUE;
		}
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
    	Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IAuthenticaterService.class);
        return l;
    }
 
    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
    	Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(IAuthenticaterService.class, this);
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
        restApi = context.getServiceImpl(IRestApiService.class);
        buffer = new ConcurrentCircularBuffer<AuthenticateMessage>(AuthenticateMessage.class, 100);
        macAddress=new HashSet<>();
    }
 
    @Override
    public void startUp(FloodlightModuleContext context) {
    	floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        restApi.addRestletRoutable(new AuthenticaterWebRoutable());
    }
}