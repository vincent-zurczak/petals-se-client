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

package org.ow2.petals.engine.client.swt.syntaxhighlighting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.ow2.petals.engine.client.misc.PreferencesManager;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion.XmlRegionType;

/**
 * A class that manages color resources.
 * @author Vincent Zurczak - Linagora
 */
public class ColorCacheManager {

	private final Map<XmlRegionType,Color> regionTypeTocolor;
	private final Map<String,XmlRegionType> prefToXmlRegionType;


	/**
	 * Constructor.
	 */
	public ColorCacheManager() {

		this.regionTypeTocolor = new ConcurrentHashMap<XmlRegion.XmlRegionType,Color> ();
		this.prefToXmlRegionType = new HashMap<String, XmlRegion.XmlRegionType> ();

		this.prefToXmlRegionType.put( PreferencesManager.COLOR_COMMENT, XmlRegionType.COMMENT );
		this.prefToXmlRegionType.put( PreferencesManager.COLOR_MARKUP, XmlRegionType.MARKUP );
		this.prefToXmlRegionType.put( PreferencesManager.COLOR_ATTRIBUTE, XmlRegionType.ATTRIBUTE );
		this.prefToXmlRegionType.put( PreferencesManager.COLOR_ATTRIBUTE_VALUE, XmlRegionType.ATTRIBUTE_VALUE );
		this.prefToXmlRegionType.put( PreferencesManager.COLOR_CDATA, XmlRegionType.CDATA );
		this.prefToXmlRegionType.put( PreferencesManager.COLOR_INSTRUCTION, XmlRegionType.INSTRUCTION );

		for( String colorKey : this.prefToXmlRegionType.keySet())
			updateColor( colorKey );
	}


	/**
	 * Updates a color in the cache.
	 */
	public void updateColor( String colorKey ) {

		XmlRegionType xrt = this.prefToXmlRegionType.get( colorKey );
		RGB rgb = PreferencesManager.INSTANCE.getColor( colorKey );
		if( xrt != null && rgb != null ) {
			Color newColor = new Color( Display.getDefault(), rgb );
			Color previousColor = this.regionTypeTocolor.put( xrt, newColor );
			if( previousColor != null )
				previousColor.dispose();
		}
	}


	/**
	 * Disposes all the resources.
	 */
	public void dispose() {

		for( Color c : this.regionTypeTocolor.values()) {
			if( ! c.isDisposed())
				c.dispose();
		}

		this.regionTypeTocolor.clear();
	}


	/**
	 * @param xrt a XML region type
	 * @return a color (can be null)
	 */
	public Color getColor( XmlRegionType xrt ) {
		return this.regionTypeTocolor.get( xrt );
	}


	/**
	 * @param xrt a XML region type
	 * @return a color (can be null)
	 */
	public Color getColor( String colorKey ) {
		XmlRegionType xrt = this.prefToXmlRegionType.get( colorKey );
		return xrt != null ? this.regionTypeTocolor.get( xrt ) : null;
	}
}
