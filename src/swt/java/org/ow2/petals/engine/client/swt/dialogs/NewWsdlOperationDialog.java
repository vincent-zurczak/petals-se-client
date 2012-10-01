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

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.Mep;
import org.ow2.petals.engine.client.model.OperationBean;

/**
 * A dialog to create a custom WSDL operation.
 * @author Vincent Zurczak - Linagora
 */
public class NewWsdlOperationDialog extends TitleAreaDialog {

	private Mep mep = Mep.IN_OUT;
	private String localPart, namespaceUri;


	/**
	 * Constructor.
	 * @param parentShell
	 * @param op
	 */
	public NewWsdlOperationDialog( Shell parentShell, OperationBean op ) {
		super( parentShell );
		setShellStyle( SWT.SHELL_TRIM );

		if( op != null ) {
			if( op.getMep() != null )
				this.mep = op.getMep();

			if( op.getOperationName() != null ) {
				this.localPart = op.getOperationName().getLocalPart();
				this.namespaceUri = op.getOperationName().getNamespaceURI();
			}
		}
	}


	/**
	 * @return the operation bean, as specified in this dialog
	 */
	public OperationBean getOperationBean() {
		return new OperationBean(this.mep, new QName( this.namespaceUri, this.localPart ));
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "New WSDL Operation" );
		setTitle( "New WSDL Operation" );
		setMessage( "Create a custom WSDL operation." );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		container.setLayout( new GridLayout( 2, false ));
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		// Local Part
		Label l = new Label( container, SWT.NONE );
		l.setText( "Operation Name:" );
		l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		Text text = new Text( container, SWT.SINGLE | SWT.BORDER );
		text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		if( this.localPart != null )
			text.setText( this.localPart );

		text.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				NewWsdlOperationDialog.this.localPart = ((Text) e.widget).getText();
				validate();
			}
		});


		// Name space URI
		l = new Label( container, SWT.NONE );
		l.setText( "Operation Namespace:" );
		l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		text = new Text( container, SWT.SINGLE | SWT.BORDER );
		text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		if( this.namespaceUri != null )
			text.setText( this.namespaceUri );

		text.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				NewWsdlOperationDialog.this.namespaceUri = ((Text) e.widget).getText();
				validate();
			}
		});


		// MEP
		l = new Label( container, SWT.NONE );
		l.setText( "MEP:" );
		l.setToolTipText( "Message Exchange Pattern" );
		l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		final ComboViewer mepViewer = new ComboViewer( container, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN );
		mepViewer.getCombo().setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		mepViewer.setContentProvider( new ArrayContentProvider());
		mepViewer.setLabelProvider( new LabelProvider());
		mepViewer.setInput( Mep.values());

		mepViewer.setSelection( new StructuredSelection( this.mep ));
		mepViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent e ) {
				NewWsdlOperationDialog.this.mep = (Mep) ((IStructuredSelection) mepViewer.getSelection()).getFirstElement();
				validate();
			}
		});

		return container;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar( Composite parent ) {

		Control control = super.createButtonBar( parent );
		control.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		parent.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		Button b = getButton( Dialog.OK );
		if( b != null ) {
			b.setFocus();
			b.setEnabled( false );
		}

		return control;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar( Composite parent ) {
		parent.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		super.createButtonsForButtonBar( parent );
	}


	/**
	 * Validates the fields.
	 */
	private void validate() {

		String msg = null;
		if( Utils.isEmptyString( this.localPart ))
			msg = "You must define the local part of the operation name.";
		else if( Utils.isEmptyString( this.namespaceUri ))
			msg = "You must define the name space URI of the operation name.";

		setErrorMessage( msg );
		Button b = getButton( Dialog.OK );
		if( b != null )
			b.setEnabled( msg == null );
	}
}
