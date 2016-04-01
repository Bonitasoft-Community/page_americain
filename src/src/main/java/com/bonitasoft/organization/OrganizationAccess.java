package com.bonitasoft.organization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.OrganizationExportException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileCriterion;
import org.bonitasoft.engine.search.SearchOptionsBuilder;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.organization.ParametersOperation.RegisterNewUserInProfile;
import com.bonitasoft.organization.csv.impl.OrganizationSourceCSV;


// https://docs.google.com/a/bonitasoft.com/document/d/1z_k-T1vH984ZFIak6uR1CagGXmQrqPO_u7EumgHhLXE/edit
public class OrganizationAccess {

		private IdentityAPI identityAPI;
		private ProfileAPI profileAPI;
		
		public OrganizationAccess(IdentityAPI identityAPI, ProfileAPI profileAPI) {
				this.identityAPI = identityAPI;
				this.profileAPI = profileAPI;
		}

		// Ou placer le XML dans un studio ?
		/**
		 * return
		 * 
		 * @param sourceDirectory
		 * @param archiveDirectory
		 * @param parametersLoad
		 * @return
		 */
		public ArrayList<HashMap<String, String>> saveOrganizationFromDir(String sourceDirectory, String archiveDirectory, ParametersOperation parametersLoad ) {
				
				OrganizationLog organisationLog = new OrganizationLog(parametersLoad.logInfo);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				ArrayList<HashMap<String, String>> listStatusLoad = new ArrayList<HashMap<String, String>>();
				
				// organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "look directory [" + sourceDirectory + "]");
						
				File dir = new File(sourceDirectory);
				File[] filesList = dir.listFiles();
				// organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "found  [" + filesList.length + "] files");
				for (File file : filesList) {

						HashMap<String, String> statusLoad = new HashMap<String, String>();
						String status = "";
						try {
								if (!file.isFile() || !file.getName().endsWith(".csv")) {
										// organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "ignore file[" + file.getName() + "]");

										continue;
								}
								// organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "Files found[" + file.getCanonicalPath() + "]");

								statusLoad.put("name", "Handle Archive \"" + file.getCanonicalPath() + "\"");
								statusLoad.put("timestamp", dateFormat.format(new Date()));

								organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", " Manage deployment with options " + parametersLoad.getInfos());
								OrganizationIntSource organizationSource = null;
								if (file.getName().endsWith(".csv")) {
										organizationSource = new OrganizationSourceCSV();
										((OrganizationSourceCSV) organizationSource).loadFromFile(file.getCanonicalPath(), null);
								} else {
										statusLoad.put("status", "unknow file format");
										continue;
								}

								OrganizationAccess organizationAccess = new OrganizationAccess(identityAPI, profileAPI);
								OrganizationLog organizationLog = organizationAccess.saveOrganisation(organizationSource, parametersLoad);
								HashMap<String, Item.StatisticOnItem> statistics = organizationAccess.getStatistics();

								// Now save the statistics on the JSSON
								status += "+ is created,~ is modified, - is deleted;";
								for (String item : statistics.keySet()) {
										Item.StatisticOnItem oneStats = statistics.get(item);
										status += "(" + item + ":+" + oneStats.nbCreatedItem + " ~" + oneStats.nbUpdatedItem + " -" + oneStats.nbPurgedItem + "),";
								}
								status += organizationLog.getLog();
						} catch (Exception e) {
								StringWriter sw = new StringWriter();
								  e.printStackTrace(new PrintWriter(sw));
								  String exceptionDetails = sw.toString();

								 
								organisationLog.log(true, "OrganizationAccess.saveOrganizationFromDir:", "Error on deployment " + e.toString()+" at "+exceptionDetails);
								status += "Failure during import;";
						}

						// now move the path to the output directory
						try {
								organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "Move file now");

								String completeFileName = file.getCanonicalPath();
								String outputFileName = file.getName();
								Calendar c = Calendar.getInstance();
								outputFileName = outputFileName.replace(".csv", "");
								outputFileName = archiveDirectory + "/" + outputFileName + "-" + c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH) + "-"
												+ c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + +c.get(Calendar.SECOND) + "_" + c.get(Calendar.MILLISECOND) + ".csv";

								File outputFile = new File(outputFileName);
								outputFile.createNewFile();

								String inputFileName = file.getCanonicalPath();

								OutputStream outStream = new FileOutputStream(outputFile);
								InputStream inStream = new FileInputStream(file);

								byte[] buffer = new byte[1024];

								int length;
								// copy the file content in bytes
								while ((length = inStream.read(buffer)) > 0) {
										outStream.write(buffer, 0, length);
								}

								organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "#################################### Copy finish, now delete");
								inStream.close();

