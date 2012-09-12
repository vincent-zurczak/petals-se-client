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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A content provider for the history tab.
 * <p>
 * The input must be a directory containing ".msg" files.<br />
 * Children elements are of type Long and represent a day.<br />
 * Sub-children elements are message files.
 * </p>
 *
 * @author Vincent Zurczak - Linagora
 */
public class HistoryContentProvider implements ITreeContentProvider {

	Map<Long,List<File>> dateToFiles = new TreeMap<Long,List<File>> ();


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider
	 * #dispose()
	 */
	@Override
	public void dispose() {
		// nothing
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider
	 * #inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged( Viewer viewer, Object oldInput, Object newInput) {
		this.dateToFiles.clear();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren( Object o ) {

		Object[] result = new Object[ 0 ];
		if( o instanceof Long ) {
			List<File> files = this.dateToFiles.get( o );
			if( files != null )
				result = files.toArray( new Object[ files.size()]);
		}

		return result;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements( Object o ) {

		Object[] result = new Object[ 0 ];
		if( o instanceof File ) {
			File[] files = ((File) o).listFiles( new FileFilter() {
				@Override
				public boolean accept( File f ) {
					return f.isFile() && f.getName().toLowerCase().endsWith( ".msg" );
				}
			});

			if( files != null ) {
				for( File f : files ) {
					Long day = findLastModificationDay( f );
					List<File> associatedFiles = this.dateToFiles.get( day );
					if( associatedFiles == null ) {
						associatedFiles = new ArrayList<File> ();
						this.dateToFiles.put( day, associatedFiles );
					}

					associatedFiles.add( f );
				}
			}

			result = this.dateToFiles.keySet().toArray();
		}

		return result;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #getParent(java.lang.Object)
	 */
	@Override
	public Object getParent( Object o ) {
		return o instanceof File ? findLastModificationDay((File) o) : null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider
	 * #hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren( Object o ) {

		boolean result = false;
		if( o instanceof Long ) {
			List<File> files = this.dateToFiles.get( o );
			result = files != null && files.size() > 0;
		}

		return result;
	}


	/**
	 * Finds a unique date of the day of the last modification.
	 * @param f a history file
	 * @return a date, as a long
	 */
	private Long findLastModificationDay( File f ) {

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis( f.lastModified());
		calendar.set( Calendar.HOUR, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTimeInMillis();
	}
}
