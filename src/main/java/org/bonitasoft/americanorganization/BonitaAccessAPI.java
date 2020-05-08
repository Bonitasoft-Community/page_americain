package org.bonitasoft.americanorganization;

import java.util.HashMap;
import java.util.Map;



import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.GroupNotFoundException;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.RoleNotFoundException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.api.ProfileAPI;




/**
 * Access Bonita API with cache
 * @author Firstname Lastname
 *
 */
public class BonitaAccessAPI {
    
    
   
    private Map<String,Profile> cacheProfile = new HashMap<>();
    private Map<String,Group> cacheGroup = new HashMap<>();
    
    private Map<String,Role> cacheRole = new HashMap<>();
    ProfileAPI profileAPI;
    IdentityAPI identityAPI;
    
    public BonitaAccessAPI( ProfileAPI profileAPI,  IdentityAPI identityAPI) {
        this.identityAPI = identityAPI;
        this.profileAPI = profileAPI;
    }
    
    /* ************************************************************************* */
    /*                                                                                                                                                                  */
    /* Getter */
    /*                                                                                                                                                                  */
    /* ************************************************************************* */
    public ProfileAPI getProfileAPI() {
        return profileAPI;
    }

    
    public IdentityAPI getIdentityAPI() {
        return identityAPI;
    }
    /* ************************************************************************* */
    /*                                                                                                                                                                  */
    /* API with cache/
    /*                                                                                                                                                                  */
    /* ************************************************************************* */

    /**
     * get profile by name
     * 
     * @param profileName
     * @param profileAPI
     * @param organisationLog
     * @return
     * @throws Exception 
     */
    public Profile getProfileByName(String profileName,OrganizationLog organisationLog) throws SearchException {
       if (cacheProfile.containsKey(profileName))
           return cacheProfile.get(profileName);
       Profile profile=null;
        try {
            SearchOptionsBuilder searchOption = new SearchOptionsBuilder(0, 100);
            searchOption.filter(ProfileSearchDescriptor.NAME, profileName);
            SearchResult<Profile> listProfiles = profileAPI.searchProfiles(searchOption.done());
            if (listProfiles.getCount() == 0) 
                profile=null;
            else
                profile = listProfiles.getResult().get(0);
        } catch (Exception e) {
            cacheProfile.put(profileName, profile);
            throw e;
        }
        cacheProfile.put(profileName, profile);
        return profile;
    }
    
    public Group getGroupByPath( String groupPath, OrganizationLog organisationLog ) throws GroupNotFoundException {
        if (cacheGroup.containsKey(groupPath))
            return cacheGroup.get(groupPath);
        Group group=null;
        group = identityAPI.getGroupByPath( groupPath);
        cacheGroup.put(groupPath, group);
        return group;
       
    }
    public Role getRoleByName( String roleName, OrganizationLog organisationLog ) throws RoleNotFoundException {
        if (cacheRole.containsKey(roleName))
            return cacheRole.get(roleName);
        Role role=null;
        role = identityAPI.getRoleByName(roleName);

        cacheRole.put(roleName, role);
        return role;
       
    }
}
