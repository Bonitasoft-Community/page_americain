package com.bonitasoft.organization.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.americanorganization.ItemGroup;
import org.bonitasoft.americanorganization.ItemMembership;
import org.bonitasoft.americanorganization.ItemRole;
import org.bonitasoft.americanorganization.ItemUser;
import org.bonitasoft.americanorganization.AmericanOrganizationAPI;
import org.bonitasoft.americanorganization.ParametersOperation;
import org.bonitasoft.americanorganization.ParametersOperation.RegisterNewUserInProfile;
import org.bonitasoft.americanorganization.csv.impl.OrganizationSourceCSV;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;
import org.junit.Test;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;

public class JunitTestOrganization {

	@Test
	public void test() {
		APISession session = login("http://localhost:8081", "bonita", "Walter.Bates", "bpm");
		if (session == null) {
			fail("Can't login");
			return;
		}

		// create a task
		IdentityAPI identityAPI;
		ProfileAPI profileAPI;
		try {
			identityAPI = TenantAPIAccessor.getIdentityAPI(session);
			profileAPI = TenantAPIAccessor.getProfileAPI(session);
			AmericanOrganizationAPI organizationAccess = new AmericanOrganizationAPI(identityAPI, profileAPI);

			ParametersOperation parametersLoad = new ParametersOperation();
			parametersLoad.registerNewUserInProfileUser = RegisterNewUserInProfile.USERPROFILEIFNOTREGISTER;
			OrganizationSourceCSV organizationSourceCSV = new OrganizationSourceCSV();
			organizationSourceCSV.loadFromFile("E:/dev/workspace/TwoSigma TSLib/TestOrganisationLoad.csv", null);
			organizationAccess.saveOrganisation(organizationSourceCSV, parametersLoad);
			String xmlResult = organizationAccess.getOrganizationOnXml(parametersLoad);
			assert (xmlResult.length() > 0);
			// System.out.println(xmlResult);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}

	// @ T e s t
	public void testDocumentationUser() {
		ItemUser itemUser = new ItemUser();
		String info = itemUser.getFullFillDescription();
		assert (info != null && info.length() > 0);
	}

	// @T e s t
	public void testDocumentationGroup() {
		ItemGroup itemGroup = new ItemGroup();
		assert (itemGroup.getFullFillDescription() != null);
	}

	// @ T e s t
	public void testDocumentationRole() {

		ItemRole itemRole = new ItemRole();
		String info = itemRole.getFullFillDescription();
		assert (info != null && info.length() > 0);
	}

	// @ T e s t
	public void testDocumentationMembership() {

		ItemMembership itemMembership = new ItemMembership();
		assert (itemMembership.getFullFillDescription() != null);

	}

	/**
	 * 
	 * @param applicationUrl
	 * @param applicationName
	 * @param userName
	 * @param passwd
	 * @return
	 */
	public APISession login(String applicationUrl, String applicationName, String userName, String passwd) {
		try {
			// Define the REST parameters
			Map<String, String> map = new HashMap<String, String>();
			map.put("server.url", applicationUrl == null ? "http://localhost:8080" : applicationUrl);
			map.put("application.name", applicationName == null ? "bonita" : applicationName);
			APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, map);

			// Set the username and password
			// final String username = "helen.kelly";
			final String username = "walter.bates";
			final String password = "bpm";

			// get the LoginAPI using the TenantAPIAccessor
			LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();

			// log in to the tenant to create a session
			APISession session = loginAPI.login(username, password);
			return session;
		} catch (BonitaHomeNotSetException e) {
			e.printStackTrace();
			return null;
		} catch (ServerAPIException e) {
			e.printStackTrace();
			return null;
		} catch (UnknownAPITypeException e) {
			e.printStackTrace();
			return null;
		} catch (LoginException e) {
			e.printStackTrace();
			return null;
		}
	}

}
