package com.bonitasoft.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.ContactDataCreator;
import org.bonitasoft.engine.identity.ContactDataUpdater;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCreator;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.identity.UserUpdater;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.organization.ParametersOperation.RegisterNewUserInProfile;

public class ItemUser extends Item {

		public static final String cstItemName = "USER";

		public static final String cstUserName = "UserName";
		public static final String cstUserPassword = "UserPassword";
		public static final String cstUserEnable = "UserEnable";
		public static final String cstUserFirstName = "UserFirstName";
		public static final String cstUserLastName = "UserLastName";
		public static final String cstUserIconName = "UserIconName";
		public static final String cstUserIconPath = "UserIconPath";
		public static final String cstUserTitle = "UserTitle";
		public static final String cstUserJobTitle = "UserJobTitle";
		public static final String cstUserManagerUserName = "UserManagerUserName";

		public static final String cstContactAddress = "ContactAddress";
		public static final String cstContactBuilding = "ContactBuilding";
		public static final String cstContactCity = "ContactCity";
		public static final String cstContactCountry = "ContactCountry";
		public static final String cstContactEmail = "ContactEmail";
		public static final String cstContactFaxNumber = "ContactFaxNumber";
		public static final String cstContactMobileNumber = "ContactMobileNumber";
		public static final String cstContactPhoneNumber = "ContactPhoneNumber";
		public static final String cstContactRoom = "ContactRoom";
		public static final String cstContactState = "ContactState";
		public static final String cstContactWebsite = "ContactWebsite";
		public static final String cstContactZipCode = "ContactZipCode";

		/* ******************************************************************************** */
		/*																																									*/
		/* Different method to fullfill the object */
		/* NB : the setAttributes is in the Item class */
		/*																																									*/
		/* ******************************************************************************** */
		public ItemUser() {
		};

		public ItemUser(final String userName, final String userPassword, final boolean userEnable, final String userFirstName, final String userLastName, final String userIconName, final String userIconPath, final String userTitle,
						final String userJobTitle, final String userManagerUserName, final String proContactAddress, final String proContactBuilding, final String proContactCity, final String proContactCountry, final String proContactEmail,
						final String proContactFaxNumber, final String proContactMobileNumber, final String proContactPhoneNumber, final String proContactRoom, final String proContactState, final String proContactWebSite, final String proContactZipCode,
						final String perContactAddress, final String perContactBuilding, final String perContactCity, final String perContactCountry, final String perContactEmail, final String perContactFaxNumber, final String perContactMobileNumber,
						final String perContactPhoneNumber, final String perContactRoom, final String perContactState, final String perContactWebSite, final String perContactZipCode) {

				itemInformation.put(cstUserName, userName);
				itemInformation.put(cstUserPassword, userPassword);
				itemInformation.put(cstUserEnable, Boolean.toString(userEnable));
				itemInformation.put(cstUserFirstName, userFirstName);
				itemInformation.put(cstUserLastName, userLastName);
				itemInformation.put(cstUserIconName, userIconName);
				itemInformation.put(cstUserIconPath, userIconPath);
				itemInformation.put(cstUserTitle, userTitle);
				itemInformation.put(cstUserJobTitle, userJobTitle);
				itemInformation.put(cstUserManagerUserName, userManagerUserName);

				itemInformation.put("PRO" + cstContactAddress, perContactAddress);
				itemInformation.put("PRO" + cstContactBuilding, proContactBuilding);
				itemInformation.put("PRO" + cstContactCity, proContactCity);
				itemInformation.put("PRO" + cstContactCountry, proContactCountry);
				itemInformation.put("PRO" + cstContactEmail, proContactEmail);
				itemInformation.put("PRO" + cstContactFaxNumber, proContactFaxNumber);
				itemInformation.put("PRO" + cstContactMobileNumber, proContactMobileNumber);
				itemInformation.put("PRO" + cstContactPhoneNumber, proContactPhoneNumber);
				itemInformation.put("PRO" + cstContactRoom, proContactRoom);
				itemInformation.put("PRO" + cstContactState, proContactState);
				itemInformation.put("PRO" + cstContactWebsite, proContactWebSite);
				itemInformation.put("PRO" + cstContactZipCode, proContactZipCode);

				itemInformation.put("PER" + cstContactAddress, perContactAddress);
				itemInformation.put("PER" + cstContactBuilding, perContactBuilding);
				itemInformation.put("PER" + cstContactCity, perContactCity);
				itemInformation.put("PER" + cstContactCountry, perContactCountry);
				itemInformation.put("PER" + cstContactEmail, perContactEmail);
				itemInformation.put("PER" + cstContactFaxNumber, perContactFaxNumber);
				itemInformation.put("PER" + cstContactMobileNumber, perContactMobileNumber);
				itemInformation.put("PER" + cstContactPhoneNumber, perContactPhoneNumber);
				itemInformation.put("PER" + cstContactRoom, perContactRoom);
				itemInformation.put("PER" + cstContactState, perContactState);
				itemInformation.put("PER" + cstContactWebsite, perContactWebSite);
				itemInformation.put("PER" + cstContactZipCode, perContactZipCode);

		}

