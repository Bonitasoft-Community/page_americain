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

import org.bonitasoft.console.common.server.page.PageContext
import org.bonitasoft.console.common.server.page.PageController
import org.bonitasoft.console.common.server.page.PageResourceProvider
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

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.command.CommandDescriptor;
import org.bonitasoft.engine.command.CommandCriterion;
import org.codehaus.groovy.tools.shell.CommandAlias;


import com.bonitasoft.organization.ParametersOperation;
import com.bonitasoft.organization.OrganizationIntSource;
import com.bonitasoft.organization.OrganizationAccess;
import com.bonitasoft.organization.csv.impl.OrganizationSourceCSV;
import com.bonitasoft.organization.OrganizationLog;
import com.bonitasoft.organization.Item;
import com.bonitasoft.organization.Item.StatisticOnItem;
  
public class Index implements PageController {
    static private final String defaultDropzone = ".";
    static private final String defaultArchivezone = defaultDropzone + "/archive";
    static private final String dropzoneProperty = "dropzone";
     static private final String archivezoneProperty = "archivezone";
    
	


	/**	at each call, BOS create a new Groovy script, who create this variable again (static is not real) */
    static private String dropzone = defaultDropzone;
    static private String archivezone = defaultArchivezone;

	private HashMap<String,String> optionParametersOperation = new HashMap<String,String>();
	
	
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
        Logger logger= Logger.getLogger("org.bonitasoft");
		
