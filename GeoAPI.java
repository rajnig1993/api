package api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Util;

public class GeoAPI {

	private static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
	
	private static String API_KEY = "AIzaSyDlYYlsgZMNuMzYRpJQghXGmZ0vGEapZEg";
	
	private static boolean SENSOR = true;
	
	private static String getUrlString(String latitude, String longitude) {
		return GEO_URL + "?"
				+ "latlng=" + latitude + "," + longitude
				+ "&sensor=" + SENSOR
				+ "&key=" + API_KEY;
	}
	
	/** Determines city from latitude and longitude.
	 * @param latitude Latitude of location
	 * @param longitude Longitude of location
	 * @return Name of city.
	 */
	public static String getCity(String latitude, String longitude) {
		String url = getUrlString(latitude, longitude);
		String city = "";
		
		try {
			String response = Util.getResponse(url);
			JSONArray geoResponseArray = new JSONObject(response).getJSONArray("results");
			if (geoResponseArray.length() > 0) {
				JSONArray addComponent = geoResponseArray.getJSONObject(0).getJSONArray("address_components");
				for (int i = 0; i < addComponent.length(); i++) {
					JSONObject prop = addComponent.getJSONObject(i);
					JSONArray types = prop.getJSONArray("types");
		    		
					for (int j = 0; j < types.length(); j++) {
						if (types.getString(j).equalsIgnoreCase("administrative_area_level_1")) {
							city = prop.getString("long_name");
		    			}
		    		}
		    	}
		    }
		} catch (IOException e) {
			System.out.println("ERROR: Response not received from URL:" + url);
		} catch (JSONException e) {
			System.out.println("ERROR: Cannot parse response received from URL:" + url);
		}
	    
		return city;
	}
	
}
