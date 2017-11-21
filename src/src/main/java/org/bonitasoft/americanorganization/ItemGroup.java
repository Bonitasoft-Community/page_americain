package org.bonitasoft.americanorganization;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.GroupCreator;
import org.bonitasoft.engine.identity.GroupCriterion;
import org.bonitasoft.engine.identity.GroupNotFoundException;
import org.bonitasoft.engine.identity.GroupSearchDescriptor;
import org.bonitasoft.engine.identity.GroupUpdater;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleCriterion;
import org.bonitasoft.engine.identity.RoleNotFoundException;
import org.bonitasoft.engine.identity.GroupUpdater.GroupField;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.api.ProfileAPI;

public class ItemGroup extends Item {

	public static final String cstItemName = "GROUP";

	public static final String cstGroupName = "GroupName";
	public final static String cstGroupDescription = "GroupDescription";
	public final static String cstGroupDisplayName = "GroupDisplayName";
	public final static String cstGroupIconName = "GroupIconName";
	public final static String cstGroupIconPath = "GroupIconPath";
	public final static String cstGroupParentPath = "GroupParentPath";

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
	 * now do the operation
	 * 
	 */
	protected void saveInServer(AmericanOrganizationAPI organizationAccess, ParametersOperation parameterLoad, IdentityAPI identityAPI, ProfileAPI profileAPI, OrganizationLog organizationLog) {
		GroupCreator groupCreator;
		GroupUpdater groupUpdater;
		boolean allIsOk=true;
		if (itemInformation.get(cstGroupName) == null) {
			organizationLog.log(true, "OrganizationItemGroup.saveInServer", cstGroupName + " is mandatory");
		}
		if (itemInformation.get(cstGroupParentPath) == null)
			itemInformation.put(cstGroupParentPath, "");

		// organizationLog.log(false,
		// "GroupReferenece["+groupParentPath+"/"+groupName+"]");

		groupCreator = new GroupCreator(itemInformation.get(cstGroupName));
		groupUpdater = new GroupUpdater();


		if (itemInformation.containsKey(cstGroupDescription)) {
			groupCreator.setDescription(itemInformation.get(cstGroupDescription));
			allIsOk=invokeMethod( groupUpdater, "updateDescription", itemInformation.get(cstGroupDescription), organizationLog);
		}
		if (itemInformation.containsKey(cstGroupDisplayName)) {
			String displayName = itemInformation.get(cstGroupDisplayName);
			groupCreator.setDisplayName(displayName);
			
			// between 7.2 and 7.5, this method look like the same compile the same but.. 
			// compile in 7.2 and run in 7.5 ? we got a java.lang.NoSuchMethodError: org.bonitasoft.engine.identity.GroupUpdater.updateDisplayName(Ljava/lang/String;)Lorg/bonitasoft/engine/identity/GroupUpdater;
			// compile in 7.5 and run in 7.2 ? same error
			allIsOk=invokeMethod( groupUpdater, "updateDisplayName", displayName, organizationLog);
			//	groupUpdater.updateDisplayName(displayName);
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

		// First, the PARENT must exist
		String groupParentPath = itemInformation.get(cstGroupParentPath);
		if (groupParentPath==null || groupParentPath.length()==0)
			groupParentPath=null;
		if (groupParentPath!=null && groupParentPath.length()>0)
		{
			try {
				if (! groupParentPath.startsWith("/") && groupParentPath.length()>1)
					groupParentPath= "/"+groupParentPath;
				Group groupParent = identityAPI.getGroupByPath(groupParentPath);
				groupParentPath= groupParent.getPath();
			} catch (GroupNotFoundException e1) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Parent group Path[" + groupParentPath + "] not found");
				allIsOk=false;
			}
		}
		
		// now, check if the group exist
		Group group = null;
		// String groupPath = groupParentPath + "/" + itemInformation.get(cstGroupName);
		try {
			SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0,10);
			searchOptionsBuilder.filter( GroupSearchDescriptor.NAME,  (String) itemInformation.get(cstGroupName));
			searchOptionsBuilder.and();
			searchOptionsBuilder.filter( GroupSearchDescriptor.PARENT_PATH,  groupParentPath);
			SearchResult<Group> searchResult = identityAPI.searchGroups(searchOptionsBuilder.done());
			if (searchResult.getCount()>0)
				group = searchResult.getResult().get(0);
		} catch (SearchException e1) {
			group = null; // so this is a creation
			
		}

