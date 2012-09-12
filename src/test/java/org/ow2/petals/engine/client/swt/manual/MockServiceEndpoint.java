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

package org.ow2.petals.engine.client.swt.manual;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.w3c.dom.DocumentFragment;

/**
 * A mock implementation of service end-point.
 * @author Vincent Zurczak - Linagora
 */
public class MockServiceEndpoint implements ServiceEndpoint {

	private String endpointName;
	private QName interfaceName, serviceName;


	/**
	 * Constructor.
	 */
	public MockServiceEndpoint() {
		// nothing
	}

	/**
	 * Constructor.
	 * @param interfaceName
	 */
	public MockServiceEndpoint(QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * Constructor.
	 * @param endpointName
	 * @param interfaceName
	 * @param serviceName
	 */
	public MockServiceEndpoint(String endpointName, QName interfaceName, QName serviceName) {
		this.endpointName = endpointName;
		this.interfaceName = interfaceName;
		this.serviceName = serviceName;
	}

	/* (non-Javadoc)
	 * @see javax.jbi.servicedesc.ServiceEndpoint#getAsReference(javax.xml.namespace.QName)
	 */
	@Override
	public DocumentFragment getAsReference( QName arg0 ) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.jbi.servicedesc.ServiceEndpoint#getEndpointName()
	 */
	@Override
	public String getEndpointName() {
		return this.endpointName;
	}

	/* (non-Javadoc)
	 * @see javax.jbi.servicedesc.ServiceEndpoint#getInterfaces()
	 */
	@Override
	public QName[] getInterfaces() {
		return new QName[] { this.interfaceName };
	}

	/* (non-Javadoc)
	 * @see javax.jbi.servicedesc.ServiceEndpoint#getServiceName()
	 */
	@Override
	public QName getServiceName() {
		return this.serviceName;
	}

	/**
	 * @return the interfaceName
	 */
	public QName getInterfaceName() {
		return this.interfaceName;
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @param endpointName the endpointName to set
	 */
	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}
}
