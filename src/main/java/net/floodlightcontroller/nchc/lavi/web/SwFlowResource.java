package net.floodlightcontroller.nchc.lavi.web;

import net.floodlightcontroller.nchc.lavi.web.GetLaviData;
import net.floodlightcontroller.nchc.lavi.web.LaviWebRoutable;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SwFlowResource extends ServerResource {
	
    @Get("json")
    public FlowJson retrieve() {
    	GetLaviData laviData = new GetLaviData(LaviWebRoutable.getFloodlightProvider(),
				   LaviWebRoutable.getDeviceService(),
				   LaviWebRoutable.getLinkDiscoveryService(),
				   LaviWebRoutable.getRoutingService());   	

    	FlowJson swFlow = new FlowJson(laviData.getSwflowDataList());
    	return swFlow;
    }
}
