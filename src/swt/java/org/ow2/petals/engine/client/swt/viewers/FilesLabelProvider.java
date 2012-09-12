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

package org.ow2.petals.engine.client.swt.viewers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * A label provider for file viewers.
 * @author Vincent Zurczak - Linagora
 */
public class FilesLabelProvider extends LabelProvider {

	private final Map<String,Image> registry = new HashMap<String,Image> ();


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider
	 * #getText(java.lang.Object)
	 */
	@Override
	public String getText( Object element ) {

		String result = "";
		if( element instanceof File ) {
			File f = (File) element;
			result = f.getName() + " - " + f.getAbsolutePath();
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider
	 * #getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {

		Image result = null;
		if( element instanceof File ) {
			String ext = ((File) element).getName();
			int index = ext.lastIndexOf( '.' );
			if( index != -1 )
				ext = ext.substring( index );

			if( ext.length() > 0 ) {
				result = this.registry.get( ext );
				if( result == null ) {
					Program p = Program.findProgram( ext );
					result = new Image( Display.getDefault(), p.getImageData());
					this.registry.put( ext, result );
				}
			}
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider
	 * #dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		for( Image img : this.registry.values()) {
			if( ! img.isDisposed())
				img.dispose();
		}

		this.registry.clear();
	}
}
