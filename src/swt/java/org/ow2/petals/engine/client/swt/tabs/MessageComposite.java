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

package org.ow2.petals.engine.client.swt.tabs;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.ow2.petals.engine.client.model.BasicMessageBean;
import org.ow2.petals.engine.client.swt.ClientApplication;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.dialogs.KeyValueDialog;
import org.ow2.petals.engine.client.swt.viewers.FilesLabelProvider;
import org.ow2.petals.engine.client.swt.viewers.MessagePropertiesContentProvider;
import org.ow2.petals.engine.client.swt.viewers.MessagePropertiesLabelProvider;

/**
 * A widget to display elements of message.
 * @author Vincent Zurczak - Linagora
 */
public class MessageComposite extends SashForm {

	private final FilesLabelProvider filesLabelProvider;
	private final Image viewMenuImg;
	private final ClientApplication clientApp;

	private Menu menu;
	private TableViewer propertiesViewer, attachmentsViewer;
	private StyledText styledText;

	private final Map<String,String> properties = new LinkedHashMap<String,String> ();
	private final Set<File> attachments = new LinkedHashSet<File> ();
	private final String title;


	/**
	 * Constructor.
	 * @param title
	 * @param parent
	 * @param clientApp
	 */
	public MessageComposite( String title, Composite parent, ClientApplication clientApp ) {
		super( parent, SWT.VERTICAL );
		setLayoutData( new GridData( GridData.FILL_BOTH ));
		setSashWidth( 10 );

		this.viewMenuImg = SwtUtils.loadImage( "/view_menu_16x16.gif" );
		this.filesLabelProvider = new FilesLabelProvider();
		this.clientApp = clientApp;
		this.title = title;

		createPayloadSection();
		createPropertiesSection();
		createAttachmentsSection();

		setWeights( new int[] { 70, 15, 15 });
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget
	 * #dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		this.filesLabelProvider.dispose();
		if( this.viewMenuImg != null && ! this.viewMenuImg.isDisposed())
			this.viewMenuImg.dispose();
	}


	/**
	 * @return the menu
	 */
	@Override
	public Menu getMenu() {
		return this.menu;
	}


