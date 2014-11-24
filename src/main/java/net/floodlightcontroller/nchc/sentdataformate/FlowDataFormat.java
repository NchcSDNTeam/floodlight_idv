package net.floodlightcontroller.nchc.sentdataformate;

public class FlowDataFormat {
	
	int dataLength = 9;
	int flowIdentified = -10;
	int msgtype;
	int xid = 0;
	int flowNumber = 1;
	int flowType = 0;
	int flowId = 0;
	int srcType;
	long srcId;
	short srcPort;
	int dstType;
	long dstId;
	short dstPort;
	int srcClientMacLen;
	String srcClientMac;
	int srcClientIpLen;
	String srcClientIp;
	int dstClientMacLen;
	String dstClientMac;
	int dstClientIpLen;
	String dstClientIp;
	int ethernetTypeLen;
	String ethernetType;
	int pathLen = 0;
	
	public FlowDataFormat(int msgType, int srcType, long srcId, short srcPort, int dstType, long dstId, short dstPort,
			int srcClientMaclen, String srcClientMac, int srcClientIpLen, String srcClientIp,
			int dstClientMaclen, String dstClientMac, int dstClientIpLen, String dstClientIp,
			int ethernetTypeLen, String ethernetType)
	{
		this.msgtype = msgType;
		this.srcType = srcType;
		this.srcId = srcId;
		this.srcPort = srcPort;
		this.dstType = dstType;
		this.dstId = dstId;
		this.dstPort = dstPort;
		this.srcClientMacLen = srcClientMaclen;
		this.srcClientMac = srcClientMac;
		this.srcClientIpLen = srcClientIpLen;
		this.srcClientIp = srcClientIp;
		
		this.dstClientMacLen = dstClientMaclen;
		this.dstClientMac = dstClientMac;
		this.dstClientIpLen = dstClientIpLen;
		this.dstClientIp = dstClientIp;
		
		this.ethernetTypeLen = ethernetTypeLen;
		this.ethernetType = ethernetType;
		this.dataLength = this.dataLength+4+2+4+2+8+2+2+8+2+2+srcClientMaclen+2+srcClientIpLen+
							2+dstClientMaclen+2+dstClientIpLen+2+ethernetTypeLen+2;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getFlowIdentified() {
		return flowIdentified;
	}

	public void setFlowIdentified(int flowIdentified) {
		this.flowIdentified = flowIdentified;
	}

	public int getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public int getXid() {
		return xid;
	}

	public void setXid(int xid) {
		this.xid = xid;
	}

	public int getFlowNumber() {
		return flowNumber;
	}

	public void setFlowNumber(int flowNumber) {
		this.flowNumber = flowNumber;
	}

	public int getFlowType() {
		return flowType;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public int getSrcType() {
		return srcType;
	}

	public void setSrcType(int srcType) {
		this.srcType = srcType;
	}

	public long getSrcId() {
		return srcId;
	}

	public void setSrcId(long srcId) {
		this.srcId = srcId;
	}

	public short getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(short srcPort) {
		this.srcPort = srcPort;
	}

	public int getDstType() {
		return dstType;
	}

	public void setDstType(int dstType) {
		this.dstType = dstType;
	}

	public long getDstId() {
		return dstId;
	}

	public void setDstId(long dstId) {
		this.dstId = dstId;
	}

	public short getDstPort() {
		return dstPort;
	}

	public void setDstPort(short dstPort) {
		this.dstPort = dstPort;
	}

	public int getSrcClientMacLen() {
		return srcClientMacLen;
	}

	public void setSrcClientMacLen(int srcClientMacLen) {
		this.srcClientMacLen = srcClientMacLen;
	}

	public String getSrcClientMac() {
		return srcClientMac;
	}

	public void setSrcClientMac(String srcClientMac) {
		this.srcClientMac = srcClientMac;
	}

	public int getSrcClientIpLen() {
		return srcClientIpLen;
	}

	public void setSrcClientIpLen(int srcClientIpLen) {
		this.srcClientIpLen = srcClientIpLen;
	}

	public String getSrcClientIp() {
		return srcClientIp;
	}

	public void setSrcClientIp(String srcClientIp) {
		this.srcClientIp = srcClientIp;
	}

	public int getDstClientMacLen() {
		return dstClientMacLen;
	}

	public void setDstClientMacLen(int dstclientMacLen) {
		this.dstClientMacLen = dstclientMacLen;
	}

	public String getDstClientMac() {
		return dstClientMac;
	}

	public void setDstClientMac(String dstClientMac) {
		this.dstClientMac = dstClientMac;
	}

	public int getDstClientIpLen() {
		return dstClientIpLen;
	}

	public void setDstClientIpLen(int dstClientIpLen) {
		this.dstClientIpLen = dstClientIpLen;
	}

	public String getDstClientIp() {
		return dstClientIp;
	}

	public void setDstClientIp(String dstClientIp) {
		this.dstClientIp = dstClientIp;
	}

	public int getEthernetTypeLen() {
		return ethernetTypeLen;
	}

	public void setEthernetTypeLen(int ethernetTypeLen) {
		this.ethernetTypeLen = ethernetTypeLen;
	}

	public String getEthernetType() {
		return ethernetType;
	}

	public void setEthernetType(String ethernetType) {
		this.ethernetType = ethernetType;
	}

	public int getPathLen() {
		return pathLen;
	}

	public void setPathLen(int pathLen) {
		this.pathLen = pathLen;
	}
	
	

}
