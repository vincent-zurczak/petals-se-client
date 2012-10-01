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

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog that gives information about this component.
 * @author Vincent Zurczak - Linagora
 */
public class KeyValueDialog extends Dialog {

	private final Map<String,String> currentProperties;
	private final String[] parts = { "", "" };
	private Label errorLabel;


	/**
	 * Constructor.
	 * @param parentShell
	 * @param currentProperties
	 */
	public KeyValueDialog( Shell parentShell, Map<String,String> currentProperties ) {
		super( parentShell );
		setShellStyle( SWT.SHELL_TRIM );
		this.currentProperties = currentProperties;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "New Message Property" );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		container.setLayout( new GridLayout( 2, false ));
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		String[] labels = { "Key:", "Value:" };
		for( int i=0; i<2; i++ ) {
			Label l = new Label( container, SWT.NONE );
			l.setText( labels[ i ]);
			l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

			final Text text = new Text( container, SWT.SINGLE | SWT.BORDER );
			text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
			text.addFocusListener( new FocusAdapter() {
				@Override
				public void focusGained( FocusEvent e ) {
					text.selectAll();
				}
			});

			final int index = i;
			text.addModifyListener( new ModifyListener() {
				@Override
				public void modifyText( ModifyEvent e ) {
					KeyValueDialog.this.parts[ index ] = text.getText();
					validate();
				}
			});
		}

		this.errorLabel = new Label( container, SWT.NONE );
		this.errorLabel.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		GridDataFactory.swtDefaults().span( 2, 1 ).indent( 50, 10 ).applyTo( this.errorLabel );

		return container;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar( Composite parent ) {

		Control control = super.createButtonBar(parent);
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
	 * #getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point( 500, 200 );
	}


	/**
	 * Validates the fields.
	 */
	private void validate() {

		String msg = null;
		if( this.parts[ 0 ].trim().length() == 0 )
			msg = "You have to provide a valid key name.";
		else if( this.currentProperties.containsKey( this.parts[ 0 ]))
			msg = "There is already a message property with this key.";

		this.errorLabel.setText( msg == null ? "" : msg );
		this.errorLabel.update();
		this.errorLabel.getParent().layout();

		Button b = getButton( Dialog.OK );
		if( b != null )
			b.setEnabled( msg == null );
	}


	/**
	 * @return the key
	 */
	public String getKey() {
		return this.parts[ 0 ];
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return this.parts[ 1 ];
	}
}
