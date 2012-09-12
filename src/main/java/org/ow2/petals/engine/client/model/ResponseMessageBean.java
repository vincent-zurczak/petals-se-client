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

package org.ow2.petals.engine.client.model;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ResponseMessageBean extends BasicMessageBean {

    private ResponseNature nature;

    /**
     * @return the nature
     */
    public ResponseNature getNature() {
        return this.nature;
    }

    /**
     * @param nature the nature to set
     */
    public void setNature(ResponseNature nature) {
        this.nature = nature;
    }
}
