package net.floodlightcontroller.nchc.lavi.web;


import java.io.IOException;
import java.util.ArrayList;

import net.floodlightcontroller.nchc.sentdataformate.NodeDataFormat;
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
@JsonSerialize(using=NodeJson.class)
public class NodeJson extends JsonSerializer<NodeJson> {
	
	private NormalDataFormat nodeData;
	
	// Do NOT delete this, it's required for the serializer 
	public NodeJson() {};
	
	public NodeJson(NormalDataFormat nodeData) {
		this.nodeData = nodeData;
	}

	@Override
	public void serialize(NodeJson nj, JsonGenerator jgen, SerializerProvider arg2) 
			throws IOException, JsonProcessingException {
		// You ****MUST*** use nj for the fields as it's actually a different object.
		
		String fieldNameprefix = new String("Node");
		int nodeCount = 1;
		
		jgen.writeStartObject();
		
		jgen.writeNumberField("DataLength", nj.nodeData.getDataLength());
		jgen.writeNumberField("TotalIpLength", nj.nodeData.getIpStringTotalLen());
		jgen.writeNumberField("Command", nj.nodeData.getCommandType());
		jgen.writeNumberField("Xid", nj.nodeData.getXid());
		
		ArrayList<NodeDataFormat> nodeDataList = nj.nodeData.getNodeDataList();
		for(NodeDataFormat node : nodeDataList)
		{
			String fieldName = fieldNameprefix + nodeCount;
			
			jgen.writeObjectFieldStart(fieldName);
			jgen.writeNumberField("nodeType", node.getNodeType());
			jgen.writeNumberField("NodeID", node.getId());
			jgen.writeNumberField("IpLength", node.getIpStringLen());
			jgen.writeStringField("ControllerIP", node.getIpString());
			//out.write(node.getIpString().getBytes(), 0, node.getIpStringLen());
			jgen.writeEndObject();
			
			nodeCount++;
		}			
		jgen.writeEndObject();
	}
	
	@Override
	public Class<NodeJson> handledType() {
		return NodeJson.class;
	}
}
