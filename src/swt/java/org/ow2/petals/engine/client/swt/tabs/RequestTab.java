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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.Mep;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog;
import org.ow2.petals.engine.client.swt.dialogs.ShowWsdlDialog;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.ui.PetalsFacade;
import org.w3c.dom.Document;

/**
 * The request tab and its management.
 * @author Vincent Zurczak - Linagora
 */
public class RequestTab extends Composite {

	private static final String DEFAULT_SKELETON = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	private static final String COMMENT_NO_WSDL = "<!-- No skeleton could be generated (no WSDL) -->";
	private static final String COMMENT_INVALID_WSDL = "<!-- No skeleton could be generated (invalid WSDL) -->";

	private final Image sendImg;
	private MessageComposite requestComposite, responseComposite;
	private StyledText reportingStyledText;
	private ProgressBar reportingProgressBar;
	private Spinner timeoutSpinner;
	private Text itfText, srvText, edptText;
	private Button showWsdlButton, refreshDataButton;
	private Button sendButton;
	private ComboViewer operationViewer;

	private final PetalsFacade petalsFacade;
	private Description wsdlDescription;
	private String wsdlDescriptionAsString;
	private QName operationName;
	private Mep mep;



	/**
	 * Creates the request tab.
	 * @param parent
	 * @param petalsFacade
	 * @param colorManager
	 */
	public RequestTab( Composite parent, PetalsFacade petalsFacade, ColorCacheManager colorManager ) {

		super( parent, SWT.NONE );
		setLayout( new GridLayout());
	    setLayoutData( new GridData( GridData.FILL_BOTH ));

		this.petalsFacade = petalsFacade;
		this.sendImg = SwtUtils.loadImage( "/send_64x64.png" );

		createTargetServiceSection( colorManager );
		createMessageSection( colorManager );
		createToolSection();
    }


