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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.model.ResponseMessageBean;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.dialogs.KeyValueDialog;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog;
import org.ow2.petals.engine.client.swt.dialogs.ShowWsdlDialog;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegionAnalyzer;
import org.ow2.petals.engine.client.swt.viewers.FilesLabelProvider;
import org.ow2.petals.engine.client.swt.viewers.MessagePropertiesContentProvider;
import org.ow2.petals.engine.client.swt.viewers.MessagePropertiesLabelProvider;
import org.ow2.petals.engine.client.ui.PetalsFacade;
import org.w3c.dom.Document;

/**
 * The request tab and its management.
 * @author Vincent Zurczak - Linagora
 */
public class RequestTab extends Composite {

	private StyledText requestStyledText, responseStyledText;
	private final StyledText reportingStyledText;
	private TableViewer requestPropertiesViewer, responsePropertiesViewer, requestAttachmentsViewer, responseAttachmentsViewer;
	private final Text itfText, srvText, edptText;
	private final Button showWsdlButton, refreshDataButton;
	private final ComboViewer operationViewer;
	private final FilesLabelProvider filesLabelProvider;

	private final PetalsFacade petalsFacade;
	private Description targetServiceDescription;

	private final Map<String,String> requestProperties = new LinkedHashMap<String,String> ();
	private final Set<File> requestAttachments = new LinkedHashSet<File> ();



	/**
	 * Creates the request tab.
	 * @param petalsFacade
	 * @param parent
	 */
	public RequestTab( Composite parent, PetalsFacade petalsFacade ) {

		// Root elements
		super( parent, SWT.NONE );
		setLayout( new GridLayout());
	    setLayoutData( new GridData( GridData.FILL_BOTH ));

		this.filesLabelProvider = new FilesLabelProvider();
		this.petalsFacade = petalsFacade;


	    // Create the widgets for the target service
	    Group targetGroup = new Group( this, SWT.SHADOW_ETCHED_OUT );
	    targetGroup.setLayout( new GridLayout( 2, false ));
	    targetGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
	    targetGroup.setText( "Target Service" );

	    Label l = new Label( targetGroup, SWT.NONE );
	    l.setText( "The identifiers of the service that handles the requests." );
	    GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );

