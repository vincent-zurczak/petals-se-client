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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion;

/**
 * A set of utilities related to SWT.
 * @author Vincent Zurczak - Linagora
 */
public class SwtUtils {

	/**
	 * Loads an image from the resources.
	 * @param relativePath the relative path of the image (class loader search)
	 * @return an image, or null if it could not be created
	 */
	public static Image loadImage( String relativePath ) {

		Image result = null;
		InputStream in = null;
		try {
			in = SwtUtils.class.getResourceAsStream( relativePath );
			if( in != null ) {
				ImageData imgData = new ImageData( in );
				result = new Image( Display.getDefault(), imgData );
			}

		} catch( Exception e ) {
			e.printStackTrace();

		} finally {
			try {
				if( in != null )
					in.close();
			} catch (IOException e) {
				// nothing
			}
		}

		return result;
	}


	/**
	 * Computes style ranges from XML regions.
	 * @param regions an ordered list of XML regions
	 * @param colorManager
	 * @return an ordered list of style ranges for SWT styled text
	 */
	public static StyleRange[] computeStyleRanges( List<XmlRegion> regions, ColorCacheManager colorManager ) {

		List<StyleRange> styleRanges = new ArrayList<StyleRange> ();
		for( XmlRegion xr : regions ) {

			// The style itself depends on the region type
			// In this example, we use colors from the system
			StyleRange sr = new StyleRange();
			sr.foreground = colorManager.getColor( xr.getXmlRegionType());

			switch( xr.getXmlRegionType()) {
		        case MARKUP:
		        	sr.fontStyle = SWT.BOLD;
		        	break;
		        default: break;
		    }

			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add( sr );
		}

		StyleRange[] result = new StyleRange[ styleRanges.size()];
		return styleRanges.toArray( result );
	}
}
