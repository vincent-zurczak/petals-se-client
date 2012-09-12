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

package org.ow2.petals.engine.client.swt.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.ow2.petals.engine.client.swt.viewers.ServiceRegistryContentProvider;
import org.ow2.petals.engine.client.swt.viewers.ServiceRegistryLabelProvider;

/**
 * A dialog to select a service end-point in a Petals registry.
 * @author Vincent Zurczak - EBM WebSourcing
 */
public class ServiceRegistryViewerDialog extends TitleAreaDialog {

	private final static String DEFAULT_MSG = "Select a Petals service and one of its operations to invoke.";

	private ServiceRegistryLabelProvider labelProvider;
	private final List<ServiceEndpoint> serviceEndpoints;
	private String filterItfName, filterItfNs, filterSrvName, filterSrvNs, filterEdptName;

	private ServiceEndpoint serviceEndpoint;
	private QName itfToInvoke, srvToInvoke;
	private String edptToInvoke;


	/**
	 * Constructor.
	 * @param parentShell
	 * @param serviceEndpoints
	 */
	public ServiceRegistryViewerDialog( Shell parentShell, List<ServiceEndpoint> serviceEndpoints ) {
		super( parentShell );
		this.serviceEndpoints = serviceEndpoints;
		setShellStyle( SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX );
	}


	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog
	 * #close()
	 */
	@Override
	public boolean close() {

		if( this.labelProvider != null )
			this.labelProvider.dispose();

		return super.close();
	}



	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( final Composite parent ) {

		// General properties
		getShell().setText( "Consume a Petals Service" );
		setTitle( "Consume a Petals Service" );
		setMessage( DEFAULT_MSG );

		Composite outterComposite = new Composite( parent, SWT.BORDER );
		outterComposite.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ));

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		outterComposite.setLayout( layout );
		outterComposite.setLayoutData( new GridData( GridData.FILL_BOTH ));




		// Create the search filter
//		Section filterSection = this.toolkit.createSection( container,
//					ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.DESCRIPTION );
//		filterSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
//		filterSection.clientVerticalSpacing = 10;
//		filterSection.setText( "Search Filters" );
//		filterSection.setDescription( "Filter the displayed services." );
//

		Tree tree = new Tree( outterComposite, SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.SINGLE );
		GridData layoutData = new GridData( GridData.FILL_BOTH );
		layoutData.widthHint = 400;
		layoutData.heightHint = 400;
		tree.setLayoutData( layoutData );

		final TreeViewer treeViewer = new TreeViewer( tree );
		treeViewer.setContentProvider( new ServiceRegistryContentProvider());

		this.labelProvider = new ServiceRegistryLabelProvider();
		treeViewer.setLabelProvider( this.labelProvider );
		// treeViewer.addFilter( new ServiceViewerFilter());
		ColumnViewerToolTipSupport.enableFor( treeViewer, ToolTip.NO_RECREATE );


		// Prepare the input...
		Map<QName,ItfBean> itfNameToInterface = new HashMap<QName,ItfBean> ();
		for( ServiceEndpoint se : this.serviceEndpoints ) {

			// Handle the interface name
			ItfBean itfBean = itfNameToInterface.get( se.getInterfaces()[ 0 ]);
			if( itfBean == null ) {
				itfBean = new ItfBean();
				itfBean.itfName = se.getInterfaces()[ 0 ];
				itfNameToInterface.put( itfBean.itfName, itfBean );
			}

			// Handle the service name
			SrvBean srvBean = itfBean.srvNameToService.get( se.getServiceName());
			if( srvBean == null ) {
				srvBean = new SrvBean();
				srvBean.itfName = itfBean.itfName;
				srvBean.srvName = se.getServiceName();
				itfBean.srvNameToService.put( srvBean.srvName, srvBean );
			}

			// Handle the end-point name
			EdptBean edptBean = new EdptBean();
			edptBean.se = se;
			srvBean.endpoints.add( edptBean );
		}

		// ... and set it!
		treeViewer.setInput( itfNameToInterface );

		// Listen to the selection
		treeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {

				Object o = ((IStructuredSelection) e.getSelection()).getFirstElement();
				if( o instanceof ItfBean ) {
					ServiceRegistryViewerDialog.this.itfToInvoke = ((ItfBean) o).itfName;
					ServiceRegistryViewerDialog.this.srvToInvoke = null;
					ServiceRegistryViewerDialog.this.edptToInvoke = null;

				} else if( o instanceof SrvBean ) {
					ServiceRegistryViewerDialog.this.itfToInvoke = ((SrvBean) o).itfName;
					ServiceRegistryViewerDialog.this.srvToInvoke = ((SrvBean) o).srvName;
					ServiceRegistryViewerDialog.this.edptToInvoke = null;

				} else if( o instanceof EdptBean ) {
					ServiceRegistryViewerDialog.this.itfToInvoke = ((EdptBean) o).se.getInterfaces()[ 0 ];
					ServiceRegistryViewerDialog.this.srvToInvoke = ((EdptBean) o).se.getServiceName();
					ServiceRegistryViewerDialog.this.edptToInvoke = ((EdptBean) o).se.getEndpointName();
				}

				Button okButton;
				if( ServiceRegistryViewerDialog.this.itfToInvoke != null
						&& ( okButton = getButton( IDialogConstants.OK_ID )) != null )
					okButton.setEnabled( true );
			}
		});

		return outterComposite;
	}


	/*
	 * (non-Jsdoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog
	 * #createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar( Composite parent ) {

		Composite comp = (Composite) super.createButtonBar( parent );
		comp.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		Button okButton = getButton( IDialogConstants.OK_ID );
		if( okButton != null ) {
			okButton.getParent().setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ));
			okButton.setEnabled( false );
		}

		return comp;
	}


	/**
	 * A bean that describes an interface.
	 */
	public static class ItfBean {
		public QName itfName;
		public Map<QName,SrvBean> srvNameToService = new HashMap<QName,SrvBean> ();
	}


	/**
	 * A bean that describes a service.
	 */
	public static class SrvBean {
		public QName srvName, itfName;
		public List<EdptBean> endpoints = new ArrayList<EdptBean> ();
	}


	/**
	 * A bean that describes an end-point.
	 */
	public static class EdptBean {
		public ServiceEndpoint se;
	}


	/**
	 * @return the itfToInvoke
	 */
	public QName getItfToInvoke() {
		return this.itfToInvoke;
	}


	/**
	 * @return the srvToInvoke
	 */
	public QName getSrvToInvoke() {
		return this.srvToInvoke;
	}


	/**
	 * @return the edptToInvoke
	 */
	public String getEdptToInvoke() {
		return this.edptToInvoke;
	}


	/**
	 * @return the serviceEndpoint
	 */
	public ServiceEndpoint getServiceEndpoint() {
		return this.serviceEndpoint;
	}
}
