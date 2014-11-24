package net.floodlightcontroller.nchc.lavi.web;

import java.util.ArrayList;
import java.util.HashMap;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.nchc.enviconnection.*;
import net.floodlightcontroller.nchc.sentdataformate.FlowDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;

public class GetLaviData {

	private NormalDataFormat switchData;
	private NormalDataFormat hostData;
	private NormalDataFormat sw2swLinkData;
	private NormalDataFormat host2swLinkData;
	private ArrayList <FlowDataFormat> swflowDataList;
	private ArrayList <FlowDataFormat> hostflowDataList;
	
	
	public GetLaviData(IFloodlightProviderService floodlightProvider, 
					   IDeviceService deviceService, 
					   ILinkDiscoveryService linkDiscoveryService,
					   IRoutingService routingService) {
		boolean socketConnection = false;
    	SwToEnvi swToEnvi = new SwToEnvi(floodlightProvider);
    	ArrayList <Long> swList = swToEnvi.setSwitchData(socketConnection);
    	switchData = swToEnvi.getNodeData();
    	
    	HostToEnvi hostToEnvi = new HostToEnvi(null, deviceService);
    	ArrayList<IDevice> deviceList = hostToEnvi.setHostData(swList, socketConnection);
    	hostData = hostToEnvi.getHostData();
    	
    	SwLinkToEnvi swLinkToEnvi = new SwLinkToEnvi(null,linkDiscoveryService);
    	HashMap<Long, ArrayList<Short>> interDomainPort = swLinkToEnvi.setSwlinkData(swList, socketConnection);
    	sw2swLinkData = swLinkToEnvi.getLinkDataForRest();
    	
    	HostLinkToEnvi hostLinkToEnvi = new HostLinkToEnvi(null,deviceList);
    	HashMap<Long, ArrayList<Link>> hostLinkMap = hostLinkToEnvi.setHostLinkData(swList,interDomainPort, socketConnection);
    	host2swLinkData = hostLinkToEnvi.getHostLinkDataForRest();
    	
    	SwFlowToEnvi swFlowToEnvi = new SwFlowToEnvi(null, socketConnection);
    	HashMap<Ethernet, ArrayList<Route>> hostFlowMap = swFlowToEnvi.sendMessage(hostLinkMap, interDomainPort);
    	swflowDataList = swFlowToEnvi.getFlList();
    	
    	HostFlowToEnvi hostFlowToEnvi = new HostFlowToEnvi(null, socketConnection);
    	hostFlowToEnvi.sendMessage(hostFlowMap);	
    	hostflowDataList = hostFlowToEnvi.getFlList();
	}


	public NormalDataFormat getSwitchData() {
		return switchData;
	}
	
	public NormalDataFormat getHostData() {
		return hostData;
	}


	public NormalDataFormat getSw2swLinkData() {
		return sw2swLinkData;
	}
	
	public NormalDataFormat getHost2swLinkData() {
		return host2swLinkData;
	}


	public ArrayList<FlowDataFormat> getSwflowDataList() {
		return swflowDataList;
	}
	
	public ArrayList<FlowDataFormat> getHostflowDataList() {
		return hostflowDataList;
	}

}
