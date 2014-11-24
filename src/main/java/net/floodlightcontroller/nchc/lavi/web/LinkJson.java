package net.floodlightcontroller.nchc.lavi.web;


import java.io.IOException;

import net.floodlightcontroller.nchc.sentdataformate.LinkDataFormat;
import net.floodlightcontroller.nchc.sentdataformate.NormalDataFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * This class is both the datastructure and the serializer
 * for a link with the corresponding type of link.
 * @author alexreimers
 */
@JsonSerialize(using=LinkJson.class)
public class LinkJson extends JsonSerializer<LinkJson> {
	
	private NormalDataFormat linkData;
	
	// Do NOT delete this, it's required for the serializer 
	public LinkJson() {};
	
	
	public LinkJson(NormalDataFormat linkData) {
		this.linkData = linkData;
	}

	@Override
	public void serialize(LinkJson lj, JsonGenerator jgen, SerializerProvider arg2) 
			throws IOException, JsonProcessingException {
		// You ****MUST*** use nj for the fields as it's actually a different object.
		
		
		String fieldNameprefix = new String("Link");
		int linkCount = 1;
		
		
		jgen.writeStartObject();
		
		jgen.writeNumberField("DataLength", lj.linkData.getDataLength());
		jgen.writeNumberField("TotalIpLength", lj.linkData.getIpStringTotalLen());
		jgen.writeNumberField("Command", lj.linkData.getCommandType());
		jgen.writeNumberField("Xid", lj.linkData.getXid());

		for(LinkDataFormat link : lj.linkData.getLinkDataList())
		{
			
			String fieldName = fieldNameprefix + linkCount;
			
			jgen.writeObjectFieldStart(fieldName);
			jgen.writeNumberField("Host2Switch", link.getLinkType());
			jgen.writeNumberField("InterDomainLink", link.getInterDomainLink());
			jgen.writeNumberField("LinkType", link.getWire());
			jgen.writeNumberField("SrcNodeType", link.getSrcNodeType());
			jgen.writeNumberField("SrcId", link.getSrcId());
			jgen.writeNumberField("SrcPort", link.getSrcPort());
			jgen.writeNumberField("DstNodeType", link.getDstNodeType());
			jgen.writeNumberField("DstId", link.getDstId());
			jgen.writeNumberField("DstPort", link.getDstPort());
			jgen.writeNumberField("CapacityBps", link.getCapacityBps());
			jgen.writeEndObject();
			
			linkCount++;
		}
		
/*		ArrayList<NodeDataFormat> nodeDataList = nj.nodeData.getNodeDataList();
		for(NodeDataFormat node : nodeDataList)
		{
			jgen.writeNumberField("nodeType", node.getNodeType());
			jgen.writeNumberField("NodeID", node.getId());
			jgen.writeNumberField("IpLength", node.getIpStringLen());
			jgen.writeStringField("ControllerIP", node.getIpString());
			//out.write(node.getIpString().getBytes(), 0, node.getIpStringLen());
		}	
		*/		
		jgen.writeEndObject();
	}
	
	@Override
	public Class<LinkJson> handledType() {
		return LinkJson.class;
	}
}
