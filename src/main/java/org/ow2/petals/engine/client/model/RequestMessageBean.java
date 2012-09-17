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

package org.ow2.petals.engine.client.model;

import javax.xml.namespace.QName;

/**
 * @author Christophe Hamerling - Linagora
 */
public class RequestMessageBean extends BasicMessageBean implements Cloneable {

    private QName interfaceName;
    private QName serviceName;
    private String endpointName;

    private Mep mep;
    private long timeout;
    private QName operation;


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
     * @return the serviceName
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the endpointName
     */
    public String getEndpointName() {
        return this.endpointName;
    }

    /**
     * @param endpointName the endpointName to set
     */
    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     * @return the mep
     */
    public Mep getMep() {
        return this.mep;
    }

    /**
     * @param mep the mep to set
     */
    public void setMep(Mep mep) {
        this.mep = mep;
    }

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the operation
     */
    public QName getOperation() {
        return this.operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(QName operation) {
        this.operation = operation;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object
     * #clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {

    	RequestMessageBean clone = new RequestMessageBean();
    	clone.endpointName = this.endpointName;
    	clone.interfaceName = this.interfaceName;
    	clone.mep = this.mep;
    	clone.operation = this.operation;
    	clone.serviceName = this.serviceName;
    	clone.timeout = this.timeout;

    	return clone;
    }
}
