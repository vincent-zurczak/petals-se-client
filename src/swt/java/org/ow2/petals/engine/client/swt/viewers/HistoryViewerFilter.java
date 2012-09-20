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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.ow2.petals.engine.client.misc.Utils;

/**
 * A viewer filter for the history.
 * @author Vincent Zurczak - Linagora
 */
public class HistoryViewerFilter extends ViewerFilter {

	private String searchText;


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter
	 * #select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( Viewer viewer, Object parentElement, Object element ) {

		boolean result = true;
		if( element instanceof File
				&& ! Utils.isEmptyString( this.searchText )) {
			result = ((File) element).getName().toLowerCase().contains( this.searchText.toLowerCase());
		}

		return result;
	}


	/**
	 * @param searchText the search text to set
	 */
	public void setSearchText( String searchText ) {
		this.searchText = searchText;
	}
}
