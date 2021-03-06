/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.discovery.internal.console;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.io.console.Console;
import org.eclipse.smarthome.io.console.extensions.ConsoleCommandExtension;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * {@link DiscoveryConsoleCommandExtension} provides console commands for thing discovery.
 * 
 * @author Kai Kreuzer - Initial contribution
 * @author Dennis Nobel - Added background discovery commands
 */
public class DiscoveryConsoleCommandExtension implements ConsoleCommandExtension {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryConsoleCommandExtension.class);
    
    private DiscoveryServiceRegistry discoveryServiceRegistry;

    private ConfigurationAdmin configurationAdmin;
    
    private final static String COMMAND_DISCOVERY = "discovery";

    private final static List<String> SUPPORTED_COMMANDS = Lists.newArrayList(COMMAND_DISCOVERY);

    @Override
    public boolean canHandle(String[] args) {
        String firstArgument = args[0];
        return SUPPORTED_COMMANDS.contains(firstArgument);
    }

    @Override
    public void execute(String[] args, Console console) {
        String command = args[0];
        switch (command) {
        case COMMAND_DISCOVERY:
            if(args.length > 1) {
                String subCommand = args[1];
                switch (subCommand) {
                case "start":
                    if (args.length > 2) {
                    	String arg2 = args[2];
                    	if(arg2.contains(":")) {
                    		ThingTypeUID thingTypeUID = new ThingTypeUID(arg2);
                    		runDiscoveryForThingType(console, thingTypeUID);
                    	} else {
                    		runDiscoveryForBinding(console, arg2);
                    	}
                    } else {
						console.println("Specify thing type id or binding id to discover: discovery "
								+ "start <thingTypeUID|bindingID> (e.g. \"hue:bridge\" or \"hue\")");
                    }
                    return;
                case "enableBackgroundDiscovery":
                    if (args.length > 2) {
                        String discoveryServiceName = args[2];
                        configureBackgroundDiscovery(console, discoveryServiceName, true);
                    } else {
                        console.println("Specify discovery service PID to configure background discovery: discovery "
                                + "enableBackgroundDiscovery <PID> (e.g. \"hue.discovery\")");
                    }
                    return;
                case "disableBackgroundDiscovery":
                    if (args.length > 2) {
                        String discoveryServiceName = args[2];
                        configureBackgroundDiscovery(console, discoveryServiceName, false);
                    } else {
                        console.println("Specify discovery service PID to configure background discovery: discovery "
                                + "disableBackgroundDiscovery <PID> (e.g. \"hue.discovery\")");
                    }
                    return;
                default:
                    break;
                }
            } else {
            	console.println(getUsages().get(0));
            }
            return;
        default:
            return;
        }
    }

    private void configureBackgroundDiscovery(Console console, String discoveryServicePID, boolean enabled) {
        try {
            Configuration configuration = configurationAdmin.getConfiguration(discoveryServicePID);
            Dictionary<String, Object> properties = configuration.getProperties();
            if (properties == null) {
                properties = new Hashtable<>();
            }
            properties.put(DiscoveryService.CONFIG_PROPERTY_BACKGROUND_DISCOVERY_ENABLED, enabled);
            configuration.update(properties);
            console.println("Background discovery for discovery service '" + discoveryServicePID + "' was set to " + enabled
                    + ".");
        } catch (IOException ex) {
            String errorText = "Error occured while trying to configure background discovery with PID '"
                    + discoveryServicePID + "': " + ex.getMessage();
            logger.error(errorText, ex);
            console.println(errorText);
        }
    }

    private void runDiscoveryForThingType(Console console, ThingTypeUID thingTypeUID) {
		discoveryServiceRegistry.startScan(thingTypeUID, null);
	}
    
    private void runDiscoveryForBinding(Console console, String bindingId) {
		discoveryServiceRegistry.startScan(bindingId, null);
	}

    public List<String> getUsages() {
        return Lists
                .newArrayList(
                        "discovery start <thingTypeUID|bindingID> - runs a discovery on a given thing type or binding",
                        "discovery enableBackgroundDiscovery <PID> - enables background discovery for the discovery service with the given PID",
                        "discovery disableBackgroundDiscovery <PID> - disables background discovery for the discovery service with the given PID");
    }

	protected void setDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
        this.discoveryServiceRegistry = discoveryServiceRegistry;
    }

    protected void unsetDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
        this.discoveryServiceRegistry = null;
    }
    
    protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }
    
    protected void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }
    
}