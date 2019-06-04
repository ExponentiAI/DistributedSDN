package net.floodlightcontroller.networkcalculus;

public class MinPlusConvolution {
	
//	static final double[] f1 = {2.31, 4.61, 6.96, 9.31, 11.61, 13.96, 16.31, 18.65, 20.99, 23.33, 25.68, 28.01, 30.3, 32.63, 34.98,
//			                                 37.32, 39.65, 41.99, 44.34, 46.69, 48.42, 50.15, 51.85, 53.58, 55.32, 57.06, 58.8, 60.53, 62.26, 63.99,
//			                                 65.74, 67.49, 69.22, 70.95, 72.71, 74.45, 76.19, 77.94, 79.69, 81.44, 83.18, 84.92, 86.64, 88.38, 90.13,
//			                                 91.86, 93.6, 95.34, 97.07, 98.8, 100.53, 102.26, 104.02, 105.77, 107.51, 109.25, 110.99, 112.72, 114.45, 116.18};
	public static int Y = 0;
	
	static final double b1= 20.0;
	static final double t1= 10.0;
	
	static final double b2= 15.0;
	static final double t2= 600.0;
	
	static final double b3= 3.58;
	static final double t3= 2;
	
	static final double b4= 800.0;
	static final double t4= 2.0;
	
	static final double b5= 863.0;
	static final double t5= 2.0;
	
	static final double b6= 250000.0;
	static final double t6= 1.0;
	
	
	public static double Arrival(int x){
		return NetworkStore.arriveCurve.get(x);
	}
	
	public static double Server(int x){
		double y = b6*(x - t6);
		if(y <= 0)
			return 0;
		return y;
	}
	//最小加卷积
	public  double minPlusConvolution(int  t){
		double min = 9999999999.0;
		for(int i = 0; i <=t; i++){
			double Z = Arrival(i) + Server(t-i);
			if(Z <= min){
				min = Z;
			}
		}
		return min;
	}
	
//	public void changeService(boolean flag) {
//			
//	}
	
//	public static void main(String[] args) {
//		MinPlusConvolution mpc = new MinPlusConvolution();
//		double r = mpc.minPlusConvolution(60);	
////		for(int i = 0; i <r.length; i++){
////			System.out.println(i + " : " + r[i]);
////		}	
//		for(int i = 0; i < 60; i++){
//			System.out.println(i + ":" +Server(i));
//		}
//	}
}
