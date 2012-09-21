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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.ui.IClientUI;
import org.ow2.petals.engine.client.ui.PetalsFacade;

/**
 * A SWT client for the client component.
 * @author Vincent Zurczak - Linagora
 */
public class SwtClient implements IClientUI {

    private PetalsFacade petalsFacade;
    private ClientApplication clientApp;
    private Logger logger;


    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #open()
     */
    @Override
    public void open() {
        this.clientApp = new ClientApplication( this );
        this.clientApp.setBlockOnOpen( true );
        this.clientApp.open();
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #close()
     */
    @Override
    public void close() {
    	this.clientApp.close();
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
	public void setPetalsLogger( Logger logger ) {
		this.logger = logger;
	}

    /**
	 * @return the petalsFacade
	 */
	public PetalsFacade getPetalsFacade() {
		return this.petalsFacade;
	}

	/**
	 * @return the clientApp
	 */
	public ClientApplication getClientApp() {
		return this.clientApp;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Restarts the user interface (e.g. to take new preferences into account).
	 */
	public void restartUserInterface() {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run( IProgressMonitor monitor )
			throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask( "Restarting the user interface...", 3 );
					monitor.worked( 2 );
					Thread.sleep( 1000 );
					monitor.worked( 1 );

				} finally {
					monitor.done();
				}
			}
		};

		ProgressMonitorDialog dlg = new ProgressMonitorDialog( new Shell());
		try {
			dlg.run( false, false, runnable );
			open();

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
