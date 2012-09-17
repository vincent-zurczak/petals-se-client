/****************************************************************************
 *
 * Copyright (c) 2005-2012, Linagora
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

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ow2.petals.engine.client.model.Mep;
import org.ow2.petals.engine.client.model.RequestMessageBean;

/**
 * @author Christophe Hamerling - Linagora
 * @author Vincent Zurczak - Linagora
 */
public class RequestMessageBeanUtils {

    public static final String INTERFACE = "interface";
    public static final String SERVICE = "service";
    public static final String ENDPOINT = "endpoint";

    public static final String MEP = "mep";
    public static final String TIMEOUT = "timeout";
    public static final String OPERATION = "operation";

    public static final String XML_PAYLOAD = "xml-payload";
    public static final String ATTACHMENTS = "attachments";
    public static final String PROPERTIES = "properties";



    /**
     * Reads a {@link RequestMessageBean} from properties.
     * @param properties the properties
     * @return a non-null {@link RequestMessageBean}
     */
    public static RequestMessageBean read( Properties properties ) throws Exception {

        RequestMessageBean result = new RequestMessageBean();

        String data = properties.getProperty( INTERFACE );
        result.setInterfaceName( data != null ? QName.valueOf( data ) : null );

        data = properties.getProperty( SERVICE );
        result.setServiceName( data != null ? QName.valueOf( data ) : null );

        result.setEndpointName( properties.getProperty( ENDPOINT ));
        result.setXmlPayload( properties.getProperty( XML_PAYLOAD ));

        data = properties.getProperty( MEP );
        result.setMep( Mep.valueOf( data ));

        data = properties.getProperty( TIMEOUT, "-1" );
        result.setTimeout( Long.valueOf( data ));

        data = properties.getProperty( OPERATION );
        result.setOperation( data != null ? QName.valueOf( data ) : null );

        data = properties.getProperty( ATTACHMENTS, "" );
        Set<File> attachments = new LinkedHashSet<File> ();
        result.setAttachments( attachments );
        for( String s : data.split( "\\|" )) {
            s = s.trim();
            if( s.length() != 0 )
                attachments.add( new File( s ));
        }

        data = properties.getProperty( PROPERTIES, "" );
        Properties msgProperties = new Properties();
        for( String s : data.split( "\\|" )) {
            s = s.trim();
            int index = s.indexOf( '=' );
            if( index < 1 )
                continue;

            String key = s.substring( 0, index ).trim();
            String value = index == s.length() -1 ? "" : s.substring( ++index ).trim();
            msgProperties.setProperty( key, value );
        }

        return result;
    }


    /**
     * Writes a {@link RequestMessageBean} in properties.
     * @param bean the {@link RequestMessageBean}
     * @return properties to write the content of the request
     */
    public static Properties write( RequestMessageBean bean ) {

        Properties properties = new Properties();
        properties.setProperty( ENDPOINT, bean.getEndpointName());
        if( bean.getInterfaceName() != null )
            properties.setProperty( INTERFACE, bean.getInterfaceName().toString());

        if( bean.getServiceName() != null )
            properties.setProperty( SERVICE, bean.getServiceName().toString());

        if( bean.getMep() != null )
        	properties.setProperty( MEP, bean.getMep().toString());

        properties.setProperty( TIMEOUT, String.valueOf( bean.getTimeout()));
        if( bean.getOperation() != null )
            properties.setProperty( OPERATION, bean.getOperation().toString());

        StringBuilder sb = new StringBuilder();
        if( bean.getProperties() != null ) {
            for( Map.Entry<String,String> entry : bean.getProperties().entrySet())
                sb.append( entry.getKey() + " = " + entry.getValue() + "|" );
        }

        properties.setProperty( PROPERTIES, sb.toString());
        properties.setProperty( XML_PAYLOAD, bean.getXmlPayload());

        sb = new StringBuilder();
        if( bean.getAttachments() != null ) {
            for( File f : bean.getAttachments())
                sb.append( f.getAbsolutePath() + "|" );
        }

        properties.setProperty( ATTACHMENTS, sb.toString());
        return properties;
    }
}
