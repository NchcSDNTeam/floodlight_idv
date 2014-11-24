package net.floodlightcontroller.nchc.sentdataformate;

public class EnviProtocol {
	
	public static int PUBLIC_IP = 0;
	public static short MONITOR_PORT = 0;
	
	//NormalType
	public static final int NODE_REQUEST = 16;
	public static final int LINK_REQUEST = 19;
	public static final int FLOW_REQUEST = 22;	
	public static final int REQUEST = 1;
	public static final int SUBSCRIBE = 2;
	public static final int UNSUBSCRIBE = 3;	
	
	//commandType
	public static final byte NODEADD = 17;
	public static final byte NODEDELETE = 18;
	public static final byte LINKADD = 20;
	public static final byte LINKDELETE = 21;
	public static final byte FLOWADD = 23;
	public static final byte FLOWDELETE = 24;
	
	//node data
	public static final short SWITCH = 1;
	public static final short HOST = 256;
	public static final short HostPort = 0;
	
	
	//link data
	public static final int WIRE = 1;
	public static final int WIRELESS = 2;
	public static final int SWITCHLINK = 0;
	public static final int HOSTLINK = 1;
	public static final int DONT_CARE_INTERDOMAIN = 3;
	public static final int HOST_INTERDOMAIN = 1;
	public static final int HOST_INTRADOMAIN = 0;
	
	

}
