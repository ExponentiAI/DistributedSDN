package net.floodlightcontroller.monitordelay;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IMonitorDelayService extends IFloodlightService {
    /**
     * 获取链路之间的时间延迟
     * @return Map<MyEntry<NodePortTuple,NodePortTuple>,Integer> 链路：时延
     */
//  public Map<MyEntry<NodePortTuple,NodePortTuple>,Integer> getLinkDelay();
}
