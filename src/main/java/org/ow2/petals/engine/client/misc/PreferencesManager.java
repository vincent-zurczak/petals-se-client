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

/**
 * The preferences manager.
 * @author Vincent Zurczak - Linagora
 */
public class PreferencesManager {

	public static final PreferencesManager INSTANCE = new PreferencesManager();

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
	public void setHistoryDirectory( File f ) {
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
	public void setDefaultTimeout( Long timeout ) {
		getPreferences().putLong( DEFAULT_TIMEOUT, timeout == null ? 3000 : timeout );
	}


	/**
	 * @return the preferences' package
	 */
	private Preferences getPreferences() {
		return Preferences.userNodeForPackage( getClass());
	}
}
