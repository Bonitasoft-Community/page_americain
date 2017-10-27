package org.bonitasoft.americanorganization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ParametersOperation {
	public enum OperationOnItem {
		NONE, INSERTONLY, UPDATEONLY, BOTH
	};

	public OperationOnItem operationGroups = OperationOnItem.BOTH;
	public OperationOnItem operationRoles = OperationOnItem.BOTH;
	public OperationOnItem operationUsers = OperationOnItem.BOTH;
	public OperationOnItem operationMemberships = OperationOnItem.BOTH;
	public OperationOnItem operationProfiles = OperationOnItem.BOTH;
	public OperationOnItem operationProfileMembers = OperationOnItem.BOTH;

	boolean purgeGroups = true;
	boolean purgeRoles = true;
	boolean purgeUsers = true;
	boolean purgeMembership = true;
	boolean purgeProfile = true;
	boolean purgeProfileMember = true;

	public enum RegisterNewUserInProfile {
		NONE, ALWAYSUSERPROFILE, USERPROFILEIFNOTREGISTER
	};

	public RegisterNewUserInProfile registerNewUserInProfileUser = RegisterNewUserInProfile.USERPROFILEIFNOTREGISTER;

	boolean logInfo = true;

	public static List<String> listParametersOperation = Arrays.asList("optoperationgroups", "optoperationroles", "optoperationusers", "optoperationmemberships", "optoperationprofiles", "optoperationprofilemembers", "optpurgegroups", "optpurgeroles", "optpurgeusers", "optpurgememberships",
			"optpurgeprofiles", "optpurgeprofilemembers", "optregisternewuserinprofileuser");

	public static ParametersOperation getInstance(Map<String, String> parameters) {
		Logger logger = Logger.getLogger("org.bonitasoft");
		logger.info("ParametersOperation.getInstance: parameters[" + parameters + "]");
		ParametersOperation parametersOperation = new ParametersOperation();
		try {

			parametersOperation.operationGroups = parameters.get("optoperationgroups") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationgroups"));
			parametersOperation.operationRoles = parameters.get("optoperationroles") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationroles"));
			parametersOperation.operationUsers = parameters.get("optoperationusers") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationusers"));
			parametersOperation.operationMemberships = parameters.get("optoperationmemberships") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationmemberships"));
			parametersOperation.operationProfiles = parameters.get("optoperationprofiles") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationprofiles"));
			parametersOperation.operationProfileMembers = parameters.get("optoperationprofilemembers") == null ? OperationOnItem.BOTH : OperationOnItem.valueOf(parameters.get("optoperationprofilemembers"));

			parametersOperation.purgeGroups = parameters.get("optpurgegroups") == null ? false : Boolean.valueOf(parameters.get("optpurgegroups"));
			parametersOperation.purgeRoles = parameters.get("optpurgeroles") == null ? false : Boolean.valueOf(parameters.get("optpurgeroles"));
			parametersOperation.purgeUsers = parameters.get("optpurgeusers") == null ? false : Boolean.valueOf(parameters.get("optpurgeusers"));
			parametersOperation.purgeMembership = parameters.get("optpurgememberships") == null ? false : Boolean.valueOf(parameters.get("optpurgememberships"));
			parametersOperation.purgeProfile = parameters.get("optpurgeprofiles") == null ? false : Boolean.valueOf(parameters.get("optpurgeprofiles"));
			parametersOperation.purgeProfileMember = parameters.get("optpurgeprofilemembers") == null ? false : Boolean.valueOf(parameters.get("optpurgeprofilemembers"));

			parametersOperation.registerNewUserInProfileUser = parameters.get("optregisternewuserinprofileuser") == null ? RegisterNewUserInProfile.NONE : RegisterNewUserInProfile.valueOf(parameters.get("optregisternewuserinprofileuser"));

		} catch (Exception e) {
			logger.severe("ParametersOperation.getInstance: During decode parameters " + e.toString());
			return null;
		}
		return parametersOperation;
	}

	public String getInfos() {
		return "operationGroups[" + operationGroups + "] operationRoles[" + operationRoles + "] operationUsers[" + operationUsers + "] operationMemberships[" + operationMemberships + "] operationProfiles[" + operationProfiles + "] operationProfileMembers[" + operationProfileMembers
				+ "] purgeGroups[" + purgeGroups + "] purgeRoles[" + purgeGroups + "] purgeUsers[" + purgeUsers + "] purgeMembership[" + purgeMembership + "] purgeProfile[" + purgeProfile + "] purgeProfileMember[" + purgeProfileMember + "] registerNewUserInProfileUser["
				+ registerNewUserInProfileUser + "]";

	}
}