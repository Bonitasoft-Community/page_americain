package com.bonitasoft.organization;

import java.util.logging.Logger;


/**
 * this class collect all log during operations
 * @author pierre-yves
 *
 */
public class OrganizationLog {

		private StringBuffer logBuffer = new StringBuffer();
		Logger logger = Logger.getLogger("org.bonitasoft");
		
		private boolean collectInfo;
		OrganizationLog( boolean collectInfo)
		{
				this.collectInfo = collectInfo;
		}
		/**
		 * Log an information. 
		 * @param isError
		 * @param origine the className where the log is sent
		 * @param log the message
		 */
		public void log(boolean isError,String origine, String log)
		{
				if (! isError && ! collectInfo)
						return; // ignore
				logBuffer.append(origine+":"+log+"\n");
				
				if (isError)
						logger.severe(origine+":"+log);
				else
						logger.info(origine+":"+log);
		}
		
		/**
		 * return the complete message saved
		 * @return
		 */
		public String getLog()
		{ return logBuffer.toString();
		}
}
