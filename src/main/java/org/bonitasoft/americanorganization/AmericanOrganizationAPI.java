package org.bonitasoft.americanorganization;

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
import java.util.Map;
import java.util.logging.Logger;

import org.bonitasoft.americanorganization.ParametersOperation.RegisterNewUserInProfile;
import org.bonitasoft.americanorganization.csv.impl.OrganizationSourceCSV;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.identity.OrganizationExportException;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileCriterion;
import org.bonitasoft.log.event.BEvent;
import org.bonitasoft.log.event.BEventFactory;
import org.bonitasoft.properties.BonitaProperties;
import org.json.simple.JSONValue;

import com.bonitasoft.engine.api.ProfileAPI;

// https://docs.google.com/a/bonitasoft.com/document/d/1z_k-T1vH984ZFIak6uR1CagGXmQrqPO_u7EumgHhLXE/edit
public class AmericanOrganizationAPI {

    public static Logger logger = Logger.getLogger(AmericanOrganizationAPI.class.getName());

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private IdentityAPI identityAPI;
    private ProfileAPI profileAPI;

    public final static String cstActionErrors = "errors";
    public final static String cstActionStatus = "status";
    public final static String cstActionName = "name";
    public final static String cstActionTimeStamp = "timestamp";

    public AmericanOrganizationAPI(IdentityAPI identityAPI, ProfileAPI profileAPI) {
        this.identityAPI = identityAPI;
        this.profileAPI = profileAPI;
        
    }

    /**
     * return
     * 
     * @param sourceDirectory
     * @param archiveDirectory
     * @param parametersLoad
     * @return
     */
    public List<Map<String, String>> saveOrganizationFromDir(String sourceDirectory, String archiveDirectory, ParametersOperation parametersLoad) {

        OrganizationLog organisationLog = new OrganizationLog(parametersLoad.logInfo);
        ArrayList<Map<String, String>> listStatusLoad = new ArrayList<>();

        // organisationLog.log(false,
        // "OrganizationAccess.saveOrganizationFromDir:", "look directory [" +
        // sourceDirectory + "]");

        File dir = new File(sourceDirectory);
        File[] filesList = dir.listFiles();
        // organisationLog.log(false,
        // "OrganizationAccess.saveOrganizationFromDir:", "found [" +
        // filesList.length + "] files");
        StringBuilder analyseContentDirectory = new StringBuilder();
        int countNbFilesDetected = 0;
        String prefix=null;
        for (File file : filesList) {

            HashMap<String, String> statusLoad = new HashMap<>();
            String status = "";
            String errors = "";
            try {
                prefix = checkFileReadyToProcess(file,analyseContentDirectory);

                if (prefix == null)
                    continue;

                // organisationLog.log(false,
                // "OrganizationAccess.saveOrganizationFromDir:", "Files found["
                // + file.getCanonicalPath() + "]");
                String fileName = file.getCanonicalPath();
                String fileNameWithoutPrefix = fileName.substring(0, fileName.length() - (prefix.length()));

                // Tag the file name, to be sure two thread does not process then at the same time
                File newfile = new File(fileNameWithoutPrefix + CST_TAGINPROGRESS + prefix);

                if (file.renameTo(newfile)) {
                    file = newfile;
                } else {
                    analyseContentDirectory.append("Can't rename file to " + newfile.getAbsolutePath() + "] Don't process it;");
                    organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "Can't rename file to " + newfile.getAbsolutePath() + "] Don't process it");
                    continue;
                }

                countNbFilesDetected++;
                statusLoad.put(cstActionName, "Handle Archive [" + fileName + "]");
                statusLoad.put(cstActionTimeStamp, sdf.format(new Date()));

                organisationLog.log(false, false, "OrganizationAccess.saveOrganizationFromDir:", " Manage deployment with options " + parametersLoad.getInfos());
                OrganizationIntSource organizationSource = null;
                if (prefix.equals(".csv")) {
                    organizationSource = new OrganizationSourceCSV();
                    ((OrganizationSourceCSV) organizationSource).loadFromFile(file.getCanonicalPath(), null);
                } else {
                    statusLoad.put(cstActionStatus, "unknow file format");
                    statusLoad.put(cstActionErrors, "unknow file format : only .csv is accepted]");
                    continue;
                }

                AmericanOrganizationAPI organizationAccess = new AmericanOrganizationAPI(identityAPI, profileAPI);
                OrganizationLog organizationLog = organizationAccess.saveOrganisation(organizationSource, parametersLoad);
                HashMap<String, Item.StatisticOnItem> statistics = organizationAccess.getStatistics();

                // Now save the statistics on the JSSON
                status += "+ is created,~ is modified, - is deleted;";
                for (String item : statistics.keySet()) {
                    Item.StatisticOnItem oneStats = statistics.get(item);
                    status += "(" + item + ": +" + oneStats.nbCreatedItem + " ~ " + oneStats.nbUpdatedItem + " - " + oneStats.nbPurgedItem + " in " + oneStats.totalTimeOperation + " ms ";
                    if (oneStats.nbCreatedItem + oneStats.nbUpdatedItem + oneStats.nbPurgedItem > 0)
                        status += " average " + (oneStats.totalTimeOperation / (oneStats.nbCreatedItem + oneStats.nbUpdatedItem + oneStats.nbPurgedItem)) + " ms";
                    status += "),";
                }
                status += organizationLog.getLog();
                errors += organizationLog.getErrors();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionDetails = sw.toString();

                organisationLog.log(true, true, "OrganizationAccess.saveOrganizationFromDir:", "Error on deployment " + e.toString() + " at " + exceptionDetails);
                errors += "Failure during import;";
            }

            // now move the file to the output directory
            try {
                organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "Move file now");

                String completeFileName = file.getCanonicalPath();
                String outputFileName = file.getName();
                Calendar c = Calendar.getInstance();
                outputFileName = outputFileName.replace(prefix, "");
                outputFileName = archiveDirectory + "/" + outputFileName + "-" + c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + +c.get(Calendar.SECOND) + "_"
                        + c.get(Calendar.MILLISECOND) + prefix;

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

                organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "#################################### Copy finish, now delete");
                inStream.close();

