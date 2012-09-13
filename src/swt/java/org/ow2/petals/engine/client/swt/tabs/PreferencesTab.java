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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;

/**
 * The preferences tab.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesTab extends Composite {

	/**
	 * Constructor.
	 * @param parent
	 * @param colorManager
	 */
	public PreferencesTab( Composite parent, ColorCacheManager colorManager ) {

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
					PreferencesManager.INSTANCE.saveHistoryDirectory( new File( dir ));
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


		// Syntax highlight
		Group shGroup = new Group( this, SWT.SHADOW_ETCHED_IN );
		shGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		shGroup.setLayout( new GridLayout( 2, false ));
		shGroup.setText( "Syntax Highlighting" );

		final Map<String,String> keyToLabel = new LinkedHashMap<String,String> ();
		keyToLabel.put( PreferencesManager.COLOR_COMMENT, "XML Comments" );
		keyToLabel.put( PreferencesManager.COLOR_MARKUP, "XML Mark-ups" );
		keyToLabel.put( PreferencesManager.COLOR_ATTRIBUTE, "XML Attributes" );
		keyToLabel.put( PreferencesManager.COLOR_ATTRIBUTE_VALUE, "Attribute Values" );
		keyToLabel.put( PreferencesManager.COLOR_CDATA, "CDATA Sections" );
		keyToLabel.put( PreferencesManager.COLOR_INSTRUCTION, "XML Instructions" );

		// List the customizable elements...
		ComboViewer styleViewer = new ComboViewer( shGroup, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
		styleViewer.setContentProvider( new ArrayContentProvider());
		styleViewer.setLabelProvider( new LabelProvider() {
			@Override
			public String getText( Object element ) {
				return keyToLabel.get( element );
			};
		});

		GridDataFactory.swtDefaults().align( SWT.CENTER, SWT.BEGINNING ).hint( 200, SWT.DEFAULT ).applyTo( styleViewer.getCombo());
		styleViewer.setInput( keyToLabel.keySet());

		// ... and their properties
		Composite styleComposite = new Composite( shGroup, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 0, 0 ).numColumns( 5 ).applyTo( styleComposite );
		styleComposite.setLayoutData( new GridData( GridData.FILL_BOTH ));

		final Button boldButton = new Button( styleComposite, SWT.CHECK );
		boldButton.setText( "Bold" );

		final Button italicButton = new Button( styleComposite, SWT.CHECK );
		italicButton.setText( "Italic" );

		final Button underlineButton = new Button( styleComposite, SWT.CHECK );
		underlineButton.setText( "Underline" );

		new Label( styleComposite, SWT.NONE ).setText( "Foreground Color:" );
		Button b = new Button( styleComposite, SWT.PUSH );
		b.setText( "Edit..." );

		StyledText st = new StyledText( styleComposite, SWT.MULTI | SWT.BORDER );
		st.setLayoutData( new GridData( GridData.FILL_BOTH ));
		GridDataFactory.swtDefaults().span( 5, 1 ).applyTo( st );
	}
}
