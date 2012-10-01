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

package org.ow2.petals.engine.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;

import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.model.ResponseMessageBean;
import org.ow2.petals.engine.client.model.ResponseNature;

/**
 * Message listener of the Client component.
 * @author Adrien Louis - Linagora
 * @author ddesjardins - Linagora
 * @author Vincent Zurczak - Linagora
 */
public class ClientJbiListener implements Runnable {

    private final ClientSe clientSe;
    private boolean running = true;



    /**
     * Constructor.
     * @param clientSe the client SE
     */
    public ClientJbiListener( ClientSe clientSe ) {
        this.clientSe = clientSe;
    }


    /**
     * Waits for message exchanges and processes them.
     */
    @Override
    public void run() {

        while( this.running ) {
            try {
                // We do not expect thousands of messages by second, this is an interactive component.
                // No need to use executors and pools.
                MessageExchange msg = this.clientSe.getDeliveryChannel().accept();
                if( msg != null )
                    process( msg );

            } catch( MessagingException e ) {
            	Utils.log( "Error while receiving a response in Petals.", e, Level.SEVERE, this.clientSe.getLogger());
            	this.clientSe.getUiClient().reportCommunicationProblem( e );
            }
        }

    }

    /**
     * Processes the received message exchange.
     * @param msg the message exchange
     */
    public void process( MessageExchange msg ) {

        this.clientSe.getLogger().log(Level.FINE, "The Client message is processing " + msg.getExchangeId());

        // Check the exchange
        ResponseMessageBean response = new ResponseMessageBean();
        if( ExchangeStatus.DONE.equals( msg.getStatus())) {
            response.setNature( ResponseNature.ACK );
            response.setXmlPayload( "DONE Acknowledgment (status)." );

        } else if( ExchangeStatus.ERROR.equals( msg.getStatus())) {
            response.setNature( ResponseNature.ACK );
            response.setXmlPayload( "ERROR Acknowledgment (status)" );

        } else {
            try {
                // OUT message
                NormalizedMessage nm = msg.getMessage( "OUT" );
                if( nm != null && nm.getContent() != null ) {

                    response.setNature( ResponseNature.OUT );
                    response.setXmlPayload( Utils.createString( nm.getContent()));

                    Set<File> attachments = new HashSet<File> ();
                    response.setAttachments( attachments );
                    for( Object o : nm.getAttachmentNames()) {
                    	String name = (String) o;
                    	File f = saveTempAttachment( name, nm.getAttachment( name ));
                    	if( f != null )
                    		attachments.add( f );
                    }
                }

                // FAULT
                else if( msg.getFault() != null ) {
                    response.setNature( ResponseNature.FAULT );
                    response.setXmlPayload( Utils.createString( msg.getFault().getContent()));
                }

                // Always check the properties
                Map<String,String> props = new LinkedHashMap<String, String> ();
                response.setProperties( props );
                if( msg.getPropertyNames() != null ) {
                    for( Object o : msg.getPropertyNames()) {
                        String name = (String) o;
                        props.put( name, String.valueOf( msg.getProperty( name )));
                    }
                }

                // Close the exchange
                msg.setStatus( ExchangeStatus.DONE );
                this.clientSe.getDeliveryChannel().send( msg );

                // Report the change in the UI
                this.clientSe.getUiClient().displayResponse( response );

            } catch( Exception e ) {
            	Utils.log( "Error while processing a response in Petals.", e, Level.SEVERE, this.clientSe.getLogger());
            	this.clientSe.getUiClient().reportCommunicationProblem( e );
            }
        }
    }


    /**
     * Stops processing Petals messages.
     */
    public void stopProcessing() {
        this.running = false;
    }


    /**
     * Saves an attachment as a temporary file.
     * @param name
     * @param dh
     */
    private File saveTempAttachment( String name, DataHandler dh ) {

    	File targetFile = null;
    	String ext = null;
    	if( name.indexOf( '.' ) < 0 )
    		ext = ".txt";

    	try {
	    	InputStream in = null;
	    	FileOutputStream fos = null;
	    	try {
				in = dh.getInputStream();
				targetFile = File.createTempFile( name, ext );
				fos = new FileOutputStream( targetFile );
				Utils.copyStream( in, fos );

			} finally {
				try {
					if( in != null )
						in.close();
				} finally {
					if( fos != null )
						fos.close();
				}
			}

	    } catch( IOException e ) {
			Utils.log( "Failed to save an attachment.", e, Level.SEVERE, this.clientSe.getLogger());
	    }

    	if( targetFile != null
    			&& targetFile.exists())
    		targetFile.deleteOnExit();

    	return targetFile;
    }
}
