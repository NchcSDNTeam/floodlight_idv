package net.floodlightcontroller.nchc.flowdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

public class FlowData {
	
	protected static Logger log =
        LoggerFactory.getLogger(FlowData.class);
	
	public static HashMap<Ethernet, Route>  flowMap = new HashMap<Ethernet, Route>();
	
	public static void addFlowToMap(FloodlightContext cntx, Route route)
	{
		Ethernet eth =
            IFloodlightProviderService.bcStore.
            get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		flowMap.put(eth, route);	
		log.info("Add 1 new flow to flowMap :"+eth.toString());
	}
	
	public static void removeFlowFromMap(FloodlightContext cntx)
	{
		Ethernet eth =
            IFloodlightProviderService.bcStore.
            get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		flowMap.remove(eth);
		System.out.println(flowMap.size());
	}
	
	public static void removeFlowFromMap(OFFlowRemoved msg, IOFSwitch sw)
	{
		OFMatch ofMatch = msg.getMatch();
		ArrayList<Ethernet> eraseList = new ArrayList<Ethernet>();
		for(Ethernet eth : flowMap.keySet())
		{		
			long dlSrc = eth.getSourceMAC().toLong();
			long ofDlSrc = Ethernet.toLong(ofMatch.getDataLayerSource());
			long dlDst = eth.getDestinationMAC().toLong();
			long ofDlDst = Ethernet.toLong(ofMatch.getDataLayerDestination());
						
			List<NodePortTuple> routePath = flowMap.get(eth).getPath();
			long srcSw = routePath.get(0).getNodeId();
			short srcSwPortIn = routePath.get(0).getPortId();
			short ofSrcswPortIn = ofMatch.getInputPort();
			if(dlSrc == ofDlSrc && dlDst == ofDlDst)
			{
				if(eth.getEtherType() == ofMatch.getDataLayerType())
				{
				if(srcSw == sw.getId() && srcSwPortIn == ofSrcswPortIn)
					eraseList.add(eth);
				}
			}
		}
		if( eraseList.size() > 0)
		{
			for(Ethernet eth : eraseList)
			{
				flowMap.remove(eth);
				log.info("Remove 1 flow from flowMap :"+ eth.toString());
			}
		}
	}
	
	public static boolean CheckFlowFromMap(FloodlightContext cntx)
	{
		Ethernet eth =
            IFloodlightProviderService.bcStore.
            get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		return flowMap.containsKey(eth);
	}

}