		public void setUserName(final String userName) {
				itemInformation.put(cstUserName, userName);
		}

		public void setUserPassword(final String userPassword) {
				itemInformation.put(cstUserPassword, userPassword);
		}

		public void setUserEnable(final boolean userEnable) {
				itemInformation.put(cstUserEnable, String.valueOf(userEnable));
		}

		public void setUserFirstName(final String userFirstName) {
				itemInformation.put(cstUserFirstName, userFirstName);
		}

		public void setUserLastName(final String userLastName) {
				itemInformation.put(cstUserLastName, userLastName);
		}

		public void setUserIconName(final String userIconName) {
				itemInformation.put(cstUserIconName, userIconName);
		}

		public void setUserIconPath(final String userIconPath) {
				itemInformation.put(cstUserIconPath, userIconPath);
		}

		public void setUserTitle(final String userTitle) {
				itemInformation.put(cstUserTitle, userTitle);
		}
		public void setUserJobTitle(final String userJobTitle) {
				itemInformation.put(cstUserJobTitle, userJobTitle);
		}

		public void setUserManagemerUserName(final String userManagemerUserName) {
				itemInformation.put(cstUserManagerUserName, userManagemerUserName);
		}

		public void setProContactAddress(final String proContactAddress) {
				itemInformation.put("PRO" + cstContactAddress, proContactAddress);
		}

		public void setProContactBuilding(final String proContactBuilding) {
				itemInformation.put("PRO" + cstContactBuilding, proContactBuilding);
		}

		public void setProContactCity(final String proContactCity) {
				itemInformation.put("PRO" + cstContactCity, proContactCity);
		}

		public void setProContactCountry(final String proContactCountry) {
				itemInformation.put("PRO" + cstContactCountry, proContactCountry);
		}

		public void setProContactEmail(final String proContactEmail) {
				itemInformation.put("PRO" + cstContactEmail, proContactEmail);
		}

		public void setProContactFaxNumber(final String proContactFaxNumber) {
				itemInformation.put("PRO" + cstContactFaxNumber, proContactFaxNumber);
		}

		public void setProContactMobileNumber(final String proContactMobileNumber) {
				itemInformation.put("PRO" + cstContactMobileNumber, proContactMobileNumber);
		}

		public void setProContactPhoneNumber(final String proContactPhoneNumber) {
				itemInformation.put("PRO" + cstContactPhoneNumber, proContactPhoneNumber);
		}

		public void setProContactRoom(final String proContactRoom) {
				itemInformation.put("PRO" + cstContactRoom, proContactRoom);
		}

		public void setProContactState(final String proContactState) {
				itemInformation.put("PRO" + cstContactState, proContactState);
		}

		public void setProContactWebSite(final String proContactWebSite) {
				itemInformation.put("PRO" + cstContactWebsite, proContactWebSite);
		}

