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
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.widgets.Spinner;

/**
 * A dialog to tailor the history clearance.
 * @author Vincent Zurczak - Linagora
 */
public class ClearHistoryDialog extends Dialog {

	private int days = 3;


	/**
	 * Constructor.
	 * @param parentShell
	 */
	public ClearHistoryDialog( Shell parentShell ) {
		super( parentShell );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "Clear the History older than..." );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		GridLayoutFactory.swtDefaults().numColumns( 3 ).margins( 10, 10 ).applyTo( container );
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		Label l = new Label( container, SWT.NONE );
		l.setText( "Clear the history older than..." );
		l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		Spinner daySpinner = new Spinner( container, SWT.BORDER );
		daySpinner.setValues( this.days, 1, Integer.MAX_VALUE, 0, 1, 10 );
		daySpinner.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				ClearHistoryDialog.this.days = ((Spinner) e.widget).getSelection();
			}
		});

		l = new Label( container, SWT.NONE );
		l.setText( " days." );
		l.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

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
		if( b != null )
			b.setFocus();

		return control;
	}


	/**
	 * @return the days
	 */
	public int getDays() {
		return this.days;
	}
}
