package org.bonitasoft.americanorganization;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.americanorganization.ParametersOperation.RegisterNewUserInProfile;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.GroupNotFoundException;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleNotFoundException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserMembership;
import org.bonitasoft.engine.identity.UserMembershipCriterion;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileMember;
import org.bonitasoft.engine.profile.ProfileMemberCreator;
import org.bonitasoft.engine.profile.ProfileMemberSearchDescriptor;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.api.ProfileAPI;

public class ItemProfileMember extends Item {

	public static final String cstItemName = "PROFILEMEMBER";
	public static final String cstProfileName = "ProfileName";
	public static final String cstUserName = "UserName";
	public static final String cstGroupPath = "GroupPath";
	public static final String cstRoleName = "RoleName";

	/*
	 * *************************************************************************
	 * *******
	 */
	/*																																									*/
	/* Different method to fullfill the object */
	/* NB : the setAttributes is in the Item class */
	/*																																									*/
	/*
	 * *************************************************************************
	 * *******
	 */
	public ItemProfileMember() {
	};

	public ItemProfileMember(String profileName, String userName, String groupPath, String roleName) {
		itemInformation.put(cstProfileName, profileName);
		itemInformation.put(cstUserName, userName);
		itemInformation.put(cstGroupPath, groupPath);
		itemInformation.put(cstRoleName, roleName);
	}

	public void setProfileName(String profileName) {
		itemInformation.put(cstProfileName, profileName);
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

	/*
	 * *************************************************************************
	 * *******
	 */
	/*																																									*/
	/* Description */
	/*																																									*/
	/*																																									*/
	/*
	 * *************************************************************************
	 * *******
	 */
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
		return (itemInformation.get(cstProfileName) + "#" + itemInformation.get(cstUserName) + "#" + itemInformation.get(cstGroupPath) + "#" + itemInformation.get(cstRoleName)).toLowerCase();
	}

	public String getFullFillDescription() {
		return "Parameters are " + getListAttributes().toString();
	}

	@Override
	public List<String> getListAttributes() {
		ArrayList<String> listAttributes = new ArrayList<String>();
		listAttributes.add(cstProfileName);
		listAttributes.add(cstUserName);
		listAttributes.add(cstGroupPath);
		listAttributes.add(cstRoleName);
		return listAttributes;
	}

	/*
	 * *************************************************************************
	 * *******
	 */
	/*																																									*/
	/* Operation on server */
	/*																																									*/
	/*																																									*/
	/*
	 * *************************************************************************
	 * *******
	 */

