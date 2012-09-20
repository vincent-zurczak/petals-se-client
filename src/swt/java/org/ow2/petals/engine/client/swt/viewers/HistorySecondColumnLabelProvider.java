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
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * A label provider for the second column of the history viewer.
 * @author Vincent Zurczak - Linagora
 */
public class HistorySecondColumnLabelProvider extends ColumnLabelProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider
	 * #getText(java.lang.Object)
	 */
	@Override
	public String getText( Object o ) {

		String result = "";
		if( o instanceof File ) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis( ((File) o).lastModified());
			result = new SimpleDateFormat( "HH:mm:ss" ).format( calendar.getTime());
		}

		return result;
	}
}
