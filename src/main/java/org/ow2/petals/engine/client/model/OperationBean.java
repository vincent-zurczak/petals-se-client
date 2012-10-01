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

package org.ow2.petals.engine.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.wsdl.api.Operation;

/**
 * A bean to store information about a WSDL operation.
 * @author Vincent Zurczak - Linagora
 */
public class OperationBean {

	private final Mep mep;
	private final QName operationName;


	/**
	 * Constructor.
	 * @param mep
	 * @param operationName
	 */
	public OperationBean(Mep mep, QName operationName) {
		this.mep = mep;
		this.operationName = operationName;
	}


	/**
	 * @return the mep
	 */
	public Mep getMep() {
		return this.mep;
	}


	/**
	 * @return the operationName
	 */
	public QName getOperationName() {
		return this.operationName;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object
	 * #toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder( "[ " + this.mep + " ] " );
		if( this.operationName != null ) {
			sb.append( this.operationName.getLocalPart());
			sb.append( " - " );
			sb.append( this.operationName.getNamespaceURI());
		}

		return sb.toString();
	}


	/**
	 * Creates an operation bean from a WSDL operation
	 * @param ops a list of WSDL operations (not null)
	 * @return a non-null list of beans
	 */
	public static List<OperationBean> convert( List<Operation> ops ) {

		List<OperationBean> result = new ArrayList<OperationBean> ();
		for( Operation op : ops ) {
			Mep mep = op.getInput() != null && op.getInput().getElement() != null ? Mep.IN_OUT : Mep.IN_ONLY;
			result.add( new OperationBean( mep, op.getQName()));
		}

		return result;
	}
}
