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

package org.ow2.petals.engine.client.swt;

import java.util.logging.Logger;

import org.ow2.petals.engine.client.model.ResponseMessageBean;
import org.ow2.petals.engine.client.ui.IClientUI;
import org.ow2.petals.engine.client.ui.PetalsFacade;

/**
 * A SWT client for the client component.
 * @author Vincent Zurczak - Linagora
 */
public class SwtClient implements IClientUI {

    PetalsFacade petalsFacade;


    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #open()
     */
    @Override
    public void open() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #close()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #hide()
     */
    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #show()
     */
    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #reportError(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void reportError( String msg, Throwable t ) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #displayResponse(org.ow2.petals.engine.client.model.ResponseMessageBean)
     */
    @Override
    public void displayResponse( ResponseMessageBean response ) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #clearDisplayedResponse()
     */
    @Override
    public void clearDisplayedResponse() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #setPetalsFacade(org.ow2.petals.engine.client.ui.IPetalsFacade)
     */
    @Override
    public void setPetalsFacade( PetalsFacade petalsFacade ) {
        this.petalsFacade = petalsFacade;
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #setPetalsLogger(java.util.logging.Logger)
     */
	@Override
	public void setPetalsLogger(Logger logger) {
		// TODO Auto-generated method stub
	}

	/**
	 * Restarts the user interface (e.g. to take new preferences into account).
	 */
	public void restartUserInterface() {

	}
}
