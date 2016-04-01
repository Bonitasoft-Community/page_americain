package com.bonitasoft.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.GroupCreator;
import org.bonitasoft.engine.identity.GroupCriterion;
import org.bonitasoft.engine.identity.GroupNotFoundException;
import org.bonitasoft.engine.identity.GroupUpdater;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleCriterion;
import org.bonitasoft.engine.identity.RoleNotFoundException;

import com.bonitasoft.engine.api.ProfileAPI;

public class ItemGroup extends Item {

		public static final String cstItemName = "GROUP";

		public static final String cstGroupName = "GroupName";
		public final static String cstGroupDescription = "GroupDescription";
		public final static String cstGroupDisplayName = "GroupDisplayName";
		public final static String cstGroupIconName = "GroupIconName";
		public final static String cstGroupIconPath = "GroupIconPath";
		public final static String cstGroupParentPath = "GroupParentPath";

		/* ******************************************************************************** */
		/*																																									*/
		/* Different method to fullfill the object */
		/* NB : the setAttributes is in the Item class */
		/*																																									*/
		/* ******************************************************************************** */
		public ItemGroup() {
		};

		public ItemGroup(String groupName, String groupDescription, String groupDisplayName, String groupIconName, String groupIconPath, String groupParentPath) {
				itemInformation.put(cstGroupName, groupName);
				itemInformation.put(cstGroupDescription, groupDescription);
				itemInformation.put(cstGroupDisplayName, groupDisplayName);
				itemInformation.put(cstGroupIconName, groupIconName);
				itemInformation.put(cstGroupIconPath, groupIconPath);
				itemInformation.put(cstGroupParentPath, groupParentPath);
		}

		public void setGroupName(String groupName) {
				itemInformation.put(cstGroupName, groupName);
		}

		public void setGroupDescription(String groupDescription) {
				itemInformation.put(cstGroupDescription, groupDescription);
		}

		public void setGroupDisplayName(String groupDisplayName) {
				itemInformation.put(cstGroupDisplayName, groupDisplayName);
		}

		public void setGroupIconName(String groupIconName) {
				itemInformation.put(cstGroupIconName, groupIconName);
		}

		public void setGroupIconPath(String groupIconPath) {
				itemInformation.put(cstGroupIconPath, groupIconPath);
		}

		public void setGroupParentPath(String groupParentPath) {
				itemInformation.put(cstGroupParentPath, groupParentPath);
		}

		/* ******************************************************************************** */
		/*																																									*/
		/* Description */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		/**
		 * return the name of the item, which suppose to be uniq.
		 * 
		 * @return
		 */
		public String getTypeItem() {
				return cstItemName;
		}

		@Override
		// We reference the COMPLETE path (/acme for acme for example)
		public String getId() {
				return (itemInformation.get(cstGroupParentPath) + "/" + itemInformation.get(cstGroupName)).toLowerCase();
		}

		public String getFullFillDescription() {
				return "Parameters are " + getListAttributes().toString();
		}

		@Override
		public List<String> getListAttributes() {
				ArrayList<String> listAttributes = new ArrayList<String>();
				listAttributes.add(cstGroupName);
				listAttributes.add(cstGroupDescription);
				listAttributes.add(cstGroupDisplayName);
				listAttributes.add(cstGroupIconName);
				listAttributes.add(cstGroupIconPath);
				listAttributes.add(cstGroupParentPath);
				return listAttributes;

		}

