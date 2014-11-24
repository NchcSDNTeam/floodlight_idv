package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.nchc.interdomain.InterDomainManagement;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.LinkDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;
import net.floodlightcontroller.routing.Link;

public class SwLinkToEnvi {
	
	private DataOutputStream out;
	private ILinkDiscoveryService linkDiscoveryService;
	private NormalDataFormat linkDataForRest;
//	private NormalDataFormat linkData;

	public SwLinkToEnvi(DataOutputStream out, ILinkDiscoveryService linkDiscoveryService)
	{
		this.out = out;
		this.linkDiscoveryService = linkDiscoveryService;
	}
	
	public HashMap<Long, ArrayList<Short>> setSwlinkData(ArrayList<Long> swList, boolean socketConnection)
	{
		Map<Long, Set<Link>> swLinkMap = linkDiscoveryService.getSwitchLinks();
		//HashMap<Long, Set<Link>> swLinkHashMap = new HashMap<Long, Set<Link>>();
		linkDataForRest  = new NormalDataFormat(EnviProtocol.LINKADD);
		for(Long i : swList)
		{
			//swLinkHashMap.put(i, swLinkMap.get(i));
			if(!swLinkMap.isEmpty())
			{
				
				if(swLinkMap.containsKey(i))
				{
				for(Link obtainedLink : swLinkMap.get(i))
				{
					//NormalDataFormat linkData = new NormalDataFormat(EnviProtocol.LINKADD);
			//		linkData = new NormalDataFormat(EnviProtocol.LINKADD);
			//		linkData.addLinkData(EnviProtocol.SWITCHLINK, EnviProtocol.DONT_CARE_INTERDOMAIN,
			//				EnviProtocol.SWITCH, obtainedLink.getSrc(), obtainedLink.getSrcPort(),
			//				EnviProtocol.SWITCH, obtainedLink.getDst(), obtainedLink.getDstPort());					
					
					linkDataForRest.addLinkData(EnviProtocol.SWITCHLINK, EnviProtocol.DONT_CARE_INTERDOMAIN,
							EnviProtocol.SWITCH, obtainedLink.getSrc(), obtainedLink.getSrcPort(),
							EnviProtocol.SWITCH, obtainedLink.getDst(), obtainedLink.getDstPort());
					
					
					if(socketConnection)
						//sendMessage(linkData);
						sendMessage(linkDataForRest);
				}
				}
			}
		}
		HashMap<Long, ArrayList<Short>> interDomainPort = new HashMap<Long, ArrayList<Short>>();
		if(!InterDomainManagement.interDomainLinkMap.isEmpty())
		{
			for(Link interDomainlink : InterDomainManagement.interDomainLinkMap.keySet())
			{
				ArrayList<Short> portList = interDomainPort.get(interDomainlink.getSrc());
				if( portList == null )
				{
					portList = new ArrayList<Short>();
					portList.add(interDomainlink.getSrcPort());
				}
				else
					portList.add(interDomainlink.getSrcPort());
				
				interDomainPort.put(interDomainlink.getSrc(), portList);
				//NormalDataFormat linkData = new NormalDataFormat(EnviProtocol.LINKADD);
			//	linkData = new NormalDataFormat(EnviProtocol.LINKADD);
			//	linkData.addLinkData(EnviProtocol.SWITCHLINK, EnviProtocol.DONT_CARE_INTERDOMAIN,
			//			EnviProtocol.SWITCH, interDomainlink.getSrc(), interDomainlink.getSrcPort(),
			//			EnviProtocol.SWITCH, interDomainlink.getDst(), interDomainlink.getDstPort());
				
				linkDataForRest.addLinkData(EnviProtocol.SWITCHLINK, EnviProtocol.DONT_CARE_INTERDOMAIN,
						EnviProtocol.SWITCH, interDomainlink.getSrc(), interDomainlink.getSrcPort(),
						EnviProtocol.SWITCH, interDomainlink.getDst(), interDomainlink.getDstPort());
				
				if(socketConnection)
					//sendMessage(linkData);
					sendMessage(linkDataForRest);	
			}
		}
		
		return interDomainPort;
	}
	
	
	public void sendMessage(NormalDataFormat linkData)
	{
		try {
			out.writeShort(linkData.getDataLength());
			out.writeShort(linkData.getIpStringTotalLen());
			out.writeByte(linkData.getCommandType());
			out.writeInt(linkData.getXid());

			for(LinkDataFormat link : linkData.getLinkDataList())
			{
				out.writeShort(link.getLinkType());
				out.writeShort(link.getInterDomainLink());
				out.writeShort(link.getWire());
				out.writeShort(link.getSrcNodeType());
				out.writeLong(link.getSrcId());
				out.writeShort(link.getSrcPort());
				out.writeShort(link.getDstNodeType());
				out.writeLong(link.getDstId());
				out.writeShort(link.getDstPort());
				out.writeLong(link.getCapacityBps());
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public NormalDataFormat getLinkDataForRest() {
		return linkDataForRest;
		//return linkData;
	}

}
