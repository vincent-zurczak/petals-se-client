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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.jbi.servicedesc.ServiceEndpoint;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.ow2.petals.engine.client.misc.Utils;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.EdptBean;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.ItfBean;
import org.ow2.petals.engine.client.swt.dialogs.ServiceRegistryViewerDialog.SrvBean;

/**
 * A viewer filter for the service registry.
 * @author Vincent Zurczak - Linagora
 */
public class ServiceRegistryViewerFilter extends ViewerFilter {

	private final static String WILDCARD = "*";
	private String filterItfName, filterItfNs, filterSrvName, filterSrvNs, filterEdptName;


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter
	 * #select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( Viewer viewer, Object parentElement, Object element ) {

		boolean result = false;
		if( element instanceof ItfBean ) {
			ItfBean q = (ItfBean) element;
			boolean loc = Utils.isEmptyString( this.filterItfName )
			|| WILDCARD.equals( this.filterItfName )
			|| q.itfName.getLocalPart().startsWith( this.filterItfName );

			boolean ns = Utils.isEmptyString( this.filterItfNs )
			|| WILDCARD.equals( this.filterItfNs )
			|| q.itfName.getNamespaceURI().startsWith( this.filterItfNs );

			if( loc && ns ) {
				List<SrvBean> srvBeans = filterServices(((ItfBean) element).srvNameToService.values());
				for( SrvBean srvBean : srvBeans ) {
					List<EdptBean> beans = filterEndpoints( srvBean.endpoints);
					if( beans.size() > 0 ) {
						result = true;
						break;
					}
				}
			}

		} else if( element instanceof SrvBean ) {
			List<SrvBean> srvBeans = filterServices( Arrays.asList((SrvBean) element));
			if( srvBeans.size() == 1 ) {
				List<EdptBean> beans = filterEndpoints(((SrvBean) element).endpoints);
				result = beans.size() > 0;
			}

		} else if( element instanceof EdptBean ) {
			List<EdptBean> beans = filterEndpoints( Arrays.asList((EdptBean) element));
			result = beans.size() == 1;
		}

		return result;
	}


	/**
	 * Filters a list of end-points using the filtering criteria.
	 * @param endpoints
	 * @return a non-null list
	 */
	private List<EdptBean> filterEndpoints( Collection<EdptBean> endpoints ) {

		List<EdptBean> filteredList = new ArrayList<EdptBean> ();
		for( EdptBean edptBean : endpoints ) {
			ServiceEndpoint se = edptBean.se;
			if( Utils.isEmptyString( this.filterEdptName )
					|| WILDCARD.equals( this.filterEdptName )
					|| se.getEndpointName().startsWith( this.filterEdptName ))
				filteredList.add( edptBean );
		}

		return filteredList;
	}


	/**
	 * Filters a list of services using the filtering criteria.
	 * @param endpoints
	 * @return a non-null list
	 */
	private List<SrvBean> filterServices( Collection<SrvBean> services ) {

		List<SrvBean> filteredList = new ArrayList<SrvBean> ();
		for( SrvBean srvBean : services ) {
			boolean loc = Utils.isEmptyString( this.filterSrvName )
			|| WILDCARD.equals( this.filterSrvName )
			|| srvBean.srvName.getLocalPart().startsWith( this.filterSrvName );

			boolean ns = Utils.isEmptyString( this.filterSrvNs )
			|| WILDCARD.equals( this.filterSrvNs )
			|| srvBean.srvName.getNamespaceURI().startsWith( this.filterSrvNs );

			if( loc && ns )
				filteredList.add( srvBean );
		}

		return filteredList;
	}


	/**
	 * @param filterItfName the filterItfName to set
	 */
	public void setFilterItfName(String filterItfName) {
		this.filterItfName = filterItfName;
	}


	/**
	 * @param filterItfNs the filterItfNs to set
	 */
	public void setFilterItfNs(String filterItfNs) {
		this.filterItfNs = filterItfNs;
	}


	/**
	 * @param filterSrvName the filterSrvName to set
	 */
	public void setFilterSrvName(String filterSrvName) {
		this.filterSrvName = filterSrvName;
	}


	/**
	 * @param filterSrvNs the filterSrvNs to set
	 */
	public void setFilterSrvNs(String filterSrvNs) {
		this.filterSrvNs = filterSrvNs;
	}


	/**
	 * @param filterEdptName the filterEdptName to set
	 */
	public void setFilterEdptName(String filterEdptName) {
		this.filterEdptName = filterEdptName;
	}
}
