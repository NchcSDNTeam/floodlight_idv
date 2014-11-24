package net.floodlightcontroller.nchc.sentdataformate;

public class NodeDataFormat {

	private short nodeType;
	private long id;
	private int ipStringLen;
	private String ipString;
	private int dataSzie;
	
	public NodeDataFormat(short nodeType, long id, int ipStringLen, String ipString)
	{
		this.nodeType = nodeType;
		this.id = id;
		this.ipString = ipString;
		this.ipStringLen = ipStringLen;
		this.dataSzie = 2 + 8 + 2 + this.ipString.length(); //short bye=2 long byte=8
	}

	public short getNodeType() {
		return nodeType;
	}

	public void setNodeType(short nodeType) {
		this.nodeType = nodeType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getIpStringLen() {
		return ipStringLen;
	}

	public void setIpStringLen(short ipStringLen) {
		this.ipStringLen = ipStringLen;
	}

	public String getIpString() {
		return ipString;
	}

	public void setIpString(String ipString) {
		this.ipString = ipString;
	}

	public int getDataSzie() {
		return dataSzie;
	}

	public void setDataSzie(short dataSzie) {
		this.dataSzie = dataSzie;
	}
	
	
}
