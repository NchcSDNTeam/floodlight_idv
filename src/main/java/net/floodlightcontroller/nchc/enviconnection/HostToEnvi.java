package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.nchc.interdomain.InterDomainManagement;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.LinkDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NodeDataFormat;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Link;

public class HostToEnvi{

	private DataOutputStream out;
	private IDeviceService deviceService;
	private ArrayList<IDevice> deviceList = new ArrayList<IDevice>();
	private NormalDataFormat hostData;
	
	public HostToEnvi(DataOutputStream out, IDeviceService deviceService)
	{
		this.out = out;
		this.deviceService = deviceService;
	}
	
	//send host data to Envi
	public ArrayList<IDevice> setHostData(ArrayList<Long> swList, boolean socketConnection)
	{
		Iterator<? extends IDevice> deviceIt = deviceService.getAllDevices().iterator();
		
		//get host data form deviceService
		while(deviceIt.hasNext())
		{
			IDevice device = deviceIt.next();
			deviceList.add(device);
		}
		
		hostData = new NormalDataFormat(EnviProtocol.NODEADD); 
		
		
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
			}
		}
				
		boolean match = false;
		for(IDevice idev : deviceList)
		{		
			SwitchPort[] switchPorts = idev.getAttachmentPoints();
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
							if(port != switchPorts[0].getPort())
							{
								Integer [] IPList = idev.getIPv4Addresses();
								if(IPList.length == 0)
								{
									String ip = "CannotGetIp";
									hostData.addNodeData(EnviProtocol.HOST, idev.getMACAddress(), ip.length(), ip);
								}
								else
								{
									String ip = IPv4.fromIPv4Address(IPList[0].intValue());
									hostData.addNodeData(EnviProtocol.HOST, idev.getMACAddress(), ip.length(), ip);			
								}
							}
						}
					}
					else
					{
						Integer [] IPList = idev.getIPv4Addresses();					
						
						if(IPList.length == 0)
						{
							String ip = "CannotGetIp";
							hostData.addNodeData(EnviProtocol.HOST, idev.getMACAddress(), ip.length(), ip);
						}
						else
						{
							String ip = IPv4.fromIPv4Address(IPList[0].intValue());
							hostData.addNodeData(EnviProtocol.HOST, idev.getMACAddress(), ip.length(), ip);		
						}
						
					}
				}
			}								
		}
		
		if(socketConnection)
		{
			sendMessage(hostData);
		}
				
		return deviceList;
	}

	
	public void sendMessage(NormalDataFormat hostData)
	{
		try {
			out.writeShort(hostData.getDataLength());
			out.writeShort(hostData.getIpStringTotalLen());
			out.writeByte(hostData.getCommandType());
			out.writeInt(hostData.getXid());
			ArrayList<NodeDataFormat> hostDataList = hostData.getNodeDataList();
			for(NodeDataFormat host : hostDataList)
			{
				out.writeShort(host.getNodeType());
				out.writeLong(host.getId());
				out.writeShort(host.getIpStringLen());
				out.write(host.getIpString().getBytes(), 0, host.getIpStringLen());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	public NormalDataFormat getHostData() {
		return hostData;
	}

	public ArrayList<IDevice> getDeviceList() {
		return deviceList;
	}
		
}
