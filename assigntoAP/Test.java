import java.util.Vector;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Vector<Device> devices = new Vector<Device>();
		Vector<AccessPoint> aps = new Vector<AccessPoint>();

		devices.add(new Device(0,new double[]{3,1,5}, new double[]{1,1,1}));
		devices.add(new Device(1,new double[]{1,1,1}, new double[]{2,3,3}));
		devices.add(new Device(2,new double[]{5,15,25}, new double[]{2,3,4}));
		devices.add(new Device(3,new double[]{25,15,5}, new double[]{1,2,3}));
		devices.add(new Device(4,new double[]{10,13,8}, new double[]{1,3,2}));
		
		aps.add(new AccessPoint(0,2));
		aps.add(new AccessPoint(1,3));
		aps.add(new AccessPoint(2,4));
		
		Heuristic.printAps(aps);
		Heuristic.printDevices(devices);
		
		int[] X = Heuristic.assign(devices,aps);		
	
		Heuristic.printAssignment(X);
		
		//ids are adjusted to start from 1
		Heuristic.printX(X);
	}

}
