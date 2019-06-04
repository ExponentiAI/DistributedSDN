package net.floodlightcontroller.networkcalculus;

import java.util.ArrayList;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class NetworkCalculusResource extends ServerResource {
	 
	@Get("json")
	 public String getResult() {
			System.out.println("get:");
			Long departCurve = NetworkStore.getDepartCurve();
			Long arriveCurve = NetworkStore.getArriveCurve();
			return "result:" + departCurve + "<>" + arriveCurve;
	}
}
