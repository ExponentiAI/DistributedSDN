package edu.hnu.loadingbalance;

import edu.hun.restfulapi.RestfulTool;

public class NetworkCurve {
	 /**
	  *  Calculating the backlog of data in the network partition using network calculus
	  * @return the result of backlog
	  */
	public static int getNetworkCurve(){
		 String curve = RestfulTool.getNetworkCalculus();
		 String[] substring = curve.substring(7, curve.length()).split("<>");
		 int depart = Integer.parseInt(substring[0]);
		 int arrive = Integer.parseInt(substring[1]);
		 System.out.println("depart: " + depart + "   arrive: " + arrive);
		 System.out.println("Backlog:  ");
		 System.out.println(arrive-depart);
		 return arrive-depart;
	 }
	public static void main(String[] args){
		NetworkCurve.getNetworkCurve();
	}
}
