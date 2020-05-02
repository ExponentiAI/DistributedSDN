package net.floodlightcontroller.assigntoAP;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Heuristic2 {

	//Performs optimal assignment of Device2s to access points. The ids 
	//of Device2s (in MD) and access points (in AP) should start from 0 and increase 
	//sequentially.
	public static int[] assign(List<Device2> MD, List<AccessPoint2> AP){
		
		//TODO: remove access points that have less residual capacity than the smallest demand	

		//create and initialise assignment vector
		int [] X = new int[MD.size()];
		for(int i = 0; i < MD.size(); i++)
			X[i]=-1;


		//create profit matrix
		double[][] P = new double[MD.size()][];
		int count = 0;
		for(Device2 d: MD){
			P[count++] = Arrays.copyOf(d.getProfit(),d.getProfit().length);
		}
		
		//create demand matrix
		double[][] D = new double[MD.size()][];
		count=0;
		for(Device2 d: MD){
			D[count++] = Arrays.copyOf(d.getDemand(),d.getDemand().length);
		}
		
		//create capacity matrix
		double[] C = new double[AP.size()];
		count = 0;
		for(AccessPoint2 ap: AP){
			C[count++] = ap.getCapacity();
		}

		for(AccessPoint2 ap: AP){
			int j = ap.getId();
			//create the profit vector
			double[] PVector = new double[MD.size()];
			//create the ratio (or efficiency) vector
			//double[] RVector = new double[MD.size()];
			for( Device2 d: MD){
				int i = d.getId();
				PVector[i] = (X[i] == -1) ? P[i][j]:P[i][j]-P[i][X[i]];
				LDP ldp = new LDP(d.getDemandForAp(j), ap.getCapacity(), d.getMdLatency(), ap.getFlowLatency(), ap.getExistMDCount(), ap.getNewMDCount(), ap.getQueueBacklog(), ap.getOutputQueue(), ap.getInputQueue());
				//todo
				d.setLDP(ldp.lyapunovDrift());
			}
			
			//sort MD so that the ratio vector is in nonincreasing order
			Collections.sort(MD, Device2.LDP_ORDER);
			
			//add Device2s to the knapsack (access point ap) until the breaking point is reached
			double totalDemand =0;
			double tempDemand =0;
			double capacity = ap.getCapacity();
			for(Device2 d: MD){	
				tempDemand+= d.getDemandForAp(ap.getId());
				if(tempDemand <= capacity){
					totalDemand = tempDemand;
					X[d.getId()] = ap.getId();
				}else
					break;
			}			
		}
		return X;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Test with random capacities, demands and profits
		Vector<Device2> Device2s = new Vector<Device2>();
		Vector<AccessPoint2> aps = new Vector<AccessPoint2>();
		int Device2Counter = 0;
		int apCounter = 0;
		
		//create 3 dummy access points with uniformly distributed capacities between 0 and maxCapcity
		int numAps = 3;
		double maxCapacity = 20;
		for(int i = 0; i < numAps; i++)
			aps.add(new AccessPoint2(apCounter++,maxCapacity*Math.random()));
		
		//create 10 dummy Device2s with uniformly distributed profits and demands between 0 and maxProfit/Demand
		int numDevice2s = 10;
		double maxProfit = 10;
		double maxDemand = 10;
		for(int i = 0; i < numDevice2s; i++){
			double[] profit = new double[numAps];
			double[] demand = new double[numAps];
			for(int j = 0; j < numAps; j++){
				profit[j] = maxProfit*Math.random();
				demand[j] = maxDemand*Math.random();
			}
			Device2s.add(new Device2(Device2Counter++,profit,demand));
		}
		
		
		printAps(aps);
		printDevice2s(Device2s);
		
		int[] X = assign(Device2s,aps);
		
		printDevice2s(Device2s);
		printAssignment(X);
		

	}
	
	public static void printAps(List<AccessPoint2> aps){
		System.out.println("Access Points");
		System.out.println("Capacity:");
		for(AccessPoint2 ap: aps)
			System.out.print(ap.getCapacity()+"\t");
		System.out.println();
	}
	
	public static void printDevice2s(List<Device2> Device2s){
		System.out.println("Device2s");
		System.out.println("Profit:");
		for(Device2 d : Device2s){
			double[] profit = d.getProfit();
			System.out.print(d.getId()+"\t");
			//System.out.print(d.getRatio()+"\t");
			for (int i = 0; i <profit.length;i++)
				System.out.print(profit[i]+"\t");
			System.out.println();
		}
		System.out.println("Demand:");
		for(Device2 d : Device2s){
			double[] demand = d.getDemand();
			System.out.print(d.getId()+"\t");
			for (int i = 0; i <demand.length;i++)
				System.out.print(demand[i]+"\t");
			System.out.println();
		}
	}
	
	public static void printAssignment(int[] X){
		System.out.println("Device2 \t Access Point");
		for(int i = 0; i < X.length; i++)
			if(X[i]!=-1){
				System.out.println(i+"\t"+X[i]);
			}
	}
	
	public static void printX(int[] X){
		System.out.println("Device2 \t Access Point");
		for(int i = 0; i < X.length; i++)			
				System.out.println((i+1)+"\t"+((X[i]==-1)?X[i]:(X[i]+1)));
	}

}
