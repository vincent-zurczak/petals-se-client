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
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider for message properties.
 * @author Vincent Zurczak - Linagora
 */
public class HistoryLabelProvider implements ITableLabelProvider {

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
		if( o instanceof Long ) {
			if( index == 0 )
				result = formatDateStr((Long) o);

		} else if( o instanceof File ) {
			if( index == 0 )
				result = "";
			else if( index == 1 )
				result = formatHourStr(((File) o).lastModified());
		}

		return result;
	}


	/**
	 * @param dateInMilliSeconds the date, in milliseconds
	 * @return the formatted date, as a string
	 */
	private String formatDateStr( Long dateInMilliSeconds ) {

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis( dateInMilliSeconds );
		String suffix = "'th'";

		switch ( calendar.get( Calendar.DAY_OF_MONTH )) {
		    case 1:
		    case 21:
		    case 31:
		    	suffix = "'st'";
		    	break;

		    case 2:
		    case 22:
		    	suffix = "'nd'";
		    	break;

		    case 3:
		    case 23:
		    	suffix = "'rd'";
		    	break;
		}

		return new SimpleDateFormat( "MMM, d" + suffix + " yyyy" ).format( calendar );
    }


	/**
	 * @param dateInMilliSeconds the date, in milliseconds
	 * @return the formatted hour, as a string
	 */
	private String formatHourStr( Long dateInMilliSeconds ) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis( dateInMilliSeconds );
		return new SimpleDateFormat( "HH:mm:ss" ).format( calendar );
	}
}