		/* ******************************************************************************** */
		/*																																									*/
		/* Operation on server */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		/**
		 * now do the operation
		 * 
		 */
		protected void saveInServer(OrganizationAccess organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organizationLog) {
				GroupCreator groupCreator;
				GroupUpdater groupUpdater;

				if (itemInformation.get(cstGroupName) == null) {
						organizationLog.log(true,"OrganizationItemGroup.saveInServer", cstGroupName + " is mandatory");
				}
				if (itemInformation.get(cstGroupParentPath) == null)
						itemInformation.put(cstGroupParentPath, "");

				// organizationLog.log(false,
				// "GroupReferenece["+groupParentPath+"/"+groupName+"]");

				groupCreator = new GroupCreator(itemInformation.get(cstGroupName));
				groupUpdater = new GroupUpdater();

				groupCreator.setParentPath(itemInformation.get(cstGroupParentPath));
				// ATTENTION ATTENTION ATTENTION ! If we systematicaly call an update parent path, all sub group are updated by a new parent path
				// Example, root is Path=null,         name =/bonitasoft
				//          child   Path=/bonitasoft   name=/US
				
				// update the parent of this group cause the CHILD to have a new parent path:
				// child Path=/null/bonitasoft name=/US

				if (itemInformation.containsKey(cstGroupDescription)) {
						groupCreator.setDescription(itemInformation.get(cstGroupDescription));
						groupUpdater.updateDescription(itemInformation.get(cstGroupDescription));
				}
				if (itemInformation.containsKey(cstGroupDisplayName)) {
						groupCreator.setDisplayName(itemInformation.get(cstGroupDisplayName));
						groupUpdater.updateDisplayName(itemInformation.get(cstGroupDisplayName));
				}
				if (itemInformation.containsKey(cstGroupIconName)) {
						groupCreator.setIconName(itemInformation.get(cstGroupIconName));
						groupUpdater.updateIconName(itemInformation.get(cstGroupIconName));
				}
				if (itemInformation.containsKey(cstGroupIconPath)) {
						groupCreator.setIconPath(itemInformation.get(cstGroupIconPath));
						groupUpdater.updateIconPath(itemInformation.get(cstGroupIconPath));
				}

				if (parameterLoad.operationRoles == ParametersOperation.OperationOnItem.NONE)
						return;

				Group group = null;
				String groupPath = itemInformation.get(cstGroupParentPath) + "/" + itemInformation.get(cstGroupName);
				try {
						group = identityAPI.getGroupByPath(groupPath);
				} catch (GroupNotFoundException e1) {
						group = null;
				}

				if (group == null && parameterLoad.operationGroups == ParametersOperation.OperationOnItem.UPDATEONLY) {
						organizationLog.log(false, "OrganizationItemGroup.saveInServer","Group[" + groupPath + "] does not exist and no insert allowed");
						return;
				}
				if (group != null && parameterLoad.operationGroups == ParametersOperation.OperationOnItem.INSERTONLY) {
						organizationLog.log(false, "OrganizationItemGroup.saveInServer"," Group[" + groupPath + "] exist and no update allowed");
						return;
				}

				if (group == null) {
						// a creation
						try {
								isCreated = true;

								organizationLog.log(false, "OrganizationItemGroup.saveInServer","Insert Group[" + itemInformation.get(cstGroupName) + "] path[" + itemInformation.get(cstGroupParentPath) + "]");
								Group groupCreated = identityAPI.createGroup(groupCreator);
								bonitaId = groupCreated.getId();
						} catch (AlreadyExistsException e) {
								organizationLog.log(true, "OrganizationItemGroup.saveInServer","Group[" + itemInformation.get(cstGroupName) + "] path[" + itemInformation.get(cstGroupParentPath) + "] already exist");
						} catch (CreationException e) {
								organizationLog.log(true, "OrganizationItemGroup.saveInServer","Group[" + itemInformation.get(cstGroupName) + "] path[" + itemInformation.get(cstGroupParentPath) + "] Error at creation " + e.toString());
						}
				} else {
						// an update
						try {
								isCreated = false;
								String currentParentPath = group.getParentPath()==null ? "" : group.getParentPath();
								if (	! itemInformation.get(cstGroupParentPath).equals(currentParentPath) )
										groupUpdater.updateParentPath(itemInformation.get(cstGroupParentPath));

								organizationLog.log(false, "OrganizationItemGroup.saveInServer","Update Group[" + itemInformation.get(cstGroupName) + "] Id[" + group.getId() + "]");
								identityAPI.updateGroup(group.getId(), groupUpdater);
								bonitaId = group.getId();
						} catch (GroupNotFoundException e) {
								organizationLog.log(true, "OrganizationItemGroup.saveInServer","Group[" + itemInformation.get(cstGroupName) + "] not exist");
						} catch (UpdateException e) {
								organizationLog.log(true, "OrganizationItemGroup.saveInServer","Group[" + itemInformation.get(cstGroupName) + "] Error at update " + e.toString());
						}
				}
				return;
		}

		/**
		 * to manage the purge, two step : register all item first. Then when an
		 * item is loaded/updated, the list is reduced. At the end, all pending
		 * information must be deleted
		 * 
		 * @param statisticOnItemUser
		 * @param identityAPI
		 * @param organisationLog
		 */
		protected static void photoAll(Item.StatisticOnItem statisticOnItemGroup, IdentityAPI identityAPI, OrganizationLog organisationLog) {
				int index = 0;
				while (true) {
						List<Group> listGroups = identityAPI.getGroups(index, 1000, GroupCriterion.NAME_ASC);
						index += 1000;
						if (listGroups.size() == 0)
								return;
						for (Group group : listGroups) {
								organisationLog.log(false, "OrganizationItemGroup.photo"," ParentPath[" + group.getParentPath() + "] Path[" + group.getPath() + "] Name[" + group.getName() + "]");
								// We reference the COMPLETE path (/acme for acme for example)
								// We must reference the same as the ID
								statisticOnItemGroup.listKeyItem.add(group.getId());
						}
				}
		}

		protected static void purgeFromList(Item.StatisticOnItem statisticOnItemGroup, ParametersOperation parametersLoad, IdentityAPI identityAPI, OrganizationLog organisationLog) {
				// all elements in the list should be clean. ATTENTION, clean member too
				ArrayList<Long> listIdToDelete = new ArrayList<Long>();
				for (Long groupId : statisticOnItemGroup.listKeyItem) {
						listIdToDelete.add( groupId);
				}

				// now delete the role
				try {
						identityAPI.deleteGroups(listIdToDelete);
				} catch (DeletionException e) {
						organisationLog.log(true, "OrganizationItemGroup.purgeFromList"," Can't delete Groups :" + e.toString());
				}
				statisticOnItemGroup.nbPurgedItem = listIdToDelete.size();
		}

}
