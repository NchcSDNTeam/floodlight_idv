package net.floodlightcontroller.nchc.enviconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.nchc.interdomain.InterDomainManagement;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NodeDataFormat;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.topology.NodePortTuple;

public class SwToEnvi {
	
	private DataOutputStream out;
	private IFloodlightProviderService floodlightProvider;
	private NormalDataFormat nodeData;
	
	public SwToEnvi(IFloodlightProviderService floodlightProvider)
	{
		this.out = null;
		this.floodlightProvider = floodlightProvider;
	}
	
	public SwToEnvi(DataOutputStream out,IFloodlightProviderService floodlightProvider)
	{
		this.out = out;
		this.floodlightProvider = floodlightProvider;
	}
	
	public ArrayList<Long> setSwitchData(boolean socketConnection)
	{
		ArrayList<Long> swList = new ArrayList<Long>();
		Map<Long, IOFSwitch> switchMap = floodlightProvider.getSwitches();
		nodeData = new NormalDataFormat(EnviProtocol.NODEADD);
		
		for(Long i : switchMap.keySet())
		{
			IOFSwitch ofsw = switchMap.get(i);
			swList.add(i);
			String controllerIp = "";
			if(EnviProtocol.PUBLIC_IP == 0)
				controllerIp = ofsw.getChannel().getLocalAddress().toString().substring(1);
			else
				controllerIp = IPv4.fromIPv4Address(EnviProtocol.PUBLIC_IP);
			controllerIp = controllerIp + ":" + EnviProtocol.MONITOR_PORT + "#" + Integer.toString(1);
			nodeData.addNodeData(EnviProtocol.SWITCH, i, controllerIp.length(),controllerIp);
		}	
		for(NodePortTuple sw : InterDomainManagement.interDomainSwitchesMap.keySet())
		{
			String controllerIp = InterDomainManagement.interDomainSwitchesMap.get(sw);
			nodeData.addNodeData(EnviProtocol.SWITCH, sw.getNodeId(), controllerIp.length(),controllerIp);
		}
		if(socketConnection)
		{
			sendMessage(nodeData);
		}
		return swList;
	}
	
	public void sendMessage(NormalDataFormat nodeData)
	{
		try {
			out.writeShort(nodeData.getDataLength());
			out.writeShort(nodeData.getIpStringTotalLen());
			out.writeByte(nodeData.getCommandType());
			out.writeInt(nodeData.getXid());
			ArrayList<NodeDataFormat> nodeDataList = nodeData.getNodeDataList();
			for(NodeDataFormat node : nodeDataList)
			{
				out.writeShort(node.getNodeType());
				out.writeLong(node.getId());
				out.writeShort(node.getIpStringLen());
				out.write(node.getIpString().getBytes(), 0, node.getIpStringLen());
			}						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	public NormalDataFormat getNodeData() {
		return nodeData;
	}	
}
