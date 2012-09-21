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

package org.ow2.petals.engine.client.misc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.easywsdl.schema.api.Import;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaWriter;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Types;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Vincent Zurczak - Linagora
 */
public class UtilsTests {

    private static WSDLReader wsdlReader;
    private static SchemaFactory jdkFactory;


    /**
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
       wsdlReader = WSDLFactory.newInstance().newWSDLReader();
       jdkFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
    }


    /**
     * Tests conversions between strings and sources.
     */
    @Test
    public void testConversions() throws Exception {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = getClass().getClassLoader().getResourceAsStream( "./simpleWSDL/tuxDroid.wsdl" );
        Utils.copyStream( is, os );
        Utils.closeStreamQuietly( is );

        String doc = os.toString();
        Source source = Utils.createSource( doc );
        String doc2 = Utils.createString( source );

        Assert.assertEquals( doc, doc2 );
    }


    /**
     * Tests the generation of XML from a WSDL operation.
     * @throws Exception
     */
    @Test
    public void testXmlSkeletonGeneration1() throws Exception {
        testXmlSkeletonGeneration(
                "simpleWSDL/tuxDroid.wsdl",
                new QName( "http://tuxdroid.ebmwebsourcing.com/", "speak" ),
                new QName( "http://tuxdroid.ebmwebsourcing.com/", "TuxDroidPortType" ));
    }


    /**
     * Tests the generation of XML from a WSDL operation.
     * @throws Exception
     */
    @Test
    public void testXmlSkeletonGeneration2() throws Exception {
        testXmlSkeletonGeneration(
                "simpleWSDL2/AWSECommerceService.wsdl",
                new QName( "http://webservices.amazon.com/AWSECommerceService/2008-10-06", "ItemSearch" ),
                new QName( "http://webservices.amazon.com/AWSECommerceService/2008-10-06", "AWSECommerceServicePortType" ));
    }


    /**
     * @param classLoaderResource
     * @param operationName
     * @param interfaceName
     * @throws Exception
     */
    private void testXmlSkeletonGeneration( String classLoaderResource, QName operationName, QName interfaceName )
    throws Exception {

        // Generate the XML skeleton
        URL url = getClass().getResource( "/" + classLoaderResource );
        Description desc = wsdlReader.read( url );

        // Prepare the validation against the XML schemas
        Collection<File> schemaFiles = null;
        try {
            schemaFiles = storeWsdlSchemas( desc.getTypes());

            String xml = Utils.generateXmlSkeleton( desc, interfaceName, operationName, false );
            Document doc = Utils.buildDocument( xml );
            if( ! isValidAgainstXmlSchemas( schemaFiles, doc ))
                throw new Exception( "The generated XML document could not be validated against the XML schemas (mandatory elements only)." );

            xml = Utils.generateXmlSkeleton( desc, interfaceName, operationName, true );
            doc = Utils.buildDocument( xml );
            if( ! isValidAgainstXmlSchemas( schemaFiles, doc ))
                throw new Exception( "The generated XML document could not be validated against the XML schemas (optional elements too)." );

        } finally {
            deleteTamporaryFiles( schemaFiles );
        }
    }


    /**
     * Serializes all the WSDL's schemas, updating the imports when necessary.
     *
     * @param types the WSDL type element
     * @return the list of schema files
     * @throws Exception if something went wrong
     */
    private Collection<File> storeWsdlSchemas( Types types ) throws Exception {

        Map<String,File> nsToTargetFile = new HashMap<String,File> ();

        try {
            org.ow2.easywsdl.schema.SchemaFactory factory = org.ow2.easywsdl.schema.SchemaFactory.newInstance();
            SchemaWriter writer = factory.newSchemaWriter();

            // Associate every schema with a file
            for( Schema schema : types.getSchemas()) {
                String ns = schema.getTargetNamespace();
                File f = File.createTempFile( "petalsValidation_", ".xsd" );
                f.deleteOnExit();
                nsToTargetFile.put( ns, f );
            }

            // Update all the imports in the schema to reference the right file
            // And serialize it
            FileOutputStream fo = null;
            for( Schema schema : types.getSchemas()) {

                // Update the import locations
                for( Import imp_ : schema.getImports()) {
                    File f = nsToTargetFile.get( imp_.getNamespaceURI());
                    if( f != null ) {
                        try {
                            imp_.setLocationURI( new URI( f.getName()));

                        } catch( URISyntaxException e ) {
                            // should not happen
                            e.printStackTrace();
                            // otherwise, we won't miss it
                        }
                    } else
                        throw new IOException( "The schema for (" + imp_.getNamespaceURI() + ") could not be resolved." );
                }

                // Write the schemas on the disk
                File f = nsToTargetFile.get( schema.getTargetNamespace());
                try {
                    fo = new FileOutputStream( f, false );
                    writer.writeSchema( schema, fo );

                } catch( Exception e ) {
                    throw e;

                } finally {
                    if( fo != null )
                        fo.close();
                }
            }

        } catch( Exception e ) {

            for( File f : nsToTargetFile.values()) {
                if( ! f.delete())
                    f.deleteOnExit();
            }

            throw e;
        }

        return nsToTargetFile.values();
    }


    /**
     * Validates the document against all the XML schema files.
     * @param schemaFiles the XML schemas
     * @param doc the document to validate
     * @return true if it could be validated against one schema, false otherwise
     */
    private boolean isValidAgainstXmlSchemas( Collection<File> schemaFiles, Document doc ) {

        boolean valid = false;
        for( File f : schemaFiles) {

            // Validate the document using the JDK validation mechanism
            try {
                Validator jdkValidator = jdkFactory.newSchema( f ).newValidator();
                jdkValidator.validate( new DOMSource( doc.getDocumentElement()));

                valid = true;
                break;

            } catch( IOException e ) {
                e.printStackTrace();

            } catch( SAXException e ) {
            	e.printStackTrace();
            }
        }

        return valid;
    }


    /**
     * Deletes the temporary files.
     * @param schemaFiles
     */
    private void deleteTamporaryFiles( Collection<File> schemaFiles ) {

        if( schemaFiles != null ) {
            for( File f : schemaFiles ) {
                if( f.exists() && ! f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
