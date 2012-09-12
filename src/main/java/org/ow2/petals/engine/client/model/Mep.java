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

/**
 * The Message Exchange Patterns.
 * @author Vincent Zurczak - Linagora
 */
public enum Mep {

    IN_ONLY( "InOnly" ),
    IN_OUT( "InOut" ),
    ROBUST_IN_ONLY( "RobustInOnly" ),
    IN_OPTIONAL_OUT( "InOptionalOut" );


    /**
     * The string value.
     */
    private String value;

    /**
     * Constructor.
     * @param value
     */
    Mep( String value ) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }
}