		public void setProContactZipCode(final String proContactZipCode) {
				itemInformation.put("PRO" + cstContactZipCode, proContactZipCode);
		}

		public void setPerContactAddress(final String proContactAddress) {

				itemInformation.put("PER" + cstContactAddress, proContactAddress);
		}

		public void setPerContactBuilding(final String perContactBuilding) {
				itemInformation.put("PER" + cstContactBuilding, perContactBuilding);
		}

		public void setPerContactCity(final String perContactCity) {
				itemInformation.put("PER" + cstContactCity, perContactCity);
		}

		public void setPerContactCountry(final String perContactCountry) {
				itemInformation.put("PER" + cstContactCountry, perContactCountry);
		}

		public void setPerContactEmail(final String perContactEmail) {
				itemInformation.put("PER" + cstContactEmail, perContactEmail);
		}

		public void setPerContactFaxNumber(final String perContactFaxNumber) {
				itemInformation.put("PER" + cstContactFaxNumber, perContactFaxNumber);
		}

		public void setPerContactMobileNumber(final String perContactMobileNumber) {
				itemInformation.put("PER" + cstContactMobileNumber, perContactMobileNumber);
		}

		public void setPerContactPhoneNumber(final String perContactPhoneNumber) {
				itemInformation.put("PER" + cstContactPhoneNumber, perContactPhoneNumber);
		}

		public void setPerContactRoom(final String perContactRoom) {
				itemInformation.put("PER" + cstContactRoom, perContactRoom);
		}

		public void setPerContactState(final String perContactState) {
				itemInformation.put("PER" + cstContactState, perContactState);
		}

		public void setPerContactWebSite(final String perContactWebSite) {
				itemInformation.put("PER" + cstContactWebsite, perContactWebSite);
		}

