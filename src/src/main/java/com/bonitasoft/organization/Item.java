package com.bonitasoft.organization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;

import com.bonitasoft.engine.api.ProfileAPI;

public abstract class Item {

		/**
		 * contains all attributes need for the item
		 */
    protected Map<String, String> itemInformation = new HashMap<String, String>();

		/** the source can give some contextual information on the source (for example, for a CSV loader, it maybe the line number. If the contextual information is set, then the log display it */

		public String contextualInformation="";


		/**
		 * available after the save
		 */
		protected Long bonitaId=null;

		public static class StatisticOnItem
		{
				public long nbCreatedItem=0;
				public long nbUpdatedItem=0;
				public long nbPurgedItem=0;
				public HashSet<Long> listKeyItem = new HashSet<Long>();
		}


		/** according the itemName, return the correct class. Example : it itemName is GROUP, then return a ItemGroup
		 *
		 * @param itemName
		 * @return
		 */
		public static Item getInstance( final String itemName )
		{
				if (ItemUser.cstItemName.equals(itemName) ) {
                    return new ItemUser();
                }
				if (ItemMembership.cstItemName.equals(itemName) ) {
                    return new ItemMembership();
                }
				if (ItemGroup.cstItemName.equals(itemName) ) {
                    return new ItemGroup();
                }
				if (ItemRole.cstItemName.equals(itemName) ) {
                    return new ItemRole();
                }
				if (ItemProfile.cstItemName.equals(itemName) ) {
                    return new ItemProfile();
                }
				if (ItemProfileMember.cstItemName.equals(itemName) ) {
                    return new ItemProfileMember();
                }

				return null;
		}

		/**
		 * the name is the type of the OrganisationItem
		 * @return
		 */
		public abstract String getTypeItem();
		/**
		 * the id is the ID of the object, it is different for each Item
		 *
		 * @return
		 */
		public abstract String getId();

		/**
		 * return the BonitaId. This information is available only after the save
		 * @return
		 */
		public Long getBonitaId()
		{
				return bonitaId;
		}
		/**
		 * from an hashmap, fullfill the organisationitem
		 * @param value
		 * @param identityAPI
		 * @param organizationLog
		 * @return
		 */
    public String setAttributes(final Map<String, String> value, final OrganizationLog organizationLog)
		{
				itemInformation = value;
				String report="";
				// check if they are some no-waiting attributes
				final List<String> listItemAttributes = getListAttributes();
				for (final String key : itemInformation.keySet())
				{
						if (! listItemAttributes.contains( key )) {
                            report+="["+key+"] not expected;";
                        }
				}
				if (report.length()==0) {
                    return null;
                }
				report += "Expected attributes :"+listItemAttributes.toString();
				return report;

		}

		/**
		 * return a description on the object
		 * @return
		 */
		public abstract String getFullFillDescription();

		/**
		 * return the list of all expected attributes
		 * @return
		 */
		public abstract List<String> getListAttributes();



		/**
		 * Attention, this method MUST set the bonitaId data
		 * @param organizationAccess TODO
		 * @param parameterLoad
		 * @param identityAPI
		 * @param profileAPI
		 * @param organisationLog
		 */
		protected abstract void saveInServer(OrganizationAccess organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organisationLog);


		/**
		 * keep the fact the item was CREATED or UPDATED in the server
		 */
		protected boolean isCreated=false;
		public boolean isCreated() {
				return isCreated;
		}

		public boolean isPurge( final ParametersOperation parametersOperation )
		{
				final String itemName = getTypeItem();
				if (ItemUser.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeUsers;
                }
				if (ItemGroup.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeGroups;
                }
				if (ItemRole.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeRoles;
                }
				if (ItemMembership.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeMembership;
                }
				if (ItemProfile.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeProfile;
                }
				if (ItemProfileMember.cstItemName.equals(itemName) ) {
                    return parametersOperation.purgeProfileMember;
                }
				return false;
		}


		public String getLogContextualInformation()
		{ return contextualInformation; };
}
