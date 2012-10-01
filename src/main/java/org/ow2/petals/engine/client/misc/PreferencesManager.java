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

package org.ow2.petals.engine.client.misc;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.RGB;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion.XmlRegionType;

/**
 * The preferences manager.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesManager {

	public final static PreferencesManager INSTANCE = new PreferencesManager();
	public final static String BOLD = "bold";	 	//$NON-NLS-N1
	public final static String ITALIC = "italic";  	//$NON-NLS-N1
	public final static String UNDERLINE = "underline";		 //$NON-NLS-N1

	private final static String HISTORY_DIR = "prefs.history.dir";	 //$NON-NLS-N1
	private final static String DEFAULT_TIMEOUT = "prefs.default.timeout";	 //$NON-NLS-N1
	private final static String WRAP_INSTEAD_OF_SCROLLING = "prefs.wrap.instead.of.scrolling";	 //$NON-NLS-N1



	/**
	 * Constructor.
	 */
	private PreferencesManager() {
		// nothing
	}


	/**
	 * @return the directory that contains the history (a temporary directory by default)
	 */
	public File getHistoryDirectory() {
		String s = getPreferences().get( HISTORY_DIR, null );
		return s != null ? new File( s ) : new File( System.getProperty( "java.io.tmpdir" ), "petals-esb-client-history" );
	}


	/**
	 * @return true if the history directory is the default one, false otherwise
	 */
	public boolean isDefaultHistoryDirectory() {
		return null == getPreferences().get( HISTORY_DIR, null );
	}


	/**
	 * @param f the directory that contains the history
	 */
	public void saveHistoryDirectory( File f ) {
		if( f == null )
			getPreferences().remove( HISTORY_DIR );
		else
			getPreferences().put( HISTORY_DIR, f.getAbsolutePath());
	}


	/**
	 * @return the default timeout (3000 if not overwritten)
	 */
	public int getDefaultTimeout() {
		return getPreferences().getInt( DEFAULT_TIMEOUT, 3000 );
	}


	/**
	 * @param timeout the default timeout
	 */
	public void saveDefaultTimeout( Integer timeout ) {
		if( timeout == null )
			getPreferences().remove( DEFAULT_TIMEOUT );
		else
			getPreferences().putInt( DEFAULT_TIMEOUT, timeout );
	}


	/**
	 * Gets the color for a given XML region.
	 * @param xr a XML region
	 * @return a non-null RGB
	 */
	public RGB getXmlRegionColor( XmlRegionType xr ) {

		RGB result = null;
		String colorKey = xr.toString() + ".color";

		// Look at the preferences
		String s = getPreferences().get( colorKey, null );
		if( s != null ) {
			String[] parts = s.split( "\\|" );
			if( parts.length > 2 ) {
				int red = Integer.valueOf( parts[ 0 ]);
				int green = Integer.valueOf( parts[ 1 ]);
				int blue = Integer.valueOf( parts[ 2 ]);
				result = new RGB( red, green, blue );
			}
		}

		// Default value?
		if( result == null ) {
			switch( xr ) {
				case ATTRIBUTE:
					result = new RGB( 127, 0, 171 );
					break;
				case ATTRIBUTE_VALUE:
					result = new RGB( 42, 58, 255 );
					break;
				case CDATA:
					result = new RGB( 210, 163, 61 );
					break;
				case COMMENT:
					result = new RGB( 100, 95, 213 );
					break;
				case MARKUP:
					result = new RGB( 63, 127, 127 );
					break;
				default:
					result = new RGB( 0, 0, 0 );
					break;
			}
		}

		return result;
	}


	/**
	 * Saves a color in the preferences.
	 * @param xr a XML region
	 * @param color a RGB value or null to restore the default value
	 */
	public void saveXmlRegionColor( XmlRegionType xr, RGB color ) {

		String colorKey = xr.toString() + ".color";
		if( color == null ) {
			getPreferences().remove( colorKey );
		} else {
			String s = color.red + "|" + color.green + "|" + color.blue;
			getPreferences().put( colorKey, s );
		}
	}


	/**
	 * @param xr a XML region
	 * @param styleName a style name (e.g. bold, italic)
	 * @return true if this style must be enabled, false otherwise
	 */
	public boolean getXmlRegionStyle( XmlRegionType xr, String styleName ) {
		String colorKey = xr.toString() + "." + styleName;
		return getPreferences().getBoolean( colorKey, false );
	}


	/**
	 * Saves a style in the preferences.
	 * @param xr a XML region
	 * @param styleName a style name (e.g. bold, italic)
	 * @param value the value to set (null to get back to default)
	 */
	public void saveXmlRegionStyle( XmlRegionType xr, String styleName, Boolean value ) {

		String colorKey = xr.toString() + "." + styleName;
		if( value == null )
			getPreferences().remove( colorKey );
		else
			getPreferences().putBoolean( colorKey, value );
	}


	/**
	 * @return true if styled texts should wrap text instead of displaying scroll bars
	 */
	public boolean wrapInsteadOfScrolling() {
		return getPreferences().getBoolean( WRAP_INSTEAD_OF_SCROLLING, false );
	}


	/**
	 * @param wrap true to wrap text in styled texts, false to show scroll bars instead
	 */
	public void saveWrapInsteadOfScrolling( boolean wrap ) {
		getPreferences().putBoolean( WRAP_INSTEAD_OF_SCROLLING, wrap );
	}


	/**
	 * @return the preferences' package
	 */
	private Preferences getPreferences() {
		return Preferences.userNodeForPackage( getClass());
	}
}
