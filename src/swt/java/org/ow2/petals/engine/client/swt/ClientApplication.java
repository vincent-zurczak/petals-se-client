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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
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

	private final Image appli16, appli32, appli48;
	private final PetalsFacade petalsFacade;
	private final ColorCacheManager colorManager;

	private HistoryTab historyTab;
	private RequestTab requestTab;
	private SwtClient swtClient;



	/**
	 * Constructor.
	 * @param petalsFacade
	 */
	public ClientApplication( PetalsFacade petalsFacade ) {
		super( null );
		this.petalsFacade = petalsFacade;
		this.colorManager = new ColorCacheManager();

		this.appli16 = SwtUtils.loadImage( "/petals_16x16.png" );
		this.appli32 = SwtUtils.loadImage( "/petals_32x32.png" );
		this.appli48 = SwtUtils.loadImage( "/petals_48x48.png" );
	}


	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog
	 * #close()
	 */
	@Override
	public boolean close() {
		boolean result = super.close();

		if( this.appli16 != null && ! this.appli16.isDisposed())
			this.appli16.dispose();

		if( this.appli32 != null && ! this.appli32.isDisposed())
			this.appli32.dispose();

		if( this.appli48 != null && ! this.appli48.isDisposed())
			this.appli48.dispose();

		return result;
	}


	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents( Composite parent ) {

		// Shell properties
		getShell().setText( "Petals Client" );
		getShell().setBounds( Display.getCurrent().getBounds());
		getShell().setMaximized( true );
		getShell().setImages( new Image[] { this.appli16, this.appli32, this.appli48 });

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

		TabFolder tabFolder = new TabFolder( container, SWT.TOP );
		tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ));

		TabItem tabItem = new TabItem( tabFolder, SWT.NONE );
	    tabItem.setText( "Client" );
	    tabItem.setToolTipText( "The client area" );
	    this.requestTab = new RequestTab( tabFolder, this );
	    tabItem.setControl( this.requestTab );

	    tabItem = new TabItem( tabFolder, SWT.NONE );
	    tabItem.setText( "History" );
        tabItem.setToolTipText( "The requests history" );
        this.historyTab = new HistoryTab( tabFolder, this );
	    tabItem.setControl( this.historyTab );

	    tabItem = new TabItem( tabFolder, SWT.NONE );
	    tabItem.setText( "Preferences" );
        tabItem.setToolTipText( "The user preferences" );
	    tabItem.setControl( new PreferencesTab( tabFolder, this ));

		return container;
	}


	/**
	 * @return the petalsFacade
	 */
	public PetalsFacade getPetalsFacade() {
		return this.petalsFacade;
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
	 * Validates the request.
	 */
	public void validateRequest() {
		if( this.requestTab != null )
			this.requestTab.validate();
	}


    /**
	 * @param swtClient the swtClient to set
	 */
	public void setSwtClient( SwtClient swtClient ) {
		this.swtClient = swtClient;
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
				if( ClientApplication.this.swtClient != null )
					ClientApplication.this.swtClient.restartUserInterface();
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
