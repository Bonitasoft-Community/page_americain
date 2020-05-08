package org.bonitasoft.americanorganization;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleCreator;
import org.bonitasoft.engine.identity.RoleCriterion;
import org.bonitasoft.engine.identity.RoleNotFoundException;
import org.bonitasoft.engine.identity.RoleUpdater;

import com.bonitasoft.engine.api.ProfileAPI;

public class ItemRole extends Item {

    public static final String cstItemName = "ROLE";

    /**
     * use for the fullfill to decode the hashMap
     */
    public static final String cstRoleName = "RoleName";
    public static final String cstRoleDescription = "RoleDescription";
    public static final String cstRoleDisplayName = "RoleDisplayName";
    public static final String cstRoleIconName = "RoleIconName";
    public static final String cstRoleIconPath = "RoleIconPath";

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
    public ItemRole() {
    };

    public ItemRole(String roleName, String roleDescription, String roleDisplayName, String roleIconName, String roleIconPath) {
        itemInformation.put(cstRoleName, roleName);
        itemInformation.put(cstRoleDescription, roleDescription);
        itemInformation.put(cstRoleDisplayName, roleDisplayName);
        itemInformation.put(cstRoleIconName, roleIconName);
        itemInformation.put(cstRoleIconPath, roleIconPath);
    }

    public void setRoleName(String roleName) {
        itemInformation.put(cstRoleName, roleName);
    }

    public void setRoleDescription(String roleDescription) {
        itemInformation.put(cstRoleDescription, roleDescription);
    }

    public void setRoleDisplayName(String roleDisplayName) {
        itemInformation.put(cstRoleDisplayName, roleDisplayName);
    }

    public void setRoleIconName(String roleIconName) {
        itemInformation.put(cstRoleIconName, roleIconName);
    }

    public void setRoleIconPath(String roleIconPath) {
        itemInformation.put(cstRoleIconPath, roleIconPath);
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
        return itemInformation.get(cstRoleName).toLowerCase();
    }

    /**
     * 
     */
    public String getFullFillDescription() {
        return "Parameters are " + getListAttributes().toString();
    }

    public List<String> getListAttributes() {
        ArrayList<String> listAttributes = new ArrayList<String>();
        listAttributes.add(cstRoleName);
        listAttributes.add(cstRoleDescription);
        listAttributes.add(cstRoleDisplayName);
        listAttributes.add(cstRoleIconName);
        listAttributes.add(cstRoleIconPath);
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
    protected void saveInServer(AmericanOrganizationAPI organizationAccess,  BonitaAccessAPI bonitaAccessAPI, ParametersOperation parameterLoad, OrganizationLog organizationLog) {

        if (itemInformation.get(cstRoleName) == null) {
            organizationLog.log(true, true, "ItemMemberRole.saveInServer", cstRoleName + " is mandatory "+contextualInformation);
            return;
        }

        RoleCreator roleCreator = new RoleCreator(itemInformation.get(cstRoleName));
        RoleUpdater roleUpdater = new RoleUpdater();

        if (itemInformation.containsKey(cstRoleDescription)) {
            roleCreator.setDescription(itemInformation.get(cstRoleDescription));
            roleUpdater.setDescription(itemInformation.get(cstRoleDescription));
        }

        if (itemInformation.containsKey(cstRoleDisplayName)) {
            roleCreator.setDisplayName(itemInformation.get(cstRoleDisplayName));
            roleUpdater.setDisplayName(itemInformation.get(cstRoleDisplayName));
        }

        if (itemInformation.containsKey(cstRoleIconName)) {
            roleCreator.setIconName(itemInformation.get(cstRoleIconName));
            roleUpdater.setIconName(itemInformation.get(cstRoleIconName));
        }

        if (itemInformation.containsKey(cstRoleIconPath)) {
            roleCreator.setIconPath(itemInformation.get(cstRoleIconPath));
            roleUpdater.setIconPath(itemInformation.get(cstRoleIconPath));
        }

        if (parameterLoad.operationRoles == ParametersOperation.OperationOnItem.NONE)
            return;

        Role role = null;
        try {
            role = bonitaAccessAPI.getRoleByName(itemInformation.get(cstRoleName), organizationLog);
        } catch (RoleNotFoundException e1) {
            role = null;
        }

        if (role == null && parameterLoad.operationRoles == ParametersOperation.OperationOnItem.UPDATEONLY) {
            organizationLog.log(false, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] does not exists and no insert allowed "+contextualInformation);
            return;
        }
        if (role != null && parameterLoad.operationRoles == ParametersOperation.OperationOnItem.INSERTONLY) {
            organizationLog.log(false, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] exists and no update allowed "+contextualInformation);
            return;
        }

        if (role == null) {
            // a creation
            try {
                isCreated = true;
                organizationLog.log(false, true, "ItemMemberRole.saveInServer", "Insert Role[" + itemInformation.get(cstRoleName) + "] "+contextualInformation);
                Role roleCreated = bonitaAccessAPI.getIdentityAPI().createRole(roleCreator);
                bonitaId = roleCreated.getId();
            } catch (AlreadyExistsException e) {
                organizationLog.log(true, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] already exist at creation "+contextualInformation);
            } catch (CreationException e) {
                organizationLog.log(true, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] "+contextualInformation+" Error at creation:" + e.toString());
            }
        } else {
            // an update
            try {
                isCreated = false;
                organizationLog.log(false, true, "ItemMemberRole.saveInServer", "Update Role[" + itemInformation.get(cstRoleName) + "] Id[" + role.getId() + "] "+contextualInformation);
                bonitaAccessAPI.getIdentityAPI().updateRole(role.getId(), roleUpdater);
                bonitaId = role.getId();
            } catch (RoleNotFoundException e) {
                organizationLog.log(true, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] not exist "+contextualInformation);
            } catch (UpdateException e) {
                organizationLog.log(true, true, "ItemMemberRole.saveInServer", "Role[" + itemInformation.get(cstRoleName) + "] "+contextualInformation+" Error at update " + e.toString());
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
    public static void photoAll(Item.StatisticOnItem statisticOnItemRole, IdentityAPI identityAPI, OrganizationLog organisationLog) {
        int index = 0;
        while (true) {
            List<Role> listRoles = identityAPI.getRoles(index, 1000, RoleCriterion.NAME_ASC);
            index += 1000;
            if (listRoles.size() == 0)
                return;
            for (Role role : listRoles) {
                statisticOnItemRole.listKeyItem.add(role.getId());
            }
        }
    }

    public static void purgeFromList(Item.StatisticOnItem statisticOnItemRole, ParametersOperation parametersLoad, IdentityAPI identityAPI, OrganizationLog organisationLog) {
        // all elements in the list should be clean. ATTENTION, clean member too
        ArrayList<Long> listIdToDelete = new ArrayList<Long>();
        for (Long roleId : statisticOnItemRole.listKeyItem) {
            listIdToDelete.add(roleId);
        }

        // now delete the role
        try {
            identityAPI.deleteRoles(listIdToDelete);
        } catch (DeletionException e) {
            organisationLog.log(true, true, "ItemMemberRole.saveInServer", "Can't delete Roles :" + e.toString());
        }
        statisticOnItemRole.nbPurgedItem = listIdToDelete.size();
    }

}