        try {
            def String indexContent;
            pageResourceProvider.getResourceAsStream("Index.groovy").withStream { InputStream s-> indexContent = s.getText() };
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter()

            String action = request.getParameter("action");
            logger.info("-------------------  American runs action[" + action + "] from page=[" + request.getParameter("page") + "] ");
            if (action == null || action.length() == 0 ) {
                logger.info("American load Angular JS");
                runTheBonitaIndexDoGet(request, response, pageResourceProvider, pageContext);
                return;
            }

            ArrayList<HashMap<String, Object>> actions = new ArrayList<HashMap<String, Object>>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if("getproperties".equals(action)) {
			
                logger.info("################  American getProperties");

				readProperties(pageResourceProvider);

				logger.info(" GetProperties: Set Properties Map");

                HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
                propertiesMap.put("dropzone", dropzone);
				propertiesMap.put("archivezone", archivezone);
				propertiesMap.putAll(optionParametersOperation);
				
				logger.info(" Get : Properties Map : ["+propertiesMap+"]");
               
		
				out.write(JSONValue.toJSONString(propertiesMap));
				logger.info(" Get Properties Map :  End Get["+JSONValue.toJSONString(propertiesMap)+"]");

				
				
			// -------------------------------------------------------- setproperties
            } else if("setproperties".equals(action)) {
                logger.info("################  American setProperties");
				String dropzoneNewValue = request.getParameter(dropzoneProperty);
				String archivezoneNewValue = request.getParameter(archivezoneProperty);

				logger.info("American sets properties");
					
				Properties properties = new Properties();
				
				String detailStatus= "";
				HashMap<String, Object> actionResult = new HashMap<String, Object>();
				actionResult.put("name", "Set properties");
				actionResult.put("timestamp", dateFormat.format(new Date()));
				if (dropzoneNewValue == null  || dropzoneNewValue.isEmpty()) {
					actionResult.put("errordropzone", "No new value found, it will set to: \"" + (new File(defaultDropzone)).getCanonicalPath() + "\"");
					properties.setProperty(dropzoneProperty, defaultDropzone);
				} else if(!(new File(dropzoneNewValue)).exists() || !(new File(dropzoneNewValue)).isDirectory()) {
					actionResult.put("errordropzone", "New value path ["+dropzoneNewValue+"] is not valid, it will set to: \"" + (new File(defaultDropzone)).getCanonicalPath() + "\"");
					properties.setProperty(dropzoneProperty, defaultDropzone);
				} else {
					actionResult.put("errordropzone", "");
					properties.setProperty(dropzoneProperty, dropzoneNewValue);
				}
				detailStatus="Drop Zone is ["+properties.get( dropzoneProperty) +"];";

				if (archivezoneNewValue == null  || archivezoneNewValue.isEmpty()) {
					actionResult.put("errorarchivedropzone", "No new value found, it will set to: \"" + (new File(defaultArchivezone)).getCanonicalPath() + "\"");
					properties.setProperty(archivezoneProperty, defaultArchivezone);
				} else if(!(new File(archivezoneNewValue)).exists() || !(new File(archivezoneNewValue)).isDirectory()) {
					String details = "";
					if (!(new File(archivezoneNewValue)).exists())
						details = "path is not valid";
					if (!(new File(archivezoneNewValue)).isDirectory())
						details = "path is not a directory";
						actionResult.put("errorarchivedropzone", "New value path ["+archivezoneNewValue+"] is not valid : "+details+", it will set to: \"" + (new File(defaultArchivezone)).getCanonicalPath() + "\"");
						properties.setProperty(archivezoneProperty, defaultArchivezone);
				} else {
					actionResult.put("errorarchivedropzone", "");
					properties.setProperty(archivezoneProperty, archivezoneNewValue);
				}
				
				detailStatus +="Archive Drop Zone is ["+properties.get( archivezoneProperty) +"]";
					
					
				// write all options listParametersOperation come from com.twosigma.bonitasoft.organisation.ParametersOperation
				for (String property : com.twosigma.bonitasoft.organization.ParametersOperation.listParametersOperation)
				{
					String optionValue = request.getParameter(property);
					logger.info("Properties ["+property+"] value ["+ (optionValue==null ? "IsNull":optionValue)+"]");
					if (optionValue!=null)
						properties.setProperty( property, "null".equals(optionValue) ? "False" : optionValue);
				}
	
				logger.info("Values are set, now write it");
				OutputStream output = null;
				try {
					output = new FileOutputStream(pageResourceProvider.getResourceAsFile("resources/conf/custompage.properties"));
					properties.store(output, null);
					actionResult.put("status", "Done");
					dropzone = properties.getProperty(dropzoneProperty);
					archivezone = properties.getProperty(archivezoneProperty);
					
						
					logger.info("######################################  New drop zone["+dropzone+"] archiveFile["+archivezone+"]");
					
				
				} catch (Exception e) {
					logger.severe( "Write the properties file :"+e.toString());
					actionResult.put("status", "Failure");
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (IOException e) {
							logger.severe( "Close the properties file:"+ e.toString());
						}
					}
				}
					
				// prepare the result for the history
				HashMap<String, Object> history = new HashMap<String, Object>();
				
				history.put("name", "Set Drop Zone property");
				history.put("timestamp", dateFormat.format(new Date()));
				history.put("status", actionResult.get("status")+ " "+detailStatus);
				actionResult.put("history", history);
				
                logger.info("American ends setting properties");
				out.write(JSONValue.toJSONString(actionResult));

			// ------------------------------------------------------------------------ refresh
            } else if ("refresh".equals(action))  {
				readProperties( pageResourceProvider );
				
                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) American refreshes drop zone["+dropzone+"] archiveFile["+archivezone+"]");
				
                
                File pathOutputDir = new File(archivezone);
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
                    }
                    actions.add(actionResult);
                }

                if (outputFolderIsHere) {
					APISession session = pageContext.getApiSession()
					IdentityAPI identityApi = TenantAPIAccessor.getIdentityAPI(session);
					ProfileAPI profileApi = TenantAPIAccessor.getProfileAPI(session);

					OrganizationAccess organizationAccess = new OrganizationAccess( identityApi, profileApi );
					ParametersOperation parametersLoad = ParametersOperation.getInstance( optionParametersOperation );
					
					if (parametersLoad==null)
					{
						HashMap<String, Object> actionResult = new HashMap<String, Object>();
                        actionResult.put("name", "Decode parameters");
                        actionResult.put("timestamp", dateFormat.format(new Date()));
						actionResult.put("status", "Error during decodage parameters;");
						actions.add( actionResult );
					}
					else
					{
		                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) Start pooling dir");

						actions.addAll( organizationAccess.saveOrganizationFromDir( dropzone, archivezone, parametersLoad ));
		                logger.info("~~~~~~~~~~~~~~~~Americain(Index.groovy) End Start pooling dir");
					}					
					logger.info("American ends one file");
                    
                }
				out.write(JSONArray.toJSONString(actions));
				logger.info("American ends AllFiles : "+JSONArray.toJSONString(actions));
               
            } else if ("uploadbar".equals(action))  {
			
				readProperties( pageResourceProvider );
				
                HashMap<String, Object> actionResult = new HashMap<String, Object>();
                actionResult.put("name", "Archive Upload");
                actionResult.put("timestamp", dateFormat.format(new Date()));

				String uploadedFile = request.getParameter("file");
				String uploadedFileName = request.getParameter("name");
				if (uploadedFile.length() == 0 && (new File(uploadedFile)).exists()) {
					actionResult.put("status", "Error: an error occurred during the upload request");
				} else {
					try {
						if(uploadedFile.endsWith(".csv")) {
							FileUtils.copyFile(new File(uploadedFile), new File(dropzone + File.separator + uploadedFileName));
						} else {
							actionResult.put("status", "Error: The uploaded file \"" + uploadedFile + "\" is not taken into account as it is not a CSV file");
						}
						
						(new File(uploadedFile)).delete();
						
						if(uploadedFile.endsWith(".csv")) {
							actionResult.put("status", "The Source file \"" + uploadedFile + "\" has been uploaded in the Monitor directory ["+dropzone+"]");
						}
					} catch(Exception e) {
						actionResult.put("status", "Error: The Source file \"" + uploadedFile + "\" has not been uploaded in the archive directory due to execution error");
					}
				}

                actions.add(actionResult);
				out.write(JSONArray.toJSONString(actions));
            }

            out.flush();
            out.close();
            return;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();
            logger.severe("Exception ["+e.toString()+"] at "+exceptionDetails);
        }
    } // end doGet

	
	/**
	* no way to keep value on the groovy : we have to read again the properties
	*/
	private void readProperties(PageResourceProvider pageResourceProvider )
	{
	    Logger logger= Logger.getLogger("org.bonitasoft");
		
		try {
			Properties properties = new Properties();

			InputStream is = pageResourceProvider.getResourceAsStream("resources/conf/custompage.properties");
			properties.load(is);
			is.close();

			String temp = properties.get(dropzoneProperty);
			if(temp != null && !temp.isEmpty()) {
				dropzone = temp;
			}
			temp = properties.get(archivezoneProperty);
			if(temp != null && !temp.isEmpty()) {
				archivezone = temp;
			}
			
			// read all Properties
			for (String property : com.twosigma.bonitasoft.organization.ParametersOperation.listParametersOperation)
			{
				String value = properties.get(property);
				optionParametersOperation.put(property, value );
			}
			// logger.info("ReadProperties option["+optionParametersOperation+"]");
			
		} catch(Exception e) {
				logger.severe("Exception e :"+e.toString());
		}
			
	}

	
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
