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

import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.swt.ClientApplication;

/**
 * A class to test manually the user interface.
 * @author Vincent Zurczak - Linagora
 */
public class Main {

    /**
     * @param args
     */
    public static void main( String[] args ) {

        ClientApplication app = new ClientApplication( new MockPetalsFacade( false, true ), new Shell());
        app.setBlockOnOpen( true );
        app.open();
    }
}
