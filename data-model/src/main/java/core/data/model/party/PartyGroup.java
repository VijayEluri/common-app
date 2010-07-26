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
package core.data.model.party;

import core.data.model.DataObject;
import core.service.InformationBean;

@InformationBean (beanName="partyGroupModel")
public interface PartyGroup extends DataObject, Party
{

    /**
     * Getter for groupName
     * 
     * @return the groupName
     */
    public abstract String getGroupName();

    /**
     * Setter for groupName
     * 
     * @param groupName
     *                the groupName to set
     */
    public abstract void setGroupName(String groupName);

}