                outStream.close();
                System.gc();
                organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "and then delete file [" + file.getCanonicalPath() + "]");
                File fileToDelete = new File(completeFileName);
                try {
                    boolean fileDelete = fileToDelete.delete();
                    organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "file deleted[" + completeFileName + "] : " + fileDelete);
                } catch (Exception e) {
                    organisationLog.log(true, true, "OrganizationAccess.saveOrganizationFromDir:", "American.groovy: error during delete " + e.toString());
                }

                status += "File move to archive;";
            } catch (Exception e) {
                organisationLog.log(true, true, "OrganizationAccess.saveOrganizationFromDir:", "American.groovy: error during move " + e.toString());
                status += "Done but cannot move the archive to the output folder";
            }

            statusLoad.put(cstActionStatus, status);
            statusLoad.put(cstActionErrors, errors);

            listStatusLoad.add(statusLoad);

            // actionResult.put("status", "Ok");
            // actionResult.put("name", "name");
            // actionResult.put("timestamp", "time");

            organisationLog.log(false, true, "OrganizationAccess.saveOrganizationFromDir:", "American ends one file:" + status);

        }
        if (countNbFilesDetected == 0) {
            HashMap<String, String> statusLoad = new HashMap<>();
            statusLoad.put(cstActionStatus, "No files detected "+analyseContentDirectory.toString());
            statusLoad.put(cstActionName, "");
            statusLoad.put(cstActionTimeStamp, sdf.format(new Date()));

            listStatusLoad.add(statusLoad);

        }
        return listStatusLoad;
    }

    /*
     * *************************************************************************
     * *******
     */
    /*																																									*/
    /* Saved */
    /*																																									*/
    /*																																									*/
    /*
     * *************************************************************************
     * *******
     */

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
        BonitaAccessAPI bonitaAccessAPI = new BonitaAccessAPI(profileAPI, identityAPI);
        
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
            ItemProfileMember.photoAll(statisticOnItemProfileMember, bonitaAccessAPI, parametersLoad, organizationLog);
        loadedItem.put(ItemProfileMember.cstItemName, statisticOnItemProfileMember);

        int countItem = 0;
        int numberOfItems = source.getNumberOfItems(organizationLog);
        organizationLog.log(false, false, AmericanOrganizationAPI.class.getName(), "Start load organization NumberOfItems="+numberOfItems);

        long previousPackageTime=0;
        long start = System.currentTimeMillis();
        // now load
        try {
            source.initInput(organizationLog);
            Item organisationItem = null;
            do {
                organisationItem = source.getNextItem(organizationLog);
                countItem++;
                if (countItem % 1000 == 0) {
                    long currentTime=System.currentTimeMillis();
                    String info= "Load in progress " + countItem + "/" + numberOfItems + " in " + (currentTime - start) + " ms";
                    if (previousPackageTime!=0)
                        info+= " Last 1000 items in "+(currentTime-previousPackageTime)+" ms";
                    previousPackageTime = currentTime;
                    organizationLog.log(false, false, AmericanOrganizationAPI.class.getName(), info);
                    source.traceAdvancement( countItem, numberOfItems,info, organizationLog );
                }
                if (organisationItem != null) {
                    long startItem = System.currentTimeMillis();
                    organisationItem.saveInServer(this, bonitaAccessAPI, parametersLoad, organizationLog);
                    organisationItem.timeOperation = System.currentTimeMillis() - startItem;
                    registerItem(organisationItem, parametersLoad, organizationLog);
                }

            } while (organisationItem != null);

            /**
             * according the user in profile policy, create all missing
             * membership
             */
            if (parametersLoad.registerNewUserInProfileUser == RegisterNewUserInProfile.USERPROFILEIFNOTREGISTER) {
                for (Long userId : registerMaybeInUserProfile)
                    registerInUserProfile(userId, bonitaAccessAPI, organizationLog);
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
                ItemProfileMember.purgeFromList(loadedItem.get(ItemProfileMember.cstItemName), parametersLoad, profileAPI, organizationLog);

        } catch (Exception e) {
            organizationLog.log(true, true, "OrganizationAccess.saveOrganization:", " in Error " + e.toString());
        } catch (Error er) {
            organizationLog.log(true, true, "OrganizationAccess.saveOrganization:", " in Error " + er.toString());
        }
        
        // close the source now
        try {
            source.endInput(organizationLog);
        } catch (Exception e) {
            organizationLog.log(true, true, "OrganizationAccess.saveOrganization:", "Error during close the source "+ e.toString());

        }

        return organizationLog;
    }

    private Long userProfileId = null;
    private HashSet<Long> registerMaybeInUserProfile = new HashSet<Long>();

    /**
     * the user IS systematicaly referenced in the User Profile API
     * 
     * @param userId
     * @param profileAPI
     * @param organizationLog
     * @throws Exception 
     */
    protected void registerInUserProfile(long userId, BonitaAccessAPI bonitaAccessAPI, OrganizationLog organizationLog) throws SearchException {
        if (userProfileId == null) {
            Profile profile = bonitaAccessAPI.getProfileByName(ItemProfile.cstProfileNameUser, organizationLog);
            userProfileId = profile == null ? null : profile.getId();
        }

        // is the user is in one profile ?
        List<Profile> listProfile = profileAPI.getProfilesForUser(userId, 0, 10, ProfileCriterion.ID_DESC);
        if (listProfile.isEmpty()) {
            // register the user in the userProfile
            ItemProfileMember.registerUserInProfile(userId, userProfileId, profileAPI, organizationLog);
        }
    }

    /**
     * the user must be register in the user profile if he has not explicitaly
     * referenced in the profile during the loading
     * 
     * @param userId
     * @param profileAPI
     * @param organizationLog
     */
    protected void registerInUserProfileIfNeeded(long userId, BonitaAccessAPI bonitaAccessAPI, OrganizationLog organizationLog) {
        registerMaybeInUserProfile.add(userId);
    }

    protected void userIsInProfile(long userId, long profileId, OrganizationLog organizationLog) {
        registerMaybeInUserProfile.remove(userId);
    }

    private HashMap<String, Item.StatisticOnItem> loadedItem = new HashMap<>();

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
        statisticOnItem.totalTimeOperation += organisationItem.timeOperation;
        loadedItem.put(organisationItem.getTypeItem(), statisticOnItem);

        // remove in the list : all item at the end is entity at begining and
        // not updated/created
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

    /*
     * *************************************************************************
     * *******
     */
    /*																																									*/
    /* getTheXml */
    /*																																									*/
    /*																																									*/
    /*
     * *************************************************************************
     * *******
     */

    /**
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
            organisationLog.log(true, true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
            return;
        } catch (FileNotFoundException e) {
            organisationLog.log(true, true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
        } catch (IOException e) {
            organisationLog.log(true, true, "OrganizationAccess.getOrganizationOnXml", "Error " + e.toString());
        }
    }

    // acteur filter .
    public List<Long> getCandidates(Long taskId, OrganizationIntSource source) {
        return new ArrayList<Long>();
    }

    /*
     * *************************************************************************
     * *******
     */
    /*																																									*/
    /* properties */
    /*																																									*/
    /*																																									*/
    /*
     * *************************************************************************
     * *******
     */
    public final static String cstDropZoneProperty = "dropzone";
    public final static String cstArchiveZoneProperty = "archivezone";

    public static class Configuration {

        // public String defaultArchivezone = defaultDropzone + "/archive";
        Map<String, Object> parametersOperation = new HashMap<String, Object>();

        List<BEvent> listEvents;

        public String getDropZone() {
            Object value = parametersOperation.get(cstDropZoneProperty);
            if (value == null)
                return "";
            return value.toString().trim();
        }

        public void setDropZone(String dropZone) {
            parametersOperation.put(cstDropZoneProperty, dropZone);
        }

        public String getArchiveZone() {
            Object value = parametersOperation.get(cstArchiveZoneProperty);
            if (value == null)
                return "";
            return value.toString().trim();
        }

        public void setArchiveZone(String archiveZone) {
            parametersOperation.put(cstArchiveZoneProperty, archiveZone);
        }

        public Map<String, Object> getMap() {
            return parametersOperation;
        }

        public void setFromJson(String jsontSt) {
            Object jsonObject = JSONValue.parse(jsontSt);
            if (jsonObject instanceof Map)
                parametersOperation.putAll((Map) jsonObject);

        }

    };

    /**
     * save configuration
     * 
     * @param configuration
     * @param pageName
     * @param tenantId
     * @return
     */

    public List<BEvent> saveConfiguration(Configuration configuration, String pageName, long tenantId) {
        logger.info("OrganizationAPI.saveConfiguration ~~~~~~~~" + configuration.parametersOperation);
        BonitaProperties bonitaProperties = new BonitaProperties(pageName, tenantId);

        List<BEvent> listEvents = bonitaProperties.load();
        if (BEventFactory.isError(listEvents)) {
            logger.info("OrganizationAPI.saveConfiguration , Error load properties :" + listEvents);
            return listEvents;
        }
        for (String property : configuration.parametersOperation.keySet()) {
            Object value = configuration.parametersOperation.get(property);
            // Save only flat value
            if ("options".equals(property))
                continue;
            bonitaProperties.setProperty(property, value == null ? null : value.toString());

        }
        listEvents.addAll(bonitaProperties.store());
        if (BEventFactory.isError(listEvents))
            logger.severe("OrganizationAPI.saveConfiguration : error during save" + listEvents);
        else
            logger.info("OrganizationAPI.saveConfiguration : save done");

        return listEvents;
    }

    /**
     * read configuration
     * 
     * @param pageName
     * @param tenantId
     * @return
     */
    public Configuration loadConfiguration(String pageName, long tenantId) {

        Configuration configuration = new Configuration();
        BonitaProperties bonitaProperties = new BonitaProperties(pageName, tenantId);

        configuration.listEvents = bonitaProperties.load();
        if (BEventFactory.isError(configuration.listEvents)) {
            logger.info("OrganizationAPI.saveConfiguration , Error load properties :" + configuration.listEvents);
            return configuration;
        }

        for (Object property : bonitaProperties.keySet()) {
            String value = bonitaProperties.getProperty(property.toString());
            configuration.parametersOperation.put(property.toString(), value);
        }
        logger.info("OrganizationAPI.loadConfiguration ~~~~~~~~: " + configuration.parametersOperation);

        return configuration;
    }

    private final static String CST_TAGINPROGRESS = "_INPROGRESS";
    private String[] listFilePrefix = new String[] { ".csv" };

    /**
     * check the file, return null or the prefix to process the file
     * 
     * @param file
     * @return
     */
    private String checkFileReadyToProcess(File file, StringBuilder analyseContentDirectory) {
        String prefixSelected = null;
        for (String prefix : listFilePrefix) {

            if (!file.isFile() || !file.getName().endsWith(prefix)) {
                // organisationLog.log(false,
                // "OrganizationAccess.saveOrganizationFromDir:", "ignore
                // file[" + file.getName() + "]");

                continue;
            }
            // is the file in progress ? 
            if (file.getName().endsWith(CST_TAGINPROGRESS + prefix)) {
                analyseContentDirectory.append(" ["+file.getName()+"] in progress;");
                return null;
            }
            // ok, we get one
            return prefix;
        }
        analyseContentDirectory.append(" ["+file.getName()+"] unknow prefix;");
        return null;

    }
    
 
}
