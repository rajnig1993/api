package api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import common.Util;

public class CatalogAPI {

	private static String CATALOG_URL = "http://www.stubhub.com/listingCatalog/select";

	private static String getUrlString(String city) {
		String urlStr = CATALOG_URL + "?"
				+ "q=" + "stubhubDocumentType:event AND city:" + city + " AND event_date:[NOW TO *] AND NOT totalTickets:0 AND active:1;event_date_time_local asc "
				+ "&start=" + "0"
				+ "&rows=" + "50"
				+ "&fl=genreId+description event_id event_date_time_local timezone channel event_description_en_US genreUrlPath minPrice urlpath venue_name addr1 zip lat_lon";
		
		return urlStr.replace(" ", "%20");
	}
	
	
	/** Gets XML response from catalog API.
	 * @param city Name of city
	 * @return XML response
	 */
	public static String getXMLResponse(String city) {
		String url = getUrlString(city);
		String response = "";
		
		try {
			response = Util.getResponse(url);
		} catch (IOException e) {
			System.out.println("ERROR: Response not received from URL: " + url);
		}
		
		return response;
	}
	
	/** Get list of events of type <code>eventType</code> for specified <code>city</code>.
	 * @param city Name of city
	 * @param eventType Type of Event
	 * @return List of events as JSONArray filtered by <code>eventType</code>
	 */
	public static JSONArray getEventList(String city, String eventType) {
		JSONArray response = new JSONArray();
		
		try {
			JSONObject json = XML.toJSONObject(getXMLResponse(city)).getJSONObject("response").getJSONObject("result");
			JSONArray docs = json.getJSONArray("doc");			
			
			for (int i = 0; i < docs.length(); i++) {
				JSONObject doc = docs.getJSONObject(i);
				JSONArray strArr = doc.getJSONArray("str");
				JSONObject dateObj = doc.getJSONObject("date");
				JSONObject floatObj = doc.getJSONObject("float");
				
				JSONObject docResponse = new JSONObject();
				
				Util.appendJSONObject(docResponse, Util.getIndexedValues(strArr, "name", "content"));
				Util.appendJSONObject(docResponse, Util.getIndexedJSON(dateObj, "name", "content"));
				Util.appendJSONObject(docResponse, Util.getIndexedJSON(floatObj, "name", "content"));
				
				if (checkEventType(docResponse, eventType)) {
					response.put(docResponse);
				}
			}
		} catch (JSONException e) {			
			System.out.println("ERROR: Cannot parse response received from server.");
		}
		
		return response;
	}
	
	private static boolean checkEventType(JSONObject doc, String eventType) {
		if (eventType.equalsIgnoreCase("all")) {
			return true;
		}
		
		try {
			String channel = doc.getString("channel");
			return channel.equalsIgnoreCase(eventType);
		} catch (JSONException e) {
			return false;
		}
	}

}
