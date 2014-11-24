package net.floodlightcontroller.nchc.lavi.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SwitchResource extends ServerResource {

	
    @Get("json")
    public NodeJson retrieve() {
    	
    	GetLaviData laviData = new GetLaviData(LaviWebRoutable.getFloodlightProvider(),
    										   LaviWebRoutable.getDeviceService(),
    										   LaviWebRoutable.getLinkDiscoveryService(),
    										   LaviWebRoutable.getRoutingService());   	
    	
    	NodeJson switchNode = new NodeJson(laviData.getSwitchData());
    	return switchNode;
    }   
    
}
