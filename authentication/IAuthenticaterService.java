package net.floodlightcontroller.authenticater;
 
import net.floodlightcontroller.authenticater.types.AuthenticateMessage;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;
 
public interface IAuthenticaterService extends IFloodlightService {
    public ConcurrentCircularBuffer<AuthenticateMessage> getBuffer();
}