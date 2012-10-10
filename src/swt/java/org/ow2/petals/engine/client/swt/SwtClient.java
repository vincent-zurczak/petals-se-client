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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.ResponseMessageBean;
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

    	Runnable runnable = new Runnable() {
    		@Override
			public void run() {
    			SwtClient.this.clientApp = new ClientApplication( SwtClient.this );
    			SwtClient.this.clientApp.setBlockOnOpen( true );
    			SwtClient.this.clientApp.open();
    		}
    	};

    	new Thread( runnable ).start();
    }


    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IClientUI
     * #close()
     */
    @Override
    public void close() {

    	Display.getDefault().asyncExec( new Runnable() {
			@Override
			public void run() {
				SwtClient.this.clientApp.close();
			}
		});
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


	/*
	 * (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IClientUI
	 * #reportCommunicationProblem(java.lang.Exception)
	 */
	@Override
	public void reportCommunicationProblem( Exception e ) {

		Display.getDefault().asyncExec( new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(
						new Shell(), "Error",
						"An error occurred while interacting with Petals. Check the logs for more details." );
			}
		});
	}


	/*
	 * (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IClientUI
	 * #displayResponse(org.ow2.petals.engine.client.model.ResponseMessageBean)
	 */
	@Override
	public void displayResponse( final ResponseMessageBean response ) {

		if( this.clientApp == null )
			return;

		Display.getDefault().asyncExec( new Runnable() {
			@Override
			public void run() {
				SwtClient.this.clientApp.displayResponse( response );
			}
		});
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

		} catch( InvocationTargetException e ) {
			log( "Failed to restart the UI.", e, Level.WARNING );

		} catch( InterruptedException e ) {
			// nothing
		}
	}


	/**
	 * Logs an information.
	 * @param msg a message (can be null)
	 * @param t a throwable (can be null)
	 * <p>
	 * The stack trace is logged with the FINEST level.
	 * </p>
	 *
	 * @param level the log level for the message
	 * @see Utils#log(String, Throwable, Level, Logger)
	 */
	public void log( String msg, Throwable t, Level level ) {
		Utils.log( msg, t, level, this.logger );
	}


	/*
	 * (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IClientUI
	 * #canRun()
	 */
	@Override
	public boolean canRun() {
		// For more recent versions of SWT.
		// return SWT.isLoadable():
		return true;
	}
}