	/**
	 * 
	 */
	public void saveInServer(AmericanOrganizationAPI organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organizationLog) {

		if (profileAPI == null) {
			organizationLog.log(true, "ItemProfileMember.saveInServer", "No profileAPI given");
			return;
		}
		if (itemInformation.get(cstProfileName) == null) {
			organizationLog.log(true, "ItemProfileMember.saveInServer", cstProfileName + " is mandatory");
			return;
		}
		User user = null;
		Group group = null;
		Role role = null;
		Profile profile = null;
		ProfileMemberCreator profileMemberCreator = null;
		try {
			profile = ItemProfile.getProfileByName(itemInformation.get(cstProfileName), profileAPI, organizationLog);
			if (profile == null) {
				organizationLog.log(true, "ItemProfileMember.saveInServer", getLogContextualInformation() + "Profile[" + itemInformation.get(cstProfileName) + "] notFound");
				return;

			}
			profileMemberCreator = new ProfileMemberCreator(profile.getId());
			if (itemInformation.get(cstUserName) != null && itemInformation.get(cstUserName).trim().length() > 0 && (!itemInformation.get(cstUserName).equals("-"))) {
				user = identityAPI.getUserByUserName(itemInformation.get(cstUserName));
				profileMemberCreator.setUserId(user.getId());
			}
			if (itemInformation.get(cstGroupPath) != null && itemInformation.get(cstGroupPath).trim().length() > 0 && (!itemInformation.get(cstGroupPath).equals("-"))) {
				group = identityAPI.getGroupByPath(itemInformation.get(cstGroupPath));
				profileMemberCreator.setGroupId(group.getId());
			}

			if (itemInformation.get(cstRoleName) != null && itemInformation.get(cstRoleName).trim().length() > 0 && (!itemInformation.get(cstRoleName).equals("-"))) {
				role = identityAPI.getRoleByName(itemInformation.get(cstRoleName));
				profileMemberCreator.setRoleId(role.getId());
			}

			ProfileMember profileMember = profileAPI.createProfileMember(profileMemberCreator);
			bonitaId = profileMember.getId();
			isCreated = true;
			if (user != null)
				organizationAccess.userIsInProfile(user.getId(), profile.getId(), organizationLog);

		} catch (RoleNotFoundException e1) {
			organizationLog.log(true, "ItemProfileMember.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] notFound");
		} catch (UserNotFoundException e2) {
			organizationLog.log(true, "ItemProfileMember.saveInServer", "User[" + itemInformation.get(cstUserName) + "] notFound");
		} catch (GroupNotFoundException e) {
			organizationLog.log(true, "ItemProfileMember.saveInServer", "GroupPath[" + itemInformation.get(cstGroupPath) + "] notFound");
		} catch (AlreadyExistsException e) {
			isCreated = false;
			// already exist, so what is the bonitaId ?
			ProfileMember profileMember = searchProfileMember(profile.getId(), user == null ? null : user.getId(), group == null ? null : group.getId(), role == null ? null : role.getId(), profileAPI, organizationLog);
			if (profileMember != null)
				bonitaId = profileMember.getId();
			else
				organizationLog.log(true, "ItemProfileMember.saveInServer",
						"Can't find the profileMember for [" + itemInformation.get(cstProfileName) + "] userName[" + itemInformation.get(cstUserName) + "] Group[" + itemInformation.get(cstGroupPath) + "] Role[" + itemInformation.get(cstRoleName) + "] when its exist because the creation failed");

		} catch (CreationException e) {
			organizationLog.log(true, "ItemProfileMember.saveInServer",
					"Cant' create ProfileMember on profile[" + itemInformation.get(cstProfileName) + ") userName[" + itemInformation.get(cstUserName) + "] Group[" + itemInformation.get(cstGroupPath) + "] Role[" + itemInformation.get(cstRoleName) + "] Error " + e.toString());
		}
		return;
	}

