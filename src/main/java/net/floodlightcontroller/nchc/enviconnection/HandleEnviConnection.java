package net.floodlightcontroller.nchc.enviconnection;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleEnviConnection implements Runnable{
	
	protected static Logger log = LoggerFactory.getLogger(ConnectionWithENVIManagement.class);
	
	private int [] requstArray = {0,0,0,0,0};
	private int defaultPort = 2503;
	private static int readedByte = 0;
	private boolean isReadyRead = true;
	private Socket clientSocket = null;
	private ArrayList<Long> swList = new ArrayList<Long>();
	private ArrayList<IDevice> deviceList = new ArrayList<IDevice>();
	@SuppressWarnings("unused")
	private HashMap<Long, Set<Link>> swLinkMap = new HashMap<Long, Set<Link>>();
	private HashMap<Long, ArrayList<Link>> hostLinkMap = new HashMap<Long, ArrayList<Link>>();
	HashMap<Long, ArrayList<Short>> interDomainPort = new HashMap<Long, ArrayList<Short>>();

	
	
	protected IFloodlightProviderService floodlightProvider;
	protected IDeviceService deviceService;
	protected ILinkDiscoveryService linkDiscoveryService;
	protected IRoutingService routingService;
	
	public HandleEnviConnection(IFloodlightProviderService floodlightProvider, 
			IDeviceService deviceService, 
			ILinkDiscoveryService linkDiscoveryService,
			IRoutingService routingService)
	{
		this.floodlightProvider = floodlightProvider;
		this.deviceService = deviceService;
		this.linkDiscoveryService = linkDiscoveryService;
		this.routingService = routingService;
		this.swList = null;
	}
	
	
	//wait for envi connection
	public void startSocket()
	{
        try {
            ServerSocket serverSock = new ServerSocket(defaultPort);
            while(true) {
                clientSocket = serverSock.accept();
                String showIP = "got a ENVI connection from ("+
                				clientSocket.getInetAddress()+","+
                				clientSocket.getLocalPort()+")";
                log.info(showIP);
                getRequest();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startSocket();
	}
	
	//get the request
	public void getRequest()
	{
		 try {
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()); 
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			isReadyRead = true;
			while(isReadyRead)
			{
				readedByte = 0;
				requstArray[0] = dis.readShort();
				readedByte = readedByte + 2;
				requstArray[1] = dis.readByte();
				readedByte = readedByte + 1;
				requstArray[2] = dis.readInt();
				readedByte = readedByte + 4;
				replyRequest(requstArray,dis,out);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("close connection");
			//e.printStackTrace();
		}
		
	}
	
	//check request type and then reply data to envi
	public void replyRequest(int [] request,DataInputStream in,DataOutputStream out) throws IOException
	{
		boolean sendDataToSocket = true;
		SwToEnvi swToEnvi = new SwToEnvi(out,floodlightProvider);
		HostToEnvi hostToEnvi = new HostToEnvi(out,deviceService);
		SwLinkToEnvi swLinkToEnvi = new SwLinkToEnvi(out,linkDiscoveryService);
		HostLinkToEnvi hostLinkToEnvi = new HostLinkToEnvi(out,deviceList);
		SwFlowToEnvi swFlowToEnvi = new SwFlowToEnvi(out, sendDataToSocket);
		HostFlowToEnvi hostFlowToEnvi = new HostFlowToEnvi(out, sendDataToSocket);
		
		//node request
		if(request[1] == EnviProtocol.NODE_REQUEST)
		{			
			requstArray[3] = in.readByte();
			readedByte = readedByte + 1;
			requstArray[4] = in.readShort();
			readedByte = readedByte + 2;
			
			//reply data
			if(requstArray[3] == EnviProtocol.REQUEST)
			{	
				
				swList = swToEnvi.setSwitchData(sendDataToSocket);	
				deviceList = hostToEnvi.setHostData(swList, sendDataToSocket);	
			}
		}
		else if(request[1] == EnviProtocol.LINK_REQUEST)
		{
			requstArray[3] = in.readByte();
			readedByte = readedByte + 1;
			requstArray[4] = in.readShort();
			readedByte = readedByte + 2;
	//		for(int i:requstArray)
	//		{
	//			log.info(Integer.toString(i));
	//		}			
			//skip addition 10 byte non-use data
			if(readedByte != requstArray[0])
			{
					in.skipBytes(10);
					readedByte = readedByte + 10;
			}
			
			//reply data
			if(requstArray[3] == EnviProtocol.REQUEST)
			{
				interDomainPort = swLinkToEnvi.setSwlinkData(swList, sendDataToSocket);
				hostLinkMap = hostLinkToEnvi.setHostLinkData(swList,interDomainPort, sendDataToSocket);
			}
		}
		
		//flow request
		else if(request[1] == EnviProtocol.FLOW_REQUEST)
		{
			requstArray[3] = in.readByte();
			readedByte = readedByte + 1;
			requstArray[4] = in.readShort();
			readedByte = readedByte + 2;
			
			//reply data
			if(requstArray[3] == EnviProtocol.REQUEST)
			{
				HashMap<Ethernet, ArrayList<Route>> hostFlowMap = new HashMap<Ethernet, ArrayList<Route>>();
				hostFlowMap = swFlowToEnvi.sendMessage(hostLinkMap, interDomainPort);
				hostFlowToEnvi.sendMessage(hostFlowMap);		
			}
		}
		else
		{
			log.info("unknown data stream.");
			isReadyRead = false;
		}

	}

}
