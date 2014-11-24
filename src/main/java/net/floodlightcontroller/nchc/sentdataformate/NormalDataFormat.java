package net.floodlightcontroller.nchc.sentdataformate;

import java.util.ArrayList;

public class NormalDataFormat {

	private int dataLength = 9;
	private int ipStringTotalLen = 0;
	private byte commandType = 0;
	private int xid = 0;
	private ArrayList<NodeDataFormat> nodeDataList = new ArrayList<NodeDataFormat>();
	private ArrayList<LinkDataFormat> linkDataList;
	
	public NormalDataFormat(byte commandType)
	{
			this.commandType = commandType;
			linkDataList = new ArrayList<LinkDataFormat>();
	}
	
	public void addNodeData(short nodeType, long id, int ipStringLen, String ipString)
	{
		NodeDataFormat node = new NodeDataFormat(nodeType,id,ipStringLen,ipString);
		ipStringTotalLen = ipStringTotalLen + node.getIpStringLen();
		ipStringTotalLen = ipStringTotalLen + 2;
		dataLength = dataLength + node.getDataSzie();
		nodeDataList.add(node);
	}
	
	public void addLinkData(int linkType, int interDomainLink,
			short srcNodeType, long srcId, short srcPort,
			short dstNodeType, long dstId, short dstPort)
	{
		LinkDataFormat nowlink = new LinkDataFormat(linkType,interDomainLink,
				srcNodeType,srcId,srcPort,
				dstNodeType,dstId,dstPort);
		boolean linkMatch = false;
		if(!linkDataList.isEmpty()) {
			for( LinkDataFormat linkData : linkDataList){
				if(linkData.equals(nowlink)) {
					linkMatch = true;
					break;
				}		
			}
			if(!linkMatch)
			{
				dataLength = dataLength + nowlink.getDataSzie();
				this.linkDataList.add(nowlink);
			}
		}
		else
		{
			dataLength = dataLength + nowlink.getDataSzie();
			this.linkDataList.add(nowlink);
		}
	}
	
	public void addFlowData(int linkType, int interDomainLink,
			short srcNodeType, long srcId, short srcPort,
			short dstNodeType, long dstId, short dstPort)
	{
		LinkDataFormat link = new LinkDataFormat(linkType,interDomainLink,
				srcNodeType,srcId,srcPort,
				dstNodeType,dstId,dstPort);
		dataLength = dataLength + link.getDataSzie();
		linkDataList.add(link);

	}
	

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getIpStringTotalLen() {
		return ipStringTotalLen;
	}

	public void setIpStringTotalLen(int ipStringTotalLen) {
		this.ipStringTotalLen = ipStringTotalLen;
	}

	public byte getCommandType() {
		return commandType;
	}

	public void setCommandType(byte commandType) {
		this.commandType = commandType;
	}

	public int getXid() {
		return xid;
	}

	public void setXid(int xid) {
		this.xid = xid;
	}


	public ArrayList<NodeDataFormat> getNodeDataList() {
		return nodeDataList;
	}

	public ArrayList<LinkDataFormat> getLinkDataList() {
		return linkDataList;
	}
}
