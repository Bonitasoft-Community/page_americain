package org.bonitasoft.americanorganization.prg;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.americanorganization.AmericanOrganizationAPI;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;

public class GetXmlOrganization {

    /**
     * Call a server, and then extrace the Organisation, and save it on a file
     * 
     * @param args
     *        URL to application, login, password, filename
     */
    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("Usage: serverHost applicationName UserName UserPassword FileName");
            System.out.println("Example : http://localhost:8080 bonita Walter.Bates bpm c:/tmp/organisation.xml");
            return;
        }

        String serverHost = args[0];
        String applicationName = args[1];
        String user = args[2];
        String password = args[3];
        String fileNameDestination = args[4];
        System.out.println("Server[" + serverHost + "] ApplicationName[" + applicationName + "] user[" + user + "] passwd[" + password + "] fileDestination[" + fileNameDestination + "]");
        APISession session = login(serverHost, applicationName, user, password);
        if (session == null) {
            fail("Can't login");
            return;
        }

        // get the file
        IdentityAPI identityAPI;
        ProfileAPI profileAPI;
        try {
            identityAPI = TenantAPIAccessor.getIdentityAPI(session);
            profileAPI = TenantAPIAccessor.getProfileAPI(session);
            AmericanOrganizationAPI organizationAccess = new AmericanOrganizationAPI(identityAPI, profileAPI);
            organizationAccess.getOrganizationOnXml(true, fileNameDestination);
            System.out.println("File generated.");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static APISession login(String applicationUrl, String applicationName, String userName, String passwd) {
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
