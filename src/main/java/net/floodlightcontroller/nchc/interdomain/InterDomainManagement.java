package net.floodlightcontroller.nchc.interdomain;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.openflow.protocol.OFPacketIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.NodePortTuple;

public class InterDomainManagement {
	
	public static boolean lock = false;
	public static HashMap<NodePortTuple, String> interDomainSwitchesMap = new HashMap<NodePortTuple, String>();
	public static HashMap<Link, LinkInfo> interDomainLinkMap = new HashMap<Link, LinkInfo>();
	protected static Logger log = LoggerFactory.getLogger(InterDomainManagement.class);
	private String ipPortString;
	
	//add interdomain node and link.
	public void  addInterDomainData(ByteBuffer controllerBB, NodePortTuple interDomainSwitch, 
			IOFSwitch iofswitch, OFPacketIn pi, boolean isStandard)
	{
		
		lock = true;		
		String ipString = getSwitchIp(controllerBB);
		
		if(!interDomainSwitchesMap.containsKey(interDomainSwitch))
		{
			interDomainSwitchesMap.put(interDomainSwitch,ipString);
			if (log.isTraceEnabled()) 					
				log.trace("InterDomainSwitch :"+ interDomainSwitchesMap.toString());
		}
			
		Link newLink = new Link(iofswitch.getId(), pi.getInPort(), 
				interDomainSwitch.getNodeId(), interDomainSwitch.getPortId());	
		Long lastLldpTime = null;
		Long lastBddpTime = null;

		Long firstSeenTime = System.currentTimeMillis();

		if (isStandard)
			lastLldpTime = System.currentTimeMillis();
		else
			lastBddpTime = System.currentTimeMillis();

		LinkInfo newInfo = new LinkInfo(firstSeenTime, lastLldpTime, lastBddpTime, 0, 0);
			
		if(interDomainLinkMap.containsKey(newLink))
		{
			LinkInfo oldInfo = interDomainLinkMap.get(newLink);
			if (newInfo.getUnicastValidTime() == null) 
			{
				// This is due to a multicast LLDP, so copy the old unicast
				// value.
				if (oldInfo.getUnicastValidTime() != null) 
					newInfo.setUnicastValidTime(oldInfo.getUnicastValidTime());
			} 
			else if (newInfo.getMulticastValidTime() == null) 
			{
				// This is due to a unicast LLDP, so copy the old multicast
				// value.
				if (oldInfo.getMulticastValidTime() != null) 
					newInfo.setMulticastValidTime(oldInfo.getMulticastValidTime());
			}
			interDomainLinkMap.put(newLink, newInfo);		
			//System.out.println(interDomainLinkMap.toString());
		}
		else
		{
			
			boolean samePortLink = false;
			ArrayList<Link> eraseList = new ArrayList<Link>();
			if(interDomainLinkMap.size() > 0)
			{			
				for(Link link : interDomainLinkMap.keySet())
				{				
					if(link.getSrc() == iofswitch.getId() && link.getSrcPort() ==  pi.getInPort() )
					{
						LinkInfo linkInfo = interDomainLinkMap.get(link);
						//if existed link is lldp, we set samePortLink = true
						//(Don't put current link to replace existed link)
						if(linkInfo.getUnicastValidTime() != null)
						{
							samePortLink = true;	
							break;
						}
						else
						{
							//if existed link is Bddp and current link is Bddp, we set samePortLink = true
							//(Don't put current link to replace existed link)
							if(newInfo.getMulticastValidTime()!=null)						
							{
								samePortLink = true;
								break;
							}
							else
								eraseList.add(link);
						}
					}	
				}
			}
			if(!samePortLink)
			{
				interDomainLinkMap.put(newLink, newInfo);
				if(eraseList.size() > 0)
				{
					for(Link removedLink : eraseList)
						interDomainLinkMap.remove(removedLink);
				}				
				if (log.isTraceEnabled()) 					
					log.trace(interDomainLinkMap.toString());						
			}
		}
		lock = false;
	}
	

	//check if there are idle link. if it is exist, this function remove it.
	public void removeIdleInterDomainLink(int linkTimeOut )
	{
		ArrayList<Link> eraseLinkList = new ArrayList<Link>();
		if(!lock)
		{
			for(Link link : interDomainLinkMap.keySet())
			{
				LinkInfo oldInfo = interDomainLinkMap.get(link);
				if((oldInfo.getUnicastValidTime())!= null && 
						(oldInfo.getUnicastValidTime() + (linkTimeOut*1000)) < System.currentTimeMillis() )
				{
					
					oldInfo.setUnicastValidTime(null);
				}
					
				if((oldInfo.getMulticastValidTime())!= null && 
						(oldInfo.getMulticastValidTime() + (linkTimeOut*1000)) < System.currentTimeMillis() )
				{
					
					oldInfo.setMulticastValidTime(null);
				}
							
				if(oldInfo.getUnicastValidTime() == null && oldInfo.getMulticastValidTime() == null)
				{
					
					eraseLinkList.add(link);
					System.out.println("eraseLinkList"+eraseLinkList);
				}
					
			}
			
			if(eraseLinkList.size() > 0)
			{
				for(Link link : eraseLinkList)
				{
					for(NodePortTuple sw : interDomainSwitchesMap.keySet())
					{
						if(sw.getNodeId() == link.getDst() && sw.getPortId() == link.getDstPort())
						{
							interDomainSwitchesMap.remove(sw);
							log.info("interDomainSwitchesList: "+interDomainSwitchesMap.toString());
							break;
						}
					}
					if(interDomainLinkMap.containsKey(link))
						interDomainLinkMap.remove(link);
					log.info("interDomainLinkMap: "+interDomainLinkMap.toString());
				}
			}
			removeIslandIDS();
		}
	}
	
	private void removeIslandIDS()
	{
		boolean matchLink = false;
		ArrayList<NodePortTuple> eraseList = new ArrayList<NodePortTuple>();
		for(NodePortTuple sw : interDomainSwitchesMap.keySet())
		{
			for(Link link : interDomainLinkMap.keySet())
			{
				if(sw.getNodeId() == link.getDst() && sw.getPortId() == link.getDstPort())
				{
					matchLink = true;
					break;
				}
			}
			if(!matchLink)
				eraseList.add(sw);			
		}
		if(eraseList.size() > 0)
		{
			for(NodePortTuple sw  : eraseList)
			{
				log.info("Find one island IDS, remove it("+sw.toString()+")");
				interDomainSwitchesMap.remove(sw);
				log.info("interDomainSwitchesMap switches: "+interDomainSwitchesMap.toString());
			}
		}
	}
	
	private String getSwitchIp(ByteBuffer controllerBB)
	{
		byte [] dataByte = new byte[11];
		byte [] ipByte = {0,0,0,0};
		byte [] portByte = {0,0};
		byte [] controllerTypeByte = {0};
		String ipString = "";
		for(int i = 0; controllerBB.hasRemaining();i++)
			dataByte[i] = controllerBB.get();	
		System.arraycopy(dataByte, 3, controllerTypeByte, 0, 1);
		System.arraycopy(dataByte, 4, ipByte, 0, 4);
		System.arraycopy(dataByte, 9, portByte, 0, 2);
		short port = (short)portByte[0];
		port = (short) ( (port << 8) | (0xff & portByte[1])); 
		ipString = (IPv4.fromIPv4Address(IPv4.toIPv4Address(ipByte))) + ":" + port +"#"+ controllerTypeByte[0];
		return ipString;
	}
}
