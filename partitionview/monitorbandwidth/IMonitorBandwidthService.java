package net.floodlightcontroller.monitorbandwidth;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;

import java.util.Map;

public interface IMonitorBandwidthService extends IFloodlightService {
    //带宽使用情况
    public Map<NodePortTuple, SwitchPortBandwidth> getBandwidthMap();
}
