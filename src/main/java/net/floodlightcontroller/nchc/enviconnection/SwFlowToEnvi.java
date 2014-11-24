package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.nchc.flowdata.FlowData;
import net.floodlightcontroller.nchc.interdomain.InterDomainManagement;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.FlowDataFormat;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.routing.RouteId;
import net.floodlightcontroller.topology.NodePortTuple;

public class SwFlowToEnvi {
	
	private DataOutputStream out;
	private boolean socketConnection = false;
	private ArrayList <FlowDataFormat> flList = new ArrayList<FlowDataFormat>();
	
	public SwFlowToEnvi(DataOutputStream out, boolean socketConnection)
	{
		this.out = out;
		this.socketConnection = socketConnection;
	}
	
	public HashMap<Ethernet, ArrayList<Route>> sendMessage(HashMap<Long, ArrayList<Link>> hostLinkMap,
			HashMap<Long, ArrayList<Short>> interDomainPortMap)
	{
		HashMap<Ethernet, ArrayList<Route>> hostFlowMap = new HashMap<Ethernet, ArrayList<Route>>();
		for(Ethernet eth : FlowData.flowMap.keySet())
		{
			System.out.println(FlowData.flowMap.size());
			List<NodePortTuple> routePath = FlowData.flowMap.get(eth).getPath();
			ArrayList<Route> hostRouteList = new ArrayList<Route>();
			for(int i=0; i < routePath.size(); i++)
			{			
				if(i == 0)
				{
					Route hostRoute = getHostFlow(hostLinkMap, routePath.get(i),eth, true);
					if( hostRoute != null)
						hostRouteList.add(hostRoute);					
					continue;
				}
				else if( i == (routePath.size()-1) )
				{				
					Route hostRoute = getHostFlow(hostLinkMap, routePath.get(i), eth, false);
					if( hostRoute != null)
					{
						hostRouteList.add(hostRoute);
						hostFlowMap.put(eth, hostRouteList);
					}		
					
					long endNode = routePath.get(i).getNodeId();
					short outPort = routePath.get(i).getPortId();
					ArrayList<Short> interDomainPortList = interDomainPortMap.get(endNode);
					if( interDomainPortList != null)
					{
						for(short port : interDomainPortList)
						{
							if(port == outPort)
							{
								for(Link link : (InterDomainManagement.interDomainLinkMap.keySet()))
								{
									if(link.getSrc() == endNode && link.getSrcPort() == outPort)
									{							
										processFlowData(endNode, outPort, link.getDst(), link.getDstPort(), eth);
										break;
									}
								}
							}
						}
					}
					continue;
				}
				else
				{
					int srcIndex = i;
					int dstIndex = i+1;					
					processFlowData(routePath.get(srcIndex).getNodeId(), routePath.get(srcIndex).getPortId(),
							routePath.get(dstIndex).getNodeId(), routePath.get(dstIndex).getPortId(), eth);
					i = i+1;			
				}
			}
		}
		return hostFlowMap;		
	}
	
	
	public Route getHostFlow(HashMap<Long, ArrayList<Link>> hostLinkMap, 
			NodePortTuple sw,Ethernet eth, boolean begin)
	{
		for(long deviceId : hostLinkMap.keySet())
		{
			ArrayList<Link> hostLinks = hostLinkMap.get(deviceId);
			for(Link hostLink : hostLinks)
			{
				if(hostLink.getDst() == sw.getNodeId() 
						&& hostLink.getDstPort() == sw.getPortId())
				{
					if(begin)
					{
						List<NodePortTuple> routeList = new ArrayList<NodePortTuple>();
						routeList.add(new NodePortTuple(hostLink.getSrc(), hostLink.getSrcPort()));
						routeList.add(new NodePortTuple(hostLink.getDst(), hostLink.getDstPort()));
						Route hostToSwRoute = new Route(new RouteId(hostLink.getSrc(), hostLink.getDst()), routeList);
						return hostToSwRoute;
					}
					else {
						List<NodePortTuple> routeList = new ArrayList<NodePortTuple>();
						routeList.add(new NodePortTuple(hostLink.getDst(), hostLink.getDstPort()));
						routeList.add(new NodePortTuple(hostLink.getSrc(), hostLink.getSrcPort()));
						Route hostToSwRoute = new Route(new RouteId(hostLink.getDst(), hostLink.getSrc()), routeList);
						return hostToSwRoute;
					}
				}
			}
		}	
		return null;		
	}
	
