/**
 * Copyright 2009 Core Information Solutions LLC
 *
 * This file is part of Core CommonApp Framework.
 *
 * Core CommonApp Framework is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Core CommonApp Framework is distributed in the hope that it will be  
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Core CommonApp Framework.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package core.commonapp.cache.contact;

import java.util.List;

import core.commonapp.cache.AbstractCacheHandler;
import core.commonapp.client.service.contact.ContactMechService;
import core.data.cache.KeyedCacheException;
import core.data.cache.KeyedCacheHandler;
import core.data.model.Keyable;
import core.data.model.contact.ContactMechType;
import core.service.result.ServiceResult;

public class ContactMechTypeCacheHandler extends AbstractCacheHandler implements KeyedCacheHandler
{

    @Override
    public Class getDataClass()
    {
        return ContactMechType.class;
    }

    @Override
    public Object getKey(Keyable keyableObject)
    {
        return keyableObject.getKey();
    }

    @Override
    public List getObjects()
    {
        ContactMechService contactMechService = (ContactMechService) getInformationContext().createService(ContactMechService.class);
        ServiceResult result = contactMechService.findAllContactMechTypes();
        if (result.isSuccess()) 
        {
            return (List) result.getPayload();
        }
        throw new KeyedCacheException("Failed to successfully get objects for archive: " + result.getMessage());
    }

}
