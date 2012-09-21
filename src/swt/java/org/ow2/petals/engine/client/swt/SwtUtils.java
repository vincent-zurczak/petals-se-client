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

package org.ow2.petals.engine.client.swt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegionAnalyzer;

/**
 * A set of utilities related to SWT.
 * @author Vincent Zurczak - Linagora
 */
public class SwtUtils {

	/**
	 * Computes style ranges from XML regions.
	 * @param regions an ordered list of XML regions
	 * @param colorManager a color manager
	 * @return an ordered list of style ranges for SWT styled text
	 */
	public static StyleRange[] computeStyleRanges( List<XmlRegion> regions, ColorCacheManager colorManager ) {

		List<StyleRange> styleRanges = new ArrayList<StyleRange> ();
		for( XmlRegion xr : regions ) {

			// Define the style properties
			StyleRange sr = new StyleRange();
			sr.foreground = colorManager.getColor( xr.getXmlRegionType());
			sr.underline = PreferencesManager.INSTANCE.getXmlRegionStyle( xr.getXmlRegionType(), PreferencesManager.UNDERLINE );

			boolean bold = PreferencesManager.INSTANCE.getXmlRegionStyle( xr.getXmlRegionType(), PreferencesManager.BOLD );
			boolean italic = PreferencesManager.INSTANCE.getXmlRegionStyle( xr.getXmlRegionType(), PreferencesManager.ITALIC );
			if( italic ) {
				if( bold )
					sr.fontStyle = SWT.BOLD | SWT.ITALIC;
				else
					sr.fontStyle = SWT.ITALIC;

			} else if( bold ) {
				sr.fontStyle = SWT.BOLD;
			}


			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add( sr );
		}

		StyleRange[] result = new StyleRange[ styleRanges.size()];
		return styleRanges.toArray( result );
	}


	/**
	 * Creates a XML viewer.
	 * @param parent the parent
	 * @param colorManager a color manager
	 * @param forceScrolling true to force scrolling, false to rely on preferences
	 * @return a configured instance of styled text
	 */
	public static StyledText createXmlViewer(
			Composite parent,
			final ColorCacheManager colorManager,
			boolean forceScrolling ) {

		// Each widget has its own analyzer
		final XmlRegionAnalyzer xmlRegionAnalyzer = new XmlRegionAnalyzer();
		int style = SWT.BORDER | SWT.MULTI;
		if( forceScrolling )
			style |= SWT.V_SCROLL | SWT.H_SCROLL;
		else
			style |= PreferencesManager.INSTANCE.wrapInsteadOfScrolling() ?  SWT.WRAP : SWT.V_SCROLL | SWT.H_SCROLL;

		// Create the text
		StyledText st = new StyledText( parent, style );
		st.setLayoutData( new GridData( GridData.FILL_BOTH ));
		st.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {

				StyledText st = (StyledText) e.widget;
				List<XmlRegion> regions = xmlRegionAnalyzer.analyzeXml( st.getText());
				StyleRange[] styleRanges = SwtUtils.computeStyleRanges( regions, colorManager );
				st.setStyleRanges( styleRanges );
			}
		});

		return st;
	}


	/**
	 * Clears the history and shows a progress bar.
	 * @param shell the parent shell
	 * @param olderThan see {@link Utils#clearHistory(int)}
	 * @param clientApp the client application
	 * @throws Exception
	 */
	public static void clearHistoryWithProgressBar( Shell shell, final int olderThan, ClientApplication clientApp ) {

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run( IProgressMonitor monitor )
			throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask( "Clearing the History...", IProgressMonitor.UNKNOWN );
					Utils.clearHistory( olderThan );

				} finally {
					monitor.done();
				}
			}
		};

		ProgressMonitorDialog dlg = new ProgressMonitorDialog( shell );
		dlg.setOpenOnRun( true );
		try {
			dlg.run( true, false, runnable );

		} catch( InvocationTargetException e ) {
			clientApp.log( null, e, Level.INFO );
			openErrorDialog( shell, "The history could not be cleared correctly." );

		} catch( InterruptedException e ) {
			// nothing
		}
	}


	/**
	 * @param originalData
	 * @param additionalStyle
	 * @return
	 */
	public static FontData[] getModifiedFontData( FontData[] originalData, int additionalStyle ) {

		FontData[] styleData = new FontData[ originalData.length ];
		for( int i=0; i<styleData.length; i++ ) {
			FontData base = originalData[ i ];
			styleData[ i ] = new FontData( base.getName(), base.getHeight(), base.getStyle() | additionalStyle );
		}

		return styleData;
	}


	/**
	 * Opens an error dialog.
	 * @param shell
	 * @param msg the message to display (not null)
	 */
	public static void openErrorDialog( Shell shell, String msg ) {
		MessageDialog.openError( shell, "Error", msg + "\nPlease, check the logs for more details." );
	}
}
