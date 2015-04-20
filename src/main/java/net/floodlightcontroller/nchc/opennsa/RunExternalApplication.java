package net.floodlightcontroller.nchc.opennsa;

import java.io.IOException;
import java.util.Scanner;

public class RunExternalApplication implements Runnable {
	
	public RunExternalApplication() {};
	
	public void callApplication()
	{
		String command = "python2.7 idmnsa.py sendmsg -u http://192.168.100.209:9080/NSI/services/CS";
		Process p;
		try {
			System.out.println("ready to start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			p = Runtime.getRuntime().exec(command);
			Scanner resultString = new Scanner(p.getInputStream()); 
			while(resultString.hasNextLine()) 
				System.out.println(resultString.nextLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RunExternalApplication callApp = new RunExternalApplication();
		callApp.callApplication();
		
	}
	
}
