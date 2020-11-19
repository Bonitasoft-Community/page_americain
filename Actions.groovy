import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils

import org.bonitasoft.web.extension.page.PageContext;
import org.bonitasoft.web.extension.page.PageController;
import org.bonitasoft.web.extension.page.PageResourceProvider;

import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;

/* we want to create profile, so only the subscription can do that
 * */
 
import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;

import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.command.CommandDescriptor;
import org.bonitasoft.engine.command.CommandCriterion;


import org.bonitasoft.log.event.BEvent;
import org.bonitasoft.log.event.BEventFactory;
import org.bonitasoft.log.event.BEvent.Level;


import org.bonitasoft.americanorganization.ParametersOperation;
import org.bonitasoft.americanorganization.OrganizationIntSource;
import org.bonitasoft.americanorganization.AmericanOrganizationAPI;
import org.bonitasoft.americanorganization.AmericanOrganizationAPI.Configuration;

import org.bonitasoft.americanorganization.csv.impl.OrganizationSourceCSV;
import org.bonitasoft.americanorganization.OrganizationLog;
import org.bonitasoft.americanorganization.Item;
import org.bonitasoft.americanorganization.Item.StatisticOnItem;
  
public class Actions {

	private static Logger logger= Logger.getLogger("org.bonitasoft.custompage.american.groovy");
	private HashMap<String,String> optionParametersOperation = new HashMap<String,String>();
		
		
	public static Index.ActionAnswer doAction(HttpServletRequest request, String paramJsonSt, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
    	
      
		Index.ActionAnswer actionAnswer = new Index.ActionAnswer();	
		try
		{
		
			APISession apiSession = pageContext.getApiSession()
			IdentityAPI identityApi = TenantAPIAccessor.getIdentityAPI(apiSession);
			ProfileAPI profileApi = TenantAPIAccessor.getProfileAPI(apiSession);
	
			AmericanOrganizationAPI organizationAPI = new AmericanOrganizationAPI( identityApi, profileApi );
				
		    String action = request.getParameter("action");
            logger.info("-------------------  American runs action[" + action + "] from page=[" + request.getParameter("page") + "] ");
            if (action == null || action.length() == 0 ) {
            	actionAnswer.isManaged=false;
            	logger.info("#### LongBoardCustomPage:Actions END No Actions");
                return actionAnswer;
            }
            actionAnswer.isManaged=true;
			
            //Make sure no action is executed if the CSRF protection is active and the request header is invalid
            if (! TokenValidator.checkCSRFToken(request, response)) {
                 actionAnswer.isResponseMap=false;
                 return actionAnswer;
             }
         
	        ArrayList<HashMap<String, Object>> actions = new ArrayList<HashMap<String, Object>>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if("getproperties".equals(action)) {
			
                logger.info("################  American getProperties");
                Configuration configuration = organizationAPI.loadConfiguration(pageResourceProvider.getPageName(), apiSession.getTenantId());
                HashMap<String, Object> propertiesMap = configuration.getMap();
                logger.info(" Get : Properties Map : ["+propertiesMap+"]");
                actionAnswer.setResponse( propertiesMap );
		
			// -------------------------------------------------------- setproperties
            } else if("setproperties".equals(action)) {
                logger.info("################  American setProperties");
				Configuration configuration = new Configuration();
				configuration.setFromJson(paramJsonSt );

				
				HashMap<String, Object> actionResult = new HashMap<String, Object>();
				actionResult.put("name", "Set properties");
				actionResult.put("timestamp", dateFormat.format(new Date()));
			
				
				logger.info("American sets properties");
				String defaultFilePath = (new File(".")).getCanonicalPath() ;
				if (configuration.getDropZone().isEmpty()) {
					actionResult.put("errordropzone", "No new value found, it will set to: [" + defaultFilePath + "]");
					configuration.setDropZone( defaultFilePath);
				} else if(!(new File(configuration.getDropZone())).exists() || !(new File(configuration.getDropZone())).isDirectory()) {
					actionResult.put("errordropzone", "New value path ["+configuration.getDropZone()+"] is not valid, it will set to: \"" + defaultFilePath + "\"");
					configuration.setDropZone(defaultFilePath);
				} else {
					actionResult.put("errordropzone", "");
				}
				
				String defaultArchiveFilePath=configuration.getDropZone()+"/archive";
				if (configuration.getArchiveZone().isEmpty()) {
					actionResult.put("errorarchivedropzone", "No new value found, it will set to: [" + defaultArchiveFilePath + "]");
					configuration.setArchiveZone(defaultArchiveFilePath);
				} else if(!(new File(configuration.getArchiveZone() )).exists() || !(new File(configuration.getArchiveZone())).isDirectory()) {
					actionResult.put("errorarchivedropzone", "New value path ["+configuration.getArchiveZone()+"] is not valid, it will set to: \"" + defaultArchiveFilePath + "\"");
					configuration.setArchiveZone(defaultArchiveFilePath);
				} else {
					actionResult.put("errorarchivedropzone", "");
				}
				
				
				String detailStatus= "";
				detailStatus="Drop Zone is ["+configuration.getDropZone() +"];";
				detailStatus +="Archive Drop Zone is ["+configuration.archiveZone +"]";
					
                List<BEvent> listEvents= organizationAPI.saveConfiguration( configuration,  pageResourceProvider.getPageName(), apiSession.getTenantId());
                if (BEventFactory.isError( listEvents))
                {
                	actionResult.put("status", "Error");
                }
                else
                	actionResult.put("status", "Done");
					
						
				logger.info("######################################  New drop zone["+configuration.getDropZone()+"] archiveFile["+configuration.getArchiveZone()+"]");
					
				
				// prepare the result for the history
				HashMap<String, Object> history = new HashMap<String, Object>();
				
				history.put("name", "Set Drop Zone property");
				history.put("timestamp", dateFormat.format(new Date()));
				history.put("status", actionResult.get("status")+ " "+detailStatus);
				actionResult.put("history", history);
				
                logger.info("American ends setting properties");
                actionAnswer.setResponse( actionResult );
			// ------------------------------------------------------------------------ refresh
            } else if ("refresh".equals(action))  {
                Configuration configuration = organizationAPI.loadConfiguration(pageResourceProvider.getPageName(), apiSession.getTenantId());
        		
                
                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) American refreshes drop zone["+configuration.dropZone+"] archiveFile["+configuration.archiveZone+"]");
				
                
                File pathOutputDir = new File(configuration.archiveZone);
                boolean outputFolderIsHere = pathOutputDir.exists();
                if (!outputFolderIsHere) {
                    HashMap<String, Object> actionResult = new HashMap<String, Object>();
                    actionResult.put("name", "Create Archive folder (" + pathOutputDir.getCanonicalPath() + ")");
                    actionResult.put("timestamp", dateFormat.format(new Date()));
                    if(pathOutputDir.mkdirs()) {
                        actionResult.put("status", "Done");
                        outputFolderIsHere = true;
                    } else {
                        actionResult.put("status", "Failure");
                    	actionResult.put("errors","Can't create the directory ["+pathOutputDir.getAbsolutePath()+"]");
                    }
                    actions.add(actionResult);
                }

                if (outputFolderIsHere) {

					ParametersOperation parametersLoad = ParametersOperation.getInstance( configuration.parametersOperation );
					
					if (parametersLoad==null)
					{
						HashMap<String, Object> actionResult = new HashMap<String, Object>();
                        actionResult.put("name", "Decode parameters");
                        actionResult.put("timestamp", dateFormat.format(new Date()));
                        actionResult.put("status", "Can't decode");
						actionResult.put("errors", "Error during decodage parameters;");
						actions.add( actionResult );
					}
					else
					{
		                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) Start pooling dir");

						actions.addAll( organizationAPI.saveOrganizationFromDir( configuration.getDropZone(), configuration.getArchiveZone(), parametersLoad ));
		                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) End Start pooling dir");
					}					
					logger.info("American ends one file");
                    
                }
                actionAnswer.responseMap.put("actions", actions);
				logger.info("American ends AllFiles : "+JSONArray.toJSONString(actions));
               
            } else if ("uploadbar".equals(action))  {
			
            	 Configuration configuration = organizationAPI.loadConfiguration(pageResourceProvider.getPageName(), apiSession.getTenantId());
         		
				
                HashMap<String, Object> actionResult = new HashMap<String, Object>();
                actionResult.put("name", "Archive Upload");
                actionResult.put("timestamp", dateFormat.format(new Date()));

				String uploadedFile = request.getParameter("file");
				String uploadedFileName = request.getParameter("name");
				String completeUploadFile="";

				File pageDirectory = pageResourceProvider.getPageDirectory();
				
				// get the temporary name
				List<String> listParentTmpFile = new ArrayList<String>();
				try
				{
					listParentTmpFile.add( pageDirectory.getCanonicalPath()+"/../../../tmp/");
					listParentTmpFile.add( pageDirectory.getCanonicalPath()+"/../../");
				}
				catch (Exception e)
				{
					logger.info("American : error get CanonicalPath of pageDirectory["+e.toString()+"]");					
					return;
				}
				
				for (String pathTemp : listParentTmpFile)
				{
					logger.info("American : CompleteuploadFile  TEST ["+pathTemp+uploadedFile+"]");	
					if (uploadedFile.length() > 0 && (new File(pathTemp+uploadedFile)).exists()) {
						completeUploadFile=(new File(pathTemp+uploadedFile)).getAbsoluteFile() ;
						logger.info("American : CompleteuploadFile  FOUND ["+completeUploadFile+"]");					
					}
				}
				
				if (completeUploadFile.length() == 0 && (new File(completeUploadFile)).exists()) {
                    actionResult.put("status", "Error");
					actionResult.put("explanation", "Error: an error occurred during the upload request");
				} else {
					try {
						if(completeUploadFile.endsWith(".csv")) {
							FileUtils.copyFile(new File(completeUploadFile), new File(configuration.getDropZone() + File.separator + uploadedFileName));
						} else {
                            actionResult.put("status", "Error");
							actionResult.put("explanation", "Error: The uploaded file \"" + uploadedFileName + "\" is not taken into account as it is not a CSV file");
						}
						
						(new File(completeUploadFile)).delete();
						
						if(uploadedFile.endsWith(".csv")) {
                            actionResult.put("status", "Success");
							actionResult.put("explanation", "The Source file \"" + uploadedFileName + "\" has been uploaded in the Monitor directory ["+configuration.getDropZone()+"]");
						}
					} catch(Exception e) {
                        actionResult.put("status", "Error");
                        actionResult.put("explanation", "Error: The Source file \"" + uploadedFileName + "\" has not been uploaded in the archive directory due to execution error "+e.toString());
					}
				}

                actions.add(actionResult);
                actionAnswer.setResponse( actions );
            }

            return actionAnswer;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();
            logger.severe("Exception ["+e.toString()+"] at "+exceptionDetails);
            actionAnswer.isResponseMap=true;
			actionAnswer.responseMap.put("Error", "LongBoardCustomPage:Groovy Exception ["+e.toString()+"] at "+exceptionDetails);
			return actionAnswer;

        }
    } // end doGet

	

	
    private void runTheBonitaIndexDoGet(HttpServletRequest request, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
        try {
            def String indexContent;
            pageResourceProvider.getResourceAsStream("index.html").withStream { InputStream s-> indexContent = s.getText() }

            def String pageResource="pageResource?&page="+ request.getParameter("page")+"&location=";

			// to run on BPM 7
            // indexContent= indexContent.replace("@_USER_LOCALE_@", request.getParameter("locale"));
            // indexContent= indexContent.replace("@_PAGE_RESOURCE_@", pageResource);

            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(indexContent);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
