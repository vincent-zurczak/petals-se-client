/****************************************************************************
 *
 * Copyright (c) 2012, Linagora
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

package org.ow2.petals.engine.client.ui;

import java.util.List;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.w3c.dom.Document;

/**
 * A <i>façade</i> used by the UI to interact with Petals.
 * <p>
 * This class was added so that a mock implementation could be done.
 * The mock implementation avoids the use of a real Petals server and allows to test
 * the user interface automatically or interactively.
 * </p>
 *
 * @author Vincent Zurczak - Linagora
 */
public abstract class PetalsFacade {

	private WSDLReader wsdlReader;
	private boolean wsdlReaderLoaded = false;


	/**
     * @return an instance of {@link WSDLReader}
     */
    public WSDLReader getWsdlReader() {

    	if( ! this.wsdlReaderLoaded ) {
    		this.wsdlReaderLoaded = true;
    		try {
				this.wsdlReader = WSDLFactory.newInstance().newWSDLReader();

			} catch( WSDLException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    	return this.wsdlReader;
    }


    /**
     * Sends a message to Petals service.
     * @param request the request to send (not null)
     * @throws an exception if an error occurred
     */
    public abstract void send( RequestMessageBean request ) throws Exception;

    /**
     * Finds the WSDL description for a given end-point.
     * @param se the service end-point
     * @return a document (null if there is no description)
     * @throws an exception if an error occurred
     */
    public abstract Document findWsdlDescriptionAsDocument( ServiceEndpoint se ) throws Exception;

    /**
     * Resolves a service end-point from a service identifier.
     * @param itfName the qualified name of an interface (not null)
     * @param srvName the qualified name of a service (can be null)
     * @param epdtName the name of an end-point (can be null)
     * @return a service end-point, or null if it could not be resolved
     * @throws Exception if an error occurred
     */
    public abstract ServiceEndpoint resolveServiceEndpoint( QName itfName, QName srvName, String edptName ) throws Exception;

    /**
     * Gets all the service end-points from Petals.
     * @return a non-null list of service end-points
     * @throws Exception if a problem occurred
     */
    public abstract List<ServiceEndpoint> getAllServiceEndpoints() throws Exception;
}
