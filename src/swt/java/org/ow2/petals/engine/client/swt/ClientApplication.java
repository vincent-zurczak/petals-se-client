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

import java.util.logging.Level;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.model.ResponseMessageBean;
import org.ow2.petals.engine.client.swt.dialogs.AboutDialog;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.swt.tabs.HistoryTab;
import org.ow2.petals.engine.client.swt.tabs.PreferencesTab;
import org.ow2.petals.engine.client.swt.tabs.RequestTab;
import org.ow2.petals.engine.client.ui.PetalsFacade;

/**
 * A SWT client application to send requests to Petals services.
 * @author Vincent Zurczak - Linagora
 */
public class ClientApplication extends ApplicationWindow {

	private final ColorCacheManager colorManager;
	private final SwtClient swtClient;

	private TabFolder tabFolder;
	private HistoryTab historyTab;
	private RequestTab requestTab;



	/**
	 * Constructor.
	 * @param petalsFacade
	 */
	public ClientApplication( SwtClient swtClient ) {
		super( null );
		this.swtClient = swtClient;
		this.colorManager = new ColorCacheManager();
		ImageIds.loadImageInJFaceResources( this );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.ApplicationWindow
	 * #configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell( Shell shell ) {
		super.configureShell( shell );

		// Shell properties
		shell.setText( "Petals Client" );
		shell.setBounds( Display.getCurrent().getBounds());
		shell.setImages( new Image[] {
				JFaceResources.getImage( ImageIds.PETALS_16x16 ),
				JFaceResources.getImage( ImageIds.PETALS_32x32 ),
				JFaceResources.getImage( ImageIds.PETALS_48x48 )});

		// Add tray support
		// TODO:
	}


	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents( Composite parent ) {

		// Create the container
		Composite container = new Composite( parent, SWT.NONE );

		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.verticalSpacing = 10;
		container.setLayout( layout );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		// Create the main parts
		createMenu( container );

		this.tabFolder = new TabFolder( container, SWT.TOP );
		this.tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ));

		TabItem tabItem = new TabItem( this.tabFolder, SWT.NONE );
	    tabItem.setText( "Client" );
	    tabItem.setToolTipText( "The client area" );
	    this.requestTab = new RequestTab( this.tabFolder, this );
	    tabItem.setControl( this.requestTab );

	    tabItem = new TabItem( this.tabFolder, SWT.NONE );
	    tabItem.setText( "History" );
        tabItem.setToolTipText( "The requests history" );
        this.historyTab = new HistoryTab( this.tabFolder, this );
	    tabItem.setControl( this.historyTab );

	    tabItem = new TabItem( this.tabFolder, SWT.NONE );
	    tabItem.setText( "Preferences" );
        tabItem.setToolTipText( "The user preferences" );
	    tabItem.setControl( new PreferencesTab( this.tabFolder, this ));

		return container;
	}


	/**
	 * @return the petalsFacade
	 */
	public PetalsFacade getPetalsFacade() {
		return this.swtClient.getPetalsFacade();
	}


	/**
	 * @return the colorManager
	 */
	public ColorCacheManager getColorManager() {
		return this.colorManager;
	}


	/**
	 * Refreshes the history tab.
	 */
	public void refreshHistory() {
		if( this.historyTab != null )
			this.historyTab.refreshHistory();
	}


	/**
	 * Displays a request in the request tab.
	 * @param request
	 */
	public void displayRequest( RequestMessageBean request ) {
		if( this.requestTab != null ) {
			this.requestTab.displayEntireRequest( request );
			this.tabFolder.setSelection( 0 );
		}
	}


	/**
	 * Displays the response.
	 * @param response
	 */
	public void displayResponse( ResponseMessageBean response ) {
		if( this.requestTab != null )
			this.requestTab.displayResponse( response );
	}


	/**
	 * Validates the request.
	 */
	public void validateRequest() {
		if( this.requestTab != null )
			this.requestTab.validate();
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
	 */
	public void log( String msg, Throwable t, Level level ) {
		this.swtClient.log( msg, t, level );
	}


	/**
	 * Creates the menu.
	 * @param container
	 */
	private void createMenu( Composite container ) {

		Menu menuBar = new Menu( getShell(), SWT.BAR );
		getShell().setMenuBar( menuBar );


		// The file menu
		MenuItem fileItem = new MenuItem( menuBar, SWT.CASCADE );
		fileItem.setText ( "&File" );
		Menu submenu = new Menu( getShell(), SWT.DROP_DOWN );
		fileItem.setMenu( submenu );

		MenuItem item = new MenuItem( submenu, SWT.PUSH );
		item.setText( "Clear the Request Tab" );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				if( ClientApplication.this.requestTab != null )
					ClientApplication.this.requestTab.clearTab();
			}
		});

		new MenuItem( submenu, SWT.SEPARATOR );
		item = new MenuItem( submenu, SWT.PUSH );
		item.setText( "Restart the User Interface" );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				if( ClientApplication.this.swtClient != null ) {
					close();
					ClientApplication.this.swtClient.restartUserInterface();
				}
			}
		});


		// The help menu
		fileItem = new MenuItem( menuBar, SWT.CASCADE );
		fileItem.setText ( "&Help" );
		submenu = new Menu( getShell(), SWT.DROP_DOWN );
		fileItem.setMenu( submenu );

		item = new MenuItem( submenu, SWT.PUSH );
		item.setText( "About" );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				new AboutDialog( getShell()).open();
			}
		});
	}
}