		if (group == null && parameterLoad.operationGroups == ParametersOperation.OperationOnItem.UPDATEONLY) {
			organizationLog.log(false, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] does not exist and no insert allowed");
			return;
		}
		if (group != null && parameterLoad.operationGroups == ParametersOperation.OperationOnItem.INSERTONLY) {
			organizationLog.log(false, "OrganizationItemGroup.saveInServer", " Group[" + itemInformation.get(cstGroupName) + "] exist and no update allowed");
			return;
		}

		if (! allIsOk)
			return;
		
		if (group == null) {
			// a creation
			try {
				isCreated = true;
				groupCreator.setParentPath(groupParentPath); // may be null for the root
				
				organizationLog.log(false, "OrganizationItemGroup.saveInServer", "Insert Group[" + itemInformation.get(cstGroupName) + "] path[" + groupParentPath + "]");
				Group groupCreated = identityAPI.createGroup(groupCreator);
				bonitaId = groupCreated.getId();
			} catch (AlreadyExistsException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] path[" + groupParentPath + "] already exist with a different path");
			} catch (CreationException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] path[" + groupParentPath + "] Error at creation " + e.toString());
			}
		} else {
			// an update
			try {
				isCreated = false;
				if (groupParentPath==null)
					groupParentPath="";
				String currentParentPath = group.getParentPath() == null ? "" : group.getParentPath();
				if ( !currentParentPath.equals(groupParentPath))
				{
					allIsOk= invokeMethod( groupUpdater, "updateParentPath", groupParentPath, organizationLog);
					if (!allIsOk)
					{
						organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] Error at updateParentPath to["+groupParentPath+"]");
						return;
					}
				}
				
		

				organizationLog.log(false, "OrganizationItemGroup.saveInServer", "Update Group[" + itemInformation.get(cstGroupName) + "] Id[" + group.getId() + "]");
				identityAPI.updateGroup(group.getId(), groupUpdater);
				bonitaId = group.getId();
			} catch (GroupNotFoundException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] not exist");
			} catch (UpdateException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", "Group[" + itemInformation.get(cstGroupName) + "] Error at update " + e.toString());
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
	protected static void photoAll(Item.StatisticOnItem statisticOnItemGroup, IdentityAPI identityAPI, OrganizationLog organizationLog) {
		int index = 0;
		while (true) {
			List<Group> listGroups = identityAPI.getGroups(index, 1000, GroupCriterion.NAME_ASC);
			index += 1000;
			if (listGroups.size() == 0)
				return;
			for (Group group : listGroups) {
				organizationLog.log(false, "OrganizationItemGroup.photo", " ParentPath[" + group.getParentPath() + "] Path[" + group.getPath() + "] Name[" + group.getName() + "]");
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
			listIdToDelete.add(groupId);
		}

		// now delete the role
		try {
			identityAPI.deleteGroups(listIdToDelete);
		} catch (DeletionException e) {
			organisationLog.log(true, "OrganizationItemGroup.purgeFromList", " Can't delete Groups :" + e.toString());
		}
		statisticOnItemGroup.nbPurgedItem = listIdToDelete.size();
	}

	
	private boolean invokeMethod( GroupUpdater groupUpdater, String methodeName, String value,  OrganizationLog organizationLog )
	{
	Method[] allMethods = groupUpdater.getClass().getDeclaredMethods();
    for (Method m : allMethods) {
    	if (m.getName().startsWith( methodeName ))
		{
    		// m.getGenericParameterTypes()
    		try {
				Object o = m.invoke(groupUpdater, value);
				return true;
			} catch (IllegalAccessException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", methodeName+"[" + value + "] on group[" + itemInformation.get(cstGroupName) + "] failed "+e.toString());
				return false;
			} catch (IllegalArgumentException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", methodeName+"[" + value + "] on group[" + itemInformation.get(cstGroupName) + "] failed "+e.toString());
				return false;
			} catch (InvocationTargetException e) {
				organizationLog.log(true, "OrganizationItemGroup.saveInServer", methodeName+"[" + value + "] on group[" + itemInformation.get(cstGroupName) + "] failed "+e.toString());
				return false;
			}
		}
    }
	organizationLog.log(true, "OrganizationItemGroup.saveInServer", methodeName+"[" + value + "] Method not exist. on group[" + itemInformation.get(cstGroupName) + "]");
    return false; // method not found
}
}