								outStream.close();
								System.gc();
								organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "and then delete file [" + file.getCanonicalPath() + "]");
								File fileToDelete = new File(completeFileName);
								try {
										boolean fileDelete = fileToDelete.delete();
										organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "file deleted[" + completeFileName + "] : " + fileDelete);
								} catch (Exception e) {
										organisationLog.log(true, "OrganizationAccess.saveOrganizationFromDir:", "American.groovy: error during delete " + e.toString());
								}

								status += "File move to archive;";
						} catch (Exception e) {
								organisationLog.log(true, "OrganizationAccess.saveOrganizationFromDir:", "American.groovy: error during move " + e.toString());
								status += "Done but cannot move the archive to the output folder";
						}

						statusLoad.put("status", status);
						listStatusLoad.add(statusLoad);

						// actionResult.put("status", "Ok");
						// actionResult.put("name", "name");
						// actionResult.put("timestamp", "time");

						organisationLog.log(false, "OrganizationAccess.saveOrganizationFromDir:", "American ends one file:" + status);
				}
				return listStatusLoad;
		}

		/* ******************************************************************************** */
		/*																																									*/
		/* Saved */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		// Synchronisation : faire un listener
		public OrganizationLog saveOrganisation(OrganizationIntSource source, ParametersOperation parametersLoad) {
				OrganizationLog organizationLog = new OrganizationLog(parametersLoad.logInfo);

				// how to manage the purge ? We have to compare the list of current
				// user/group/role/membership AND the list come from the source.
				// If the search can work with a UpdateDate, it's easy : keep the
				// current date, do all operations, and then search all elements with a
				// modified date BEFORE the current date : you can purge them
				// but there are no modified date accessible by the API :-(
				// So two solutions : keep FIRST all itemIt, then reduce the list for
				// each item loaded/updated. At the end, the list is the deleted object
				// OR keep the list of loaded/updated item, and at the end, load all
				// elements and then compare
				// the first solution is easiest to develop.

				// User
				Item.StatisticOnItem statisticOnItemUser = new Item.StatisticOnItem();
				if (parametersLoad.purgeUsers)
						ItemUser.photoAll(statisticOnItemUser, identityAPI, organizationLog);
				loadedItem.put(ItemUser.cstItemName, statisticOnItemUser);

				// role
				Item.StatisticOnItem statisticOnItemRole = new Item.StatisticOnItem();
				if (parametersLoad.purgeRoles || parametersLoad.purgeMembership)
						ItemRole.photoAll(statisticOnItemRole, identityAPI, organizationLog);
				loadedItem.put(ItemRole.cstItemName, statisticOnItemRole);

				// Membership : listOfUser is required
				Item.StatisticOnItem statisticOnItemMembership = new Item.StatisticOnItem();
				if (parametersLoad.purgeMembership)
						ItemMembership.photoAll(statisticOnItemMembership, statisticOnItemRole, identityAPI, organizationLog);
				loadedItem.put(ItemMembership.cstItemName, statisticOnItemMembership);

				// group
				Item.StatisticOnItem statisticOnItemGroup = new Item.StatisticOnItem();
				if (parametersLoad.purgeGroups)
						ItemGroup.photoAll(statisticOnItemGroup, identityAPI, organizationLog);
				loadedItem.put(ItemGroup.cstItemName, statisticOnItemGroup);
				
				Item.StatisticOnItem statisticOnItemProfile = new Item.StatisticOnItem();
				if (parametersLoad.purgeProfile)
						ItemProfile.photoAll(statisticOnItemProfile, profileAPI, organizationLog);
				loadedItem.put(ItemProfile.cstItemName, statisticOnItemProfile);
				
				Item.StatisticOnItem statisticOnItemProfileMember = new Item.StatisticOnItem();
				if (parametersLoad.purgeProfileMember)
						ItemProfileMember.photoAll(statisticOnItemProfileMember, parametersLoad,  profileAPI, organizationLog);
				loadedItem.put(ItemProfileMember.cstItemName, statisticOnItemProfileMember);
					
				

				// now load
				try {
						source.initInput(organizationLog);
						Item organisationItem = null;
						do {
								organisationItem = source.getNextItem(organizationLog);
								if (organisationItem != null) {
										organisationItem.saveInServer(this, parametersLoad, identityAPI, profileAPI, organizationLog);
										registerItem(organisationItem, parametersLoad, organizationLog);
								}
						} while (organisationItem != null);

						
						/** according the user in profile policy, create all missing membership
						 * 
						 */
						if (parametersLoad.registerNewUserInProfileUser == RegisterNewUserInProfile.USERPROFILEIFNOTREGISTER)
						{
								for (Long userId : registerMaybeInUserProfile)
										registerInUserProfile(  userId,  profileAPI,  organizationLog);
						}
						/**
						 * now check the purge, item per item First, user and membership
						 */
						// First, membership
						if (parametersLoad.purgeMembership)
								ItemMembership.purgeFromList(loadedItem.get(ItemMembership.cstItemName), parametersLoad, identityAPI, organizationLog);
						// then user
						if (parametersLoad.purgeUsers)
								ItemUser.purgeFromList(loadedItem.get(ItemUser.cstItemName), parametersLoad, identityAPI, organizationLog);
						if (parametersLoad.purgeGroups)
								ItemGroup.purgeFromList(loadedItem.get(ItemGroup.cstItemName), parametersLoad, identityAPI, organizationLog);
						if (parametersLoad.purgeRoles)
								ItemRole.purgeFromList(loadedItem.get(ItemRole.cstItemName), parametersLoad, identityAPI, organizationLog);
						if (parametersLoad.purgeProfile)
								ItemProfile.purgeFromList(loadedItem.get(ItemProfile.cstItemName), parametersLoad, profileAPI, organizationLog);
						if (parametersLoad.purgeProfileMember)
								ItemProfileMember.purgeFromList(loadedItem.get(ItemProfileMember.cstItemName), parametersLoad,  profileAPI, organizationLog);
						
				} catch (Exception e) {
						organizationLog.log(true, "OrganizationAccess.saveOrganization:", " in Error " + e.toString());
				}
				return organizationLog;
		}

		private Long userProfileId=null;
		private HashSet<Long> registerMaybeInUserProfile = new HashSet<Long>();
		/**
		 * the user IS systematicaly referenced in the User Profile API
		 * @param userId
		 * @param profileAPI
		 * @param organizationLog
		 */
		protected void registerInUserProfile( long userId, ProfileAPI profileAPI, OrganizationLog organizationLog)
		{
				if (userProfileId == null)
				{
						Profile profile = ItemProfile.getProfileByName(ItemProfile.cstProfileNameUser, profileAPI, organizationLog);
						userProfileId= profile==null ? null : profile.getId();
				}
				
				// is the user is in one profile ?
				List<Profile> listProfile =  profileAPI.getProfilesForUser(userId,0,10, ProfileCriterion.ID_DESC);
				if (listProfile.size()==0)
				{
						// register the user in the userProfile 
						ItemProfileMember.registerUserInProfile( userId, userProfileId, profileAPI, organizationLog );
				}
		}
		
		/**
		 * the user must be register in the user profile if he has not explicitaly referenced in the profile during the loading
		 * @param userId
		 * @param profileAPI
		 * @param organizationLog
		 */
		protected void registerInUserProfileIfNeeded( long userId, ProfileAPI profileAPI, OrganizationLog organizationLog )										
		{
				registerMaybeInUserProfile.add( userId );
		}
		protected void userIsInProfile(long userId, long profileId,OrganizationLog organizationLog )
		{
				registerMaybeInUserProfile.remove( userId );
		}
		
		private HashMap<String, Item.StatisticOnItem> loadedItem = new HashMap<String, Item.StatisticOnItem>();

		/**
		 * if the control is to PURGE all element, then we need to keep in memory
		 * all new-update item, in order to purge all non referenced element
		 * 
		 * @param organisationItem
		 * @param source
		 * @param parametersLoad
		 */
		private void registerItem(Item organisationItem, ParametersOperation parametersLoad, OrganizationLog organisationLog) {
				Item.StatisticOnItem statisticOnItem = loadedItem.get(organisationItem.getTypeItem());
				if (statisticOnItem == null) {
						statisticOnItem = new Item.StatisticOnItem();
				}
				if (organisationItem.isCreated())
						statisticOnItem.nbCreatedItem++;
				else
						statisticOnItem.nbUpdatedItem++;
				loadedItem.put(organisationItem.getTypeItem(), statisticOnItem);

				// remove in the list : all item at the end is entity at begining and not updated/created
				statisticOnItem.listKeyItem.remove(organisationItem.getBonitaId());
		}

		/**
		 * return the statistics
		 * 
		 * @return
		 */
		public HashMap<String, Item.StatisticOnItem> getStatistics() {
				return loadedItem;
		}

		/* ******************************************************************************** */
		/*																																									*/
		/* getTheXml */
		/*																																									*/
		/*																																									*/
		/* ******************************************************************************** */

		/**
		 * 
		 * @param parametersLoad
		 * @return
		 */
		public String getOrganizationOnXml(ParametersOperation parametersLoad) {
				try {
						return identityAPI.exportOrganization();
				} catch (OrganizationExportException e) {
						return null;
				}
		}

		public void getOrganizationOnXml(boolean logInfo, String fileName) {
				OrganizationLog organisationLog = new OrganizationLog(logInfo);
				try {
						String xmlContent = identityAPI.exportOrganization();
						FileOutputStream fileoutput = new FileOutputStream(fileName);
						fileoutput.write(xmlContent.getBytes());
						fileoutput.close();

				} catch (OrganizationExportException e) {
						organisationLog.log(true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
						return;
				} catch (FileNotFoundException e) {
						organisationLog.log(true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
				} catch (IOException e) {
						organisationLog.log(true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
				}
		}

		// acteur filter .
		public List<Long> getCandidates(Long taskId, OrganizationIntSource source) {
				return new ArrayList<Long>();
		}
}
