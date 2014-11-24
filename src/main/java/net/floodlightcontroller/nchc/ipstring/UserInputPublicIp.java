package net.floodlightcontroller.nchc.ipstring;

import javax.swing.JOptionPane;

import net.floodlightcontroller.nchc.sentdataformate.EnviProtocol;
import net.floodlightcontroller.packet.IPv4;

public class UserInputPublicIp{

	
	public void loadControllerIP(String ctlIP){
		if(ctlIP == null)
			ctlIP = "127.0.0.1";
		else
		{
			try {
				EnviProtocol.PUBLIC_IP = IPv4.toIPv4Address(ctlIP);
				} catch (Exception e) {
					// TODO: handle exception
					EnviProtocol.PUBLIC_IP = 0;
					}	
		}
	}
	
	
	public void runInterface(){
		String IP = JOptionPane.showInputDialog("Please input your public IP.","127.0.0.1");
		try {
			EnviProtocol.PUBLIC_IP = IPv4.toIPv4Address(IP);
		} catch (Exception e) {
			// TODO: handle exception
			EnviProtocol.PUBLIC_IP = 0;
		}
			
	}
}
