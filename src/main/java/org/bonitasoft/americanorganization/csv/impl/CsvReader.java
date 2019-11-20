package org.bonitasoft.americanorganization.csv.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {

    private List<String> listHeader = new ArrayList<String>();

    public void setHeader(String header, String charSeparator) {
        listHeader = getStringTokenizerPlus(header, charSeparator);
    }

    /**
     * decode the line
     * 
     * @param line
     * @param charSeparator
     * @return
     */
    public Map<String, String> decodeOneLine(String line, String charSeparator) {
        List<String> listLine = getStringTokenizerPlus(line, charSeparator);

        Map<String, String> contentOneItem = new HashMap<String, String>();
        boolean overFlow = false;
        for (int i = 0; i < listLine.size(); i++) {
            String value = listLine.get(i);
            if (i < listHeader.size())
                contentOneItem.put(listHeader.get(i), value);
            else
                overFlow = true;
        }
        return contentOneItem;

    }

    /**
     * a StringTokenzier which accept ;; for an empty value
     * 
     * @param line
     * @param charSeparator
     * @return
     */
    public static List<String> getStringTokenizerPlus(String line, String charSeparator) {
        List<String> list = new ArrayList<String>();
        int index = 0;
        if (line == null || line.length() == 0)
            return list;
        while (index != -1) {
            int nextPost = line.indexOf(charSeparator, index);
            if (nextPost == -1) {
                list.add(line.substring(index));
                break;
            } else
                list.add(line.substring(index, nextPost));
            index = nextPost + 1;
        }
        return list;
    }

}
