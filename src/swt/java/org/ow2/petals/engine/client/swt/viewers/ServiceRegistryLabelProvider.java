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

package org.ow2.petals.engine.client.swt.viewers;

import javax.xml.namespace.QName;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.ow2.petals.engine.client.swt.ImageIds;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.EdptBean;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.ItfBean;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.SrvBean;

/**
 * A label provider for the service registry.
 * @author Vincent Zurczak - Linagora
 */
public class ServiceRegistryLabelProvider extends ColumnLabelProvider {

	private final Image edptImage, itfImage, srvImage;


	/**
	 * Constructor.
	 */
	public ServiceRegistryLabelProvider() {
		this.itfImage = JFaceResources.getImage( ImageIds.CONTRACT_16x16 );
		this.srvImage = JFaceResources.getImage( ImageIds.SERVICE_16x16 );
		this.edptImage = JFaceResources.getImage( ImageIds.ENDPOINT_16x16 );
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider
	 * #dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		if( this.edptImage != null && ! this.edptImage.isDisposed())
			this.edptImage.dispose();

		if( this.srvImage != null && ! this.srvImage.isDisposed())
			this.srvImage.dispose();

		if( this.itfImage != null && ! this.itfImage.isDisposed())
			this.itfImage.dispose();
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider
	 * #getText(java.lang.Object)
	 */
	@Override
	public String getText( Object element ) {

		QName qname = null;
		if( element instanceof ItfBean )
			qname = ((ItfBean) element).itfName;
		else if( element instanceof SrvBean )
			qname = ((SrvBean) element).srvName;

		String text = "";
		if( qname != null )
			text = qname.getLocalPart() + " - " + qname.getNamespaceURI();
		else if( element instanceof EdptBean )
			text = ((EdptBean) element).se.getEndpointName();

		return text;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider
	 * #getImage(java.lang.Object)
	 */
	@Override
	public Image getImage( Object element ) {

		Image result = null;
		if( element instanceof ItfBean )
			result = this.itfImage;
		else if( element instanceof SrvBean )
			result = this.srvImage;
		else if( element instanceof EdptBean )
			result = this.edptImage;

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellLabelProvider
	 * #getToolTipText(java.lang.Object)
	 */
	@Override
	public String getToolTipText( Object element ) {

		QName qname = null;
		if( element instanceof ItfBean )
			qname = ((ItfBean) element).itfName;
		else if( element instanceof SrvBean )
			qname = ((SrvBean) element).srvName;

		String text = "";
		if( qname != null )
			text = qname.getLocalPart() + " - " + qname.getNamespaceURI();
		else if( element instanceof EdptBean )
			text = ((EdptBean) element).se.getEndpointName();

		return text;
	}
}
