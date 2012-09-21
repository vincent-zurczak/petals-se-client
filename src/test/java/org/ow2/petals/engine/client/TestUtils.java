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

package org.ow2.petals.engine.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.engine.client.misc.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utilities for the tests.
 * @author Vincent Zurczak - Linagora
 */
public class TestUtils {

    /**
     * Builds a document from a URL.
     * @param url the URL to parse
     * @return the document or null if it could not be loaded
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document buildDocument( URL url )
    throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
        db.setNamespaceAware( true );

        InputStream in = null;
        try {
	        in = url.openStream();
	        Document doc = db.newDocumentBuilder().parse( in );
	        return doc;

        } finally {
        	Utils.closeStreamQuietly( in );
        }
    }
}
