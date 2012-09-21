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
import java.lang.reflect.Field;
import java.util.logging.Level;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ImageIds {

	public static final String PETALS_SPLASH = "/petals_splash.png";
	public static final String SEND_64x64 = "/send_64x64.png";

	public static final String ERROR_16x16 = "/error_16x16.gif";
	public static final String WARNING_16x16 = "/warning_16x16.gif";
	public static final String INFO_16x16 = "/info_16x16.gif";

	public static final String CONTRACT_16x16 = "/contract_16x16.gif";
	public static final String SERVICE_16x16 = "/service_16x16.gif";
	public static final String ENDPOINT_16x16 = "/endpoint_16x16.gif";
	public static final String VIEW_MENU_16x16 = "/view_menu_16x16.gif";
	public static final String SEARCH_16x16 = "/search_16x16.png";

	public static final String PETALS_16x16 = "/petals_16x16.png";
	public static final String PETALS_32x32 = "/petals_32x32.png";
	public static final String PETALS_48x48 = "/petals_48x48.png";


	/**
	 * Loads all the images referenced by the class constants, into the JFace Resource manager.
	 */
	public static void loadImageInJFaceResources( ClientApplication clientapp ) {

		for( Field f : ImageIds.class.getDeclaredFields()) {
			try {
				String path = (String) f.get( String.class );
				if( path != null ) {
					Image img = loadImage( path, clientapp );
					if( img != null )
						JFaceResources.getImageRegistry().put( path, img );
				}

			} catch( IllegalArgumentException e ) {
				clientapp.log( null, e, Level.INFO );

			} catch( IllegalAccessException e ) {
				clientapp.log( null, e, Level.INFO );
			}
		}
	}


	public static void main(String[] args) {
		loadImageInJFaceResources( null );
	}


	/**
	 * Loads an image from the resources.
	 * @param relativePath the relative path of the image (class loader search)
	 * @param clientApp the client application
	 * @return an image, or null if it could not be created
	 */
	private static Image loadImage( String relativePath, ClientApplication clientApp ) {

		Image result = null;
		InputStream in = null;
		try {
			in = SwtUtils.class.getResourceAsStream( relativePath );
			if( in == null )
				throw new NullPointerException( "No image could be found at " + relativePath );

			ImageData imgData = new ImageData( in );
			result = new Image( Display.getDefault(), imgData );

		} catch( Exception e ) {
			clientApp.log( null, e, Level.WARNING );

		} finally {
			try {
				if( in != null )
					in.close();
			} catch( IOException e ) {
				// nothing
			}
		}

		return result;
	}

}
