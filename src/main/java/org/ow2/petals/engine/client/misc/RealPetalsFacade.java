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

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOptionalOut;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.RobustInOnly;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.ow2.petals.engine.client.ClientSe;
import org.ow2.petals.engine.client.model.RequestMessageBean;
import org.ow2.petals.engine.client.ui.PetalsFacade;
import org.ow2.petals.jaas.GroupPrincipal;
import org.ow2.petals.jaas.UserPrincipal;
import org.w3c.dom.Document;

/**
 * An implementation that interacts with a real Petals server.
 * @author Vincent Zurczak - Linagora
 */
public class RealPetalsFacade extends PetalsFacade {

    private final static String SECURITY_USER = "sec.user";
    private final static String SECURITY_GROUP = "sec.group";

    private final ClientSe clientSe;


    /**
     * Constructor.
     * @param clientSe
     */
    public RealPetalsFacade( ClientSe clientSe ) {
        this.clientSe = clientSe;
    }


    /* (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IPetalsFacade
     * #send(org.ow2.petals.engine.client.model.RequestMessageBean)
     */
    @Override
    public void send( RequestMessageBean request ) throws Exception {

        this.clientSe.getLogger().log(Level.INFO, "ClientSe try to send");

        // Create a message exchange
        MessageExchange msg = null;
        switch( request.getMep()) {
        	case IN_ONLY:
        		msg = this.clientSe.getComponentContext().getDeliveryChannel().createExchangeFactory().createInOnlyExchange();
        		break;

        	case IN_OPTIONAL_OUT:
        		msg = this.clientSe.getComponentContext().getDeliveryChannel().createExchangeFactory().createInOptionalOutExchange();
        		break;

        	case IN_OUT:
        		msg = this.clientSe.getComponentContext().getDeliveryChannel().createExchangeFactory().createInOutExchange();
        		break;

        	case ROBUST_IN_ONLY:
        		msg = this.clientSe.getComponentContext().getDeliveryChannel().createExchangeFactory().createRobustInOnlyExchange();
        		break;

        	default:
        		// This should never happen
        		return;
        }


        // Define the request (IN message)
        NormalizedMessage nm = msg.createMessage();
        nm.setContent( Utils.createSource( request.getXmlPayload()));


        // Handle the properties
        if( request.getProperties() != null ) {

        	String user = null, group = null;
        	for( Map.Entry<String,String> entry : request.getProperties().entrySet()) {
        		if( SECURITY_GROUP.equals( entry.getKey()))
        			group = entry.getValue();
        		else if( SECURITY_USER.equals( entry.getKey()))
        			user = entry.getValue();
        		else
        			nm.setProperty( entry.getKey(), entry.getValue());
        	}

        	// Handle security aspects
        	if( user != null )
        		nm.setSecuritySubject( createSecuritySubject( user, group ));
        }


        // Add attachments
        if( request.getAttachments() != null ) {
        	for( File f : request.getAttachments()) {
        		try {
        			nm.addAttachment( f.getName(), new DataHandler( new FileDataSource( f )));
        		} catch( MessagingException e ) {
        			throw new Exception( "Error while attaching the file " + f.getName());
        		}
        	}
        }


        // Set the target end-point
        if( request.getEndpointName() != null
        		&& request.getEndpointName().trim().length() > 0 ) {

        	ServiceEndpoint svcEndpoint = this.clientSe.getComponentContext().getEndpoint( request.getServiceName(), request.getEndpointName());
        	if( svcEndpoint == null ) {
        		throw new Exception( "The endpoint '" + request.getEndpointName() + "' for the service '"
        				+ request.getServiceName() + "' is not registered or not activated." );
        	} else {
        		msg.setEndpoint(svcEndpoint);
        	}
        }

        if( request.getServiceName() != null )
        	msg.setService( request.getServiceName());


        // Set mandatory elements
        msg.setInterfaceName( request.getInterfaceName());
        msg.setOperation( request.getOperation());

        switch( request.getMep()) {
        	case IN_ONLY:
        		((InOnly) msg).setInMessage(nm);
        		break;

        	case IN_OPTIONAL_OUT:
        		((InOptionalOut) msg).setInMessage(nm);
        		break;

        	case IN_OUT:
        		((InOut) msg).setInMessage(nm);
        		break;

        	case ROBUST_IN_ONLY:
        		((RobustInOnly) msg).setInMessage(nm);
        		break;

        	default:
        		break;
        }

        // Send the message
        if( request.getTimeout() < 0) {
        	this.clientSe.getDeliveryChannel().send( msg );
        } else {
        	boolean ok = this.clientSe.getDeliveryChannel().sendSync( msg, request.getTimeout());
        	//if( ok )
        	//	this.clientSe.getJbiListener().process( msg );
        	//else
        	//	this.clientSe.getUiClient().reportError( "A timeout occurred while sending a message to a Petals service.", null );
        }
    }


    /*
     * (non-Javadoc)
     * @see org.ow2.petals.engine.client.ui.IPetalsFacade
     * #findWsdlDescriptionAsDocument(javax.jbi.servicedesc.ServiceEndpoint)
     */
    @Override
    public Document findWsdlDescriptionAsDocument( ServiceEndpoint se ) throws Exception {
        return this.clientSe.getServiceDescription( se );
    }


    /**
     * Creates a security subject.
     * @param user the user name (not null)
     * @param group the group name (can be null)
     * @return a non-null subject
     */
    private Subject createSecuritySubject( String user, String group ) {

        Subject subject = new Subject();
        Set<Principal> principals = subject.getPrincipals();
        principals.add( new UserPrincipal( user ));
        principals.add( group != null ? new GroupPrincipal( group ) : GroupPrincipal.ALL );

        return subject;
    }


	@Override
	public ServiceEndpoint resolveServiceEndpoint(QName itfName, QName srvName, String epdtName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ServiceEndpoint> getAllServiceEndpoints() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
