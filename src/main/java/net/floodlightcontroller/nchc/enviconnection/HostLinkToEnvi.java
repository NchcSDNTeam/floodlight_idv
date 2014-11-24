package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.LinkDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;
import net.floodlightcontroller.routing.Link;

public class HostLinkToEnvi {
	
	private DataOutputStream out;
	private ArrayList<IDevice> deviceList = new ArrayList<IDevice>();
	private NormalDataFormat hostLinkDataForRest;
	
	public HostLinkToEnvi(DataOutputStream out, ArrayList<IDevice> deviceList)
	{
		this.out = out;
		this.deviceList = deviceList;	
		System.out.println("device list = "+this.deviceList.size());
	}
	
	
	public HashMap<Long, ArrayList<Link>> setHostLinkData(ArrayList<Long> swList, 
			HashMap<Long, ArrayList<Short>> interDomainPort, boolean socketConnection)
	{
		HashMap<Long, ArrayList<Link>> hostLinkMap = new HashMap<Long, ArrayList<Link>>();
		hostLinkDataForRest = new NormalDataFormat(EnviProtocol.LINKADD);
		boolean match = false;
		ArrayList<Long> existedLinkList = new ArrayList<Long>();
		for( IDevice obtainedHost : deviceList)
		{
			boolean repeat = existedLinkList.contains((Long)obtainedHost.getMACAddress());
			if(!repeat)
			{
			//	System.out.println();
			int linkType = EnviProtocol.HOST_INTRADOMAIN;
			SwitchPort[] switchPorts = obtainedHost.getAttachmentPoints();
			ArrayList<Link> links = new ArrayList<Link>();
			SwitchPort attachPoint = null;
			if(switchPorts.length >= 0)
				attachPoint = switchPorts[0];
			if(attachPoint != null)
			{
				for(long sw : swList)
				{
					if(switchPorts[0].getSwitchDPID() == sw)
					{
						match = true;
						ArrayList<Short> interDomainPortList = interDomainPort.get(sw);
						if( interDomainPortList!= null )
						{
							for(short port : interDomainPortList)
							{
								if(port == switchPorts[0].getPort())
								{
									linkType = EnviProtocol.HOST_INTERDOMAIN;
									break;
								}
							}
						}
						break;
					}
				}
				if(match)
				{
					//NormalDataFormat hostLinkData = new NormalDataFormat(EnviProtocol.LINKADD);
				//	hostLinkData.addLinkData(EnviProtocol.HOSTLINK, linkType, 
				//			EnviProtocol.HOST, obtainedHost.getMACAddress(), EnviProtocol.HostPort,
				//			EnviProtocol.SWITCH, attachPoint.getSwitchDPID(), (short)attachPoint.getPort());
					
					hostLinkDataForRest.addLinkData(EnviProtocol.HOSTLINK, linkType, 
							EnviProtocol.HOST, obtainedHost.getMACAddress(), EnviProtocol.HostPort,
							EnviProtocol.SWITCH, attachPoint.getSwitchDPID(), (short)attachPoint.getPort());					
					
					if(socketConnection)
						sendMessageOut(hostLinkDataForRest,links);
					
					//links.add(new Link(hostLink.getSrcId(), hostLink.getSrcPort(), hostLink.getDstId(), hostLink.getDstPort()));
				//	LinkDataFormat hostLink = hostLinkDataForRest.getLinkDataList().get(hostLinkDataForRest.getLinkDataList().size()-1);
					for(LinkDataFormat hostLink : hostLinkDataForRest.getLinkDataList())
						links.add(new Link(hostLink.getSrcId(), hostLink.getSrcPort(), hostLink.getDstId(), hostLink.getDstPort()));
				}
			}
			
			hostLinkMap.put(obtainedHost.getMACAddress(), links);
			existedLinkList.add(obtainedHost.getMACAddress());
			}
		}
		return hostLinkMap;
	}
	
	
	
	
	private void sendMessageOut(NormalDataFormat hostLinkData, ArrayList<Link> links)
	{
		try {
			out.writeShort(hostLinkData.getDataLength());
			out.writeShort(hostLinkData.getIpStringTotalLen());
			out.writeByte(hostLinkData.getCommandType());
			out.writeInt(hostLinkData.getXid());			
			
			for(LinkDataFormat hostLink : hostLinkData.getLinkDataList())
			{
				out.writeShort(hostLink.getLinkType());
				out.writeShort(hostLink.getInterDomainLink());
				out.writeShort(hostLink.getWire());
				out.writeShort(hostLink.getSrcNodeType());
				out.writeLong(hostLink.getSrcId());
				out.writeShort(hostLink.getSrcPort());
				out.writeShort(hostLink.getDstNodeType());
				out.writeLong(hostLink.getDstId());
				out.writeShort(hostLink.getDstPort());
				out.writeLong(hostLink.getCapacityBps());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public NormalDataFormat getHostLinkDataForRest() {
		return hostLinkDataForRest;
	}	
	

}


