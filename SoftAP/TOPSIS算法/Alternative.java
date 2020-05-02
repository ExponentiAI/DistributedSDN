package AP;

public class Alternative implements Comparable<Alternative>{
    int comp;
    String num;
    double[] attribute=new double[Topsis.D];	//指标
    double bestdis,worsedis,c;	//与正、负理想解的欧式距离,c为贴进度
    public Alternative(String num,double[] d) {
        this.num=num;
        for(int i=0;i<Topsis.D;i++){
            this.attribute[i]=d[i];
        }
    }
    public void weighted(){		//赋权
        for(int i=0;i<Topsis.D;i++){
            this.attribute[i]*=Topsis.weight[i];
        }
    }
    public int compareTo(Alternative a) {
        return (int) (this.attribute[comp]-a.attribute[comp]);
    }
}
