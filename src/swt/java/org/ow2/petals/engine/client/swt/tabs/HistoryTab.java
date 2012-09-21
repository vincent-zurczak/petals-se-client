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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.misc.RequestMessageBeanUtils;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.swt.ClientApplication;
import org.ow2.petals.engine.client.swt.ImageIds;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.viewers.HistoryContentProvider;
import org.ow2.petals.engine.client.swt.viewers.HistoryFirstColumnLabelProvider;
import org.ow2.petals.engine.client.swt.viewers.HistorySecondColumnLabelProvider;
import org.ow2.petals.engine.client.swt.viewers.HistoryViewerFilter;

/**
 * The history tab.
 * @author Vincent Zurczak - Linagora
 */
public class HistoryTab extends Composite {

	private final static String SEARCH_TEXT = "Filter...";

	private final TreeViewer historyViewer;
	private final Font boldFont;


	/**
	 * Constructor.
	 * @param parent
	 * @param clientApp
	 */
	public HistoryTab( Composite parent, final ClientApplication clientApp ) {

		// Root elements
		super( parent, SWT.NONE );
		setLayout( new GridLayout());
		setLayoutData( new GridData( GridData.FILL_BOTH ));

		SashForm sashForm = new SashForm( this, SWT.VERTICAL );
		sashForm.setLayoutData( new GridData( GridData.FILL_BOTH ));
		sashForm.setSashWidth( 5 );


		// Create the bold font
		FontData[] fd = getFont().getFontData();
		fd = SwtUtils.getModifiedFontData( fd, SWT.BOLD );
		this.boldFont = new Font( getDisplay(), fd );


		// The header, with the search bar
		Composite container = new Composite( sashForm, SWT.NONE );
	    container.setLayout( new GridLayout());
	    container.setLayoutData( new GridData( GridData.FILL_BOTH ));

	    Composite subContainer = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).numColumns( 3 ).applyTo( subContainer );
		subContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "The Requests that were previously sent to Petals Services." );
		final Text searchText = new Text( subContainer, SWT.SINGLE | SWT.BORDER );
		searchText.setText( SEARCH_TEXT );
		GridDataFactory.swtDefaults().grab( true, false ).align( SWT.END, SWT.CENTER ).hint( 200, SWT.DEFAULT ).applyTo( searchText );

		Label l = new Label( subContainer, SWT.NONE );
		l.setImage( JFaceResources.getImage( ImageIds.SEARCH_16x16 ));
		GridDataFactory.swtDefaults().align( SWT.END, SWT.CENTER ).applyTo( l );


		// The history viewer
		this.historyViewer = new TreeViewer( container, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE );
		this.historyViewer.setContentProvider( new HistoryContentProvider());

		// Columns
		TreeViewerColumn column = new TreeViewerColumn( this.historyViewer, SWT.BEGINNING );
		column.getColumn().setText( "Interface Name" );
		column.setLabelProvider( new HistoryFirstColumnLabelProvider( 0, this.boldFont ));

		column = new TreeViewerColumn( this.historyViewer, SWT.CENTER );
		column.getColumn().setText( "Service Name" );
		column.setLabelProvider( new HistoryFirstColumnLabelProvider( 1, this.boldFont ));

		column = new TreeViewerColumn( this.historyViewer, SWT.CENTER );
		column.getColumn().setText( "End-point Name" );
		column.setLabelProvider( new HistoryFirstColumnLabelProvider( 2, this.boldFont ));

		column = new TreeViewerColumn( this.historyViewer, SWT.CENTER );
		column.getColumn().setText( "Date" );
		column.setLabelProvider( new HistorySecondColumnLabelProvider());


		// Remaining properties
		final HistoryViewerFilter historyViewerFilter = new HistoryViewerFilter();
		this.historyViewer.addFilter( historyViewerFilter );
		this.historyViewer.setInput( PreferencesManager.INSTANCE.getHistoryDirectory());
		this.historyViewer.expandAll();

		this.historyViewer.getTree().setLayoutData( new GridData( GridData.FILL_BOTH ));
		this.historyViewer.getTree().setHeaderVisible( true );
		this.historyViewer.getTree().setLinesVisible( true );

	    TableLayout layout = new TableLayout();
	    layout.addColumnData( new ColumnWeightData( 30, 75, true ));
	    layout.addColumnData( new ColumnWeightData( 30, 75, true ));
	    layout.addColumnData( new ColumnWeightData( 30, 75, true ));
	    layout.addColumnData( new ColumnWeightData( 10, 75, true ));
	    this.historyViewer.getTree().setLayout( layout );

	    Link clearHistoryLink = new Link( container, SWT.NONE );
	    clearHistoryLink.setText( "<a>Clear All the History</a>" );
	    clearHistoryLink.setLayoutData( new GridData( SWT.END, SWT.CENTER, true, false ));
	    clearHistoryLink.addMouseListener( new MouseAdapter() {
        	@Override
        	public void mouseDown( MouseEvent e ) {
        		SwtUtils.clearHistoryWithProgressBar( getShell(), -1, clientApp );
        		refreshHistory();
        	}
		});


	    // The preview part
	    container = new Composite( sashForm, SWT.NONE );
		GridLayoutFactory.swtDefaults().spacing( 15, 5 ).equalWidth( true ).numColumns( 2 ).applyTo( container );
	    container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		new Label( container, SWT.NONE ).setText( "Quick Overview of the XML Payload" );
		new Label( container, SWT.NONE ).setText( "The Request Properties" );
		final StyledText xmlPayloadStyledText = SwtUtils.createXmlViewer( container, clientApp.getColorManager(), true );


		subContainer = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).numColumns( 2 ).applyTo( subContainer );
		subContainer.setLayoutData( new GridData( GridData.FILL_BOTH ));

	    Label sepLabel = new Label( subContainer, SWT.HORIZONTAL | SWT.SEPARATOR );
	    GridDataFactory.fillDefaults().span( 2, 1 ).applyTo( sepLabel );

		new Label( subContainer, SWT.NONE ).setText( "Interface Name:" );
		final Label itfNameLabel = new Label( subContainer, SWT.NONE );
		itfNameLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "Service Name:" );
		final Label srvNameLabel = new Label( subContainer, SWT.NONE );
		srvNameLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "End-point Name:" );
		final Label edptNameLabel = new Label( subContainer, SWT.NONE );
		edptNameLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "Operation Name:" );
		final Label opNameLabel = new Label( subContainer, SWT.NONE );
		opNameLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "MEP:" );
		final Label mepLabel = new Label( subContainer, SWT.NONE );
		mepLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "Properties Count:" );
		final Label propsCountLabel = new Label( subContainer, SWT.NONE );
		propsCountLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "Attachments Count:" );
		final Label attCountLabel = new Label( subContainer, SWT.NONE );
		attCountLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

		new Label( subContainer, SWT.NONE ).setText( "Sent On:" );
		final Label dateLabel = new Label( subContainer, SWT.NONE );
		dateLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));


	    // Define the sash weights
	    sashForm.setWeights( new int[] { 50, 50 });


	    // Add the listeners
	    this.historyViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent e ) {

				Object o = ((IStructuredSelection) e.getSelection()).getFirstElement();
				RequestMessageBean req = null;
				String date = null;
				if( o instanceof File ) {
					try {
						InputStream is = new FileInputStream((File) o);
						Properties props = new Properties();
						props.load( is );

						req = RequestMessageBeanUtils.read( props );
						date = HistoryFirstColumnLabelProvider.formatDateStr(((File) o).lastModified(), true );

					} catch( Exception e1 ) {
						clientApp.log( "Error while reading a history entry.", e1, Level.INFO );
					}
				}


				if( req != null ) {
					xmlPayloadStyledText.setText( req.getXmlPayload() != null ? req.getXmlPayload() : "" );
					itfNameLabel.setText( req.getInterfaceName().getLocalPart() + " - "  + req.getInterfaceName().getNamespaceURI());
					edptNameLabel.setText( Utils.isEmptyString( req.getEndpointName()) ? "-" : req.getEndpointName());
					if( req.getServiceName() != null )
						srvNameLabel.setText( req.getServiceName().getLocalPart() + " - "  + req.getServiceName().getNamespaceURI());
					else
						srvNameLabel.setText( "-" );

					mepLabel.setText( req.getMep() != null ? req.getMep().toString() : "-" );
					dateLabel.setText( date != null ? date : "-" );
					propsCountLabel.setText( String.valueOf( req.getProperties() != null ? req.getProperties().size() : 0 ));
					attCountLabel.setText( String.valueOf( req.getAttachments() != null ? req.getAttachments().size() : 0 ));
					if( req.getOperation() != null )
						opNameLabel.setText( req.getOperation().getLocalPart() + " - " + req.getOperation().getNamespaceURI());
					else
						opNameLabel.setText( "-" );

				} else {
					xmlPayloadStyledText.setText( "" );
					itfNameLabel.setText( "" );
					srvNameLabel.setText( "" );
					edptNameLabel.setText( "" );
					opNameLabel.setText( "" );
					mepLabel.setText( "" );
					propsCountLabel.setText( "" );
					attCountLabel.setText( "" );
					dateLabel.setText( "" );
				}
			}
		});

	    searchText.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {
				searchText.selectAll();
			}
		});

		searchText.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {

				String txt = searchText.getText().trim();
				if( txt.length() == 0 ) {
					searchText.setText( SEARCH_TEXT );
				} else {
					historyViewerFilter.setSearchText( SEARCH_TEXT.equals( txt ) ? null : txt );
					HistoryTab.this.historyViewer.refresh();
				}
			}
		});

		searchText.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {

				Text text = (Text) e.widget;
				if( text.getText().equals( SEARCH_TEXT )) {
					e.doit = false;
					text.setText( String.valueOf( e.character ));
					text.setSelection( 1 );
				}
			}
		});
	}


	/**
	 * Refreshes the history viewer.
	 */
	public void refreshHistory() {
		this.historyViewer.setInput( PreferencesManager.INSTANCE.getHistoryDirectory());
		this.historyViewer.refresh();
		this.historyViewer.expandAll();
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget
	 * #dispose()
	 */
	@Override
	public void dispose() {
		if( this.boldFont != null && ! this.boldFont.isDisposed())
			this.boldFont.dispose();

		super.dispose();
	}
}
