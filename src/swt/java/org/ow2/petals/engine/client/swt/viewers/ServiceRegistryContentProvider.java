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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.ItfBean;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.SrvBean;

/**
 * A content provider for the service registry.
 * @author Vincent Zurczak - Linagora
 */
public class ServiceRegistryContentProvider implements ITreeContentProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren( Object element ) {
		return element instanceof SrvBean || element instanceof ItfBean;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements( Object inputElement ) {
		return ((Map<?,?>) inputElement).values().toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren( Object parentElement ) {

		Object[] result = null;
		if( parentElement instanceof ItfBean )
			result = ((ItfBean) parentElement).srvNameToService.values().toArray();
		else if( parentElement instanceof SrvBean )
			result = ((SrvBean) parentElement).endpoints.toArray();

		if( result == null )
			result = new Object[ 0 ];

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider
	 * #dispose()
	 */
	@Override
	public void dispose() {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider
	 * #inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getParent(java.lang.Object)
	 */
	@Override
	public Object getParent( Object elt ) {
		return null;
	}
}
