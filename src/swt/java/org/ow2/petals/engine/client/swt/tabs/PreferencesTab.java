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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.swt.ClientApplication;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.dialogs.ClearHistoryDialog;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion.XmlRegionType;

/**
 * The preferences tab.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesTab extends Composite {

	/**
	 * Constructor.
	 * @param parent
	 * @param clientApp
	 */
	public PreferencesTab( Composite parent, final ClientApplication clientApp ) {

		// Root elements
		super( parent, SWT.NONE );
		GridLayoutFactory.swtDefaults().spacing( 0, 0 ).applyTo( this );
		setLayoutData( new GridData( GridData.FILL_BOTH ));

		final ColorCacheManager colorManager = clientApp.getColorManager();
		new Label( this, SWT.NONE ).setText( "The preferences for this client application." );


		// History
		Composite historyGroup = createSection( "History", 4, false );

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

		Button resetHistoryDirButton = new Button( historyGroup, SWT.PUSH );
		resetHistoryDirButton.setText( "Reset Default Directory" );
		resetHistoryDirButton.setToolTipText( "Set the history directory in the temporary files" );
		resetHistoryDirButton.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				PreferencesManager.INSTANCE.saveHistoryDirectory( null );
				File f = PreferencesManager.INSTANCE.getHistoryDirectory();
				directoryText.setText( f.getAbsolutePath());
			}
		});


		// Clear the history?
		new Label( historyGroup, SWT.NONE );
		Link link = new Link( historyGroup, SWT.NONE );
		link.setText( "<A>Clear All the History</A>" );
		link.setToolTipText( "Deletes the entire history" );
		GridDataFactory.swtDefaults().span( 3, 1 ).applyTo( link );
		link.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				SwtUtils.clearHistoryWithProgressBar( getShell(), -1 );
			}
		});

		new Label( historyGroup, SWT.NONE );
		link = new Link( historyGroup, SWT.NONE );
		link.setText( "<A>Clear the History older than...</A>" );
		link.setToolTipText( "Deletes the oldest part of the history" );
		link.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				ClearHistoryDialog dlg = new ClearHistoryDialog( getShell());
				if( dlg.open() == Window.OK ) {
					SwtUtils.clearHistoryWithProgressBar( getShell(), dlg.getDays());
					clientApp.refreshHistory();
				}
			}
		});


		// General Preferences
		Composite defaultGroup = createSection( "General Preferences", 2, false  );

		Button wrapButton = new Button( defaultGroup, SWT.CHECK );
		wrapButton.setText( "Wrap text in XML viewers" );
		wrapButton.setToolTipText( "Will allow XML viewers to wrap their content instead of only scrolling" );
		wrapButton.setSelection( PreferencesManager.INSTANCE.wrapInsteadOfScrolling());

		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( wrapButton );
		wrapButton.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				boolean wrap = ((Button) e.widget).getSelection();
				PreferencesManager.INSTANCE.saveWrapInsteadOfScrolling( wrap );

				MessageDialog.openInformation(
						getShell(), "Restart",
						"The user interface needs to be restarted to take this preference into account.\n"
						+ "Save your work and then go into \"Help > Restart User Interface\"." );
			}
		});

		l = new Label( defaultGroup, SWT.NONE );
		l.setText( "Default Timeout:" );
		l.setToolTipText( "The default timeout to use when crating a new request" );

		Spinner timeoutSpinner = new Spinner( defaultGroup, SWT.BORDER );
		long value = PreferencesManager.INSTANCE.getDefaultTimeout();
		timeoutSpinner.setValues((int) value, 0, Integer.MAX_VALUE, 0, 1000, 100 );


		// Syntax highlighting
		Composite shGroup = createSection( "Syntax Highlighting", 2, true );
		l = new Label( shGroup, SWT.NONE );
		l.setText( "XML Viewers Preview" );
		GridDataFactory.swtDefaults().span( 2, 1 ).applyTo( l );

		final Map<XmlRegionType,String> regionToLabel = new LinkedHashMap<XmlRegionType,String> ();
		regionToLabel.put( XmlRegionType.MARKUP, "XML Mark-ups" );
		regionToLabel.put( XmlRegionType.ATTRIBUTE, "XML Attributes" );
		regionToLabel.put( XmlRegionType.ATTRIBUTE_VALUE, "Attribute Values" );
		regionToLabel.put( XmlRegionType.COMMENT, "XML Comments" );
		regionToLabel.put( XmlRegionType.CDATA, "CDATA Sections" );
		regionToLabel.put( XmlRegionType.INSTRUCTION, "XML Instructions" );

		// Show a preview area
		final StyledText previewStyledText = SwtUtils.createXmlViewer( shGroup, colorManager, false );
		String previewText = Utils.loadResource( "/sample.xml" );
		previewStyledText.setText( previewText );

		// List the customizable elements...
		Composite subContainer = new Composite( shGroup, SWT.NONE );
		GridLayoutFactory.swtDefaults().margins( 10, 0 ).numColumns( 2 ).applyTo( subContainer );
		GridDataFactory.swtDefaults().align( SWT.BEGINNING, SWT.BEGINNING ).grab( false, true ).applyTo( subContainer );

		new Label( subContainer, SWT.NONE ).setText( "Select the style to edit:" );
		final ComboViewer styleViewer = new ComboViewer( subContainer, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
		styleViewer.setContentProvider( new ArrayContentProvider());
		styleViewer.setLabelProvider( new LabelProvider() {
			@Override
			public String getText( Object element ) {
				return regionToLabel.get( element );
			};
		});

		GridDataFactory.swtDefaults().hint( 200, SWT.DEFAULT ).applyTo( styleViewer.getCombo());
		styleViewer.setInput( regionToLabel.keySet());

		// ... and their properties
		final List<Button> styleButtons = new ArrayList<Button> ();

		new Label( subContainer, SWT.NONE );
		Button b = new Button( subContainer, SWT.CHECK );
		b.setText( "Bold" );
		b.setData( PreferencesManager.BOLD );
		styleButtons.add( b );

		new Label( subContainer, SWT.NONE );
		b = new Button( subContainer, SWT.CHECK );
		b.setText( "Italic" );
		b.setData( PreferencesManager.ITALIC );
		styleButtons.add( b );

		new Label( subContainer, SWT.NONE );
		b = new Button( subContainer, SWT.CHECK );
		b.setText( "Underline" );
		b.setData( PreferencesManager.UNDERLINE );
		styleButtons.add( b );

		new Label( subContainer, SWT.NONE );
		Composite colorComposite = new Composite( subContainer, SWT.NONE );
		GridLayoutFactory.swtDefaults().numColumns( 2 ).margins( 0, 0 ).applyTo( colorComposite );

		new Label( colorComposite, SWT.NONE ).setText( "Foreground Color:" );
		final ColorSelector colorSelector = new ColorSelector( colorComposite );

		new Label( subContainer, SWT.NONE );
		b = new Button( subContainer, SWT.PUSH );
		b.setText( "Reset All the Settings for this Style" );
		b.setToolTipText( "Resets all the settings for the selected style" );
		b.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		b.addListener( SWT.Selection, new Listener() {
			@Override
			public void handleEvent( Event e ) {

				XmlRegionType xr = (XmlRegionType) ((IStructuredSelection) styleViewer.getSelection()).getFirstElement();
				PreferencesManager.INSTANCE.saveXmlRegionColor( xr, null );
				colorManager.updateColor( xr );

				PreferencesManager.INSTANCE.saveXmlRegionStyle( xr, PreferencesManager.BOLD, null );
				PreferencesManager.INSTANCE.saveXmlRegionStyle( xr, PreferencesManager.ITALIC, null );
				PreferencesManager.INSTANCE.saveXmlRegionStyle( xr, PreferencesManager.UNDERLINE, null );

				previewStyledText.notifyListeners( SWT.Modify, new Event());
				styleViewer.getCombo().notifyListeners( SWT.Selection, new Event());
			}
		});


		// React to selection changes
		Listener clickListener = new Listener() {
			@Override
			public void handleEvent( Event e ) {

				XmlRegionType xr = (XmlRegionType) ((IStructuredSelection) styleViewer.getSelection()).getFirstElement();
				Button b = (Button) e.widget;
				String name = (String) b.getData();
				PreferencesManager.INSTANCE.saveXmlRegionStyle( xr, name, b.getSelection());

				previewStyledText.notifyListeners( SWT.Modify, new Event());
			}
		};

		for( Button bb : styleButtons )
			bb.addListener( SWT.Selection, clickListener );

		styleViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent e ) {

				XmlRegionType xr = (XmlRegionType) ((IStructuredSelection) styleViewer.getSelection()).getFirstElement();
				Color c = colorManager.getColor( xr );
				colorSelector.setColorValue( c.getRGB());

				for( Button b : styleButtons ) {
					String name = (String) b.getData();
					b.setSelection( PreferencesManager.INSTANCE.getXmlRegionStyle( xr, name ));
				}
			}
		});

		colorSelector.addListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {

				if( ! ColorSelector.PROP_COLORCHANGE.equals( e.getProperty()))
					return;

				XmlRegionType xr = (XmlRegionType) ((IStructuredSelection) styleViewer.getSelection()).getFirstElement();
				RGB rgb = (RGB) e.getNewValue();
				PreferencesManager.INSTANCE.saveXmlRegionColor( xr, rgb );
				colorManager.updateColor( xr );

				previewStyledText.notifyListeners( SWT.Modify, new Event());
			}
		});

		// Initial selection
		styleViewer.getCombo().select( 0 );
		styleViewer.getCombo().notifyListeners( SWT.Selection, new Event());
	}


	/**
	 * Creates a section with a title and a content to populate.
	 * @param title the section's title
	 * @param columnCount the number of column to create
	 * @param sameWidth true if columns must have the same width
	 * @return a non-null composite, with a layout and a layout data
	 */
	private Group createSection( String title, int columnCount, boolean sameWidth ) {

		Group result = new Group( this, SWT.SHADOW_ETCHED_IN );
		GridLayoutFactory.swtDefaults().margins( 6, 10 ).numColumns( columnCount ).equalWidth( sameWidth ).applyTo( result );
		GridDataFactory.swtDefaults().align( SWT.FILL, SWT.BEGINNING ).grab( true, false ).indent( 0, 20 ).applyTo( result );
		result.setText( title );

		return result;
	}
}
