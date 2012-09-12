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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog to show the content of a WSDL description.
 * @author Vincent Zurczak - Linagora
 */
public class ShowWsdlDialog extends Dialog {

	private final String wsdlContent;


	/**
	 * Constructor.
	 * @param parentShell
	 * @param wsdlContent
	 */
	public ShowWsdlDialog( Shell parentShell, String wsdlContent ) {
		super( parentShell );
		this.wsdlContent = wsdlContent;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "WSDL Content" );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		container.setLayout( new GridLayout( 2, false ));
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		StyledText styledText = new StyledText( container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI );
		styledText.setLayoutData( new GridData( GridData.FILL_BOTH ));
		styledText.setText( this.wsdlContent );

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
		return new Point( 500, 300 );
	}
}
