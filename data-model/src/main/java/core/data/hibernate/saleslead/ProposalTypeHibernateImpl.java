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
package core.data.hibernate.saleslead;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.sf.gilead.pojo.java5.LightEntity;
import core.data.model.saleslead.ProposalType;
import core.data.model.util.DataUtil;

@Entity
@Table (name="proposal_type")
public class ProposalTypeHibernateImpl extends LightEntity implements ProposalType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_type_id", nullable = false)
    private Integer proposalTypeId;

    @Column(name = "description")
    private String description;
    
    @Column (name="key")
    private String key;

    /* (non-Javadoc)
     * @see core.data.model.saleslead.ProposalType#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    
    public Integer getId()
    {
        return getProposalTypeId();
    }

    /**
     * Getter for key
     *
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see core.data.model.saleslead.ProposalType#getProposalTypeId()
     */
    public Integer getProposalTypeId()
    {
        return proposalTypeId;
    }


    
    public boolean isEquivalent(Object object)
    {
        ProposalTypeHibernateImpl type = (ProposalTypeHibernateImpl) object;
        return DataUtil.equals(getProposalTypeId(), type.getProposalTypeId())
            && DataUtil.equals(getDescription(), type.getDescription())
            && DataUtil.equals(getKey(), type.getKey());
    }

    /* (non-Javadoc)
     * @see core.data.model.saleslead.ProposalType#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    
    public void setId(Integer id)
    {
        setProposalTypeId(id);

    }

    /**
     * Setter for key
     *
     * @param key the key to set
     */
    public void setKey(Object key)
    {
        this.key = (String) key;
    }

    /* (non-Javadoc)
     * @see core.data.model.saleslead.ProposalType#setProposalTypeId(java.lang.Integer)
     */
    public void setProposalTypeId(Integer proposalTypeId)
    {
        this.proposalTypeId = proposalTypeId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    
    public String toString()
    {
        return "ProposalType("
            + "proposalTypeId="
            + getProposalTypeId()
            + ",description="
            + getDescription()
            + ",key="
            + getKey()
            + ")";
    }

}