	/**
	 * @return the styledText
	 */
	public StyledText getStyledText() {
		return this.styledText;
	}


	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}


	/**
	 * @return the attachments
	 */
	public Set<File> getAttachments() {
		return this.attachments;
	}


	/**
	 * @return the pay-load
	 */
	public String getPayload() {
		return this.styledText.getText();
	}


	/**
	 * @param payload the pay-load (can be null)
	 */
	public void setPayload( String payload ) {
		this.styledText.setText( payload == null ? "" : payload );
	}


	/**
	 * Updates the data and widgets managed by this class.
	 * @param bmb a basic message bean (null to clear all the fields)
	 */
	public void setInput( BasicMessageBean bmb ) {

		this.attachments.clear();
		this.properties.clear();
		if( bmb == null ) {
			this.styledText.setText( "" );

		} else {
			this.styledText.setText( bmb.getXmlPayload() != null ? bmb.getXmlPayload() : "" );
			if( bmb.getAttachments() != null )
				this.attachments.addAll( bmb.getAttachments());
			if( bmb.getProperties() != null )
				this.properties.putAll( bmb.getProperties());
		}

		this.attachmentsViewer.refresh();
		this.propertiesViewer.refresh();
	}


	/**
	 * Creates the section for the pay-load.
	 */
	private void createPayloadSection() {

		// Container
		Composite container = new Composite( this, SWT.NONE );
		GridLayoutFactory.swtDefaults().spacing( 5, 0 ).margins( 0, 0 ).applyTo( container );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		Composite subContainer = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).extendedMargins( 0, 5, 0, 0 ).applyTo( subContainer );
		subContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		// Menu + Label
		final ToolBar toolBar = new ToolBar( subContainer, SWT.FLAT );
		new Label( subContainer, SWT.NONE ).setText( this.title + " - XML Payload" );

		// XML Viewer
		this.styledText = SwtUtils.createXmlViewer( container, this.clientApp.getColorManager(), false );

		// Link the menu and the tool-bar
		this.menu = new Menu( getShell(), SWT.POP_UP);
		final ToolItem item = new ToolItem( toolBar, SWT.FLAT );
		item.setImage( this.viewMenuImg );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				Rectangle rect = item.getBounds();
				Point pt = new Point( rect.x, rect.y + rect.height );
				pt = toolBar.toDisplay( pt );
				MessageComposite.this.menu.setLocation( pt.x, pt.y );
				MessageComposite.this.menu.setVisible( true );
			}
		});
	}


	/**
	 * Creates the section for the message properties.
	 */
	private void createPropertiesSection() {

		// The container
		Composite container = new Composite( this, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).applyTo( container );

		Composite subContainer = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).extendedMargins( 0, 5, 0, 0 ).applyTo( subContainer );
		GridDataFactory.swtDefaults().span( 2, 1 ).grab( true, false ).align( SWT.FILL, SWT.CENTER ).applyTo( subContainer );

		// Menu + Label
		final ToolBar toolBar = new ToolBar( subContainer, SWT.FLAT );
		new Label( subContainer, SWT.NONE ).setText( "Message Properties" );

		// The properties
		this.propertiesViewer = createPropertiesViewer( container );
		this.propertiesViewer.setInput( this.properties );

		// The buttons
		Composite buttonsComposite = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( buttonsComposite );
		buttonsComposite.setLayoutData( new GridData( SWT.DEFAULT, SWT.BEGINNING, false, false ));

		Button b = new Button( buttonsComposite, SWT.PUSH );
		b.setText( "Add..." );
		b.setToolTipText( "Add a new message property" );
		b.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ));
		b.addListener( SWT.Selection, new Listener() {

			@Override
			public void handleEvent( Event e ) {

				KeyValueDialog dlg = new KeyValueDialog( getShell(),MessageComposite.this.properties );
				if( dlg.open() == Window.OK ) {
					MessageComposite.this.properties.put( dlg.getKey(), dlg.getValue());
					MessageComposite.this.propertiesViewer.refresh();
				}
			}
		});

		b = new Button( buttonsComposite, SWT.PUSH );
		b.setText( "Remove" );
		b.setToolTipText( "Remove the selected message property" );
		b.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ));
		b.addListener( SWT.Selection, new Listener() {

			@Override
			public void handleEvent( Event e ) {

				ISelection s = MessageComposite.this.propertiesViewer.getSelection();
				for( Iterator<?> it = ((IStructuredSelection) s).iterator(); it.hasNext(); ) {
					String key = (String) ((Map.Entry<?,?>) it.next()).getKey();
					MessageComposite.this.properties.remove( key );
				}

				MessageComposite.this.propertiesViewer.refresh();
			}
		});

		// Link the menu and the tool-bar
		final Menu localMenu = new Menu( getShell(), SWT.POP_UP);
		final ToolItem item = new ToolItem( toolBar, SWT.FLAT );
		item.setImage( this.viewMenuImg );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				Rectangle rect = item.getBounds();
				Point pt = new Point( rect.x, rect.y + rect.height );
				pt = toolBar.toDisplay( pt );
				localMenu.setLocation( pt.x, pt.y );
				localMenu.setVisible( true );
			}
		});

		MenuItem menuItem = new MenuItem ( localMenu, SWT.PUSH );
		menuItem.setText( "Clear All the Properties" );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				MessageComposite.this.properties.clear();
				MessageComposite.this.propertiesViewer.refresh();
			}
		});
	}


	/**
	 * Creates the section for the message attachments.
	 */
	private void createAttachmentsSection() {

		// The container
		Composite container = new Composite( this, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).applyTo( container );

		Composite subContainer = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).extendedMargins( 0, 5, 0, 0 ).applyTo( subContainer );
		GridDataFactory.swtDefaults().span( 2, 1 ).grab( true, false ).align( SWT.FILL, SWT.CENTER ).applyTo( subContainer );

		// Menu + Label
		final ToolBar toolBar = new ToolBar( subContainer, SWT.FLAT );
		new Label( subContainer, SWT.NONE ).setText( "File Attachments" );

		// The attachments
		this.attachmentsViewer = createAttachmentsViewer( container );
		this.attachmentsViewer.setInput( this.attachments );

		// The buttons
		Composite buttonsComposite = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( buttonsComposite );
		buttonsComposite.setLayoutData( new GridData( SWT.DEFAULT, SWT.BEGINNING, false, false ));

		Button b = new Button( buttonsComposite, SWT.PUSH );
		b.setText( "Add..." );
		b.setToolTipText( "Add a new message property" );
		b.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ));
		b.addListener( SWT.Selection, new Listener() {

			@Override
			public void handleEvent( Event e ) {

				FileDialog dlg = new FileDialog( getShell(), SWT.MULTI );
				if( dlg.open() != null ) {
					for( String s : dlg.getFileNames())
						MessageComposite.this.attachments.add( new File( dlg.getFilterPath(), s ));

					MessageComposite.this.attachmentsViewer.refresh();
					MessageComposite.this.clientApp.validateRequest();
				}
			}
		});

		b = new Button( buttonsComposite, SWT.PUSH );
		b.setText( "Remove" );
		b.setToolTipText( "Remove the selected message property" );
		b.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ));
		b.addListener( SWT.Selection, new Listener() {

			@Override
			public void handleEvent( Event e ) {

				ISelection s = MessageComposite.this.attachmentsViewer.getSelection();
				for( Iterator<?> it = ((IStructuredSelection) s).iterator(); it.hasNext(); )
					MessageComposite.this.attachments.remove( it.next());

				MessageComposite.this.attachmentsViewer.refresh();
				MessageComposite.this.clientApp.validateRequest();
			}
		});

		// Link the menu and the tool-bar
		final Menu localMenu = new Menu( getShell(), SWT.POP_UP);
		final ToolItem item = new ToolItem( toolBar, SWT.FLAT );
		item.setImage( this.viewMenuImg );
		item.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				Rectangle rect = item.getBounds();
				Point pt = new Point( rect.x, rect.y + rect.height );
				pt = toolBar.toDisplay( pt );
				localMenu.setLocation( pt.x, pt.y );
				localMenu.setVisible( true );
			}
		});

		MenuItem menuItem = new MenuItem ( localMenu, SWT.PUSH );
		menuItem.setText( "Remove All the Attachments" );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				MessageComposite.this.attachments.clear();
				MessageComposite.this.attachmentsViewer.refresh();
			}
		});
	}


	/**
	 * Creates a viewer for message properties.
	 * @param parent the parent
	 * @return a table viewer with the right columns
	 */
	private TableViewer createPropertiesViewer( Composite parent ) {

		TableViewer viewer = new TableViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );
		viewer.setContentProvider( new MessagePropertiesContentProvider());
		viewer.setLabelProvider( new MessagePropertiesLabelProvider());
	    viewer.getTable().setLayoutData( new GridData( GridData.FILL_BOTH ));
	    viewer.getTable().setHeaderVisible( true );
	    viewer.getTable().setLinesVisible( true );

	    new TableColumn( viewer.getTable(), SWT.NONE ).setText( "Key" );
	    new TableColumn( viewer.getTable(), SWT.NONE ).setText( "Value" );

	    TableLayout layout = new TableLayout();
	    layout.addColumnData( new ColumnWeightData( 50, 75, true ));
	    layout.addColumnData( new ColumnWeightData( 50, 75, true ));
	    viewer.getTable().setLayout( layout );

	    return viewer;
	}


	/**
	 * Creates a viewer for message attachments.
	 * @param parent the parent
	 * @return a table viewer with the right columns
	 */
	private TableViewer createAttachmentsViewer( Composite parent ) {

		TableViewer viewer = new TableViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );
		viewer.setContentProvider( ArrayContentProvider.getInstance());
		viewer.setLabelProvider( this.filesLabelProvider );

	    TableLayout layout = new TableLayout();
	    layout.addColumnData( new ColumnWeightData( 50, 75, true ));
	    layout.addColumnData( new ColumnWeightData( 50, 75, true ));
	    viewer.getTable().setLayout( layout );
	    viewer.getTable().setLayoutData( new GridData( GridData.FILL_BOTH ));

	    return viewer;
	}
}
