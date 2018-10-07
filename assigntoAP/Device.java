package net.floodlightcontroller.assigntoAP;

import java.util.Comparator;

public class Device implements Comparable<Device>{
	 final int id;
	 double[] profit;
	 double[] demand;
	 
	 //this is used for sorting a list of devices based on their efficiency (or ratio)
	 double ratio;
	 
	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public Device(int id, double[] p, double[] d){
		this.id = id;
		if(d.length != p.length){
			throw (new RuntimeException("Unequal profit and demand size.")) ;
		}			
		profit = new double[p.length];
		demand = new double[d.length];
	
		for(int i = 0; i < Math.max(p.length, d.length); i++){
			profit[i] = p[i];
			demand[i] = d[i];
		}
	}
	
	public double[] getProfit() {
		return profit;
	}

	public double[] getDemand() {
		return demand;
	}

	public double getDemandForAp(int apId){
		return demand[apId];
	}

	@Override
	public int compareTo(Device o) {
		return (this.id == o.id) ? 0:((this.id > o.id)?1:-1);
	}
	
	//TODO: hashCode should be overriden
	
	static final Comparator<Device> RATIO_ORDER = 
            new Comparator<Device>() {
				public int compare(Device d1, Device d2) {
					return (d2.getRatio() == d1.getRatio()) ? 0:((d2.getRatio() > d1.getRatio())?1:-1);
				}
	};

	public int getId() {
		return id;
	}

}
