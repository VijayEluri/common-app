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
package core.commonapp.server.dao.party;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import core.commonapp.client.dao.party.PersonDao;
import core.commonapp.server.dao.BaseDaoJpaImpl;
import core.data.cache.KeyedCache;
import core.data.cache.KeyedCacheStore;
import core.data.cache.contact.ContactMechTypeKey;
import core.data.helper.contact.PhoneNumberHelper;
import core.data.helper.contact.PostalAddressHelper;
import core.data.model.contact.ContactMechType;
import core.data.model.contact.PhoneNumber;
import core.data.model.contact.PostalAddress;
import core.data.model.party.Party;
import core.data.model.party.PartyRelationship;
import core.data.model.party.Person;
import core.data.model.security.UserLogin;
import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

@Repository
public class PersonDaoHibernateImpl extends BaseDaoJpaImpl<Person> implements PersonDao
{

    /** logger for this class */
    private static final Logger log = LogFactory.getLogger(PersonDaoHibernateImpl.class);
    
    @Autowired
    private KeyedCache cache;

    /**
     * 
     * @param criteria
     * @param field
     * @param value
     */
    private void addRestrictionIfNotEmpty(DetachedCriteria criteria, String field, String value)
    {
        if (!StringUtils.isEmpty(value))
        {
            criteria.add(Restrictions.eq(field, value));
        }
    }

    @Override
    @Transactional
    public List<Person> findAllContactPeople(Integer partyId)
    {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        
        Root<Person> contactPerson = query.from(Person.class);
        Join<Person, PartyRelationship> partyToRelationships = contactPerson.join("partyToRelationships");

        query.where(
        		builder.equal(partyToRelationships.get("partyFrom.partyId"), partyId),
        		builder.isNull(partyToRelationships.get("thruDate"))
        	);

        List<Person> people = getEntityManager().createQuery(query).getResultList();
        
        PartyDaoHibernateImpl.lazyLoad((Party)people, true, true, false, false);

        return people;
    }

    @Override
    @Transactional
    public Person findById(Integer partyId, boolean loadPartyType, boolean loadPartyContactMechs,
            boolean loadPartyFromRelationships, boolean loadPartyToRelationships)
    {
        Person person = findById(partyId);
        
        PartyDaoHibernateImpl.lazyLoad(person, loadPartyType, loadPartyContactMechs, loadPartyFromRelationships, loadPartyToRelationships);
        return person;
    }

    @Override
    @Transactional
    public Set<Party> findContactPerson(String partyName, PhoneNumber phoneNumber, PostalAddress postalAddress,
            String emailAddress, UserLogin userLogin)
    {
        log.debug("PersonDaoHibernateImpl.findContactPerson({0},{1},{2},{3},{4},{5})", partyName, phoneNumber,
                postalAddress, emailAddress, userLogin);
        DetachedCriteria partyCriteria = DetachedCriteria.forClass(Person.class);

        // relationship to user
        DetachedCriteria relationshipFromCriteria = partyCriteria.createCriteria("partyToRelationships");
        relationshipFromCriteria.add(Restrictions.eq("partyFrom", userLogin.getParty()));

        // party name
        if (!StringUtils.isEmpty(partyName))
        {
            int comma = partyName.indexOf(",");
            if (comma > 0)
            {
                partyCriteria.add(Restrictions.eq("lastName", partyName.substring(0, comma).trim()));
                partyCriteria.add(Restrictions.eq("firstName", partyName.substring(comma + 1).trim()));
            }
            else
            {
                partyCriteria.add(Restrictions.eq("lastName", partyName.trim()));
            }
        }
        
        // contact mech type cache
        KeyedCacheStore<ContactMechType> contactMechTypeCache = cache.getCacheStore(ContactMechType.class);

        // email address
        if (!StringUtils.isEmpty(emailAddress))
        {
            DetachedCriteria partyContactMechCriteria = partyCriteria.createCriteria("partyContactMechs");
            DetachedCriteria contactMechCriteria = partyContactMechCriteria.createCriteria("contactMech");
            contactMechCriteria.add(Restrictions.eq("contactMechType.contactMechTypeId",
                    contactMechTypeCache.getObject(ContactMechTypeKey.KEY_EMAIL_ADDRESS).getId()));
            contactMechCriteria.add(Restrictions.eq("emailAddress", emailAddress));
        }

        // phone number
        PhoneNumberHelper phoneNumberHelper = new PhoneNumberHelper(phoneNumber);
        if (phoneNumber != null && !phoneNumberHelper.isEmpty())
        {
            DetachedCriteria partyContactMechCriteria = partyCriteria.createCriteria("partyContactMechs");
            DetachedCriteria contactMechCriteria = partyContactMechCriteria.createCriteria("contactMech");
            contactMechCriteria.add(Restrictions.eq("contactMechType.contactMechTypeId",
                    contactMechTypeCache.getObject(ContactMechTypeKey.KEY_PHONE_NUMBER).getId()));
            addRestrictionIfNotEmpty(contactMechCriteria, "countryCode", phoneNumber.getCountryCode());
            addRestrictionIfNotEmpty(contactMechCriteria, "areaCode", phoneNumber.getAreaCode());
            addRestrictionIfNotEmpty(contactMechCriteria, "contactNumber", phoneNumber.getContactNumber());
            addRestrictionIfNotEmpty(contactMechCriteria, "extension", phoneNumber.getExtension());
        }

        // postal address
        PostalAddressHelper postalAddressHelper = new PostalAddressHelper(postalAddress);
        if (postalAddress != null && !postalAddressHelper.isEmpty())
        {
            DetachedCriteria partyContactMechCriteria = partyCriteria.createCriteria("partyContactMechs");
            DetachedCriteria contactMechCriteria = partyContactMechCriteria.createCriteria("contactMech");
            contactMechCriteria.add(Restrictions.eq("contactMechType.contactMechTypeId",
                    contactMechTypeCache.getObject(ContactMechTypeKey.KEY_POSTAL_ADDRESS).getId()));
            addRestrictionIfNotEmpty(contactMechCriteria, "addressLine1", postalAddress.getAddressLine1());
            addRestrictionIfNotEmpty(contactMechCriteria, "addressLine2", postalAddress.getAddressLine2());
            addRestrictionIfNotEmpty(contactMechCriteria, "city", postalAddress.getCity());
            addRestrictionIfNotEmpty(contactMechCriteria, "postalCode", postalAddress.getPostalCode());
            if (postalAddress.getStateGeo() != null)
            {
                contactMechCriteria.add(Restrictions.eq("stateGeo.geoId", postalAddress.getStateGeo().getGeoId()));
            }
            if (postalAddress.getCountryGeo() != null)
            {
                contactMechCriteria.add(Restrictions.eq("countryGeo.geoId", postalAddress.getCountryGeo().getGeoId()));
            }
        }

        List<Party> contacts = null; //(List<Party>) getHibernateTemplate().findByCriteria(partyCriteria);
        log.debug("Found " + contacts.size() + " contacts matching search criteria.");

        PartyDaoHibernateImpl.lazyLoad(contacts, true, true, false, false);

        return new HashSet(contacts);
    }

	@Override
	public Class<Person> getPersistClass() {
		return Person.class;
	}

}
