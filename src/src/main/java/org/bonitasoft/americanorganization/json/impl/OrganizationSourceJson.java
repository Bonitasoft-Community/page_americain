package org.bonitasoft.americanorganization.json.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.americanorganization.Item;
import org.bonitasoft.americanorganization.ItemGroup;
import org.bonitasoft.americanorganization.ItemMembership;
import org.bonitasoft.americanorganization.ItemProfile;
import org.bonitasoft.americanorganization.ItemProfileMember;
import org.bonitasoft.americanorganization.ItemRole;
import org.bonitasoft.americanorganization.ItemUser;
import org.bonitasoft.americanorganization.OrganizationIntSource;
import org.bonitasoft.americanorganization.OrganizationLog;
import org.json.simple.JSONValue;

public class OrganizationSourceJson implements OrganizationIntSource {

	private int counterTypeItem = 0;
	private final int counter = 0;
	private final ArrayList<Item> listItemInProgress = new ArrayList<Item>();
	private int pageRank = 0;

	/**
	 * parameters for the LOGIN call
	 */
	private String urlLoginAddress;
	private String urlLoginPostParameters;
	private String contentTypeLoginParameters;

	private String login;
	private String password;
	private List<String> listCookies;

	/**
	 * parameters to get data
	 */
	private String urlJsonAddress;

	public void SetStartParameters(final String parameters) {

	}

	/**
	 * set the different parameters to log.
	 * 
	 * @param urlLoginAddress
	 * @param login
	 * @param password
	 * @param urlLoginPostParameters
	 *            if set, then the request is switch to POST
	 */
	public void setUrlLogin(final String urlLoginAddress, final String login, final String password, final String urlLoginPostParameters, final String contentTypeLoginParameters) {
		this.urlLoginAddress = urlLoginAddress;
		this.urlLoginPostParameters = urlLoginPostParameters;
		this.contentTypeLoginParameters = contentTypeLoginParameters;
		this.login = login;
		this.password = password;
	}

	public void setUrl(final String urlAddress) {
		urlJsonAddress = urlAddress;
	}

	public String getDescription() {
		return "OrganizationSourceJson";
	}

	/**
	 * init the input : log if necessary
	 */
	public void initInput(final OrganizationLog organizationLog) {
		counterTypeItem = 0;
		if (urlLoginAddress != null) {
			connectionLoginPassword(organizationLog);
		}
	}

	/**
	 * get nextItem
	 */
	public static String[] listItems = { ItemUser.cstItemName, ItemMembership.cstItemName, ItemGroup.cstItemName, ItemRole.cstItemName, ItemProfile.cstItemName, ItemProfileMember.cstItemName };

	public Item getNextItem(final OrganizationLog organizationLog) {
		// retrieve the current item
		if (listItemInProgress.size() > 0) {
			final Item item = listItemInProgress.get(0);
			listItemInProgress.remove(0);
			return item;
		}
		final boolean checkNextItem = true;
		while (checkNextItem) {
			organizationLog.log(false, "com.twosigma.bonitasoft.organization.json.impl", "counterTypeItem[" + counterTypeItem + "]->[" + listItems[counterTypeItem] + "] PageRank[" + pageRank + "]");
			// pageRank = -1 means the current item is finish
			if (pageRank == -1) {
				counterTypeItem++;
				pageRank = 0;
			}
			if (counterTypeItem > listItems.length) {
				break;
			}
			getItems(listItems[counterTypeItem], organizationLog);

			if (listItemInProgress.size() > 0) {
				break; // we have data !
			}
			if (pageRank == -1) {
				continue; // no more, go to the next kind of item
			}

		}
		// we get a new set of data ?
		if (listItemInProgress.size() > 0) {
			final Item item = listItemInProgress.get(0);
			listItemInProgress.remove(0);
			return item;
		}
		organizationLog.log(false, "com.twosigma.bonitasoft.organization.json.impl", "That's all for the JSON input !");
		return null;
	}

	public void endInput(final OrganizationLog organizationLog) {
		// nothing to do
	}

	/**
	 * return information on the user
	 *
	 */
	public static class UserInfo {
		public String utcTime;
	}

	public static UserInfo getUserInfo(final long UserId) {
		return new UserInfo();
	}

