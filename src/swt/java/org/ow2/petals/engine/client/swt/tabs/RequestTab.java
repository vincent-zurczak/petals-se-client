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
import java.util.Set;
import java.util.logging.Level;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.OperationBean;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.model.ResponseMessageBean;
import org.ow2.petals.engine.client.swt.ClientApplication;
import org.ow2.petals.engine.client.swt.ImageIds;
import org.ow2.petals.engine.client.swt.dialogs.NewWsdlOperationDialog;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog;
import org.ow2.petals.engine.client.swt.dialogs.ShowWsdlDialog;
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
	private static final String CDATA_SECTION = "<![CDATA[<!-- Insert your mark-ups here -->]]>";

	private final ClientApplication clientApp;

	private MessageComposite requestComposite, responseComposite;
	private StyledText reportingStyledText;
	private Label reportingImageLabel;
	private ProgressBar reportingProgressBar;
	private Spinner timeoutSpinner;
	private Text itfText, srvText, edptText;
	private Button showWsdlButton, sendButton;
	private ComboViewer operationViewer;

	private Description wsdlDescription;
	private String wsdlDescriptionAsString;
	private OperationBean operationBean;



	/**
	 * Creates the request tab.
	 * @param parent
	 * @param clientApp
	 */
	public RequestTab( Composite parent, ClientApplication clientApp ) {
		super( parent, SWT.NONE );
		this.clientApp = clientApp;

		setLayout( new GridLayout());
	    setLayoutData( new GridData( GridData.FILL_BOTH ));

		createTargetServiceSection();
		createMessageSection();
		createToolSection();
    }


	/**
	 * Validates the user entries.
	 */
	public void validate() {

		String msg = null;
		if( this.itfText.getData() == null )
			msg = "No target service was specified.";
		else if( this.operationBean == null )
			msg = "You must specify the name of the operation to invoke.";
		else if( Utils.isEmptyString( this.requestComposite.getPayload()))
			msg = "You must define a XML payload in the request message.";
		else {
			Set<File> attachments = this.requestComposite.getAttachments();
			if( attachments != null ) {
				for( File f : attachments ) {
					if( ! f.exists()) {
						msg = "The attached file '" + f.getName() + "' does not exist.";
						break;
					}
				}
			}

			if( msg == null ) {
				try {
					Utils.buildDocument( this.requestComposite.getPayload());
				} catch( Exception e ) {
					msg = "The request's payload is not a valid XML document.";
					this.clientApp.log( msg, e, Level.INFO );
				}
			}
		}

		updateMessage( msg, IStatus.ERROR );
		RequestTab.this.sendButton.setEnabled( msg == null );
	}


	/**
	 * Clears the tab.
	 */
	public void clearTab( ) {
		this.requestComposite.setInput( null );
		this.responseComposite.setInput( null );
		updateMessage( null, IStatus.OK );
	}


	/**
	 * Displays the response in the user interface.
	 * @param response
	 */
	public void displayResponse( ResponseMessageBean response ) {
		this.responseComposite.setInput( response );
	}


	/**
	 * Creates the section for the target service.
	 */
	private void createTargetServiceSection() {

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
	    GridLayoutFactory.swtDefaults().numColumns( 3 ).margins( 0, 0 ).applyTo( lineContainer );
	    lineContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

	    this.operationViewer = new ComboViewer( new CCombo( lineContainer, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY ));
	    this.operationViewer.getCCombo().setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
	    this.operationViewer.setContentProvider( new ArrayContentProvider());
	    this.operationViewer.setLabelProvider( new LabelProvider());

	    this.operationViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent e ) {

				// Generate a new request? Yes, if empty.
				boolean validate = true;
				if( Utils.isEmptyString( RequestTab.this.requestComposite.getPayload()))
					RequestTab.this.requestComposite.setPayload( generateSkeleton());
				else {
					validate = false;
					updateMessage( "The request may not match the selected service operation.", IStatus.WARNING );
				}

				// Get data
				ISelection selection = RequestTab.this.operationViewer.getSelection();
				if( selection.isEmpty()) {
					RequestTab.this.operationBean = null;
				} else {
					RequestTab.this.operationBean = (OperationBean) ((IStructuredSelection) selection).getFirstElement();
				}

				// Validate?
				if( validate )
					validate();
			}
		});

	    Button customOperationButton = new Button( lineContainer, SWT.PUSH );
	    customOperationButton.setText( "Define a Custom Operation..." );
	    customOperationButton.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent( Event e ) {
	    		NewWsdlOperationDialog dlg = new NewWsdlOperationDialog( getShell(), RequestTab.this.operationBean );
	    		if( dlg.open() == Window.OK )
	    			updateOperationName( dlg.getOperationBean());
	    	}
	    });

	    this.showWsdlButton = new Button( lineContainer, SWT.PUSH );
	    this.showWsdlButton.setText( "Show WSDL" );
	    this.showWsdlButton.setEnabled( false );
	    this.showWsdlButton.addListener( SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent( Event e ) {
	    		ShowWsdlDialog.openShowWsdlDialog( getShell(), RequestTab.this.wsdlDescriptionAsString, RequestTab.this.clientApp );
	    	}
	    });
	}


	/**
	 * Creates the section with the message parts.
	 */
	private void createMessageSection() {

		// The basics...
		SashForm sashForm = new SashForm( this, SWT.HORIZONTAL );
	    GridDataFactory.swtDefaults().align( SWT.FILL, SWT.FILL ).grab( true, true ).indent( 0, 10 ).applyTo( sashForm );
        sashForm.setSashWidth( 20 );

        this.requestComposite = new MessageComposite( "Request", sashForm, this.clientApp );
        this.responseComposite = new MessageComposite( "Response", sashForm, this.clientApp );
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
						MessageDialog.openInformation( getShell(), "Error", "An error occurred while saving the request." );
						RequestTab.this.clientApp.log( "Error while saving a request.", e1, Level.INFO );
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
						MessageDialog.openInformation( getShell(), "Error", "An error occurred while saving the request." );
						RequestTab.this.clientApp.log( "Error while saving a request.", e1, Level.INFO );
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

		// Add a context menu on the request's styled text
		final Menu contextMenu = new Menu( this.requestComposite.getStyledText());
		menuItem = new MenuItem ( contextMenu, SWT.PUSH );
		menuItem.setText( "Insert a CDATA section" );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				int caret = RequestTab.this.requestComposite.getStyledText().getCaretOffset();
				RequestTab.this.requestComposite.getStyledText().insert( CDATA_SECTION );

				int start = caret + 9;
				int end = caret + CDATA_SECTION.length() - 3;
				RequestTab.this.requestComposite.getStyledText().setSelection( start, end );
			}
		});

		this.requestComposite.getStyledText().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {

				if( e.button != 3 )
					return;

				int caret = RequestTab.this.requestComposite.getStyledText().getCaretOffset() - 1;
				String txt = RequestTab.this.requestComposite.getPayload();
				char c = '!';
				while( caret > 0
						&& Character.isWhitespace((c = txt.charAt( caret ))))
					caret --;

				if( c == '>' )
					contextMenu.setVisible( true );
			}
		});


		// Validate the request when it changes
		this.requestComposite.getStyledText().addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				validate();
			}
		});


		// Allow to save response attachments
		final Menu contextMenu2 = new Menu( this.responseComposite.getAttachmentsViewer().getTable());
		menuItem = new MenuItem ( contextMenu2, SWT.PUSH );
		menuItem.setText( "Save as..." );
		menuItem.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				ISelection s = RequestTab.this.responseComposite.getAttachmentsViewer().getSelection();
				if( s.isEmpty())
					return;

				FileDialog dlg = new FileDialog( getShell(), SWT.SAVE );
				String result = dlg.open();
				if( result == null )
					return;

				File outputFile = new File( result );
				File inputFile = (File) ((IStructuredSelection) s).getFirstElement();
				try {
					Utils.copyFile( inputFile, outputFile );

				} catch( IOException e1 ) {
					MessageDialog.openInformation( getShell(), "Error", "An error occurred while saving an attachment." );
					RequestTab.this.clientApp.log( "Error while saving an attachment.", e1, Level.INFO );
				}
			}
		});

		this.responseComposite.getAttachmentsViewer().getTable().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown( MouseEvent e ) {

				if( e.button != 3 )
					return;

				ISelection s = RequestTab.this.responseComposite.getAttachmentsViewer().getSelection();
				if( ! s.isEmpty())
					contextMenu.setVisible( true );
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

        // The "send "options
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
        this.sendButton.setImage( JFaceResources.getImage( ImageIds.SEND_64x64 ));
        this.sendButton.setEnabled( false );


        // ... and to display reports
        Composite reportingComposite = new Composite( sendGroup, SWT.NONE );
        GridLayoutFactory.swtDefaults().margins( 25, 8 ).applyTo( reportingComposite );
        GridDataFactory.fillDefaults().span( 1, 2 ).grab( true, true ).align( SWT.FILL, SWT.END ).applyTo( reportingComposite );

        // Use a sub-composite, to keep UI consistency when updating the messages (parent's layout())
        Composite subComposite = new Composite( reportingComposite, SWT.NONE );
        GridLayoutFactory.swtDefaults().margins( 0, 0 ).numColumns( 2 ).applyTo( subComposite );
        subComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));

        this.reportingImageLabel = new Label( subComposite, SWT.NONE );
        this.reportingStyledText = new StyledText( subComposite, SWT.MULTI );
        this.reportingStyledText.setLayoutData( new GridData( GridData.FILL_BOTH ));

        this.reportingProgressBar = new ProgressBar( reportingComposite, SWT.INDETERMINATE );
        this.reportingProgressBar.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
        this.reportingProgressBar.setVisible( false );


        // What to do when "send" is clicked?
        this.sendButton.addListener( SWT.Selection, new Listener() {
        	@Override
        	public void handleEvent( Event e ) {

        		RequestMessageBean req = null;
        		try {
					req = createRequestInstance();
					RequestTab.this.clientApp.getPetalsFacade().send( req );
					updateMessage( "The request was successfully sent.", IStatus.INFO );

				} catch( Exception e1 ) {
					MessageDialog.openInformation( getShell(), "Error", "An error occurred while sending a request to a Petals service." );
					RequestTab.this.clientApp.log( "Error while sending a Petals message.", e1, Level.INFO );

				} finally {

					if( req != null ) {
						File f = Utils.getNewHistoryFile( req );
						try {
							Utils.saveRequest( f, req, false );
							RequestTab.this.clientApp.refreshHistory();

						} catch( IOException e1 ) {
							RequestTab.this.clientApp.log( "Error while updating the history.", e1, Level.INFO );
						}
					}
				}
        	}
        });
	}


	/**
	 * Displays a request in the tab.
	 * @param req a request (not null)
	 */
	public void displayEntireRequest( RequestMessageBean req ) {

		updateInterfaceName( req.getInterfaceName());
		updateServiceName( req.getServiceName());
		updateEndpointName( req.getEndpointName());
		updateOperationName( new OperationBean( req.getMep(), req.getOperation()));

		this.requestComposite.setInput( req );
		this.responseComposite.setInput( null );
		resolveServiceEndpoint();
	}



	/**
	 * Updates the progress report part.
	 * <p>
	 * If the description is not null, it shows the action description and activates a progress bar.
	 * Otherwise, no report is shown anymore.
	 * </p>
	 *
	 * @param description the action's description or null to stop reporting information
	 */
	private void updateProgressReport( String description ) {
		this.reportingProgressBar.setVisible( description != null );
		updateMessage( description, IStatus.OK );
	}


	/**
	 * Resolves the real service end-point from a service identifier.
	 */
	private void resolveServiceEndpoint() {

		// UI thread: get what we need, just in case
		final QName itfName = (QName) this.itfText.getData();
		final QName srvName = (QName) this.srvText.getData();
		final String edptName = this.edptText.getText();
		final List<OperationBean> ops = new ArrayList<OperationBean> ();;

		// Runnable to execute in the UI thread, once communications with Petals are over
		final Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {

				// Update the list of operations to show
				RequestTab.this.operationViewer.setInput( ops );
				RequestTab.this.operationViewer.refresh();
				RequestTab.this.operationViewer.getCCombo().setVisibleItemCount( ops.isEmpty() ? 5 : ops.size());
				if( ops.size() > 0 )
					RequestTab.this.operationViewer.setSelection( new StructuredSelection( ops.get( 0 )));

				// Update the buttons
				RequestTab.this.showWsdlButton.setEnabled( RequestTab.this.wsdlDescriptionAsString != null );

				// Complete the operation
				updateProgressReport( null );
				RequestTab.this.operationViewer.getCCombo().notifyListeners( SWT.Selection, new Event());
			}
		};

		// Background thread: get data from Petals
		Runnable petalsRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Resolve things
					PetalsFacade petalsFacade = RequestTab.this.clientApp.getPetalsFacade();
					ServiceEndpoint resolvedSe = null;
					if( resolvedSe == null )
						resolvedSe = petalsFacade.resolveServiceEndpoint( itfName, srvName, edptName );

					Document doc = null;
					if( resolvedSe != null )
						doc = petalsFacade.findWsdlDescriptionAsDocument( resolvedSe );

					// Store the WSDL as a string, as we may need to display it later
					RequestTab.this.wsdlDescriptionAsString = doc == null ? null : Utils.writeDocument( doc, false );

					// Parse the document as a WSDL
					WSDLReader wsdlReader = petalsFacade.getWsdlReader();
					RequestTab.this.wsdlDescription = wsdlReader != null && doc != null ? wsdlReader.read( doc ) : null;

					// Get the operations
					if( RequestTab.this.wsdlDescription != null ) {
						for( InterfaceType it : RequestTab.this.wsdlDescription.getInterfaces())
							ops.addAll( OperationBean.convert( it.getOperations()));
					}

					// Update the user interface
					Display.getDefault().asyncExec( uiRunnable );

				} catch( Exception e ) {
					RequestTab.this.clientApp.log( "An error occurred while generating a request.", e, Level.INFO );
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
					List<ServiceEndpoint> serviceEndpoints =
							RequestTab.this.clientApp.getPetalsFacade().getAllServiceEndpoints();

					if( serviceEndpoints != null )
						ses.addAll( serviceEndpoints );

					// Update the user interface
					Display.getDefault().asyncExec( uiRunnable );

				} catch( Exception e ) {
					RequestTab.this.clientApp.log( "Error while retrieving Petals services.", e, Level.INFO );
				}
			}
		};

		// Start communications with Petals
		updateProgressReport( "Retrieving all the service end-points..." );
		new Thread( petalsRunnable ).start();

		return ses;
	}


	/**
	 * Updates the message being displayed.
	 * @param msg a message (null to display nothing)
	 * @param status an IStatus constant for the message's severity
	 */
	private void updateMessage( String msg, int status ) {

		Color color = null;
		Image img = null;
		if( msg != null ) {
			if( status == IStatus.INFO ) {
				img = JFaceResources.getImage( ImageIds.INFO_16x16 );
				color = getDisplay().getSystemColor( SWT.COLOR_BLACK );

			} else if( status == IStatus.OK ) {
				img = null;
				color = getDisplay().getSystemColor( SWT.COLOR_BLACK );

			} else if( status == IStatus.WARNING ) {
				img = JFaceResources.getImage( ImageIds.WARNING_16x16 );
				color = this.clientApp.getColorManager().getOrangeColor();

			} else if( status == IStatus.ERROR ) {
				img = JFaceResources.getImage( ImageIds.ERROR_16x16 );
				color = getDisplay().getSystemColor( SWT.COLOR_RED );
			}
		}

		this.reportingImageLabel.setImage( img );
		if( msg == null || color == null )
			this.reportingStyledText.setText( "" );
		else {
			this.reportingStyledText.setText( msg );
			StyleRange sr = new StyleRange();
			sr.foreground = color;
			sr.start = 0;
			sr.length = msg.length();
			this.reportingStyledText.setStyleRange( sr );
		}

		this.reportingImageLabel.getParent().layout();
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
	 * Updates the operation.
	 * @param op
	 */
	private void updateOperationName( OperationBean op ) {

		List<Object> newInput = new ArrayList<Object> ();
		Object input = this.operationViewer.getInput();
		if( input instanceof List<?> )
			newInput.addAll((List<?>) input);

		newInput.add( op );
		RequestTab.this.operationViewer.setInput( newInput );

		this.operationViewer.setSelection( new StructuredSelection( op ));
		RequestTab.this.operationViewer.getCCombo().notifyListeners( SWT.Selection, new Event());
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
		if( this.operationBean != null ) {
			result.setOperation( this.operationBean.getOperationName());
			result.setMep( this.operationBean.getMep());
		}

		return result;
	}


	/**
	 * @return a non-null string representing a new XML pay-load
	 */
	private String generateSkeleton() {

		String requestPayload = null;
		ISelection selection = RequestTab.this.operationViewer.getSelection();
		if( ! selection.isEmpty()
				&& this.wsdlDescription != null ) {

			OperationBean op = (OperationBean) ((IStructuredSelection) selection).getFirstElement();
			QName itfName = (QName) RequestTab.this.itfText.getData();
			try {
				requestPayload = Utils.generateXmlSkeleton(
						RequestTab.this.wsdlDescription,
						itfName, op.getOperationName(), true );

			} catch( Exception e1 ) {
				RequestTab.this.clientApp.log( "Error while generating a XML skeleton.", e1, Level.INFO );
				requestPayload = DEFAULT_SKELETON + COMMENT_INVALID_WSDL;
			}
		}

		if( requestPayload == null ) {
			requestPayload = DEFAULT_SKELETON + COMMENT_NO_WSDL;
		}

		return requestPayload;
	}
}
