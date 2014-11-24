package net.floodlightcontroller.nchc.lavi.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Sw2SwResource extends ServerResource {
	
    @Get("json")
    public LinkJson retrieve() {
    	GetLaviData laviData = new GetLaviData(LaviWebRoutable.getFloodlightProvider(),
				   LaviWebRoutable.getDeviceService(),
				   LaviWebRoutable.getLinkDiscoveryService(),
				   LaviWebRoutable.getRoutingService());   	

    	LinkJson sw2Sw = new LinkJson(laviData.getSw2swLinkData());
    	return sw2Sw;
    }
}
