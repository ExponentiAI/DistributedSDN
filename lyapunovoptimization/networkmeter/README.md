#Introduction

* networkmeter:(杨张凤)
   origin：算法
       LDP：通过采样获得的基础数据，计算ap中的队列阻塞情况，新设备加入时对带宽、延时等产生的相应影响
       Heuristic2：网络数据测量模块
   其余为获取数据代码
       HttpRequest：发送get post API请求
       BandMeter：下发statsrequest消息，获取带宽信息 
       NetworkMeter：NetworkMeterThread  网络测量模块
       NetworkStore：网络链路数据获取与保存
       TimeDelayMeter：网络延时获取
       PacketLossMeter：网络丢包


