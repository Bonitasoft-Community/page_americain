package com.bonitasoft.organization.service;

import java.util.logging.Logger;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.PlatformAPI;
import org.bonitasoft.engine.api.impl.PlatformAPIImpl;
import org.bonitasoft.engine.connector.ConnectorAPIAccessorImpl;
import org.bonitasoft.engine.platform.PlatformNotFoundException;
import org.bonitasoft.engine.platform.PlatformState;

import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.organization.OrganizationAccess;
import com.bonitasoft.organization.ParametersOperation;

public class OrganizationRobot implements Runnable {

		private IdentityAPI identityAPI;
		private ProfileAPI profileAPI;
		private final String pathMonitor;
		ParametersOperation parametersOperation;
		long tenantId;
		long poolingTimeInSec;

		public OrganizationRobot(final IdentityAPI identityAPI, final ProfileAPI profileAPI, final long tenantId, final String pathMonitor, final long poolingTimeInSec) {
				this.identityAPI = identityAPI;
				this.profileAPI = profileAPI;
				this.pathMonitor = pathMonitor;
				parametersOperation = new ParametersOperation();
				this.tenantId = tenantId;
				this.poolingTimeInSec = poolingTimeInSec;
		}

		public void start() {
				final Thread T = new Thread(this);
				T.start();
		}

		public void run() {
				OrganizationAccess organizationAccess = null;
		
				final Logger logger = Logger.getLogger("org.bonitasoft");
				logger.info("==================== com.twosigma.bonitasoft.organization.service.OrganizationRobot started");
				if (poolingTimeInSec < 10) {
                    poolingTimeInSec = 10;
                }
				while (true) {

						try {
								Thread.sleep(poolingTimeInSec*1000);

								final PlatformAPI platformAPI = new PlatformAPIImpl();
								// logger.info("==================== com.twosigma.bonitasoft.organization.service.OrganizationRobot PlatformAPIState [" + platformAPI.getPlatformState() + "]");
								if (!(platformAPI.getPlatformState() == PlatformState.STARTED)) {
										logger.info("==================== com.twosigma.bonitasoft.organization.service.OrganizationRobot Platform ["+platformAPI.getPlatformState()+"] not started, wait");
										continue;
								}

								if (identityAPI == null) {
										final ConnectorAPIAccessorImpl connectorAccessorAPI = new ConnectorAPIAccessorImpl(tenantId);

										identityAPI = connectorAccessorAPI.getIdentityAPI();
										final Object profileObj = connectorAccessorAPI.getProfileAPI();
										if (profileObj instanceof com.bonitasoft.engine.api.ProfileAPI) {
                                            profileAPI = (com.bonitasoft.engine.api.ProfileAPI) profileObj;
                                        } else {
                                            logger.severe("==================== com.twosigma.bonitasoft.organization.service.OrganizationRobot profileAPI is not a com.bonitasoft : ["+ (profileObj==null ? null : profileObj.getClass().getName())+"]");
                                        }
												
										organizationAccess = new OrganizationAccess(identityAPI, profileAPI);

								}
								

								logger.info("==================== Monitor path["+pathMonitor+"] sleep Time["+poolingTimeInSec+"] s");
								if (organizationAccess != null) {
                                    organizationAccess.saveOrganizationFromDir(pathMonitor, pathMonitor + "/archive", parametersOperation);
                                }

						} catch (final InterruptedException e) {
								logger.severe("Error during sleep");

						} catch (final PlatformNotFoundException e1) {
								logger.severe("Error during getPlatformState [" + e1.toString() + "]");

						} catch (final Exception e) {
								logger.severe("Error during robot " + e.toString());
						}
				}

		}
}
