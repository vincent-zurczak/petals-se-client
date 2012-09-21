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

package org.ow2.petals.engine.client.ui;

import java.util.logging.Logger;

/**
 * An interface that can be used to interact with a user interface.
 * @author Vincent Zurczak - Linagora
 */
public interface IClientUI {

    /**
     * Shows a shell with a user interface.
     */
    void open();

    /**
     * Closes the user interface.
     */
    void close();

    /**
     * Sets the Petals <i>façade</i> to use in the UI.
     * @param petalsFacade the Petals <i>façade</i>
     */
    void setPetalsFacade( PetalsFacade petalsFacade );

    /**
     * Sets the Petals logger.
     * @param logger the logger (not null)
     */
    void setPetalsLogger( Logger logger );
}
