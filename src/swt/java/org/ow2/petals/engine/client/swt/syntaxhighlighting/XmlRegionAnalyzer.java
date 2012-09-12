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

import java.util.ArrayList;
import java.util.List;

import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion.XmlRegionType;

/**
 * A class that builds style ranges from a XML input.
 * @author Vincent Zurczak - Linagora
 */
public class XmlRegionAnalyzer {

	private int offset;


	/**
	 * Analyzes a XML document.
	 * @param xml the XML text (may be an invalid XML document)
	 * @return a non-null list of XML positions
	 */
	public List<XmlRegion> analyzeXml( String xml ) {

		this.offset = 0;
		List<XmlRegion> positions = new ArrayList<XmlRegion> ();

		// White Spaces
		analyzeWhitespaces( xml, positions );

		// Start the analysis
		if( this.offset < xml.length()) {
			if( xml.charAt( this.offset ) != '<' )
				positions.add( new XmlRegion( XmlRegionType.UNEXPECTED, this.offset, xml.length()));
			else
				analyzeInstruction( xml, positions );
		}

		return positions;
	}


	/**
	 * Tries to analyze a XML instruction.
	 * <p>
	 * A XML instruction can be followed by a comment or a mark-up.<br />
	 * If an instruction is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeInstruction( String xml, List<XmlRegion> positions ) {

		// White Spaces
		analyzeWhitespaces( xml, positions );

		// Find and process an instruction
		int newPos = this.offset;
		if( newPos < xml.length()
				&& xml.charAt( newPos ) == '<'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '?' ) {

			while( ++ newPos < xml.length()
					&& xml.charAt( newPos ) != '>' )
				newPos = xml.indexOf( '?', newPos );

			if( xml.charAt( newPos ) == '>' )
				positions.add( new XmlRegion( XmlRegionType.INSTRUCTION, this.offset, newPos + 1 ));

			this.offset = newPos + 1;
		}

		// Process the possible followers
		analyzeComment( xml, positions );
	}


	/**
	 * Tries to analyze a XML comment.
	 * <p>
	 * A XML comment can be followed by a comment or a mark-up.<br />
	 * If a comment is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeComment( String xml, List<XmlRegion> positions ) {

		// White spaces
		analyzeWhitespaces( xml, positions );

		// Find and process a comment
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		if( xml.charAt( newPos ) == '<'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '!'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '-'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '-' ) {

			int seq = 0;
			while( seq != 3
					&& ++ newPos < xml.length()) {
				char c = xml.charAt( newPos );
				seq = c == '-' && seq < 2 || c == '>' && seq == 2 ? seq + 1 : 0;
			}

			if( seq == 3 )
				newPos ++;

			positions.add( new XmlRegion( XmlRegionType.COMMENT, this.offset, newPos ));
			this.offset = newPos;
			analyzeComment( xml, positions );
		}

		// Process the possible followers
		analyzeMarkup( xml, positions );
	}


	/**
	 * Tries to analyze a XML mark-up.
	 * <p>
	 * A XML mark-up can be followed by a comment, a mark-up, a CDATA section or a mark-up value.<br />
	 * If a mark-up is found, a XML position is stored and the offset is updated.
	 * </p>
	 * <p>
	 * A XML mark-up can contain zero or several attributes.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeMarkup( String xml, List<XmlRegion> positions ) {

		// White spaces
		analyzeWhitespaces( xml, positions );

		// Find and process a mark-up
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		// "<..."
		boolean processFollowers = false;
		if( xml.charAt( newPos ) == '<' ) {

			// Do not process a CData section or a comment as a mark-up
			if( newPos + 1 < xml.length()
					&& xml.charAt( newPos + 1 ) == '!' )
				return;

			// We are in a mark-up, followers will have to be analyzed
			processFollowers = true;

			// Mark-up name
			char c = '!';
			while( newPos < xml.length()
					&& (c = xml.charAt( newPos)) != '>'
					&& ! Character.isWhitespace( c ))
				newPos ++;

			if( c == '>' )
				newPos ++;

			positions.add( new XmlRegion( XmlRegionType.MARKUP, this.offset, newPos ));
			this.offset = newPos;

			// Attributes?
			if( c != '>' )
				analyzeAttribute( xml, positions );
		}

		// "/>"
		else if( xml.charAt( newPos ) == '/'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '>' ) {

			processFollowers = true;
			positions.add( new XmlRegion( XmlRegionType.MARKUP, this.offset, ++ newPos ));
			this.offset = newPos;
		}

		// "attributes... >"
		else if( xml.charAt( newPos ) == '>' ) {

			processFollowers = true;
			positions.add( new XmlRegion( XmlRegionType.MARKUP, this.offset, ++ newPos ));
			this.offset = newPos;
		}

		// Process the possible followers
		if( processFollowers
				&& this.offset < xml.length()) {

			analyzeComment( xml, positions );
			analyzeMarkupValue( xml, positions );
			analyzeWhitespaces( xml, positions );
			analyzeCData( xml, positions );
			analyzeMarkup( xml, positions );
		}
	}


	/**
	 * Tries to analyze a XML attribute.
	 * <p>
	 * A XML attribute must be followed by an attribute value.<br />
	 * If an attribute is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeAttribute( String xml, List<XmlRegion> positions ) {

		// White spaces
		analyzeWhitespaces( xml, positions );

		// Find and process an attribute
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		char c;
		while( newPos < xml.length()
				&& (c = xml.charAt( newPos )) != '='
				&& c != '/'
				&& c != '>'
				&& ! Character.isWhitespace( c ))
			newPos ++;

		// Found one?
		if( newPos != this.offset ) {
			positions.add( new XmlRegion( XmlRegionType.ATTRIBUTE, this.offset, newPos ));
			this.offset = newPos;

			// Process the possible followers
			analyzeAttributeValue( xml, positions );
		}
	}


	/**
	 * Tries to analyze a mark-up's value.
	 * <p>
	 * A XML mark-up's value can be followed by a comment or a (closing) mark-up.<br />
	 * If a mark-up value is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeMarkupValue( String xml, List<XmlRegion> positions ) {

		// Do not process white spaces here!
		// Find and process a mark-up's value
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		while( newPos < xml.length()
					&& xml.charAt( newPos ) != '<' )
			newPos ++;

		// We read something and this something is not only made up of white spaces
		if( this.offset != newPos ) {

			// We must here repair the list if the previous position is made up of white spaces
			XmlRegion xr = positions.get( positions.size() - 1 );
			int start = this.offset;
			if( xr.getXmlRegionType() == XmlRegionType.WHITESPACE ) {
				start = xr.getStart();
				positions.remove( xr );
			}

			positions.add( new XmlRegion( XmlRegionType.MARKUP_VALUE, start, newPos ));
			this.offset = newPos;

			// Process the possible followers
			analyzeComment( xml, positions );
			analyzeMarkup( xml, positions );
		}
	}


	/**
	 * Tries to analyze a XML attribute's value.
	 * <p>
	 * A XML attribute's value can be followed by an attribute or a (closing) mark-up.<br />
	 * If an attribute's value is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeAttributeValue( String xml, List<XmlRegion> positions ) {

		// White spaces
		analyzeWhitespaces( xml, positions );

		// Find and process an attribute's value
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		if( xml.charAt( newPos ) == '=' ) {
			analyzeWhitespaces( xml, positions );

			int cpt = 0;
			char previous = '!';
			while( ++ newPos < xml.length()) {
				char c = xml.charAt( newPos );
				if( previous != '\\' && c == '"' )
					cpt ++;

				previous = c;
				if( cpt == 2 ) {
					newPos ++;
					break;
				}
			}

			positions.add( new XmlRegion( XmlRegionType.ATTRIBUTE_VALUE, this.offset, newPos ));
			this.offset = newPos;
		}

		// Process the possible followers
		analyzeAttribute( xml, positions );
		analyzeMarkup( xml, positions );
	}


	/**
	 * Tries to analyze a CDATA section.
	 * <p>
	 * A CDATA section can be followed by an comment or a (closing) mark-up.<br />
	 * If a CDATA section is found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeCData( String xml, List<XmlRegion> positions ) {

		// White spaces
		analyzeWhitespaces( xml, positions );

		// Find and process an attribute's value
		int newPos = this.offset;
		if( newPos >= xml.length())
			return;

		if( xml.charAt( newPos ) == '<'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '!'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '['
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == 'C'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == 'D'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == 'A'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == 'T'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == 'A'
				&& ++ newPos < xml.length()
				&& xml.charAt( newPos ) == '[') {

			int cpt = 0;
			while( ++ newPos < xml.length()) {
				char c = xml.charAt( newPos );
				if( cpt < 2 && c == ']'
						|| cpt == 2 && c == '>' )
					cpt ++;
				else
					cpt = 0;

				if( cpt == 3 ) {
					newPos ++;
					break;
				}
			}

			positions.add( new XmlRegion( XmlRegionType.CDATA, this.offset, newPos ));
			this.offset = newPos;
		}

		// Process the possible followers
		analyzeComment( xml, positions );
		analyzeMarkup( xml, positions );
	}


	/**
	 * Tries to analyze white spaces.
	 * <p>
	 * If white spaces are found, a XML position is stored and the offset is updated.
	 * </p>
	 *
	 * @param xml the XML text
	 * @param positions the positions already found
	 */
	void analyzeWhitespaces( String xml, List<XmlRegion> positions ) {

		int i = this.offset;
		while( i < xml.length()
				&& Character.isWhitespace( xml.charAt( i )))
			i++;

		if( i != this.offset ) {
			positions.add( new XmlRegion( XmlRegionType.WHITESPACE, this.offset, i ));
			this.offset = i;
		}
	}
}
