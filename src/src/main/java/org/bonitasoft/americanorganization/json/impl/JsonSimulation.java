package org.bonitasoft.americanorganization.json.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bonitasoft.americanorganization.csv.impl.CsvReader;
import org.json.simple.JSONValue;

public class JsonSimulation {

	/**
	 * get the simulation for a path
	 * 
	 * @param path
	 * @param typeItem
	 * @param pageRank
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getSimulationJson(String pathDataSimulation, String filterTypeItem, int pageRank) throws FileNotFoundException {
		Logger logger = Logger.getLogger("org.bonitasoft.com");
		logger.info("getsimulationjson");
		ArrayList<HashMap<String, Object>> resultJson = new ArrayList<HashMap<String, Object>>();
		try {

			for (String typeItem : OrganizationSourceJson.listItems) {
				if (filterTypeItem != null && !filterTypeItem.equals(typeItem))
					continue;

				HashMap<String, Object> oneResultTypeItem = new HashMap<String, Object>();
				oneResultTypeItem.put("type", typeItem);
				if (pageRank != -1) {
					oneResultTypeItem.put("pagerank", pageRank);
				}
				ArrayList<Map<String, String>> listOneType = getSimulationJsonOneType(pathDataSimulation, typeItem, pageRank);
				oneResultTypeItem.put("list", listOneType);
				resultJson.add(oneResultTypeItem);
			}
		} catch (Exception e) {
			HashMap<String, Object> oneResultTypeItem = new HashMap<String, Object>();
			oneResultTypeItem.put("Status", e.toString());
			resultJson.add(oneResultTypeItem);
			logger.info("Exception e:" + e.toString());
		}
		String jsonText = JSONValue.toJSONString(resultJson);
		return jsonText;

	}

	private static ArrayList<Map<String, String>> getSimulationJsonOneType(String path, String typeItem, int pageRank) throws FileNotFoundException {
		Logger logger = Logger.getLogger("com.twosigma.bonitasoft.organization.json.impl");
		ArrayList<Map<String, String>> resultListJson = new ArrayList<Map<String, String>>();
		CsvReader csvReader = new CsvReader();
		int countLine = -1;
		BufferedReader br = null;
		;
		try {
			br = new BufferedReader(new FileReader(path + "/" + typeItem + ".txt"));
			String line = br.readLine();

			while (line != null) {
				if (countLine == -1) {
					// header
					csvReader.setHeader(line, ";");
				} else {
					if (pageRank == -1 || (countLine >= pageRank * 5 && countLine < (pageRank + 1) * 5)) {
						resultListJson.add(csvReader.decodeOneLine(line, ";"));
					}
				}
				countLine++;
				line = br.readLine();
			}
		} catch (Exception e) {
			logger.severe("Error" + e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
		}
		return resultListJson;
	}

}
