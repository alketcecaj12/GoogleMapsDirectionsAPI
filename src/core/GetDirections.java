package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetDirections {

	static int indexAPI = 0;
	static String [] APIKEY = {
		"AIzaSyDEwU53qBOtoqO0_K5IjyroDivv63YZuW8", 
		"AIzaSyA12ARJCFElkupUVsGB0uD3kCsIrbEf2vk", 
		"AIzaSyCP2yc_l_7yi03Kr-phGGOQKslHgDCCpT8", 
		"AIzaSyAAV8gBF6t9_N0NJ6fZWwH_wbHAXwjYM_8", 
		"AIzaSyAh-4B8wZZk4bQrfkf5csckOzgE7AhQyKM", 
		"AIzaSyBQTEHxZMhURmYR5wYpSXt-O3U3V8uQz40", 
		"AIzaSyBSAqotF1nAawaQq6TUe_YZk8z0s8GFhsw",
		"AIzaSyBerIDs0ySsNOfD76BRCTO3gbNQF1mtZAU",
		"AIzaSyDTwn233eontMyW6dy4DJiq9fzZ2N_Y2I8",
		"AIzaSyAimn6pmMAIMSjaFkSCGNrcpjh7sx64aR8",
		"AIzaSyAbs_8SN7ODr-wI8YnVMHsGFGiLT0vK0Lg",
		"AIzaSyAXtU9NjA8Di_9Efy2YFTN4zZZyfJ7ZW7k",
		"AIzaSyBEL85VvTnLIG7MI_pSoQxsVjWRKmkfj2w",
		"AIzaSyBSI5uYPru0-HI4VDXBFFqSLeaxbgW9cu4"
	};

	public static void main (String args[]) throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File("coord-data.csv")));
		Point origin = new Point(44.55, 11.55);
		Point destination = new Point(44.75, 11.25);
		Map<Double, ArrayList<Point>> distanceAndRoute = calcDistanceAndRouteWithGoogleAPI(origin, destination);
		System.out.println("From:\t"+origin.toString());
		System.out.println("To:\t"+destination.toString());
		for (Double dist : distanceAndRoute.keySet()) {
			
		out.println("From:\t"+origin.toString());
		out.println("To:\t"+destination.toString());
			
				out.println("Route:\t"+Arrays.toString(distanceAndRoute.get(dist).toArray()));
				out.println("Length:\t"+dist);
			
			
			System.out.println("Route:\t"+Arrays.toString(distanceAndRoute.get(dist).toArray()));
			System.out.println("Length:\t"+dist);
		}
	}
	
	private static HashMap<Double, ArrayList<Point>> calcDistanceAndRouteWithGoogleAPI(Point origin, Point destination) {
		HashMap<Double, ArrayList<Point>> distanceRoute = new HashMap<Double, ArrayList<Point>>();
		JSONArray routeObject = new JSONArray();
		JSONObject resp = new JSONObject();
		try {
			String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.getLat()+","+origin.getLng()+"&destination="+destination.getLat()+","+destination.getLng()+"&sensor=false&key=" + APIKEY[indexAPI];

			resp = new JSONObject(downloadUrl(url));

			String status = resp.getString("status");

			if (status.compareTo("OK")==0) {
				routeObject = resp.getJSONArray("routes");
				JSONObject tag_routes = routeObject.getJSONObject(0);
				JSONObject overviewPolylines = tag_routes
						.getJSONObject("overview_polyline");
				String encodedString = overviewPolylines.getString("points");

				distanceRoute.put(tag_routes.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value"), decodePoly(encodedString));

			} else if (status.compareTo("NOT_FOUND")==0
					|| status.compareTo("ZERO_RESULTS")==0) {
				//skip

			} else if (indexAPI<APIKEY.length-1) {
				System.out.println("\t\turl:"+url);
				System.out.println("\t\tstatus:"+status);
				indexAPI++;
				distanceRoute = calcDistanceAndRouteWithGoogleAPI(origin, destination);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return distanceRoute;
	}
	
	private static String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();
			// Connecting to url
			urlConnection.connect();
			// Reading data from url
			iStream = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
			StringBuffer sb  = new StringBuffer();
			String line = "";
			while( ( line = br.readLine())  != null){
				sb.append(line);
			}
			data = sb.toString();
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}



	private static ArrayList<Point> decodePoly(String encoded) {

		ArrayList<Point> poly = new ArrayList<Point>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			Point p = new Point((((double) lat / 1E5)),(((double) lng / 1E5)));
			poly.add(p);
		}


		return poly;
	}
}
