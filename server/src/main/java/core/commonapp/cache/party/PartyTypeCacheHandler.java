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
package core.commonapp.cache.party;

import java.util.List;

import core.commonapp.cache.AbstractCacheHandler;
import core.commonapp.client.service.party.PartyService;
import core.data.cache.KeyedCacheException;
import core.data.cache.KeyedCacheHandler;
import core.data.model.Keyable;
import core.data.model.party.PartyType;
import core.service.result.ServiceResult;

public class PartyTypeCacheHandler extends AbstractCacheHandler implements KeyedCacheHandler
{

    @Override
    public Class getDataClass()
    {
        return PartyType.class;
    }

    @Override
    public Object getKey(Keyable keyableObject)
    {
        return keyableObject.getKey();
    }

    @Override
    public List getObjects()
    {
        PartyService partyService = (PartyService) getInformationContext().createService(PartyService.class);
        ServiceResult result = partyService.findAllPartyTypes();
        if (!result.isSuccess())
        {
            throw new KeyedCacheException("Failed to successfully get objects for archive: " + result.getMessage());
        }
        return (List) result.getPayload();
    }

}