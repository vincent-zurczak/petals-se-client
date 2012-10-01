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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.swt.ImageIds;

/**
 * A dialog that gives information about this component.
 * @author Vincent Zurczak - Linagora
 */
public class AboutDialog extends Dialog {

	private final Image petalsImg;


	/**
	 * Constructor.
	 * @param parentShell
	 */
	public AboutDialog( Shell parentShell ) {
		super( parentShell );
		setShellStyle( SWT.SHELL_TRIM );
		this.petalsImg = JFaceResources.getImage( ImageIds.PETALS_SPLASH );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #close()
	 */
	@Override
	public boolean close() {

		boolean result = super.close();
		if( this.petalsImg != null && ! this.petalsImg.isDisposed())
			this.petalsImg.dispose();

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "About" );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		container.setLayout( new GridLayout( 2, false ));
		GridDataFactory.fillDefaults().hint( 700, SWT.DEFAULT ).applyTo( container );

		// The image
		Label imgLabel = new Label( container, SWT.NONE );
		imgLabel.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		imgLabel.setImage( this.petalsImg );

		// The "about" text
		Composite subContainer = new Composite( container, SWT.NONE );
		subContainer.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).applyTo( subContainer );
		subContainer.setLayoutData( new GridData( GridData.FILL_BOTH ));

		Label aboutLabel = new Label( subContainer, SWT.WRAP );
		aboutLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		aboutLabel.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		aboutLabel.setText( "\n\nPetals Client Component\n\n"
				+ "This user interface was created by a Petals component.\n"
				+ "It will run as long as the component is deployed in Petals ESB.\n"
				+ "The Petals Client Component is a small utility program that aims at defining, sending and saving requests for Petals services.\n" );

		Link visitUsLink = new Link( subContainer, SWT.NONE );
		visitUsLink.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		visitUsLink.setText( "\nVisit Petals ESB at <a>http://petals.ow2.org</a>\n" +
				"Visit Petals Link at <a>http://petalslink.com</a>\n" +
				"Visit Linagora at <a>http://linagora.com</a>" );

		aboutLabel = new Label( subContainer, SWT.WRAP );
		aboutLabel.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		aboutLabel.setText( "\n\"Petals Link\" is a registered Trademark of Linagora.\n"
				+ "(c) Copyright Linagora, 2012 - All Rights Reserved\n" );

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


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
	 */
	@Override
	protected Button createButton( Composite parent, int id, String label, boolean defaultButton ) {
		return id == Dialog.CANCEL ? null : super.createButton( parent, id, label, defaultButton );
	}
}
