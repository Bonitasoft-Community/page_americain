package com.bonitasoft.organization.service;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.math.NumberUtils;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.PlatformAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.api.impl.IdentityAPIImpl;
import org.bonitasoft.engine.api.impl.PlatformAPIImpl;
import org.bonitasoft.engine.authentication.AuthenticationConstants;

import org.bonitasoft.engine.authentication.AuthenticationException;

import org.bonitasoft.engine.authentication.AuthenticationService;

import org.bonitasoft.engine.authentication.GenericAuthenticationService;
import org.bonitasoft.engine.connector.ConnectorAPIAccessorImpl;

import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;

import org.bonitasoft.engine.home.BonitaHomeServer;

import org.bonitasoft.engine.identity.IdentityService;

import org.bonitasoft.engine.identity.SUserNotFoundException;

import org.bonitasoft.engine.identity.model.SUser;

import org.bonitasoft.engine.io.PropertiesManager;

import org.bonitasoft.engine.service.ModelConvertor;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.SSessionException;

import org.bonitasoft.engine.session.SSessionNotFoundException;

import org.bonitasoft.engine.session.SessionService;

import org.bonitasoft.engine.session.model.SSession;

import org.bonitasoft.engine.sessionaccessor.STenantIdNotSetException;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;
import org.bonitasoft.engine.sessionaccessor.SessionIdNotSetException;

import com.bonitasoft.engine.api.ProfileAPI;

public class OrganizationService {

		Logger logger = Logger.getLogger("org.bonitasoft");

		public OrganizationService(
						final SessionAccessor sessionAccessor,
						// final String tenantIdSt,
						final String pathMonitor
						// final String poolingTimeInSecSt
						) {
				String poolingTimeInSecSt ="600";
				try {
						// long tenantId = Long.valueOf(tenantIdSt);
						long tenantId = sessionAccessor.getTenantId();
						logger.info("========================================= com.twosigma.bonitasoft.organization.service.OrganizationService : start the service tenantId[" + tenantId + "] pathMonitor["
										+ pathMonitor + "]");
					
						long poolingTimeInSec = 0;
						try
						{
								poolingTimeInSec = Long.valueOf(poolingTimeInSecSt);
						}
						catch(Exception e)
						{
								logger.severe("========================================= com.twosigma.bonitasoft.organization.service.OrganizationService : parameters[poolingTimeInSecSt] is not a long : [" + poolingTimeInSecSt + "]. Use 10 s");
								poolingTimeInSec=10;
						}
						OrganizationRobot organizationRobot = new OrganizationRobot(null, null, tenantId, pathMonitor.toString(), poolingTimeInSec);
						organizationRobot.start();
						logger.info("com.twosigma.bonitasoft.organization.service.OrganizationService : service started");
						// create a thread to monitor the source
						/*
						 * } catch (SessionIdNotSetException e) { logger.severe(
						 * "com.twosigma.bonitasoft.organization.service.OrganizationService : Error "
						 * +e.toString());
						 * 
						 * } catch (STenantIdNotSetException e) { logger.severe(
						 * "com.twosigma.bonitasoft.organization.service.OrganizationService : Error "
						 * +e.toString());
						 */
				} catch (Exception e) {
						logger.severe("com.twosigma.bonitasoft.organization.service.OrganizationService : Error " + e.toString());

						//
				}
		}
}
