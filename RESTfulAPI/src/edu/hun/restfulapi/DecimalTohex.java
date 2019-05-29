package edu.hun.restfulapi;

public class DecimalTohex {
	public static String toHexIP(String ip){
		String[] decimal = ip.split("\\.");
		StringBuffer hexip = new StringBuffer();
		for(int i = 0; i < decimal.length; i++){
			Integer intdecimal = Integer.valueOf(decimal[i]);
			String Strhex = Integer.toHexString(intdecimal);
			if(intdecimal < 16){
				hexip.append("0" + Strhex + ":");
			} else {
				hexip.append(Strhex + ":");
			}
		}
		String newhexip = hexip.substring(0, hexip.length()-1);
		System.out.println(newhexip);
		return newhexip;
	}
}
