package net.floodlightcontroller.assigntoAP;

public class LDP {

private double MDdemand ;
private double APBand;
private double mdLatency;  // 设备最大容允延迟
private double flowLatency; // 传输过程中的流延迟
private int existMDCount;//每个设备中已存在的设备数量
private int newMDCount; // 新加入的设备数
private double queueBacklog; // 节点中已存在的队列长度
private double outputQueue;
private double inputQueue;


private final  double  wd = 0.3, wl = 0.4, wa = 0.3;
private  final double penaltyConstant = 0.3;


    /**
     * 计算Q(t + 1)
     * @param queueBacklog:ap中存在的队列长度
     * @param inputQueue:AP接收到的数据
     *@param  outputQueue:AP发送的数据
     */
    public static double nextQueueBacklog(double queueBacklog, double inputQueue, double outputQueue) {
        return Math.max((queueBacklog - outputQueue), 0) + inputQueue;
    }



    public static double average(double s[]){
        double sum = 0;
        for (int i = 0;  i < s.length ;i++){
            sum += s[i];
        }
        return sum/s.length;
    }


/**  计算某个设备接入某个ap时的功能函数
 * @param  APBand 带宽
 * @param MDdemand 需求
 */
    public static double ud(double APBand, double MDdemand){
        return Math.log(1 + APBand / MDdemand);
    }

    /** 计算设备延迟的影响效果
     * @param mdLatency 最大延时
     * @param flowLatency 网络间的流延时
     */
    public static double ul(double mdLatency, double flowLatency){
        return Math.log(1 + mdLatency / flowLatency);
    }

    /**
     * ap的负载度量函数值
     * @param existMDCount  已存在的MD数量
     * @param newMDCount  新加入的MD数量
     * @return
     */
    public static double ua(int existMDCount, int newMDCount){
        return Math.log(1 + newMDCount / (existMDCount + newMDCount));
    }

    //normalize
    public static double normalize(double a[], int i){
        double sum = 0;
        for (int j = 0; j <a.length; j++){
            sum += a[j];
        }
        return a[i]/sum;
    }

    /**total utility  i连接j时的功能函数
     *
     */
public  double i2jUtility(){
    return wd*ud(this.APBand,this.MDdemand) + wl*ul(this.mdLatency,this.flowLatency) + wa*ua(this.existMDCount,this.newMDCount);
}

    /** Lyapunov drift  namely LDP=∆(L(t))−V u(i; j)-Vu
     *
     */
    public  double  lyapunovDrift(){
       return 0.5*( Math.sqrt(nextQueueBacklog(this.queueBacklog, this.inputQueue, this.outputQueue)) - Math.sqrt(this.queueBacklog)) - penaltyConstant * i2jUtility();
    }



    public LDP(double MDdemand, double APBand, double mdLatency, double flowLatency, int existMDCount, int newMDCount, double queueBacklog, double outputQueue, double inputQueue) {
        this.MDdemand = MDdemand;
        this.APBand = APBand;
        this.mdLatency = mdLatency;
        this.flowLatency = flowLatency;
        this.existMDCount = existMDCount;
        this.newMDCount = newMDCount;
        this.queueBacklog = queueBacklog;
        this.outputQueue = outputQueue;
        this.inputQueue = inputQueue;
    }

    public double getWd() {
        return wd;
    }

    public double getWl() {
        return wl;
    }

    public double getWa() {
        return wa;
    }

    public double getPenaltyConstant() {
        return penaltyConstant;
    }

    public double getMDdemand() {
        return MDdemand;
    }

    public void setMDdemand(double MDdemand) {
        this.MDdemand = MDdemand;
    }

    public double getAPBand() {
        return APBand;
    }

    public void setAPBand(double APBand) {
        this.APBand = APBand;
    }

    public double getmdLatency() {
        return mdLatency;
    }

    public void setmdLatency(double mdLatency) {
        this.mdLatency = mdLatency;
    }

    public double getFlowLatency() {
        return flowLatency;
    }

    public void setFlowLatency(double flowLatency) {
        this.flowLatency = flowLatency;
    }

    public int getExistMDCount() {
        return existMDCount;
    }

    public void setExistMDCount(int existMDCount) {
        this.existMDCount = existMDCount;
    }

    public int getNewMDCount() {
        return newMDCount;
    }

    public void setNewMDCount(int newMDCount) {
        this.newMDCount = newMDCount;
    }

    public double getQueueBacklog() {
        return queueBacklog;
    }

    public void setQueueBacklog(double queueBacklog) {
        this.queueBacklog = queueBacklog;
    }

    public double getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(double outputQueue) {
        this.outputQueue = outputQueue;
    }

    public double getInputQueue() {
        return inputQueue;
    }

    public void setInputQueue(double inputQueue) {
        this.inputQueue = inputQueue;
    }
}
