package net.floodlightcontroller.assigntoAP;

public class Utility {

	//this function is used to calculate the utility matrix
	public static double utility(double B, double d, double lt, double l,double numForMD, double N) {
		
		// B means a residual bandwidth capacity for one ap
		// d means a bandwidth demand for one ap
		// lt means the maximum tolerable latency for one md
		// l means the waiting time of access point waiting queue
		// numForMD means the number of md
		// N means the number of md that have access to the ap
		double ud;
		double ul;
		double ua;
		double U = 0;
		// adjustable parameters, but the following conditions must be 'wd+wl+wa=1'
		double wd,wl,wa;
		wd = 0.6;
		wl = 0.3;
		wa = 0.1;
		
		ud = Math.log(1+B/d);
		ul = Math.log(1+lt/l);
		ua = Math.log(1+numForMD/(N+numForMD));
		
		U = wd*ud+wl*ul+wa*ua;
		
		return U;
	}
}
