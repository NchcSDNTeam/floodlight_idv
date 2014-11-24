package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.FlowDataFormat;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

public class HostFlowToEnvi {
	
	private DataOutputStream out;
	private boolean socketConnection;
	private ArrayList <FlowDataFormat> flList = new ArrayList<FlowDataFormat>();
	
	public HostFlowToEnvi( DataOutputStream out,  boolean socketConnection)
	{
		this.out = out;
		this.socketConnection = socketConnection;
		
	}
	
	public void sendMessage(HashMap<Ethernet, ArrayList<Route>> hostFlowMap){
		
		
		for(Ethernet eth : hostFlowMap.keySet())
		{
			ArrayList<Route> hostRouteList = hostFlowMap.get(eth);
			for(Route route : hostRouteList)
			{
				List<NodePortTuple> routePath = route.getPath();				
				
				for(int i=0; i < routePath.size(); i++)
				{	
					int srcIndex = i;
					int dstIndex = i+1;					
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
								EnviProtocol.SWITCH, routePath.get(srcIndex).getNodeId(), routePath.get(srcIndex).getPortId(), 
								EnviProtocol.SWITCH, routePath.get(dstIndex).getNodeId(), routePath.get(dstIndex).getPortId(), 
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
								EnviProtocol.SWITCH, routePath.get(srcIndex).getNodeId(), routePath.get(srcIndex).getPortId(), 
								EnviProtocol.SWITCH, routePath.get(dstIndex).getNodeId(), routePath.get(dstIndex).getPortId(), 
								srcClientMac.length(), srcClientMac, srcClientIp.length(), srcClientIp, 
								dstClientMac.length(), dstClientMac, dstClientIp.length(), dstClientIp, 
								ethernetType.length(), ethernetType); 
						
						flList.add(flowData);
						
						if(this.socketConnection)
							sendMessageOut(flowData);
						
					}
					i = i+1;
				}		
				
			}			
			
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