		public void setPerContactZipCode(final String perContactZipCode) {
				itemInformation.put("PER" + cstContactZipCode, perContactZipCode);
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
		@Override
        public String getTypeItem() {
				return cstItemName;
		}

		@Override
		public String getId() {
				return itemInformation.get(cstUserName).toLowerCase();
		}



		@Override
        public String getFullFillDescription() {
				final String info = "Parameters ares " + getListAttributes().toString();
				return info;

		}

		@Override
        public List<String> getListAttributes() {
				final ArrayList<String> listAttributes = new ArrayList<String>();
				listAttributes.add(cstUserName);
				listAttributes.add(cstUserPassword);
				listAttributes.add(cstUserEnable);
				listAttributes.add(cstUserFirstName);
				listAttributes.add(cstUserLastName);
				listAttributes.add(cstUserIconName);
				listAttributes.add(cstUserIconPath);
				listAttributes.add(cstUserTitle);
				listAttributes.add(cstUserJobTitle);
				listAttributes.add(cstUserManagerUserName);

				listAttributes.add("PRO" + cstContactAddress);
				listAttributes.add("PRO" + cstContactBuilding);
				listAttributes.add("PRO" + cstContactCity);
				listAttributes.add("PRO" + cstContactCountry);
				listAttributes.add("PRO" + cstContactEmail);
				listAttributes.add("PRO" + cstContactFaxNumber);
				listAttributes.add("PRO" + cstContactMobileNumber);
				listAttributes.add("PRO" + cstContactPhoneNumber);
				listAttributes.add("PRO" + cstContactRoom);
				listAttributes.add("PRO" + cstContactState);
				listAttributes.add("PRO" + cstContactWebsite);
				listAttributes.add("PRO" + cstContactZipCode);

				listAttributes.add("PER" + cstContactAddress);
				listAttributes.add("PER" + cstContactBuilding);
				listAttributes.add("PER" + cstContactCity);
				listAttributes.add("PER" + cstContactCountry);
				listAttributes.add("PER" + cstContactEmail);
				listAttributes.add("PER" + cstContactFaxNumber);
				listAttributes.add("PER" + cstContactMobileNumber);
				listAttributes.add("PER" + cstContactPhoneNumber);
				listAttributes.add("PER" + cstContactRoom);
				listAttributes.add("PER" + cstContactState);
				listAttributes.add("PER" + cstContactWebsite);
				listAttributes.add("PER" + cstContactZipCode);

				return listAttributes;
		}

		/* ******************************************************************************** */
		/*																																									*/
		/* Operation on server */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		/**
		 * saveInServer
		 */
		@Override
		protected void saveInServer(final OrganizationAccess organizationAccess, final ParametersOperation parameterLoad, final IdentityAPI identityAPI, final ProfileAPI profileAPI, final OrganizationLog organizationLog) {

				if (itemInformation.get(cstUserName) == null || itemInformation.get(cstUserName).length() == 0) {
						organizationLog.log(true, "ItemMemberUser.saveInServer",cstUserName + " is mandatory, ");
						return;
				}

				final UserCreator userCreator = new UserCreator(itemInformation.get(cstUserName), itemInformation.get(cstUserPassword));
				final UserUpdater userUpdator = new UserUpdater();

				if (itemInformation.containsKey(cstUserEnable)) {
						userCreator.setEnabled(Boolean.valueOf(itemInformation.get(cstUserEnable)));
						userUpdator.setEnabled(Boolean.valueOf(itemInformation.get(cstUserEnable)));
				}

				if (itemInformation.containsKey(cstUserFirstName)) {
						userCreator.setFirstName(itemInformation.get(cstUserFirstName));
						userUpdator.setFirstName(itemInformation.get(cstUserFirstName));
				}

				if (itemInformation.containsKey(cstUserLastName)) {
						userCreator.setLastName(itemInformation.get(cstUserLastName));
						userUpdator.setLastName(itemInformation.get(cstUserLastName));
				}

				if (itemInformation.containsKey(cstUserIconName)) {
						userCreator.setIconName(itemInformation.get(cstUserIconName));
						userUpdator.setIconName(itemInformation.get(cstUserIconName));
				}

				if (itemInformation.containsKey(cstUserIconPath)) {
						userCreator.setIconPath(itemInformation.get(cstUserIconPath));
						userUpdator.setIconPath(itemInformation.get(cstUserIconPath));
				}

				if (itemInformation.containsKey(cstUserTitle)) {
						userCreator.setTitle(itemInformation.get(cstUserTitle));
						userUpdator.setTitle(itemInformation.get(cstUserTitle));
				}

				if (itemInformation.containsKey(cstUserJobTitle)) {
						userCreator.setJobTitle(itemInformation.get(cstUserJobTitle));
						userUpdator.setJobTitle(itemInformation.get(cstUserJobTitle));
				}
				if (itemInformation.get(cstUserManagerUserName) != null && itemInformation.get(cstUserManagerUserName).length() > 0) {
						User user;
						try {
								user = identityAPI.getUserByUserName(itemInformation.get(cstUserManagerUserName));

								if (user == null) {
                                    organizationLog.log(true,  "ItemMemberUser.saveInServer","Manager [" + itemInformation.get(cstUserManagerUserName) + "] of user [" + itemInformation.get(cstUserName) + "] not found");
                                } else {
										userCreator.setManagerUserId(user.getId());
										userUpdator.setManagerId(user.getId());
								}
						} catch (final UserNotFoundException e) {
								organizationLog.log(true,  "ItemMemberUser.saveInServer", getLogContextualInformation()+"Manager [" + itemInformation.get(cstUserManagerUserName) + "] of user [" + itemInformation.get(cstUserName) + "] :" + e.toString());
						}
				}

				ResultFunction resultFunction = getContactDataFromHashmap("PRO", itemInformation);
				userCreator.setProfessionalContactData(resultFunction.contactCreator);
				userUpdator.setProfessionalContactData(resultFunction.contactUpdator);

				resultFunction = getContactDataFromHashmap("PER", itemInformation);
				userCreator.setPersonalContactData(resultFunction.contactCreator);
				userUpdator.setPersonalContactData(resultFunction.contactUpdator);


				if (parameterLoad.operationUsers == ParametersOperation.OperationOnItem.NONE) {
                    return;
                }

				User user = null;
				String logItemOperation="User ["+itemInformation.get(cstUserName)+"] ";
				boolean logItemLevelError=false;
				try {
						user = identityAPI.getUserByUserName(itemInformation.get(cstUserName));
				} catch (final UserNotFoundException e1) {
						user = null;
				}

				if (user == null && parameterLoad.operationUsers == ParametersOperation.OperationOnItem.UPDATEONLY) {
						organizationLog.log(false,  "ItemMemberUser.saveInServer","User[" + itemInformation.get(cstUserName) + "] does not exist and no insert allowed");
						return;
				}
				if (user != null && parameterLoad.operationUsers == ParametersOperation.OperationOnItem.INSERTONLY) {
						organizationLog.log(false,  "ItemMemberUser.saveInServer","User[" + itemInformation.get(cstUserName) + "] not exist and no update allowed");
						return;
				}

				if (user == null) {
						// a creation
						try {
								isCreated = true;
								logItemOperation+= "Insert ";
								final User userCreated = identityAPI.createUser(userCreator);
								bonitaId = userCreated.getId();
								// now, do the registration policy
								if (parameterLoad.registerNewUserInProfileUser == RegisterNewUserInProfile.ALWAYSUSERPROFILE)
								{
										logItemOperation+="Register in profile["+ItemProfile.cstProfileNameUser+"]";
										organizationAccess.registerInUserProfile(userCreated.getId(), profileAPI, organizationLog);
								} else if (parameterLoad.registerNewUserInProfileUser == RegisterNewUserInProfile.USERPROFILEIFNOTREGISTER)
								{
										logItemOperation+="Register to check profile existance";
										organizationAccess.registerInUserProfileIfNeeded(userCreated.getId(),profileAPI, organizationLog );
								}

						} catch (final AlreadyExistsException e) {
								organizationLog.log(true,  "ItemMemberUser.saveInServer","User[" + itemInformation.get(cstUserName) + "] already exist");
						} catch (final CreationException e) {
								organizationLog.log(true,  "ItemMemberUser.saveInServer","User[" + itemInformation.get(cstUserName) + "] Error at creation " + e.toString());
						}
				} else {
						// an update
						try {
								isCreated = false;
								logItemOperation+="Update userId["+user.getId() + "]";
								identityAPI.updateUser(user.getId(), userUpdator);
								bonitaId = user.getId();

						} catch (final UserNotFoundException e) {
								logItemOperation+=" User Not Found";
								logItemLevelError=true;
						} catch (final UpdateException e) {
								logItemOperation+= " Update error";
								logItemLevelError=true;
						}
				}
				organizationLog.log(logItemLevelError,  "ItemMemberUser.saveInServer", getLogContextualInformation()+logItemOperation );
				return;
		}

		private class ResultFunction {
				ContactDataCreator contactCreator = new ContactDataCreator();
				ContactDataUpdater contactUpdator = new ContactDataUpdater();
		}

    private ResultFunction getContactDataFromHashmap(final String prefix, final Map<String, String> value) {
				final ResultFunction resultFunction = new ResultFunction();
				if (value.containsKey(prefix + cstContactAddress)) {
						resultFunction.contactCreator.setAddress(value.get(prefix + cstContactAddress));
						resultFunction.contactUpdator.setAddress(value.get(prefix + cstContactAddress));
				}

				if (value.containsKey(prefix + cstContactBuilding)) {
						resultFunction.contactCreator.setBuilding(value.get(prefix + cstContactBuilding));
						resultFunction.contactUpdator.setBuilding(value.get(prefix + cstContactBuilding));
				}

				if (value.containsKey(prefix + cstContactCity)) {
						resultFunction.contactCreator.setCity(value.get(prefix + cstContactCity));
						resultFunction.contactUpdator.setCity(value.get(prefix + cstContactCity));
				}

				if (value.containsKey(prefix + cstContactCountry)) {
						resultFunction.contactCreator.setCountry(value.get(prefix + cstContactCountry));
						resultFunction.contactUpdator.setCountry(value.get(prefix + cstContactCountry));
				}

				if (value.containsKey(prefix + cstContactEmail)) {
						resultFunction.contactCreator.setEmail(value.get(prefix + cstContactEmail));
						resultFunction.contactUpdator.setEmail(value.get(prefix + cstContactEmail));
				}

				if (value.containsKey(prefix + cstContactFaxNumber)) {
						resultFunction.contactCreator.setFaxNumber(value.get(prefix + cstContactFaxNumber));
						resultFunction.contactUpdator.setFaxNumber(value.get(prefix + cstContactFaxNumber));
				}

				if (value.containsKey(prefix + cstContactMobileNumber)) {
						resultFunction.contactCreator.setMobileNumber(value.get(prefix + cstContactMobileNumber));
						resultFunction.contactUpdator.setMobileNumber(value.get(prefix + cstContactMobileNumber));
				}

				if (value.containsKey(prefix + cstContactPhoneNumber)) {
						resultFunction.contactCreator.setPhoneNumber(value.get(prefix + cstContactPhoneNumber));
						resultFunction.contactUpdator.setPhoneNumber(value.get(prefix + cstContactPhoneNumber));
				}

				if (value.containsKey(prefix + cstContactRoom)) {
						resultFunction.contactCreator.setRoom(value.get(prefix + cstContactRoom));
						resultFunction.contactUpdator.setRoom(value.get(prefix + cstContactRoom));
				}

				if (value.containsKey(prefix + cstContactState)) {
						resultFunction.contactCreator.setState(value.get(prefix + cstContactState));
						resultFunction.contactUpdator.setState(value.get(prefix + cstContactState));
				}

				if (value.containsKey(prefix + cstContactWebsite)) {
						resultFunction.contactCreator.setWebsite(value.get(prefix + cstContactWebsite));
						resultFunction.contactUpdator.setWebsite(value.get(prefix + cstContactWebsite));
				}

				if (value.containsKey(prefix + cstContactZipCode)) {
						resultFunction.contactCreator.setZipCode(value.get(prefix + cstContactZipCode));
						resultFunction.contactUpdator.setZipCode(value.get(prefix + cstContactZipCode));
				}
				return resultFunction;
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
		public static void photoAll(final Item.StatisticOnItem statisticOnItemUser, final IdentityAPI identityAPI, final OrganizationLog organisationLog) {

				int index = 0;
				while (true) {
						final List<User> listUsers = identityAPI.getUsers(index, 1000, UserCriterion.USER_NAME_ASC);
						index += 1000;
						if (listUsers.size() == 0) {
                            return;
                        }
						for (final User user : listUsers) {
								organisationLog.log(false, "OrganizationItemUser.photo"," User[" + user.getUserName() + "]");
								// We reference the same as the ID
								statisticOnItemUser.listKeyItem.add(user.getId());
						}
				}
		}

		/**
		 *
		 * @param statisticOnItemUser
		 * @param identityAPI
		 * @param organisationLog
		 */
		public static void purgeFromList(final Item.StatisticOnItem statisticOnItemUser, final ParametersOperation parametersLoad, final IdentityAPI identityAPI, final OrganizationLog organisationLog) {
				final ArrayList<Long> listIdToDelete = new ArrayList<Long>();
				for (final Long userId : statisticOnItemUser.listKeyItem) {
						listIdToDelete.add( userId );
				}
			// now delete the role
				try {
						identityAPI.deleteUsers(listIdToDelete);
				} catch (final DeletionException e) {
						organisationLog.log(true, "OrganizationItemGroup.purgeFromList"," Can't delete Groups :" + e.toString());
				}
				statisticOnItemUser.nbPurgedItem = listIdToDelete.size();
		}

}
