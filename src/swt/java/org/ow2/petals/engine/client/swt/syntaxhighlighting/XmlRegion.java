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

package org.ow2.petals.engine.client.swt.syntaxhighlighting;

/**
 * A XML region, with a type, a start position (included) and an end position (excluded).
 * <p>
 * A XML region is limited in the range [start, end[
 * </p>
 *
 * @author Vincent Zurczak - Linagora
 */
public class XmlRegion {

	public enum XmlRegionType {
		INSTRUCTION,
		COMMENT,
		CDATA,
		MARKUP,
		ATTRIBUTE,
		MARKUP_VALUE,
		ATTRIBUTE_VALUE,
		WHITESPACE,
		UNEXPECTED;
	}

	private final XmlRegionType xmlRegionType;
	private final int start;
	private int end;


	/**
	 * Constructor.
	 * @param xmlRegionType
	 * @param start
	 */
	public XmlRegion( XmlRegionType xmlRegionType, int start ) {
		this.xmlRegionType = xmlRegionType;
		this.start = start;
	}


	/**
	 * Constructor.
	 * @param xmlRegionType
	 * @param start
	 * @param end
	 */
	public XmlRegion( XmlRegionType xmlRegionType, int start, int end ) {
		this( xmlRegionType, start );
		this.end = end;
	}


	/**
	 * @return the end
	 */
	public int getEnd() {
		return this.end;
	}


	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}


	/**
	 * @return the xmlRegionType
	 */
	public XmlRegionType getXmlRegionType() {
		return this.xmlRegionType;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return this.start;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object
	 * #toString()
	 */
	@Override
	public String toString() {
		return this.xmlRegionType + " [" + this.start + ", " + this.end + "[";
	}
}
