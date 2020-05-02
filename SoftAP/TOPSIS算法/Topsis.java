package AP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class Topsis {
    public final static int D = 6;//指标数
    public final static double[] weight={9,7,7,8,6,5};	//各指标权重
    public static int T = 0;
    private static Scanner s;
    public static void main(String[] args) {
        List<Alternative> al =new LinkedList<Alternative>();
        //从文件录入数据
        try(BufferedReader br=new BufferedReader( new FileReader("data2.txt"))){
            String tmp=null;
            while((tmp=br.readLine())!=null){
                s = new Scanner(tmp);
                double[] data=new double[D];
                String num=s.next();
                for(int i=0;i<D;i++){
                    data[i]=s.nextDouble();
                }
                al.add(new Alternative(num,data));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        double x[]=new double[D],y[]=new double[D];
        for(int i=0;i<D;i++){
            x[i]=0;
            y[i]=0;
        }
        for(Alternative a:al){
            for(int i=0;i<D;i++){
                x[i]+=Math.pow(a.attribute[i],2);
            }
        }
        for(int i=0;i<D;i++){
            x[i]=Math.sqrt(x[i]);
        }
        for(Alternative a:al){
            for(int i=0;i<D;i++){
                a.attribute[i]=a.attribute[i]/x[i];
            }
            a.weighted();
        }
        //计算正负理想解
        for(int i=0;i<D;i++){
            for(Alternative a:al){
                a.comp=i;
            }
            x[i]=Collections.max(al).attribute[i];
            y[i]=Collections.min(al).attribute[i];
        }

        Alternative best = new Alternative("",x);
        Alternative worse = new Alternative("",y);
        //计算正负理想解的距离、贴进度
        ListIterator<Alternative> it = al.listIterator();
        while(it.hasNext()){
            Alternative t=it.next();
            t.bestdis=0;
            for(int j=0;j<D;j++){
                t.bestdis+=Math.pow(t.attribute[j]-best.attribute[j],2);
            }
            t.bestdis=Math.sqrt(t.bestdis);
        }
        it = al.listIterator();
        while(it.hasNext()){
            Alternative t=it.next();
            t.worsedis=0;
            for(int j=0;j<D;j++){
                t.worsedis+=Math.pow(t.attribute[j]-worse.attribute[j],2);
            }
            t.worsedis=Math.sqrt(t.worsedis);
            t.c=t.worsedis/(t.worsedis+t.bestdis);
        }
        //按照贴进度排序
        Collections.sort(al,new Comparator<Alternative>(){
            public int compare(Alternative a1, Alternative a2){
                return a2.c>a1.c?1:-1;
            }
        });
        for(Alternative a:al){
            System.out.println(a.num+" "+a.c);
        }
    }
}
