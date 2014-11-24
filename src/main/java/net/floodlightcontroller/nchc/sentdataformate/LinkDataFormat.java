package net.floodlightcontroller.nchc.sentdataformate;

public class LinkDataFormat {
	
	private int dataSzie;
	private int linkType;
	private int interDomainLink;
	private int wire = EnviProtocol.WIRE;
	private short srcNodeType;
	private long srcId;
	private short srcPort;
	private short dstNodeType;
	private long dstId;
	private short dstPort;
	private long capacityBps = 10000000;
	
	public LinkDataFormat(int linkType, int interDomainLink, 
			short srcNodeType, long srcId, short srcPort,
			short dstNodeType, long dstId, short dstPort)
	{
		this.linkType = linkType;
		this.interDomainLink = interDomainLink;
		this.srcNodeType = srcNodeType;
		this.srcId = srcId;
		this.srcPort = srcPort;
		this.dstNodeType = dstNodeType;
		this.dstId = dstId;
		this.dstPort = dstPort;	
		this.dataSzie = 2+2+2+2+8+2+2+8+2+8;
	}
	
	public boolean equals(LinkDataFormat comparedLink) {
		
		if(this.srcId == comparedLink.getSrcId() &&
				this.srcPort == comparedLink.getSrcPort()) {
					
			if(this.dstId == comparedLink.getDstId() &&
					this.dstPort == comparedLink.getDstPort()) {
				return true;
			}
		}
		return false;	
	}

	public int getDataSzie() {
		return dataSzie;
	}

	public int getLinkType() {
		return linkType;
	}

	public int getInterDomainLink() {
		return interDomainLink;
	}

	public int getWire() {
		return wire;
	}

	public short getSrcNodeType() {
		return srcNodeType;
	}

	public long getSrcId() {
		return srcId;
	}

	public short getSrcPort() {
		return srcPort;
	}

	public short getDstNodeType() {
		return dstNodeType;
	}

	public long getDstId() {
		return dstId;
	}

	public short getDstPort() {
		return dstPort;
	}

	public long getCapacityBps() {
		return capacityBps;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public void setInterDomainLink(int interDomainLink) {
		this.interDomainLink = interDomainLink;
	}

	public void setWire(int wire) {
		this.wire = wire;
	}

	public void setSrcNodeType(short srcNodeType) {
		this.srcNodeType = srcNodeType;
	}

	public void setSrcId(long srcId) {
		this.srcId = srcId;
	}

	public void setSrcPort(short srcPort) {
		this.srcPort = srcPort;
	}

	public void setDstNodeType(short dstNodeType) {
		this.dstNodeType = dstNodeType;
	}

	public void setDstId(long dstId) {
		this.dstId = dstId;
	}

	public void setDstPort(short dstPort) {
		this.dstPort = dstPort;
	}

	public void setCapacityBps(long capacityBps) {
		this.capacityBps = capacityBps;
	}
	
}
