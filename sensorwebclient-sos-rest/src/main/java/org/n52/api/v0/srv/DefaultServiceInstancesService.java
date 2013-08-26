/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.api.v0.srv;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.api.v0.out.ServiceInstance;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.ResourceNotFoundException;

public class DefaultServiceInstancesService implements ServiceInstancesService {

    @Override
    public Collection<ServiceInstance> getServiceInstances() {
        ArrayList<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (SOSMetadata metadata : ConfigurationContext.getSOSMetadatas()) {
            serviceInstances.add(new ServiceInstance(metadata));
        }
        return serviceInstances;
    }

    @Override
    public ServiceInstance getServiceInstance(String id) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(id);
        if (metadata == null) {
            throw new ResourceNotFoundException();
        }
        return new ServiceInstance(metadata);
    }

	@Override
	public boolean containsServiceInstance(String serviceInstance) {
		return ConfigurationContext.containsServiceInstance(serviceInstance);
	}

	@Override
	public SOSMetadata getSOSMetadataForItemName(String serviceInstance) {
		SOSMetadata sosMetadata = ConfigurationContext.getSOSMetadataForItemName(serviceInstance);
		return sosMetadata;
	}

	@Override
	public Collection<SOSMetadata> getSOSMetadatas() {
		return ConfigurationContext.getSOSMetadatas();
	}

}