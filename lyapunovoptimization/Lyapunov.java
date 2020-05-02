package net.floodlightcontroller.assigntoAP;

public class Lyapunov {
	
	// this function is used to implement LDP algorithm
	public static double lyapunov(double qData,double apDealData,double apReciveData,double U, double V) {
		
		// qData represents the data queue of the access point in time slot t
		// apDealData represents the data received by the access point in time slot t
		// apReciveData represents the data that the access point can process in time slot t
		// U means utility function, and V means penalty constant
		double S; 
		double Q; 
		double L; 
		
		Q = Math.max(qData - apDealData, 0) + apReciveData;
		L = 0.5 * (Math.pow(Q, 2) - Math.pow(qData, 2));
		S = L - V*U;
		
		return S;
	}
	
	//TODO:  useing to calculate expectations,but  not to be used here
	public static double getEx(double[] data, int t ) {
		double E = 0;
		
		for(int i=0;i<data.length;i++) {
			E += data[i]*1.0/t;
		}
		
		return E;
	}
}