	protected static void registerUserInProfile(Long userId, Long profileId, ProfileAPI profileAPI, OrganizationLog organizationLog) {
		ProfileMemberCreator profileMemberCreator = new ProfileMemberCreator(profileId);
		profileMemberCreator.setUserId(userId);
		try {
			profileAPI.createProfileMember(profileMemberCreator);
		} catch (AlreadyExistsException e) {
			organizationLog.log(false, "ItemProfileMember.saveInServer", "AlreadyExist ProfileMember on profile[" + profileId + ") userId[" + userId + "]");
		} catch (CreationException e) {
			organizationLog.log(false, "ItemProfileMember.saveInServer", "Error creation ProfileMember on profile[" + profileId + ") userId[" + userId + "] :" + e.toString());
		}

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

	protected static void photoAll(Item.StatisticOnItem statisticOnProfileMember, ParametersOperation parametersLoad, ProfileAPI profileAPI, OrganizationLog organizationLog) {
		if (profileAPI == null) {
			organizationLog.log(true, "ItemProfile.saveInServer", "No profileAPI given");
			return;
		}
		try {
			int index = 0;

			int memberTypePos = 0;
			String[] memberType = new String[] { "user", "role", "group", "roleAndGroup" };

			Profile profileToExclude = null;
			if (parametersLoad.registerNewUserInProfileUser == RegisterNewUserInProfile.ALWAYSUSERPROFILE) {
				// in that case, we MUST not set in the photo the profileMember
				// from the User profile. Doing that, theses profileMember is
				// never purge
				profileToExclude = ItemProfile.getProfileByName(ItemProfile.cstProfileNameUser, profileAPI, organizationLog);
			}

			while (true) {
				SearchOptionsBuilder searchOption = new SearchOptionsBuilder(index, 1000);
				SearchResult<ProfileMember> listProfileMembers = profileAPI.searchProfileMembers(memberType[memberTypePos], searchOption.done());
				index += 1000;

				for (ProfileMember profileMember : listProfileMembers.getResult()) {
					if (profileToExclude != null && profileMember.getProfileId() == profileToExclude.getId())
						continue;
					statisticOnProfileMember.listKeyItem.add(profileMember.getId());
				}
				if (listProfileMembers.getCount() < 1000) {
					memberTypePos++;
					index = 0;
					if (memberTypePos >= memberType.length) {
						organizationLog.log(false, "ItemProfile.photoAll", "List of profileMember found " + statisticOnProfileMember.listKeyItem.toString() + "]");
						return;
					}
					continue;
				}
			}

		} catch (SearchException e) {
			organizationLog.log(true, "ItemProfile.photoAll", "Errorwhen get list of all profile " + e.toString());
		}
	}

	/** Purge on the list */
	protected static void purgeFromList(Item.StatisticOnItem statisticOnItemProfileMember, ParametersOperation parametersLoad, ProfileAPI profileAPI, OrganizationLog organizationLog) {
		if (profileAPI == null) {
			organizationLog.log(true, "ItemProfile.saveInServer", "No profileAPI given");
			return;
		}

		for (Long profileMemberId : statisticOnItemProfileMember.listKeyItem) {
			try {
				profileAPI.deleteProfileMember(profileMemberId);
			} catch (DeletionException e) {
				// if the profile is part of a delete profile, it's normal that
				// the profilemember is not found organisationLog.log(true,
				// "ItemProfile.purgeFromList", "profileMember
				// ["+profileMemberId+"] " + e.toString());

			}
		}

	}

	/**
	 * search the membership for the user, group, role
	 * 
	 * @param userId
	 * @param groupId
	 * @param roleId
	 * @param identityAPI
	 * @return
	 */
	private ProfileMember searchProfileMember(Long profileId, Long userId, Long groupId, Long roleId, ProfileAPI profileAPI, OrganizationLog organizationLog) {
		String memberType = "";
		SearchOptionsBuilder searchOption = new SearchOptionsBuilder(0, 1000);
		searchOption.filter(ProfileMemberSearchDescriptor.PROFILE_ID, profileId);
		if (userId != null) {
			memberType = "user";
			searchOption.filter(ProfileMemberSearchDescriptor.USER_ID, userId);
		} else if (groupId != null && roleId != null) {
			memberType = "roleandgroup";
			searchOption.filter(ProfileMemberSearchDescriptor.GROUP_ID, groupId);
			searchOption.filter(ProfileMemberSearchDescriptor.ROLE_ID, roleId);
		} else if (groupId != null) {
			memberType = "group";
			searchOption.filter(ProfileMemberSearchDescriptor.GROUP_ID, groupId);
		} else if (roleId != null) {
			memberType = "role";
			searchOption.filter(ProfileMemberSearchDescriptor.ROLE_ID, roleId);
		}

		SearchResult<ProfileMember> listProfileMembers;
		try {
			listProfileMembers = profileAPI.searchProfileMembers(memberType, searchOption.done());

			for (ProfileMember profileMember : listProfileMembers.getResult()) {
				return profileMember;
			}
		} catch (SearchException e) {
			organizationLog.log(true, "ItemProfile.searchProfileMember", "Errorwhen get list of profileMember" + e.toString());

		}
		return null;
	}

}
