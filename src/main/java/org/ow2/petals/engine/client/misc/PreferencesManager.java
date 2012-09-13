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

/**
 * The preferences manager.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesManager {

	public static final PreferencesManager INSTANCE = new PreferencesManager();

	public final static String COLOR_COMMENT = "prefs.color.comment";
	public final static String COLOR_MARKUP = "prefs.color.markup";
	public final static String COLOR_ATTRIBUTE = "prefs.color.attribute";
	public final static String COLOR_INSTRUCTION = "prefs.color.instruction";
	public final static String COLOR_CDATA = "prefs.color.cdata";
	public final static String COLOR_ATTRIBUTE_VALUE = "prefs.color.attribute-value";

	private final static String HISTORY_DIR = "prefs.history.dir";
	private final static String DEFAULT_TIMEOUT = "prefs.default.timeout";



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
	 * @param f the directory that contains the history
	 */
	public void saveHistoryDirectory( File f ) {
		getPreferences().put( HISTORY_DIR, f != null ? f.getAbsolutePath() : null );
	}


	/**
	 * @return the default timeout (3000 if not overwritten)
	 */
	public long getDefaultTimeout() {
		return getPreferences().getLong( DEFAULT_TIMEOUT, 3000 );
	}


	/**
	 * @param timeout the default timeout
	 */
	public void saveDefaultTimeout( Long timeout ) {
		if( timeout == null )
			getPreferences().remove( DEFAULT_TIMEOUT );
		else
			getPreferences().putLong( DEFAULT_TIMEOUT, timeout );
	}


	/**
	 * Gets the color for a given XML region.
	 * @param colorKey the key of the color (class constants)
	 * @return a non-null RGB
	 */
	public RGB getColor( String colorKey ) {

		RGB result = null;

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
			if( COLOR_COMMENT.equals( colorKey ))
				result = new RGB( 100, 95, 213 );
			else if( COLOR_MARKUP.equals( colorKey ))
				result = new RGB( 63, 127, 127 );
			else if( COLOR_ATTRIBUTE.equals( colorKey ))
				result = new RGB( 127, 0, 171 );
			else if( COLOR_ATTRIBUTE_VALUE.equals( colorKey ))
				result = new RGB( 42, 58, 255 );
			else if( COLOR_CDATA.equals( colorKey ))
				result = new RGB( 210, 163, 61 );
			else
				result = new RGB( 0, 0, 0 );
		}

		return result;
	}


	/**
	 * Saves a color in the preferences.
	 * @param colorKey the key of the color (class constants)
	 * @param color a RGB value or null to restore the default value
	 */
	public void saveColor( String colorKey, RGB color ) {

		if( color == null ) {
			getPreferences().remove( colorKey );
		} else {
			String s = color.red + "|" + color.green + "|" + color.blue;
			getPreferences().put( colorKey, s );
		}
	}


	/**
	 * @return the preferences' package
	 */
	private Preferences getPreferences() {
		return Preferences.userNodeForPackage( getClass());
	}
}
