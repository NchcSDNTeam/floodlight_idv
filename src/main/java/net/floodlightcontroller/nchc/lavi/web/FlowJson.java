package net.floodlightcontroller.nchc.lavi.web;


import java.io.IOException;
import java.util.ArrayList;

import net.floodlightcontroller.nchc.sentdataformate.FlowDataFormat;

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
@JsonSerialize(using=FlowJson.class)
public class FlowJson extends JsonSerializer<FlowJson> {
	
	private ArrayList<FlowDataFormat> flowList;
	private int dataLength = 9;
	
	// Do NOT delete this, it's required for the serializer 
	public FlowJson() {};
	
	
	public FlowJson(ArrayList<FlowDataFormat> flowList) {
		this.flowList = flowList;
		
		for( FlowDataFormat flowData : flowList) {			
		//	dataLength = dataLength + flowData.getDataLength();
		//	dataLength = dataLength - 9;
			dataLength = flowData.getDataLength();
		}
	}

	@Override
	public void serialize(FlowJson fj, JsonGenerator jgen, SerializerProvider arg2) 
			throws IOException, JsonProcessingException {
		// You ****MUST*** use nj for the fields as it's actually a different object.
		
		String fieldNameprefix = new String("Flow");
		int flowCount = 1;
		
		
		jgen.writeStartObject();
		
		
		if(!(fj.flowList.isEmpty())) {		
			
			jgen.writeNumberField("DataLength", fj.dataLength);
			jgen.writeNumberField("isFlow", -10);
			jgen.writeNumberField("Command", fj.flowList.get(0).getMsgtype());
			jgen.writeNumberField("Xid", 0);
			
			int remainFlowNumber = fj.flowList.size();
			for( FlowDataFormat flowData : fj.flowList) {
				
				String fieldName = fieldNameprefix + flowCount;
				
				jgen.writeObjectFieldStart(fieldName);
				jgen.writeNumberField("RemainFlowNumber", remainFlowNumber);
				remainFlowNumber = remainFlowNumber - flowData.getFlowNumber();
				jgen.writeNumberField("FlowType", flowData.getFlowType());
				jgen.writeNumberField("FlowID", flowData.getFlowId());
				jgen.writeNumberField("SrcNodeType", flowData.getSrcType());
				jgen.writeNumberField("SrcNodeID", flowData.getSrcId());
				jgen.writeNumberField("SrcPort", flowData.getSrcPort());
				jgen.writeNumberField("DstNodeType", flowData.getDstType());
				jgen.writeNumberField("DstNodeID", flowData.getDstId());
				jgen.writeNumberField("DstPort", flowData.getDstPort());
				
				jgen.writeNumberField("SrcHostMACLength", flowData.getSrcClientMacLen());
				jgen.writeStringField("SrcHostMAC", flowData.getSrcClientMac());
				jgen.writeNumberField("SrcHostIPLength", flowData.getSrcClientIpLen());
				jgen.writeStringField("SrcHostIP", flowData.getSrcClientIp());
				
				jgen.writeNumberField("DstHostMACLength", flowData.getDstClientMacLen());
				jgen.writeStringField("DstHostMAC", flowData.getDstClientMac());
				jgen.writeNumberField("DstHostIPLength", flowData.getDstClientIpLen());
				jgen.writeStringField("DstHostIP", flowData.getDstClientIp());
				
				jgen.writeNumberField("EthernetTypeLength", flowData.getEthernetTypeLen());
				jgen.writeStringField("EthernetType", flowData.getEthernetType());
				jgen.writeNumberField("PathLength", flowData.getPathLen());
				jgen.writeEndObject();
				
				
				flowCount++;
				
			}	
			
				
		}
		else
			jgen.writeStringField("FlowStatus", "no Flows");
		jgen.writeEndObject();
	}
			
	
	/*
	
	private void sendMessageOut(FlowDataFormat flowData)
	{
		try {
			out.writeShort(flowData.getDataLength());
			out.writeShort(flowData.getFlowIdentified());
			out.writeByte(flowData.getMsgtype());
			out.writeInt(flowData.getXid());			
			out.writeInt(flowData.getFlowNumber());
			out.writeShort(flowData.getFlowType());
			out.writeInt(flowData.getFlowId());
			
			out.writeShort(flowData.getSrcType());
			out.writeLong(flowData.getSrcId());
			out.writeShort(flowData.getSrcPort());
			
			out.writeShort(flowData.getDstType());
			out.writeLong(flowData.getDstId());
			out.writeShort(flowData.getDstPort());
			
			
			out.writeShort(flowData.getSrcClientMacLen());
			out.write(flowData.getSrcClientMac().getBytes(), 0, flowData.getSrcClientMacLen());
			out.writeShort(flowData.getSrcClientIpLen());
			out.write(flowData.getSrcClientIp().getBytes(), 0, flowData.getSrcClientIpLen());
			
			out.writeShort(flowData.getDstClientMacLen());
			out.write(flowData.getDstClientMac().getBytes(), 0, flowData.getDstClientMacLen());
			out.writeShort(flowData.getDstClientIpLen());
			out.write(flowData.getDstClientIp().getBytes(), 0, flowData.getDstClientIpLen());
			
			out.writeShort(flowData.getEthernetTypeLen());
			out.write(flowData.getEthernetType().getBytes(), 0, flowData.getEthernetTypeLen());
			
			out.writeShort(flowData.getPathLen());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	
	@Override
	public Class<FlowJson> handledType() {
		return FlowJson.class;
	}
}
