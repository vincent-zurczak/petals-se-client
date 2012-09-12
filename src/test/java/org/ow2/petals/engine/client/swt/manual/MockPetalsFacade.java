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

package org.ow2.petals.engine.client.swt.manual;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.ow2.petals.engine.client.TestUtils;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.ui.PetalsFacade;
import org.w3c.dom.Document;

/**
 * A mock to test manually the user interface.
 * @author Vincent Zurczak - Linagora
 */
public class MockPetalsFacade extends PetalsFacade {

	private final boolean testError, debug;
	private final Map<QName,Document> itfNameToDocument;


	/**
	 * Constructor.
	 * @param testError
	 * @param debug
	 */
	public MockPetalsFacade( boolean testError, boolean debug ) {
		this.testError = testError;
		this.debug = debug;

		this.itfNameToDocument = new HashMap<QName,Document> ();
		String[] resources = { "/simpleWSDL/tuxDroid.wsdl", "/simpleWSDL2/AWSECommerceService.wsdl" };
		String[] ns = { "http://tuxdroid.ebmwebsourcing.com/", "http://webservices.amazon.com/AWSECommerceService/2008-10-06" };
		String[] names = { "TuxDroidPortType", "AWSECommerceServicePortType" };
		try {
			for( int i=0; i<ns.length; i++ ) {
				URL url = getClass().getResource( resources[ i ]);
				Document doc = TestUtils.buildDocument( url );
				QName itfName = new QName( ns[ i ], names[ i ]);
				this.itfNameToDocument.put( itfName, doc );
			}

		} catch( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/* (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IPetalsFacade
	 * #send(org.ow2.petals.engine.client.model.RequestMessageBean)
	 */
	@Override
	public void send( RequestMessageBean request ) throws Exception {

		if( this.debug )
			System.out.println( "Invoked 'send'." );

		if( this.testError )
			throw new Exception( "An error occurred in the mock facade." );
	}


	/* (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IPetalsFacade
	 * #findWsdlDescriptionAsDocument(javax.jbi.servicedesc.ServiceEndpoint)
	 */
	@Override
	public Document findWsdlDescriptionAsDocument( ServiceEndpoint se )
	throws Exception {

		if( this.debug )
			System.out.println( "Invoked 'findWsdlDescriptionAsDocument'." );

		if( this.testError )
			throw new Exception( "An error occurred in the mock facade." );

		return this.itfNameToDocument.get( se.getInterfaces()[ 0 ]);
	}


	/* (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IPetalsFacade
	 * #resolveServiceEndpoint(javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
	 */
	@Override
	public ServiceEndpoint resolveServiceEndpoint( final QName itfName, final QName srvName, final String edptName )
	throws Exception {

		if( this.debug )
			System.out.println( "Invoked 'resolveServiceEndpoint'." );

		if( this.testError )
			throw new Exception( "An error occurred in the mock facade." );

		return new MockServiceEndpoint( edptName, itfName, srvName );
	}


	/*
	 * (non-Javadoc)
	 * @see org.ow2.petals.engine.client.ui.IPetalsFacade
	 * #getAllServiceEndpoints()
	 */
	@Override
	public List<ServiceEndpoint> getAllServiceEndpoints() throws Exception {

		if( this.debug )
			System.out.println( "Invoked 'getAllServiceEndpoints'." );

		if( this.testError )
			throw new Exception( "An error occurred in the mock facade." );

		List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint> ();
		for( int i=0; i<34; i++ ) {
			String s = UUID.randomUUID().toString().substring( 0, 10 );
			for( int j=0; j<=i; j++ ) {
				for( int k=0; k<=i; k++ ) {

					MockServiceEndpoint se = new MockServiceEndpoint();
					if( i % 5 == 0 ) {
						se.setInterfaceName( new QName( "http://petals.ow2.org/" + s, s ));
					} else {
						int index = new Random().nextInt( this.itfNameToDocument.size());
						QName itfName = (QName) this.itfNameToDocument.keySet().toArray()[ index ];
						se.setInterfaceName( itfName );
					}

					se.setServiceName( new QName( "http://petals.ow2.org/" + s, s + "-Srv-" + j ));
					se.setEndpointName( s + "-Endpoint-" + k );
					result.add( se );
				}
			}
		}

		return result;
	}
}
