package org.bonitasoft.americanorganization;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileNotFoundException;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.profile.ProfileCreator;
import com.bonitasoft.engine.profile.ProfileUpdater;

public class ItemProfile extends Item {

    public static final String cstProfileNameUser = "USER";

    public static final String cstItemName = "PROFILE";

    /**
     * use for the fullfill to decode the hashMap
     */
    public static final String cstProfileName = "ProfileName";
    public static final String cstProfileDescription = "ProfileDescription";

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
    public ItemProfile() {
    };

    public ItemProfile(String profileName, String profileDescription) {
        itemInformation.put(cstProfileName, profileName);
        itemInformation.put(cstProfileDescription, profileDescription);
    }

    public void setProfileName(String profileName) {
        itemInformation.put(cstProfileName, profileName);
    }

    public void setProfileDescription(String profileDescription) {
        itemInformation.put(cstProfileDescription, profileDescription);
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
     * /** return the name of the item, which suppose to be uniq.
     * 
     * @return
     */
    public String getTypeItem() {
        return cstItemName;
    }

    @Override
    public String getId() {
        return itemInformation.get(cstProfileName).toLowerCase();
    }

    /**
     * 
     */
    public String getFullFillDescription() {
        return "Parameters are " + getListAttributes().toString();
    }

    public List<String> getListAttributes() {
        ArrayList<String> listAttributes = new ArrayList<String>();
        listAttributes.add(cstProfileName);
        listAttributes.add(cstProfileDescription);
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
    @Override
    protected void saveInServer(AmericanOrganizationAPI organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organizationLog) {

        if (profileAPI == null) {
            organizationLog.log(true, "ItemProfile.saveInServer", "No profileAPI given");
            return;
        }
        if (itemInformation.get(cstProfileName) == null) {
            organizationLog.log(true, "ItemProfile.saveInServer", cstProfileName + " is mandatory");
            return;
        }

        ProfileCreator profileCreator = new ProfileCreator(itemInformation.get(cstProfileName));
        ProfileUpdater profileUpdater = new ProfileUpdater();

        if (itemInformation.containsKey(cstProfileDescription)) {
            profileCreator.setDescription(itemInformation.get(cstProfileDescription));
            profileUpdater.description(itemInformation.get(cstProfileDescription));
        }

        if (parameterLoad.operationRoles == ParametersOperation.OperationOnItem.NONE)
            return;

        Profile profile = getProfileByName(itemInformation.get(cstProfileName), profileAPI, organizationLog);

        if (profile == null && parameterLoad.operationProfiles == ParametersOperation.OperationOnItem.UPDATEONLY) {
            organizationLog.log(false, "ItemMemberProfile.saveInServer", "Profile[" + itemInformation.get(cstProfileName) + "] does not exists and no insert allowed");
            return;
        }
        if (profile != null && parameterLoad.operationProfiles == ParametersOperation.OperationOnItem.INSERTONLY) {
            organizationLog.log(false, "ItemMemberProfile.saveInServer", "profile[" + itemInformation.get(cstProfileName) + "] exists and no update allowed");
            return;
        }

        if (profile == null) {
            // a creation
            try {
                isCreated = true;
                organizationLog.log(false, "ItemMemberProfile.saveInServer", "Insert profile[[" + itemInformation.get(cstProfileName) + "]");
                Profile profileCreated = profileAPI.createProfile(profileCreator);
                bonitaId = profileCreated.getId();
            } catch (AlreadyExistsException e) {
                organizationLog.log(true, "ItemMemberProfile.saveInServer", "profile[[" + itemInformation.get(cstProfileName) + "] already exist at creation");
            } catch (CreationException e) {
                organizationLog.log(true, "ItemMemberProfile.saveInServer", "Role[" + itemInformation.get(cstProfileName) + "] Error at creation " + e.toString());
            }
        } else {
            // an update
            try {
                isCreated = false;
                organizationLog.log(false, "ItemProfile.saveInServer", "Update profile[" + itemInformation.get(cstProfileName) + "] Id[" + profile.getId() + "]");
                profileAPI.updateProfile(profile.getId(), profileUpdater);
                bonitaId = profile.getId();
            } catch (AlreadyExistsException e) {
                organizationLog.log(true, "ItemProfile.saveInServer", "profile[" + itemInformation.get(cstProfileName) + "] override an existing profile");
            } catch (UpdateException e) {
                organizationLog.log(true, "ItemProfile.saveInServer", "profile[" + itemInformation.get(cstProfileName) + "] Error at update " + e.toString());
            } catch (ProfileNotFoundException e) {
                organizationLog.log(true, "ItemProfile.saveInServer", "profile[" + itemInformation.get(cstProfileName) + "] not exist");
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
    public static void photoAll(Item.StatisticOnItem statisticOnItemRole, ProfileAPI profileAPI, OrganizationLog organizationLog) {
        if (profileAPI == null) {
            organizationLog.log(true, "ItemProfile.saveInServer", "No profileAPI given");
            return;
        }
        try {
            int index = 0;
            while (true) {
                SearchOptionsBuilder searchOption = new SearchOptionsBuilder(index, 1000);
                // searchOption.filter(ProfileSearchDescriptor.IS_DEFAULT,
                // Boolean.FALSE);
                SearchResult<Profile> listProfiles = profileAPI.searchProfiles(searchOption.done());
                index += 1000;
                for (Profile profile : listProfiles.getResult()) {
                    if (!profile.isDefault())
                        statisticOnItemRole.listKeyItem.add(profile.getId());
                }
                if (listProfiles.getCount() < 1000) {
                    organizationLog.log(false, "ItemProfile.photoAll", "List of profiles found " + statisticOnItemRole.listKeyItem.toString() + "]");
                    return;
                }
            }

        } catch (SearchException e) {
            organizationLog.log(true, "ItemProfile.photoAll", "Errorwhen get list of all profile " + e.toString());
        }
    }

    public static void purgeFromList(Item.StatisticOnItem statisticOnItemRole, ParametersOperation parametersLoad, ProfileAPI profileAPI, OrganizationLog organizationLog) {
        if (profileAPI == null) {
            organizationLog.log(true, "ItemProfile.saveInServer", "No profileAPI given");
            return;
        }
        // all elements in the list should be clean. ATTENTION, clean member too
        for (Long profileId : statisticOnItemRole.listKeyItem) {
            try {
                profileAPI.deleteProfile(profileId);
            } catch (DeletionException e) {
                organizationLog.log(true, "ItemProfile.saveInServer", "Can't delete profile[" + profileId + "] " + e.toString());
            }
        }
        statisticOnItemRole.nbPurgedItem = statisticOnItemRole.listKeyItem.size();
    }

    /**
     * get profile by name
     * 
     * @param profileName
     * @param profileAPI
     * @param organisationLog
     * @return
     */
    protected static Profile getProfileByName(String profileName, ProfileAPI profileAPI, OrganizationLog organisationLog) {
        try {
            SearchOptionsBuilder searchOption = new SearchOptionsBuilder(0, 100);
            searchOption.filter(ProfileSearchDescriptor.NAME, profileName);
            SearchResult<Profile> listProfiles = profileAPI.searchProfiles(searchOption.done());
            if (listProfiles.getCount() == 0)
                return null;
            return listProfiles.getResult().get(0);
        } catch (Exception e) {
            organisationLog.log(true, "ItemProfile.getprofileByName", "Error during search profile[" + profileName + "] " + e.toString());
            return null;
        }
    }

}
