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
import java.util.Locale;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.ow2.petals.engine.client.misc.Utils;

/**
 * A label provider for the first column of the history viewer.
 * @author Vincent Zurczak - Linagora
 */
public class HistoryFirstColumnLabelProvider extends ColumnLabelProvider {

	private final int type;
	private final Font boldFont;


	/**
	 * Constructor.
	 * @param type 0 for the interface, 1 for the service, 2 for the end-point
	 * @param boldFont a shared font to display things in bold
	 */
	public HistoryFirstColumnLabelProvider( int type, Font boldFont ) {
		this.type = type;
		this.boldFont = boldFont;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider
	 * #getText(java.lang.Object)
	 */
	@Override
	public String getText( Object o ) {

		String result = "";
		if( o instanceof Long && this.type == 0  ) {
			result = formatDateStr((Long) o);

		} else if( o instanceof File ) {
			String name = ((File) o).getName();
			if( name.toLowerCase().matches( ".*--\\d{4}-\\d{2}-\\d{2}--\\d{2}-\\d{2}-\\d{2}\\.txt" ))
				name = name.substring( 0, name.length() - 26 );

			if( this.type == 0 ) {
				int index = name.indexOf( "==s=" );
				if( index == -1 )
					index = name.indexOf( "==e=" );

				if( index > 0 )
					result = name.substring( 2, index );

			} else if( this.type == 1 ) {
				int start = name.indexOf( "==s=" );
				if( start > 0 ) {
					int end = name.indexOf( "==e=" );
					if( end > start )
						result = name.substring( start + 4, end );
				}

			} else if( this.type == 2 ) {
				int index = result.indexOf( "==e=" );
				if( index > 0 )
					result = name.substring( index + 4 );
			}

			if( Utils.isEmptyString( result ))
				result = "-";
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider
	 * #getFont(java.lang.Object)
	 */
	@Override
	public Font getFont( Object element ) {
		return this.type == 0 && element instanceof Long ? this.boldFont : super.getFont( element );
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

		return new SimpleDateFormat( "MMMM, EEEE d" + suffix + " yyyy", Locale.ENGLISH ).format( calendar.getTime());
    }
}
