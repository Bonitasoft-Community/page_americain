package org.bonitasoft.americanorganization;

import java.util.logging.Logger;

/**
 * this class collect all log during operations
 * 
 * @author pierre-yves
 */
public class OrganizationLog {

    private StringBuffer logBuffer = new StringBuffer();
    private StringBuffer logErrorsBuffer = new StringBuffer();
    Logger logger = Logger.getLogger("org.bonitasoft");

    private boolean collectInfo;

    OrganizationLog(boolean collectInfo) {
        this.collectInfo = collectInfo;
    }

    /**
     * Log an information.
     * 
     * @param isError
     * @param isDebug TODO
     * @param origine
     *        the className where the log is sent
     * @param log
     *        the message
     */
    public void log(boolean isError, boolean isDebug, String origine, String log) {
        if (isDebug && !collectInfo)
            return; // ignore

        if (isError) {
            logErrorsBuffer.append(origine + ":" + log + "\n");
            logger.severe(origine + ":" + log);
        } else if (isDebug){
            logBuffer.append(origine + ":" + log + "\n");
            logger.fine(origine + ":" + log);
        } else {
            logBuffer.append(origine + ":" + log + "\n");
            logger.info(origine + ":" + log);
        }

    }

    /**
     * return the complete message saved
     * 
     * @return
     */
    public String getLog() {
        return logBuffer.toString();
    }

    public String getErrors() {
        return logErrorsBuffer.toString();
    }

}
