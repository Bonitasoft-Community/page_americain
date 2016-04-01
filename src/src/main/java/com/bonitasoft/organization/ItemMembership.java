package com.bonitasoft.organization;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.GroupNotFoundException;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleCriterion;
import org.bonitasoft.engine.identity.RoleNotFoundException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.identity.UserMembership;
import org.bonitasoft.engine.identity.UserMembershipCriterion;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.api.ProfileAPI;

public class ItemMembership extends Item {

		public static final String cstItemName = "MEMBERSHIP";

		public static final String cstUserName = "UserName";
		public static final String cstGroupPath = "GroupPath";
		public static final String cstRoleName = "RoleName";

		/* ******************************************************************************** */
		/*																																									*/
		/* Different method to fullfill the object */
		/* NB : the setAttributes is in the Item class */
		/*																																									*/
		/* ******************************************************************************** */
		public ItemMembership() {
		};

		public ItemMembership(String userName, String groupPath, String roleName) {
				itemInformation.put(cstUserName, userName);
				itemInformation.put(cstGroupPath, groupPath);
				itemInformation.put(cstRoleName, roleName);
		}

		public void setUserName(String userName) {
				itemInformation.put(cstUserName, userName);
		}

		public void setGroupPath(String groupPath) {
				itemInformation.put(cstGroupPath, groupPath);
		};

		public void setRoleName(String roleName) {
				itemInformation.put(cstRoleName, roleName);
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
		public String getId() {
				return (itemInformation.get(cstUserName) + "#" + itemInformation.get(cstGroupPath) + "#" + itemInformation.get(cstRoleName)).toLowerCase();
		}

		public String getFullFillDescription() {
				return "Parameters are " + getListAttributes().toString();
		}

		@Override
		public List<String> getListAttributes() {
				ArrayList<String> listAttributes = new ArrayList<String>();
				listAttributes.add(cstUserName);
				listAttributes.add(cstGroupPath);
				listAttributes.add(cstRoleName);
				return listAttributes;
		}

		/* ******************************************************************************** */
		/*																																									*/
		/* Operation on server */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		/**
		 * 
		 */
		public void saveInServer(OrganizationAccess organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organizationLog) {

				if (itemInformation.get(cstUserName) == null) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", cstUserName + " is mandatory");
						return;
				}
				if (itemInformation.get(cstGroupPath) == null) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", cstGroupPath + " is mandatory");
						return;
				}
				if (itemInformation.get(cstRoleName) == null) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", cstRoleName + " is mandatory");
						return;
				}
				User user=null;
				Group group =null;
				Role role=null;
				try {
						user = identityAPI.getUserByUserName(itemInformation.get(cstUserName));
						group = identityAPI.getGroupByPath(itemInformation.get(cstGroupPath));
						role = identityAPI.getRoleByName(itemInformation.get(cstRoleName));
						identityAPI.addUserMembership(user.getId(), group.getId(), role.getId());
						isCreated = true;

				} catch (RoleNotFoundException e1) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] notFound");
				} catch (UserNotFoundException e2) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", "User[" + itemInformation.get(cstUserName) + "] notFound");
				} catch (GroupNotFoundException e) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", "GroupPath[" + itemInformation.get(cstGroupPath) + "] notFound");
				} catch (AlreadyExistsException e) {
						isCreated = false;
						UserMembership userMembership = searchMemberships( user.getId(), group.getId(), role.getId(), identityAPI);
						if ( userMembership!=null)
								bonitaId =  userMembership.getId();

				} catch (CreationException e) {
						organizationLog.log(true, "ItemMemberShip.saveInServer", "Cant' create User[" + itemInformation.get(cstUserName) + "] Group[" + itemInformation.get(cstGroupPath) + "] Role["
										+ itemInformation.get(cstRoleName) + "] Error " + e.toString());
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

		protected static void photoAll(Item.StatisticOnItem statisticOnItemMembership, Item.StatisticOnItem statisticOnItemRole, IdentityAPI identityAPI, OrganizationLog organisationLog) {
				try {
						int index = 0;
						while (true) {
								List<User> listUsers = identityAPI.getUsers(index, 1000, UserCriterion.USER_NAME_ASC);
								index += 1000;
								if (listUsers.size() == 0) {
										organisationLog.log(false, "ItemMemberShip.photoAll", "List of membership found " + statisticOnItemMembership.listKeyItem.toString() + "]");
										return;
								}

								for (User user : listUsers) {
										// loop per role
										int subIndex = 0;
										List<UserMembership> listMembership;
										do {
												listMembership = identityAPI.getUserMemberships(user.getId(), subIndex, 1000, UserMembershipCriterion.ASSIGNED_DATE_ASC);
												subIndex += 1000;
												for (UserMembership userMemberShip : listMembership) {
														statisticOnItemMembership.listKeyItem.add(userMemberShip.getId());
												}
										} while (listMembership.size() > 0);
								}
						}
				} catch (Exception e) {
						organisationLog.log(true, "ItemProfile.photoAll", "Errorwhen get list of all profile " + e.toString());
				}

		}

		protected static void purgeFromList(Item.StatisticOnItem statisticOnItemMembership, ParametersOperation parametersLoad, IdentityAPI identityAPI, OrganizationLog organisationLog) {
				for (Long membership : statisticOnItemMembership.listKeyItem) {
						try {
								identityAPI.deleteUserMembership(membership);
						} catch (DeletionException e) {
								organisationLog.log(true, "OrganizationItemGroup.purgeFromList", " Can't delete Groups :" + e.toString());
						}
				}
		}
		
		/**
		 * search the membership for the user, group, role
		 * @param userId
		 * @param groupId
		 * @param roleId
		 * @param identityAPI
		 * @return
		 */
		private UserMembership searchMemberships( Long userId, Long groupId, Long roleId, IdentityAPI identityAPI)
		{
				// assuming there are less then 1000 item
				List<UserMembership> listMembership = identityAPI.getUserMemberships(userId, 0, 10000, UserMembershipCriterion.ASSIGNED_DATE_ASC);
				for (UserMembership userMemberShip : listMembership)
				{
						if (userMemberShip.getRoleId() == roleId && userMemberShip.getGroupId() == groupId)
								return userMemberShip;
								
				}
				return null;                        
		}
}
