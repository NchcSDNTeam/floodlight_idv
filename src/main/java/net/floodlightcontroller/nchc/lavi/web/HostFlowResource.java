package net.floodlightcontroller.nchc.lavi.web;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HostFlowResource extends ServerResource {

	
    @Get("json")
    public FlowJson retrieve() { 
    	GetLaviData laviData = new GetLaviData(LaviWebRoutable.getFloodlightProvider(),
				   LaviWebRoutable.getDeviceService(),
				   LaviWebRoutable.getLinkDiscoveryService(),
				   LaviWebRoutable.getRoutingService());   	

    	FlowJson hostFlow = new FlowJson(laviData.getHostflowDataList());
    	return hostFlow;
    }

}
