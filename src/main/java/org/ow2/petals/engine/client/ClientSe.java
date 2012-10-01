/****************************************************************************
 *
 * Copyright (c) 2005-2012, Linagora
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *****************************************************************************/

package org.ow2.petals.engine.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.Component;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.jbi.component.ServiceUnitManager;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.ObjectName;

import org.ow2.petals.engine.client.misc.RealPetalsFacade;
import org.ow2.petals.engine.client.swt.SwtClient;
import org.ow2.petals.engine.client.ui.IClientUI;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * Main class of the Sample Client component.
 * <p>
 * This client is used to manage a container and to test it.
 * </p>
 *
 * @author Adrien Louis - Linagora
 * @author ddesjardins - Linagora
 * @author Marc Dutoo - Open Wide
 * @author Vincent Zurczak - Linagora
 */
public class ClientSe implements Component, ComponentLifeCycle {

	private ComponentContext context;
	private DeliveryChannel channel;
	private Logger logger;

	private IClientUI uiClient;
	private ClientJbiListener listener;



	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.ComponentLifeCycle
	 * #init(javax.jbi.component.ComponentContext)
	 */
	@Override
    public void init( ComponentContext context ) throws JBIException {
		this.context = context;
		this.logger = context.getLogger( "", null );
		this.channel = this.context.getDeliveryChannel();
		this.listener = new ClientJbiListener( this );

		// We here directly instantiate a UI client.
		// But this component was designed to have several UI clients.
		// We could imagine a Swing or Web Client.
		// In that case, we could use Java Service Providers to select one.
		this.uiClient = new SwtClient();
		this.uiClient.setPetalsFacade( new RealPetalsFacade( this ));

		this.logger.log(Level.INFO, "init");
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.ComponentLifeCycle#start()
	 */
	@Override
    public void start() throws JBIException {

	    // Log
	    this.logger.log(Level.INFO, "start");

	    // Start to listen messages
		Thread listenerThread = new Thread( this.listener, this.context.getComponentName() + "-JBI listener thread" );
		listenerThread.start();

		// Run the UI
		this.uiClient.open();
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.ComponentLifeCycle#stop()
	 */
	@Override
    public void stop() throws JBIException {

	    // Log
		this.logger.log(Level.INFO, "stop");

		// Stop accepting messages
		this.listener.stopProcessing();

		// Close the UI
		this.uiClient.close();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.ComponentLifeCycle#shutDown()
	 */
	@Override
    public void shutDown() throws JBIException {

	    // Log
		this.logger.log(Level.INFO, "shutDown");

		// Close the channel
		this.channel.close();
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component#getLifeCycle()
	 */
	@Override
    public ComponentLifeCycle getLifeCycle() {
		return this;
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component
	 * #getServiceDescription(javax.jbi.servicedesc.ServiceEndpoint)
	 */
	@Override
    public Document getServiceDescription(ServiceEndpoint arg0) {
		return null;  // This component is not a service provider
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component
	 * #getServiceUnitManager()
	 */
	@Override
    public ServiceUnitManager getServiceUnitManager() {
		return null;  // No SU is allowed
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component
	 * #isExchangeWithConsumerOkay(javax.jbi.servicedesc.ServiceEndpoint, javax.jbi.messaging.MessageExchange)
	 */
	@Override
    public boolean isExchangeWithConsumerOkay( ServiceEndpoint arg0, MessageExchange arg1 ) {
		return false; // This component is not a service provider
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component
	 * #isExchangeWithProviderOkay(javax.jbi.servicedesc.ServiceEndpoint, javax.jbi.messaging.MessageExchange)
	 */
	@Override
    public boolean isExchangeWithProviderOkay(ServiceEndpoint arg0, MessageExchange arg1) {
		this.logger.log(Level.INFO, "ClientSe accept the exchange");
		return true;
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.Component
	 * #resolveEndpointReference(org.w3c.dom.DocumentFragment)
	 */
	@Override
    public ServiceEndpoint resolveEndpointReference(DocumentFragment arg0) {
		return null;
	}


	/*
	 * (non-Javadoc)
	 * @see javax.jbi.component.ComponentLifeCycle
	 * #getExtensionMBeanName()
	 */
	@Override
    public ObjectName getExtensionMBeanName() {
		return null;  // No MBean
	}


    /**
     * @return the context
     */
    public ComponentContext getComponentContext() {
        return this.context;
    }


    /**
     * @return the delivery channel
     */
    public DeliveryChannel getDeliveryChannel() {
        return this.channel;
    }


    /**
     * @return the logger
     */
    public Logger getLogger() {
        return this.logger;
    }


    /**
     * @return the uiClient
     */
    public IClientUI getUiClient() {
        return this.uiClient;
    }


    /**
     * @return the JBI listener
     */
    public ClientJbiListener getJbiListener() {
        return this.listener;
    }
}
