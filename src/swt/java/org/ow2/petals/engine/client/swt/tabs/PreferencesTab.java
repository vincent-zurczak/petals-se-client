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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.ow2.petals.engine.client.misc.PreferencesManager;

/**
 * The preferences tab.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesTab extends Composite {

	/**
	 * Constructor.
	 * @param parent
	 */
	public PreferencesTab( Composite parent ) {

		// Root elements
		super( parent, SWT.NONE );
		GridLayoutFactory.swtDefaults().spacing( 0, 20 ).applyTo( this );
		setLayoutData( new GridData( GridData.FILL_BOTH ));

		new Label( this, SWT.NONE ).setText( "The preferences for this client application." );


		// History
		Group historyGroup = new Group( this, SWT.SHADOW_ETCHED_IN );
		historyGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		historyGroup.setLayout( new GridLayout( 3, false ));
		historyGroup.setText( "History" );

		Label l = new Label( historyGroup, SWT.NONE );
		l.setText( "History Directory:" );
		l.setToolTipText( "The directory in which old requests are stored" );

		final Text directoryText = new Text( historyGroup, SWT.BORDER | SWT.READ_ONLY );
		directoryText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		directoryText.setText( PreferencesManager.INSTANCE.getHistoryDirectory().getAbsolutePath());

		Button browseButton = new Button( historyGroup, SWT.PUSH );
		browseButton.setText( "Browse..." );
		browseButton.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				DirectoryDialog dlg = new DirectoryDialog( getShell());
				String dir = dlg.open();
				if( dir != null ) {
					PreferencesManager.INSTANCE.setHistoryDirectory( new File( dir ));
					directoryText.setText( dir );
				}
			}
		});


		// Clear the history?
		l = new Label( historyGroup, SWT.NONE );
		l.setText( "Clear History:" );
		l.setLayoutData( new GridData( SWT.BEGINNING, SWT.BEGINNING, false, false ));

		Composite subContainer = new Composite( historyGroup, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).numColumns( 3 ).applyTo( subContainer );
		GridDataFactory.swtDefaults().align( SWT.FILL, SWT.TOP ).grab( true, false ).span( 2, 1 ).applyTo( subContainer );
		GridDataFactory.swtDefaults().span( 3, 1 ).applyTo( new Label( subContainer, SWT.NONE ));

		Button b1 = new Button( subContainer, SWT.RADIO );
		b1.setText( "Clear the History older than" );

		Spinner historySpinner = new Spinner( subContainer, SWT.BORDER );
		historySpinner.setValues( 1, 1, Integer.MAX_VALUE, 0, 1, 10 );
		new Label( subContainer, SWT.NONE ).setText( "day(s)." );

		Button b2 = new Button( subContainer, SWT.RADIO );
		b2.setText( "Clear All the History" );
		GridDataFactory.swtDefaults().span( 3, 1 ).applyTo( b2 );


		// Default values
		Group defaultGroup = new Group( this, SWT.SHADOW_ETCHED_IN );
		defaultGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		defaultGroup.setLayout( new GridLayout( 2, false ));
		defaultGroup.setText( "Default Values" );

		l = new Label( defaultGroup, SWT.NONE );
		l.setText( "Default Timeout:" );
		l.setToolTipText( "The default timeout to use when crating a new request" );

		Spinner timeoutSpinner = new Spinner( defaultGroup, SWT.BORDER );
		long value = PreferencesManager.INSTANCE.getDefaultTimeout();
		timeoutSpinner.setValues((int) value, 0, Integer.MAX_VALUE, 0, 1000, 100 );
	}
}
