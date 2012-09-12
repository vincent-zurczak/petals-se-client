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

import javax.jbi.JBIException;
import javax.jbi.component.Bootstrap;
import javax.jbi.component.InstallationContext;
import javax.management.ObjectName;

/**
 * The component bootstrap.
 * @author Adrien Louis - Linagora
 * @author Olivier Fabre - Linagora
 * @author ddesjardins - Linagora
 */
public class ClientBootstrap implements Bootstrap {

    /*
     * (non-Javadoc)
     * @see javax.jbi.component.Bootstrap#cleanUp()
     */
    @Override
    public void cleanUp() throws JBIException {
        // nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.jbi.component.Bootstrap#getExtensionMBeanName()
     */
    @Override
    public ObjectName getExtensionMBeanName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.jbi.component.Bootstrap
     * #init(javax.jbi.component.InstallationContext)
     */
    @Override
    public void init( InstallationContext installContext ) throws JBIException {
        // nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.jbi.component.Bootstrap#onInstall()
     */
    @Override
    public void onInstall() throws JBIException {
        // nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.jbi.component.Bootstrap#onUninstall()
     */
    @Override
    public void onUninstall() throws JBIException {
        // nothing
    }
}