	/**
	 * Creates the section for the target service.
	 * @param colorManager
	 */
	private void createTargetServiceSection( final ColorCacheManager colorManager ) {

		Group targetGroup = new Group( this, SWT.SHADOW_ETCHED_OUT );
	    targetGroup.setLayout( new GridLayout( 2, false ));
	    targetGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
	    targetGroup.setText( "Target Service" );

	    Link selectAnotherServiceLink = new Link( targetGroup, SWT.NONE );
	    selectAnotherServiceLink.setText( "<a>Select another Target Service...</a>" );
	    selectAnotherServiceLink.setToolTipText( "Select another target service" );
	    GridDataFactory.swtDefaults().span( 2, 1 ).align( SWT.END, SWT.BEGINNING ).applyTo( selectAnotherServiceLink );
	    selectAnotherServiceLink.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent( Event e ) {
	    		retrieveAllServiceEndpoints();
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
	    	public String getText( Object elt ) {
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
	    	public void handleEvent( Event e ) {
	    		ShowWsdlDialog.openShowWsdlDialog( getShell(), RequestTab.this.wsdlDescriptionAsString, colorManager );
	    	}
	    });

	    this.refreshDataButton = new Button( lineContainer, SWT.PUSH );
	    this.refreshDataButton.setEnabled( false );
	    this.refreshDataButton.setText( "Refresh Service" );
	    this.refreshDataButton.setToolTipText( "Resolves the service end-point and reloads the service WSDL" );
	    this.refreshDataButton.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent( Event e ) {
	    		resolveServiceEndpoint();
	    	}
	    });
	}


	/**
	 * Creates the section with the message parts.
	 * @param colorManager
	 */
	private void createMessageSection( ColorCacheManager colorManager ) {

		// The basics...
		SashForm sashForm = new SashForm( this, SWT.HORIZONTAL );
	    GridDataFactory.swtDefaults().align( SWT.FILL, SWT.FILL ).grab( true, true ).indent( 0, 10 ).applyTo( sashForm );
        sashForm.setSashWidth( 20 );

        this.requestComposite = new MessageComposite( "Request", sashForm, colorManager );
        this.responseComposite = new MessageComposite( "Response", sashForm, colorManager );
        sashForm.setWeights( new int[] { 50, 50 });

        // Add menus items for the request part
        MenuItem menuItem = new MenuItem ( this.requestComposite.getMenu(), SWT.PUSH );
		menuItem.setText( "New Request" );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				// Clear the request
				RequestTab.this.requestComposite.setInput( null );
				RequestTab.this.responseComposite.setInput( null );

				// Display a new request
				RequestTab.this.requestComposite.setPayload( generateSkeleton());
			}
		});

		new MenuItem( this.requestComposite.getMenu(), SWT.SEPARATOR );
		menuItem = new MenuItem ( this.requestComposite.getMenu(), SWT.PUSH );
		menuItem.setText( "Save As..." );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				FileDialog dlg = new FileDialog( getShell(), SWT.SAVE | SWT.SINGLE );
				dlg.setFilterNames( new String[] { "Text File (*.txt)" });
				dlg.setFilterExtensions( new String[] { "*.txt" });

				String target = dlg.open();
				if( target != null ) {
					if( ! target.toLowerCase().endsWith( ".txt" ))
						target += ".txt";

					RequestMessageBean req = createRequestInstance();
					try {
						req = Utils.saveRequest( new File( target ), req, false );
						displayEntireRequest( req );

					} catch( IOException e1 ) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		menuItem = new MenuItem ( this.requestComposite.getMenu(), SWT.PUSH );
		menuItem.setText( "Save With Attachments..." );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				DirectoryDialog dlg = new DirectoryDialog( getShell());
				String target = dlg.open();
				if( target != null ) {
					RequestMessageBean req = createRequestInstance();
					try {
						req = Utils.saveRequest( new File( target ), req, true );
						displayEntireRequest( req );

					} catch( IOException e1 ) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		// Add menu items for the response part
		menuItem = new MenuItem ( this.responseComposite.getMenu(), SWT.PUSH );
		menuItem.setText( "Use as Request" );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				RequestMessageBean req = new RequestMessageBean();
				req.setProperties( RequestTab.this.responseComposite.getProperties());
				req.setAttachments( RequestTab.this.responseComposite.getAttachments());
				req.setXmlPayload( RequestTab.this.responseComposite.getPayload());

				RequestTab.this.requestComposite.setInput( req );
				RequestTab.this.responseComposite.setInput( null );
			}
		});
	}


	/**
	 * Creates the section for the tools and reporting.
	 */
	private void createToolSection() {

		Group sendGroup = new Group( this, SWT.SHADOW_ETCHED_OUT );
        GridLayoutFactory.swtDefaults().numColumns( 3 ).applyTo( sendGroup );
        GridDataFactory.swtDefaults().grab( true, false ).align( SWT.FILL, SWT.END ).indent( 0, 8 ).applyTo( sendGroup );
        sendGroup.setText( "Message Sending" );

        Composite metaComposite = new Composite( sendGroup, SWT.NONE );
        GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 5, 3 ).spacing( 5, 10 ).applyTo( metaComposite );

        Button syncButton = new Button( metaComposite, SWT.CHECK );
        GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( syncButton );
        syncButton.setText( "Send Synchroneously" );

        new Label( metaComposite, SWT.NONE ).setText( "Timeout:" );
        this.timeoutSpinner = new Spinner( metaComposite, SWT.BORDER );
        int defaultTimeout = PreferencesManager.INSTANCE.getDefaultTimeout();
        this.timeoutSpinner.setValues( defaultTimeout, 0, Integer.MAX_VALUE, 0, 1000, 100 );

        this.sendButton = new Button( sendGroup, SWT.PUSH );
        GridDataFactory.swtDefaults().grab( false, true ).align( SWT.BEGINNING, SWT.FILL ).applyTo( this.sendButton );
        this.sendButton.setText( "Send  " );
        this.sendButton.setToolTipText( "Send the Request to the Target Service" );
        this.sendButton.setImage( this.sendImg );


        // ... and to display reports
        Composite reportingComposite = new Composite( sendGroup, SWT.NONE );
        GridLayoutFactory.swtDefaults().margins( 25, 8 ).applyTo( reportingComposite );
        GridDataFactory.fillDefaults().span( 1, 2 ).grab( true, true ).align( SWT.FILL, SWT.END ).applyTo( reportingComposite );

        this.reportingStyledText = new StyledText( reportingComposite, SWT.MULTI );
        this.reportingStyledText.setLayoutData( new GridData( GridData.FILL_BOTH ));

        this.reportingProgressBar = new ProgressBar( reportingComposite, SWT.INDETERMINATE );
        this.reportingProgressBar.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
        this.reportingProgressBar.setVisible( false );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget
	 * #dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if( this.sendImg != null && ! this.sendImg.isDisposed())
			this.sendImg.dispose();
	}


	/**
	 * Displays a request in the tab.
	 * @param req a request (not null)
	 */
	public void displayEntireRequest( RequestMessageBean req ) {

		this.operationName = req.getOperation();
		this.mep = req.getMep();

		updateInterfaceName( req.getInterfaceName());
		updateServiceName( req.getServiceName());
		updateEndpointName( req.getEndpointName());

		this.requestComposite.setInput( req );
		this.responseComposite.setInput( null );
		resolveServiceEndpoint();
	}



	/**
	 * Updates the progress report part.
	 * <p>
	 * If the description is not null, this disables the "send" button, shows the action description and activates a progress bar.
	 * Otherwise, the "send" button is enabled, and no report is shown anymore.
	 * </p>
	 *
	 * @param description the action's description or null to stop reporting information
	 */
	private void updateProgressReport( String description ) {

		boolean shown = description != null;
		this.sendButton.setEnabled( shown );
		this.reportingProgressBar.setVisible( shown );

		this.reportingStyledText.setStyleRanges( new StyleRange[ 0 ]);
		this.reportingStyledText.setText( description != null ? description : "" );
		this.reportingStyledText.getParent().layout();
	}


	/**
	 * Resolves the real service end-point from a service identifier.
	 */
	private void resolveServiceEndpoint() {

		// UI thread: get what we need, just in case
		final QName itfName = (QName) this.itfText.getData();
		final QName srvName = (QName) this.srvText.getData();
		final String edptName = this.edptText.getText();
		final List<Operation> ops = new ArrayList<Operation> ();;

		// Runnable to execute in the UI thread, once communications with Petals are over
		final Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {

				// Ready to be sent?
				validate();

				// Update the list of operations to show
				RequestTab.this.operationViewer.setInput( ops );
				RequestTab.this.operationViewer.refresh();
				RequestTab.this.operationViewer.getCCombo().setVisibleItemCount( ops.isEmpty() ? 5 : ops.size());
				if( ops.size() > 0 )
					RequestTab.this.operationViewer.setSelection( new StructuredSelection( ops.get( 0 )));

				updateProgressReport( null );
			}
		};

		// Background thread: get data from Petals
		Runnable petalsRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Resolve things
					ServiceEndpoint resolvedSe = null;
					if( resolvedSe == null )
						resolvedSe = RequestTab.this.petalsFacade.resolveServiceEndpoint( itfName, srvName, edptName );

					Document doc = null;
					if( resolvedSe != null )
						doc = RequestTab.this.petalsFacade.findWsdlDescriptionAsDocument( resolvedSe );

					// Store the WSDL as a string, as we may need to display it later
					RequestTab.this.wsdlDescriptionAsString = doc == null ? null : Utils.writeDocument( doc, false );

					// Parse the document as a WSDL
					WSDLReader wsdlReader = RequestTab.this.petalsFacade.getWsdlReader();
					RequestTab.this.wsdlDescription = wsdlReader != null && doc != null ? wsdlReader.read( doc ) : null;

					// Get the operations
					if( RequestTab.this.wsdlDescription != null ) {
						for( InterfaceType it : RequestTab.this.wsdlDescription.getInterfaces())
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
		updateProgressReport( "Retrieving the WSDL description..." );
		new Thread( petalsRunnable ).start();
	}


	/**
	 * Retrieves all the service end-points from the ESB.
	 */
	private List<ServiceEndpoint> retrieveAllServiceEndpoints() {

		// UI thread: get what we need, just in case
		final List<ServiceEndpoint> ses = new ArrayList<ServiceEndpoint> ();

		// Runnable to execute in the UI thread, once communications with Petals are over
		final Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				updateProgressReport( null );
				ServiceRegistryViewerDialog dlg = new ServiceRegistryViewerDialog( getShell(), ses );
				if( dlg.open() == Window.OK ) {

					// Update the UI
					updateInterfaceName( dlg.getItfToInvoke());
					updateServiceName( dlg.getSrvToInvoke());
					updateEndpointName( dlg.getEdptToInvoke());

					// WSDL description to resolve
					resolveServiceEndpoint();
				}
			}
		};

		// Background thread: get data from Petals
		Runnable petalsRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					List<ServiceEndpoint> serviceEndpoints = RequestTab.this.petalsFacade.getAllServiceEndpoints();
					if( serviceEndpoints != null )
						ses.addAll( serviceEndpoints );

					// Update the user interface
					Display.getDefault().asyncExec( uiRunnable );

				} catch( Exception e ) {
					// TODO
					e.printStackTrace();
				}
			}
		};

		// Start communications with Petals
		updateProgressReport( "Retrieving all the service end-points..." );
		new Thread( petalsRunnable ).start();

		return ses;
	}


	/**
	 * Validates the user entries.
	 */
	private void validate() {

		String msg = null;


		RequestTab.this.showWsdlButton.setEnabled( msg == null );
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


	/**
	 * @return a non-null instance of {@link RequestMessageBean}
	 */
	private RequestMessageBean createRequestInstance() {

		RequestMessageBean result = new RequestMessageBean();
		result.setInterfaceName((QName) this.itfText.getData());
		result.setServiceName((QName) this.srvText.getData());
		result.setEndpointName( this.edptText.getText());

		result.setAttachments( this.requestComposite.getAttachments());
		result.setProperties( this.requestComposite.getProperties());
		result.setXmlPayload( this.requestComposite.getPayload());

		result.setTimeout( this.timeoutSpinner.getSelection());
		result.setOperation( this.operationName );
		result.setMep( this.mep );

		return result;
	}


	/**
	 * @return a non-null string representing a new XML pay-load
	 */
	private String generateSkeleton() {

		String requestPayload;
		ISelection selection = RequestTab.this.operationViewer.getSelection();
		if( ! selection.isEmpty()) {
			Operation op = (Operation) ((IStructuredSelection) selection).getFirstElement();
			QName itfName = (QName) RequestTab.this.itfText.getData();
			try {
				requestPayload = Utils.generateXmlSkeleton(
						RequestTab.this.wsdlDescription,
						itfName, op.getQName(), true );

			} catch( Exception e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				requestPayload = DEFAULT_SKELETON + COMMENT_INVALID_WSDL;
			}

		} else {
			requestPayload = DEFAULT_SKELETON + COMMENT_NO_WSDL;
		}

		return requestPayload;
	}
}
