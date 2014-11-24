package net.floodlightcontroller.nchc.enviconnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.annotations.LogMessageCategory;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.forwarding.Forwarding;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.nchc.ipstring.UserInputPublicIp;
import net.floodlightcontroller.nchc.lavi.web.LaviWebRoutable;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;


@LogMessageCategory("ENVI Programming")
public class ConnectionWithENVIManagement implements IOFMessageListener,
		IFloodlightModule {

	protected static Logger log = LoggerFactory.getLogger(Forwarding.class);
	
	
	protected IFloodlightProviderService floodlightProvider;
	protected IDeviceService deviceService;
	protected ILinkDiscoveryService linkDiscoveryService;
	protected IRoutingService routingService;
	protected IRestApiService restApi;
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ConnectionWithENVIManagement";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l =
	        new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    l.add(IRestApiService.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub		
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		deviceService = context.getServiceImpl(IDeviceService.class);
		linkDiscoveryService  = context.getServiceImpl(ILinkDiscoveryService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		
		
		Map<String, String> configOptions = context.getConfigParams(this);
		String ctlIP = configOptions.get("controllerIP");
		String enableUI = configOptions.get("enableUIOfInputIP");	
        UserInputPublicIp inputIp = new UserInputPublicIp();
        Short enableIPUI = 0;

        try{
        	if(enableUI != null)
        		enableIPUI = Short.parseShort(enableUI);
        } catch( NumberFormatException e)
        {
            log.warn("Error parsing enable IP input UI setting, " +
           		 "using default of {} ",
           		0);        	
        }
        
        if(enableIPUI != 0 || enableIPUI != 1)
        {
        	log.warn("Error parsing enable IP input UI setting, " +
              		 "using default of {} ",
              		0);   
        	enableIPUI = 0;
        }
        
        switch (enableIPUI)
        {
        case 0:
        	inputIp.loadControllerIP(ctlIP);
        	break;
        case 1:
        	inputIp.runInterface();
        	break;
        }
        
        
        
        
  //      if(enableUI == null)
  //      	enableUI = "0";
  //      Short.parseShort(enableUI);
        
    //    enableUI
 //       inputIp.loadControllerIP();
 //       inputIp.runInterface();
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		HandleEnviConnection connection = new HandleEnviConnection(floodlightProvider,deviceService,
											linkDiscoveryService,
											routingService);
		Thread connectionThread = new Thread(connection);
		addRestletRoutable(floodlightProvider,deviceService,
						   linkDiscoveryService,
						   routingService);
		connectionThread.start();
		
	}
	
	
    protected void addRestletRoutable(IFloodlightProviderService floodlightProvider, 
									  IDeviceService deviceService, 
									  ILinkDiscoveryService linkDiscoveryService,
									  IRoutingService routingService) {
        restApi.addRestletRoutable(new LaviWebRoutable(floodlightProvider, deviceService,
        											   linkDiscoveryService, routingService));
    }
	
	

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
	    
		// TODO Auto-generated method stub
        return Command.CONTINUE;
    }
	

}