	    Link selectAnotherServiceLink = new Link( targetGroup, SWT.NONE );
	    selectAnotherServiceLink.setText( "<a>Select another Target Service...</a>" );
	    selectAnotherServiceLink.setToolTipText( "Select another target service" );
	    GridDataFactory.swtDefaults().span( 2, 1 ).align( SWT.END, SWT.BEGINNING ).applyTo( selectAnotherServiceLink );
	    selectAnotherServiceLink.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {

	    		try {
					List<ServiceEndpoint> serviceEndpoints = RequestTab.this.petalsFacade.getAllServiceEndpoints();
					ServiceRegistryViewerDialog dlg = new ServiceRegistryViewerDialog( getShell(), serviceEndpoints );
					if( dlg.open() == Window.OK ) {

						// Update the UI
						updateInterfaceName( dlg.getItfToInvoke());
						updateServiceName( dlg.getSrvToInvoke());
						updateEndpointName( dlg.getEdptToInvoke());

						// WSDL description
						resolveServiceEndpoint( dlg.getServiceEndpoint());
					}

				} catch( Exception e1 ) {
					e1.printStackTrace();
					// TODO: ...
				}
	    	}
	    });

	    new Label( targetGroup, SWT.NONE ).setText( "Interface Name:" );
	    this.itfText = new Text( targetGroup, SWT.BORDER | SWT.READ_ONLY );
	    this.itfText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

	    new Label( targetGroup, SWT.NONE ).setText( "Service Name:" );
	    this.srvText = new Text( targetGroup, SWT.BORDER | SWT.READ_ONLY );
	    this.srvText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

	    new Label( targetGroup, SWT.NONE ).setText( "End-point Name:" );
	    this.edptText = new Text( targetGroup, SWT.BORDER | SWT.READ_ONLY );
	    this.edptText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

	    new Label( targetGroup, SWT.NONE ).setText( "Operation Name:" );
	    Composite lineContainer = new Composite( targetGroup, SWT.NONE );
	    GridLayoutFactory.swtDefaults().numColumns( 4 ).margins( 0, 0 ).applyTo( lineContainer );
	    lineContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

	    this.operationViewer = new ComboViewer( new CCombo( lineContainer, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY ));
	    this.operationViewer.getCCombo().setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
	    this.operationViewer.setContentProvider( new ArrayContentProvider());
	    this.operationViewer.setLabelProvider( new LabelProvider() {
	    	@Override
	    	public String getText(Object elt) {
	    		return elt instanceof Operation ? ((Operation) elt).getQName().getLocalPart() : "";
	    	}
	    });

	    Button customOperationButton = new Button( lineContainer, SWT.PUSH );
	    customOperationButton.setText( "Define a Custom Operation..." );

	    this.showWsdlButton = new Button( lineContainer, SWT.PUSH );
	    this.showWsdlButton.setText( "Show WSDL" );
	    this.showWsdlButton.setEnabled( false );
	    this.showWsdlButton.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event arg0) {

	    		String text = "TODO";
	    		new ShowWsdlDialog( getShell(), text ).open();
	    	}
	    });

	    this.refreshDataButton = new Button( lineContainer, SWT.PUSH );
	    this.refreshDataButton.setEnabled( false );
	    this.refreshDataButton.setText( "Refresh Service" );
	    this.refreshDataButton.setToolTipText( "Resolves the service end-point and reloads the service WSDL" );
	    this.refreshDataButton.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent( Event e ) {
	    		resolveServiceEndpoint( null );
	    	}
	    });


	    // Create the widgets for the message parts
	    SashForm sashForm = new SashForm( this, SWT.HORIZONTAL );
	    GridDataFactory.swtDefaults().align( SWT.FILL, SWT.FILL ).grab( true, true ).indent( 0, 10 ).applyTo( sashForm );
        sashForm.setSashWidth( 20 );

        createRequestPart( sashForm );
        createResponsePart( sashForm );
        sashForm.setWeights( new int[] { 50, 50 });


        // Add a link to clear the fields
        Link clearAllFields = new Link( this, SWT.NONE );
        clearAllFields.setText( "<A>Clear All the Fields</A>" );
        clearAllFields.setLayoutData( new GridData( SWT.END, SWT.CENTER, true, false ));
        clearAllFields.addMouseListener( new MouseAdapter() {
        	@Override
        	public void mouseDown( MouseEvent e ) {
        		clearAllFields();
        	}
		});


        // Add buttons to send the request and report errors
        Group sendGroup = new Group( this, SWT.SHADOW_ETCHED_OUT );
        sendGroup.setLayout( new GridLayout( 3, false ));
        sendGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
        sendGroup.setText( "Message Sending" );

        Composite metaComposite = new Composite( sendGroup, SWT.NONE );
        GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 5, 3 ).spacing( 5, 10 ).applyTo( metaComposite );

        Button sendSyncButton = new Button( metaComposite, SWT.CHECK );
        GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( sendSyncButton );
        sendSyncButton.setText( "Send Synchroneously" );

        new Label( metaComposite, SWT.NONE ).setText( "Timeout:" );
        Spinner timeoutSpinner = new Spinner( metaComposite, SWT.BORDER );
        timeoutSpinner.setValues( 3000, 0, Integer.MAX_VALUE, 0, 1000, 100 );

        Button sendButton = new Button( sendGroup, SWT.PUSH );
        GridDataFactory.swtDefaults().grab( false, true ).align( SWT.BEGINNING, SWT.FILL ).hint( 90, SWT.DEFAULT ).applyTo( sendButton );
        sendButton.setText( "Send" );

        this.reportingStyledText = new StyledText( sendGroup, SWT.MULTI );
        GridDataFactory.fillDefaults().span( 1, 2 ).grab( true, true ).applyTo( this.reportingStyledText );


        // Add remaining listeners
        final XmlRegionAnalyzer xmlRegionAnalyzer = new XmlRegionAnalyzer();
		ModifyListener syntaxHighlightListener = new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {

				StyledText st = (StyledText) e.widget;
				List<XmlRegion> regions = xmlRegionAnalyzer.analyzeXml( st.getText());
				StyleRange[] styleRanges = SwtUtils.computeStyleRanges( regions );
				st.setStyleRanges( styleRanges );
			}
		};

		this.requestStyledText.addModifyListener( syntaxHighlightListener );
		this.responseStyledText.addModifyListener( syntaxHighlightListener );

		this.operationViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent e ) {

				Operation op = (Operation) ((IStructuredSelection) e.getSelection()).getFirstElement();
				QName itfName = (QName) RequestTab.this.itfText.getData();
				String requestPayload = null;
				try {
					requestPayload = Utils.generateXmlSkeleton( RequestTab.this.targetServiceDescription, itfName, op.getQName(), true );

				} catch( Exception e1 ) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if( requestPayload == null )
					requestPayload = "<?xml ?>";

				RequestTab.this.requestStyledText.setText( requestPayload );
				RequestTab.this.responseStyledText.setText( "" );
			}
		});
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
	}


	/**
	 * Displays a request in the tab.
	 * @param req a request (not null)
	 */
	public void displayRequest( RequestMessageBean req ) {

		// Clear all the fields
		clearAllFields();

		// Update the UI
		updateInterfaceName( req.getInterfaceName());
		updateServiceName( req.getServiceName());
		updateEndpointName( req.getEndpointName());

		this.requestStyledText.setText( req.getXmlPayload());
		if( req.getAttachments() != null ) {
			this.requestAttachments.addAll( req.getAttachments());
			this.requestAttachmentsViewer.refresh();
		}

		if( req.getProperties() != null ) {
			this.requestProperties.putAll( req.getProperties());
			this.requestPropertiesViewer.refresh();
		}

		// Resolve the end-point and get its WSDL description
		resolveServiceEndpoint( null );
	}


	/**
	 * Updates the response area.
	 * @param response
	 */
	public void updateResponse( ResponseMessageBean response ) {

		this.responseStyledText.setText( response.getXmlPayload() != null ? response.getXmlPayload() : "" );
		this.responseAttachmentsViewer.setInput( response.getAttachments() != null ? response.getAttachments() : Collections.emptySet());
		this.responseAttachmentsViewer.refresh();
		this.responsePropertiesViewer.setInput( response.getProperties() != null ? response.getProperties() : Collections.emptyMap());
		this.responsePropertiesViewer.refresh();
	}


	/**
	 * Clears all the fields.
	 */
	public void clearAllFields() {

		this.requestStyledText.setText( "" );
		this.requestAttachments.clear();
		this.requestAttachmentsViewer.refresh();
		this.requestProperties.clear();
		this.requestPropertiesViewer.refresh();

		this.responseStyledText.setText( "" );
		this.responseAttachmentsViewer.setInput( Collections.emptySet());
		this.responseAttachmentsViewer.refresh();
		this.responsePropertiesViewer.setInput( Collections.emptyMap());
		this.responsePropertiesViewer.refresh();
	}


	/**
	 * Resolves the real service end-point from a service identifier.
	 * @param se a service end-point (will be found from the UI values if null)
	 * FIXME: add a timeout or a way to stop threads...
	 */
	private void resolveServiceEndpoint( final ServiceEndpoint se ) {

		// UI thread: get what we need, just in case
		final QName itfName = (QName) this.itfText.getData();
		final QName srvName = (QName) this.srvText.getData();
		final String edptName = this.edptText.getText();
		final List<Operation> ops = new ArrayList<Operation> ();;

		// Runnable to execute in the UI thread, once communications with Petals are over
		final Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {

				// WSDL to show?
				RequestTab.this.showWsdlButton.setEnabled( RequestTab.this.targetServiceDescription != null );

				// Update the list of operations to show
				RequestTab.this.operationViewer.setInput( ops );
				RequestTab.this.operationViewer.refresh();
				RequestTab.this.operationViewer.getCCombo().setVisibleItemCount( ops.isEmpty() ? 5 : ops.size());
				if( ops.size() > 0 )
					RequestTab.this.operationViewer.setSelection( new StructuredSelection( ops.get( 0 )));
			}
		};

		// Background thread: get data from Petals
		Runnable petalsRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Resolve things
					ServiceEndpoint resolvedSe = se;
					if( resolvedSe == null )
						resolvedSe = RequestTab.this.petalsFacade.resolveServiceEndpoint( itfName, srvName, edptName );

					Document doc = null;
					if( resolvedSe != null )
						doc = RequestTab.this.petalsFacade.findWsdlDescriptionAsDocument( resolvedSe );

					// Parse the document as a WSDL
					WSDLReader wsdlReader = RequestTab.this.petalsFacade.getWsdlReader();
					RequestTab.this.targetServiceDescription = wsdlReader != null && doc != null ? wsdlReader.read( doc ) : null;

					// Get the operations
					if( RequestTab.this.targetServiceDescription != null ) {
						for( InterfaceType it : RequestTab.this.targetServiceDescription.getInterfaces())
							ops.addAll( it.getOperations());
					}

					// Update the user interface
					Display.getDefault().asyncExec( uiRunnable );

				} catch( Exception e ) {
					// TODO
					e.printStackTrace();
				}
			}
		};

		// Start communications with Petals
		new Thread( petalsRunnable ).start();
	}


	/**
	 * Creates the request part.
	 * @param parent
	 */
	private void createRequestPart( SashForm parent ) {

		SashForm sashForm = new SashForm( parent, SWT.VERTICAL );
		sashForm.setLayoutData( new GridData( GridData.FILL_BOTH ));
		sashForm.setSashWidth( 10 );

		// The XML payload
		Composite container = new Composite( sashForm, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( container );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		new Label( container, SWT.NONE ).setText( "Request - XML Payload" );
		this.requestStyledText = new StyledText( container, SWT.BORDER | SWT.MULTI );
		this.requestStyledText.setLayoutData( new GridData( GridData.FILL_BOTH ));


		// The properties
		container = new Composite( sashForm, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).applyTo( container );

		Label l = new Label( container, SWT.NONE );
		l.setText( "Message Properties" );
		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );

		this.requestPropertiesViewer = createPropertiesViewer( container );
		this.requestPropertiesViewer.setInput( this.requestProperties );

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

				KeyValueDialog dlg = new KeyValueDialog( getShell(), RequestTab.this.requestProperties );
				if( dlg.open() == Window.OK ) {
					RequestTab.this.requestProperties.put( dlg.getKey(), dlg.getValue());
					RequestTab.this.requestPropertiesViewer.refresh();
					validate();
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

				ISelection s = RequestTab.this.requestPropertiesViewer.getSelection();
				for( Iterator<?> it = ((IStructuredSelection) s).iterator(); it.hasNext(); ) {
					String key = (String) ((Map.Entry<?,?>) it.next()).getKey();
					RequestTab.this.requestProperties.remove( key );
				}

				RequestTab.this.requestPropertiesViewer.refresh();
				validate();
			}
		});


		// The attachments
		container = new Composite( sashForm, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).applyTo( container );

		l = new Label( container, SWT.NONE );
		l.setText( "File Attachments" );
		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );

		this.requestAttachmentsViewer = createAttachmentsViewer( container );
		this.requestAttachmentsViewer.setInput( this.requestAttachments );

		buttonsComposite = new Composite( container, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( buttonsComposite );
		buttonsComposite.setLayoutData( new GridData( SWT.DEFAULT, SWT.BEGINNING, false, false ));

		b = new Button( buttonsComposite, SWT.PUSH );
		b.setText( "Add..." );
		b.setToolTipText( "Add a new message property" );
		b.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ));
		b.addListener( SWT.Selection, new Listener() {

			@Override
			public void handleEvent( Event e ) {

				FileDialog dlg = new FileDialog( getShell(), SWT.MULTI );
				if( dlg.open() != null ) {
					for( String s : dlg.getFileNames())
						RequestTab.this.requestAttachments.add( new File( dlg.getFilterPath(), s ));

					RequestTab.this.requestAttachmentsViewer.refresh();
					validate();
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

				ISelection s = RequestTab.this.requestAttachmentsViewer.getSelection();
				for( Iterator<?> it = ((IStructuredSelection) s).iterator(); it.hasNext(); )
					RequestTab.this.requestAttachments.remove( it.next());

				RequestTab.this.requestAttachmentsViewer.refresh();
				validate();
			}
		});


		// Define the sash weights
		sashForm.setWeights( new int[] { 70, 15, 15 });
	}


	/**
	 * Creates a message part.
	 * @param parent
	 */
	private void createResponsePart( SashForm parent ) {

		SashForm sashForm = new SashForm( parent, SWT.VERTICAL );
		sashForm.setLayoutData( new GridData( GridData.FILL_BOTH ));
		sashForm.setSashWidth( 10 );

		// The XML payload
		Composite container = new Composite( sashForm, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( container );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		new Label( container, SWT.NONE ).setText( "Response - XML Payload" );
		this.responseStyledText = new StyledText( container, SWT.BORDER | SWT.MULTI );
		this.responseStyledText.setLayoutData( new GridData( GridData.FILL_BOTH ));


		// The properties
		container = new Composite( sashForm, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( container );

		Label l = new Label( container, SWT.NONE );
		l.setText( "Message Properties" );
		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );
		this.responsePropertiesViewer = createPropertiesViewer( container );


		// The attachments
		container = new Composite( sashForm, SWT.NONE );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( container );

		l = new Label( container, SWT.NONE );
		l.setText( "File Attachments" );
		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );
		this.responseAttachmentsViewer = createAttachmentsViewer( container );


		// Define the sash weights
		sashForm.setWeights( new int[] { 70, 15, 15 });
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


	/**
	 * Validates the user entries.
	 * @return an error message or null if everything is correct
	 */
	private String validate() {

		String msg = null;


		return msg;
	}


	/**
	 * Updates the interface name.
	 * @param itfName
	 */
	private void updateInterfaceName( QName itfName ) {
		this.itfText.setData( itfName );
		String value = itfName == null ? "" : itfName.getLocalPart() + " - " + itfName.getNamespaceURI();
		this.itfText.setText( value );
	}


	/**
	 * Updates the service name.
	 * @param srvName
	 */
	private void updateServiceName( QName srvName ) {
		this.srvText.setData( srvName );
		String value = srvName == null ? "" : srvName.getLocalPart() + " - " + srvName.getNamespaceURI();
		this.srvText.setText( value );
	}


	/**
	 * Updates the end-point name.
	 * @param edptName
	 */
	private void updateEndpointName( String edptName ) {
		this.edptText.setData( edptName );
		this.edptText.setText( edptName == null ? "" : edptName );
	}
}
