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

import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider for message properties.
 * @author Vincent Zurczak - Linagora
 */
public class MessagePropertiesLabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider
	 * #addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener arg0) {
		// nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider
	 * #dispose()
	 */
	@Override
	public void dispose() {
		// nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider
	 * #isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider
	 * #removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider
	 * #getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider
	 * #getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText( Object o, int index ) {

		String result = "";
		if( index == 0 )
			result = (String) ((Map.Entry<?,?>) o).getKey();
		else if( index == 1 )
			result = (String) ((Map.Entry<?,?>) o).getValue();

		return result;
	}
}
