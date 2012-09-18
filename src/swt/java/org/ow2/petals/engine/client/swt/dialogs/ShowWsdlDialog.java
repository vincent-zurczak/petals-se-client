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

package org.ow2.petals.engine.client.swt.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ow2.petals.engine.client.swt.SwtUtils;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.ColorCacheManager;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegion;
import org.ow2.petals.engine.client.swt.syntaxhighlighting.XmlRegionAnalyzer;

/**
 * A dialog to show the content of a WSDL description.
 * @author Vincent Zurczak - Linagora
 */
public class ShowWsdlDialog extends Dialog {

	private final String wsdlDescription;
	private final StyleRange[] styleRanges;


	/**
	 * Constructor.
	 * @param parentShell
	 * @param wsdlDescription
	 * @param styleRanges
	 */
	public ShowWsdlDialog( Shell parentShell, String wsdlDescription, StyleRange[] styleRanges ) {
		super( parentShell );
		setShellStyle( SWT.SHELL_TRIM );

		this.wsdlDescription = wsdlDescription;
		this.styleRanges = styleRanges;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {

		getShell().setText( "WSDL Content" );

		Composite container = new Composite( parent, SWT.NONE );
		container.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		container.setLayout( new GridLayout());
		container.setLayoutData( new GridData( GridData.FILL_BOTH ));

		int style = SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY;
		StyledText st = new StyledText( parent, style );
		st.setLayoutData( new GridData( GridData.FILL_BOTH ));
		st.setText( this.wsdlDescription );
		st.setStyleRanges( this.styleRanges );

		return container;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar( Composite parent ) {

		Control control = super.createButtonBar( parent );
		control.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));
		parent.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WHITE ));

		Button b = getButton( Dialog.OK );
		if( b != null )
			b.setFocus();

		return control;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		Rectangle rect = Display.getDefault().getBounds();
		return new Point( rect.width / 2, rect.height / 2 );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog
	 * #createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
	 */
	@Override
	protected Button createButton( Composite parent, int id, String label, boolean defaultButton ) {
		return id == Dialog.CANCEL ? null : super.createButton( parent, id, label, defaultButton );

	}


	/**
	 * A convenience method to display both a progress dialog and then the WSDL dialog.
	 * @param shell
	 * @param xmlText
	 * @param colorManager
	 */
	public static void openShowWsdlDialog( Shell shell, final String xmlText, final ColorCacheManager colorManager ) {

		final Object[] array = new Object[ 1 ];
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run( IProgressMonitor monitor )
			throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask( "Formatting the WSDL...", IProgressMonitor.UNKNOWN );
					List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml( xmlText );
					StyleRange[] styleRanges = SwtUtils.computeStyleRanges( regions, colorManager );
					array[ 0 ] = styleRanges;

				} finally {
					monitor.done();
				}
			}
		};

		try {
			new ProgressMonitorDialog( shell ).run( true, false, runnable );
			StyleRange[] styleRanges = (StyleRange[]) array[ 0 ];
			new ShowWsdlDialog( shell, xmlText, styleRanges ).open();

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