	public boolean isManageTheFile(final String fileName) {
		return false;
	}

	public void manageTheFile(final String fileName) {

	}

	/**
	 * load all information for a currentItem, for a dedicated pageRank
	 *
	 * @param currentItem
	 * @param organizationLog
	 */
	private void getItems(final String currentItem, final OrganizationLog organizationLog) {
		final String url = urlJsonAddress + "/" + currentItem + "/" + pageRank;
		Object jsonList;
		try {
			jsonList = readJsonFromUrl(url);
			if (jsonList instanceof List) {
				final List<Object> listItem = (List) jsonList;
				for (final Object jsonLine : listItem) {
					if (jsonLine instanceof Map) {
						final Map<String, String> oneItemAttributes = (Map) jsonLine;
						final Item organizationItem = Item.getInstance(currentItem);
						final String report = organizationItem.setAttributes(oneItemAttributes, organizationLog);
						listItem.add(organizationItem);
					} else {
						organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Item[" + currentItem + "]:We expect a Map on each line : found " + (jsonLine == null ? "null" : jsonLine.getClass().getName()));
					}
				}

			} else {
				organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Item[" + currentItem + "]:We expect a List of Map " + (jsonList == null ? "null" : jsonList.getClass().getName()));
			}
			// in any case, add the page rank
			pageRank++;
		} catch (final IOException e) {
			organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Item[" + currentItem + "]:Error at reading : " + e.toString());
			pageRank = -1; // stop to read this item
		}
	}

	/**
	 *
	 * @param urlConnectionAddress
	 * @param login
	 * @param password
	 * @param organizationLog
	 */
	private boolean connectionLoginPassword(final OrganizationLog organizationLog) {
		// login first

		URL url;
		try {

			// HttpRequest httpRequest = DefaultHttpRequestFactory.();

			url = new URL(urlLoginAddress);

			final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			if (urlLoginPostParameters != null) {
				urlConn.setRequestMethod("POST");
				if (contentTypeLoginParameters != null) {
					urlConn.setRequestProperty("content-type", contentTypeLoginParameters);
				}
				urlConn.setDoOutput(true);
				final DataOutputStream wr = new DataOutputStream(urlConn.getOutputStream());
				wr.writeBytes(urlLoginPostParameters);
				wr.flush();
				wr.close();
			}
			urlConn.connect();
			final int responseCode = urlConn.getResponseCode();
			if (responseCode != 200) {
				organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Response[" + responseCode + "] Can't use connection[" + urlLoginAddress + "]");
				return false;
			}

			listCookies = urlConn.getHeaderFields().get("Set-Cookie");
			/*
			 * for (String oneCookie : cookies) { String cookieName =
			 * oneCookie.substring(0, oneCookie.indexOf("=")); String
			 * cookieValue = oneCookie.substring(oneCookie.indexOf("=") + 1,
			 * oneCookie.length()); HttpCookie cookie = new
			 * HttpCookie(cookieName,cookieValue); listCookies.add( cookie ); }
			 */
			return true;
		} catch (final MalformedURLException e) {
			organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Can't use connection[" + urlLoginAddress + "] : " + e.toString());

		} catch (final IOException e) {
			organizationLog.log(true, "com.twosigma.bonitasoft.organization.json.impl.OrganizationSourceJson", "Can't use connection[" + urlLoginAddress + "] : " + e.toString());

		}
		return false;
	}

	/**
	 * read from the URL. THis url contain the item, and the pageRank.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private Object readJsonFromUrl(final String urlJsonItemAddress) throws IOException {

		final String cookiesSt = "";

		final URL url = new URL(urlJsonItemAddress);

		final URLConnection urlConn = url.openConnection();
		for (final String cookie : listCookies) {
			System.out.println("Here's the cookie from the FIRST call:\t" + cookie);
			urlConn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
		}

		final InputStream is = url.openStream();
		try {
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			final String jsonText = readAll(rd);

			final Object obj = JSONValue.parse(jsonText);
			return obj;
		} catch (final IOException e) {
			throw e;
		} finally {
			is.close();
		}
	}

	/** read all in a string */

	private static String readAll(final Reader rd) throws IOException {
		final StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
