package org.bonitasoft.americanorganization.csv.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.americanorganization.Item;
import org.bonitasoft.americanorganization.ItemGroup;
import org.bonitasoft.americanorganization.ItemMembership;
import org.bonitasoft.americanorganization.ItemRole;
import org.bonitasoft.americanorganization.ItemUser;
import org.bonitasoft.americanorganization.OrganizationIntSource;
import org.bonitasoft.americanorganization.OrganizationLog;

public class OrganizationSourceCSV implements OrganizationIntSource {

    private String fileName;
    private String encoding;

    private FileInputStream fileinputStream;
    private LineNumberReader lineNumberReader;
    private String currentLine;
    private int currentLineNumber;
    private StringBuffer logReport;

    private HashMap<String, ArrayList<String>> describeStructureCsv;

    /**
     * Set all needed information
     * 
     * @param fileName
     */
    public void loadFromFile(final String fileName, final String encoding) {
        this.fileName = fileName;
        this.encoding = encoding;
    }

    /**
     * init the loading of an new file
     * 
     * @throws Exception
     */
    public void initInput(final OrganizationLog organizationLog) throws Exception {
        try {
            fileinputStream = new FileInputStream(new File(fileName));
            final InputStreamReader inputStreamReader = new InputStreamReader(fileinputStream, encoding == null ? "ISO-8859-1" : encoding);

            lineNumberReader = new LineNumberReader(inputStreamReader);
            // read all header Line. Each line start by
            // HEADER;<typeOfLine>;<listOfFile>
            currentLineNumber = 0;
            logReport = new StringBuffer();
            moveNextLine(organizationLog);
            describeStructureCsv = new HashMap<String, ArrayList<String>>();

            while (currentLine != null && (currentLine.startsWith("HEADER;") || currentLine.startsWith("#"))) {
                if (currentLine.startsWith("#")) {
                    moveNextLine(organizationLog);
                    continue;
                }
                // this describe a new line
                final List<String> list = getStringTokenizerPlus(currentLine, ";");
                final String typeOfLine = list.size() > 1 ? list.get(1) : null;
                final ArrayList<String> structureOneLine = new ArrayList<String>();
                for (int i = 2; i < list.size(); i++) {
                    if (list.get(i).trim().length() > 0)
                        structureOneLine.add(list.get(i));
                }
                if (typeOfLine != null) {
                    describeStructureCsv.put(typeOfLine, structureOneLine);
                }

                moveNextLine(organizationLog);
            }
            // At this point, currentLine is on the first content
        } catch (final Exception e) {
            logTheLoad(true, "Error opening the file [" + fileName + "] : " + e.toString(), organizationLog);
            throw e;
        }
    }

    /**
     * issue with the stringTokenizer is if the line contains ;; it consider
     * only one ; then line FirstName;LastName;UserName; for a value
     * "Pierre-Yves;;pierre-yves.monnet" consider the lastName as
     * "pierre-yves.monnet" and not as null.
     * 
     * @param line
     * @param charSeparator
     * @return
     */
    List<String> getStringTokenizerPlus(String line, final String charSeparator) {
        final List<String> list = new ArrayList<String>();
        int index = 0;
        if (line == null || line.length() == 0) {
            return list;
        }
        // now remove all empty string at the end of the list (keep the minimal)
        // then if string is "hello;this;;is;the;word;;;;"
        // line is reduce to "hello;this;;is;the;word"
        // nota : if the line is
        // then if string is "hello;this;;is;the;word;; ;;"
        // then "hello;this;;is;the;word;; "
        while (line.endsWith(";"))
            line = line.substring(0, line.length() - 1);
        while (index != -1) {
            final int nextPost = line.indexOf(charSeparator, index);
            if (nextPost == -1) {
                list.add(line.substring(index));
                break;
            } else {
                list.add(line.substring(index, nextPost));
            }
            index = nextPost + 1;
        }

        return list;
    }

    /**
     * get the next Item, or null if no more item is avaiable
     * 
     * @return
     */
    public Item getNextItem(final OrganizationLog organizationLog) throws Exception {
        // check the currentLine
        if (currentLine == null) {
            return null;
        }
        final boolean foundNextItem = false;
        while (!foundNextItem && currentLine != null) {
            final List<String> list = getStringTokenizerPlus(currentLine, ";");
            final String currentItem = list.size() > 0 ? list.get(0) : null;
            // currentItem is null if the line is empty
            if (currentItem == null || currentItem.startsWith("#")) {
                moveNextLine(organizationLog);
                continue;
            }
            final ArrayList<String> structureOneLine = describeStructureCsv.get(currentItem);
            if (structureOneLine == null) {
                logTheLoad(true, "Type unknown in CSV [" + currentItem + "] on line [" + currentLine + "]", organizationLog);
                moveNextLine(organizationLog);
                continue;
            }
            final HashMap<String, String> contentOneItem = new HashMap<String, String>();
            boolean overFlow = false;
            for (int i = 1; i < list.size(); i++) {
                final String value = list.get(i);
                if (i - 1 < structureOneLine.size()) {
                    contentOneItem.put(structureOneLine.get(i - 1), value);
                } else {
                    overFlow = true;
                }
            }
            if (overFlow) {
                organizationLog.log(true, "OrganizationSourceCSV", "Line [" + currentLine + "] too much data on the line. Wait " + structureOneLine.size() + " : decoding " + contentOneItem.toString());
            }

            // ok, create the Item
            final Item organizationItem = Item.getInstance(currentItem);
            if (organizationItem == null) {
                logTheLoad(true, "Line [" + currentLine + "] OrganizationItem unknown [" + currentItem + "], expect[" + ItemUser.cstItemName + "," + ItemMembership.cstItemName + "," + ItemGroup.cstItemName + "," + ItemRole.cstItemName + "]", organizationLog);
                moveNextLine(organizationLog);
                continue;
            }
            organizationItem.contextualInformation = fileName + "(" + currentLineNumber + "):";
            // now give the Hashmap to fullfill the item. The hashMap contains
            // keys according the itemType.
            final String report = organizationItem.setAttributes(contentOneItem, organizationLog);
            if (report != null) {
                logTheLoad(true, report, organizationLog);
                moveNextLine(organizationLog);
                continue;
            }
            // report is null : the item is correctely fullfill
            moveNextLine(organizationLog);

            return organizationItem;
        }
        return null;
    }

    /**
     * end the loading
     */
    public void endInput(final OrganizationLog organizationLog) throws Exception {
        if (fileinputStream != null) {
            try {
                fileinputStream.close();
            } catch (final IOException e) {
                logTheLoad(true, e.toString(), organizationLog);
            }
        }
    }

    /**
     * return the input of the value
     */
    public String getReportInput() {
        return logReport.toString();
    }

    /**
     * 
     */
    private void moveNextLine(final OrganizationLog organizationLog) {
        try {
            currentLine = lineNumberReader.readLine();
            currentLineNumber++;
        } catch (final Exception e) {
            logTheLoad(true, "Error reading line " + e.toString(), organizationLog);
            currentLine = null;
        }

    }

    /**
     * @param isError
     * @param report
     */
    private void logTheLoad(final boolean isError, final String report, final OrganizationLog organizationLog) {
        organizationLog.log(isError, "OrganizationSourceCSV.logTheLoad", "Line [" + currentLineNumber + "] : " + report);

    }

}
