package net.floodlightcontroller.assigntoAP;

public class AccessPoint {
	final int id;
	public int getId() {
		return id;
	}
	double capacity;
	
	public AccessPoint(int id, double c){
		this.id = id;
		this.capacity = c;
	}
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