	private void processFlowData(long src, short srcPort, long dst, short dstPort, Ethernet eth)
	{		
		String srcClientMac = null;
		String srcClientIp = null;					
		String dstClientMac = null;
		String dstClientIp = null;
		String ethernetType = null;
		if (eth.getPayload() instanceof ARP) 
		{
			ethernetType = "ARP";
            ARP arp = (ARP) eth.getPayload();
            srcClientMac = eth.getSourceMAC().toString();
            srcClientIp = IPv4.fromIPv4Address(IPv4.toIPv4Address(arp.getSenderProtocolAddress()));
            dstClientMac = eth.getDestinationMAC().toString();
            dstClientIp = IPv4.fromIPv4Address(IPv4.toIPv4Address(arp.getTargetProtocolAddress()));
            FlowDataFormat flowData = new FlowDataFormat(EnviProtocol.FLOWADD, 
					EnviProtocol.SWITCH, src, srcPort, 
					EnviProtocol.SWITCH, dst, dstPort, 
					srcClientMac.length(), srcClientMac, srcClientIp.length(), srcClientIp, 
					dstClientMac.length(), dstClientMac, dstClientIp.length(), dstClientIp, 
					ethernetType.length(), ethernetType); 
            
            flList.add(flowData);
            
            if(this.socketConnection)
            	sendMessageOut(flowData);
            
        }
		else if (eth.getPayload() instanceof IPv4) 
        {
	        if (eth.getPayload() instanceof ICMP)
	        	ethernetType = "ICMP";
	        else if (eth.getPayload() instanceof IPv4)
	        	ethernetType = "IP";
	        else if (eth.getPayload() instanceof DHCP)
	        	ethernetType = "DHCP";
	        else  ethernetType = Short.toString(eth.getEtherType());
			
	        IPv4 ipv4 = (IPv4) eth.getPayload();
	        
            eth.getSourceMAC().toString();
            srcClientMac = eth.getSourceMAC().toString();
            srcClientIp = IPv4.fromIPv4Address(ipv4.getSourceAddress());
            dstClientMac = eth.getDestinationMAC().toString();
            dstClientIp = IPv4.fromIPv4Address(ipv4.getDestinationAddress());
            
            
            FlowDataFormat flowData = new FlowDataFormat(EnviProtocol.FLOWADD, 
 					EnviProtocol.SWITCH, src, srcPort, 
 					EnviProtocol.SWITCH, dst, dstPort, 
 					srcClientMac.length(), srcClientMac, srcClientIp.length(), srcClientIp, 
 					dstClientMac.length(), dstClientMac, dstClientIp.length(), dstClientIp, 
 					ethernetType.length(), ethernetType); 
          
            flList.add(flowData);
            
            if(this.socketConnection)
            	sendMessageOut(flowData);
			
        }
		
	}
	
	
	private void sendMessageOut(FlowDataFormat flowData)
	{
		try {
			out.writeShort(flowData.getDataLength());
			out.writeShort(flowData.getFlowIdentified());
			out.writeByte(flowData.getMsgtype());
			out.writeInt(flowData.getXid());			
			out.writeInt(flowData.getFlowNumber());
			out.writeShort(flowData.getFlowType());
			out.writeInt(flowData.getFlowId());
			
			out.writeShort(flowData.getSrcType());
			out.writeLong(flowData.getSrcId());
			out.writeShort(flowData.getSrcPort());
			
			out.writeShort(flowData.getDstType());
			out.writeLong(flowData.getDstId());
			out.writeShort(flowData.getDstPort());
			
			
			out.writeShort(flowData.getSrcClientMacLen());
			out.write(flowData.getSrcClientMac().getBytes(), 0, flowData.getSrcClientMacLen());
			out.writeShort(flowData.getSrcClientIpLen());
			out.write(flowData.getSrcClientIp().getBytes(), 0, flowData.getSrcClientIpLen());
			
			out.writeShort(flowData.getDstClientMacLen());
			out.write(flowData.getDstClientMac().getBytes(), 0, flowData.getDstClientMacLen());
			out.writeShort(flowData.getDstClientIpLen());
			out.write(flowData.getDstClientIp().getBytes(), 0, flowData.getDstClientIpLen());
			
			out.writeShort(flowData.getEthernetTypeLen());
			out.write(flowData.getEthernetType().getBytes(), 0, flowData.getEthernetTypeLen());
			
			out.writeShort(flowData.getPathLen());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ArrayList<FlowDataFormat> getFlList() {
		return flList;
	}
	
	
}
