package net.floodlightcontroller.nchc.lavi.web;


import org.restlet.Context;
import org.restlet.routing.Router;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.restserver.RestletRoutable;
import net.floodlightcontroller.routing.IRoutingService;

public class LaviWebRoutable implements RestletRoutable {
    /**
     * Create the Restlet router and bind to the proper resources.
     */
	static private IFloodlightProviderService floodlightProvider;
	static private IDeviceService deviceService;
	static private ILinkDiscoveryService linkDiscoveryService;
	static private IRoutingService routingService;
	
	public LaviWebRoutable(IFloodlightProviderService floodlightProvider, 
						   IDeviceService deviceService, 
						   ILinkDiscoveryService linkDiscoveryService,
						   IRoutingService routingService) {
		LaviWebRoutable.floodlightProvider = floodlightProvider;
		LaviWebRoutable.deviceService = deviceService;
		LaviWebRoutable.linkDiscoveryService = linkDiscoveryService;
		LaviWebRoutable.routingService = routingService;
	}
	
    @Override
    public Router getRestlet(Context context) {
        Router router = new Router(context);

   //     router.attach("/node/switch", switchResource.getClass());
        router.attach("/node/switch", SwitchResource.class);
        router.attach("/node/host", HostResource.class);
        router.attach("/link/sw2sw", Sw2SwResource.class);
        router.attach("/link/host2sw", Host2SwResource.class);
        router.attach("/flow/swflows", SwFlowResource.class);
        router.attach("/flow/hostflows", HostFlowResource.class);

     //   router.attach("/tunnellinks/json", TunnelLinksResource.class);
     //   router.attach("/switchclusters/json", SwitchClustersResource.class);
     //   router.attach("/broadcastdomainports/json", BroadcastDomainPortsResource.class);
      //  router.attach("/enabledports/json", EnabledPortsResource.class);
     //   router.attach("/blockedports/json", BlockedPortsResource.class);
     //   router.attach("/route/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", RouteResource.class);
        return router;
    }

    public static IFloodlightProviderService getFloodlightProvider() {
		return floodlightProvider;
	}

	public static IDeviceService getDeviceService() {
		return deviceService;
	}

	public static ILinkDiscoveryService getLinkDiscoveryService() {
		return linkDiscoveryService;
	}

	public static IRoutingService getRoutingService() {
		return routingService;
	}


	/**
     * Set the base path for the Topology
     */
    @Override
    public String basePath() {
        return "/lavi/data";
    }
}

