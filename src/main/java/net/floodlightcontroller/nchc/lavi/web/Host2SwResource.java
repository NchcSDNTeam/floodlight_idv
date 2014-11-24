package net.floodlightcontroller.nchc.lavi.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Host2SwResource extends ServerResource {
	
    @Get("json")
    public LinkJson retrieve() {
    	GetLaviData laviData = new GetLaviData(LaviWebRoutable.getFloodlightProvider(),
				   LaviWebRoutable.getDeviceService(),
				   LaviWebRoutable.getLinkDiscoveryService(),
				   LaviWebRoutable.getRoutingService());   	

    	LinkJson host2Sw = new LinkJson(laviData.getHost2swLinkData());
    	return host2Sw;
    }
}

