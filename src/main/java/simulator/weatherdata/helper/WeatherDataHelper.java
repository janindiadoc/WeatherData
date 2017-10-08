package simulator.weatherdata.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.json.JSONObject;

import simulator.weatherdata.util.DataConstants;
import simulator.weatherdata.util.WeatherReportVO;

public class WeatherDataHelper {
	static Logger log = Logger.getLogger(WeatherDataHelper.class.getName());
	StringBuilder reportDat = new StringBuilder();

	/*
	 * Method that is invoked from the main class which controls the child
	 * methods.
	 * 
	 * @reportVO
	 */
	public void fetchDataFromWeatherUnderground(WeatherReportVO reportVO) {
		log.info("Method fetchDataFromWeatherUnderground Start");
		// Reading the locations from property file and storing it in a string array
		Properties propertyFile = reportVO.getLocationproperties();
		String[] cityAndStates = propertyFile.getProperty(
				DataConstants.CITY_AND_STATE).split(",");
		String apiKey = propertyFile.getProperty(DataConstants.API_KEY);
		reportVO.setApiKey(apiKey);
		String urlpart2 = propertyFile.getProperty(DataConstants.URL_PART2);
		reportVO.setUrlPart2(urlpart2);
		for (int i = 0; i < cityAndStates.length; i++) {
			fetchAPIData(cityAndStates[i], reportVO);
		}
		reportVO.setOutputFileName(DataConstants.OUTPUT_FILE);
		writeWeatherReportIntoFile(reportDat,reportVO);
		log.info("Method fetchDataFromWeatherUnderground End");
	}

	/*
	 * Core method that makes the REST API call to weather underground
	 * application and fetches the response
	 * 
	 * @apiKey (Generated from the weather underground application)
	 * 
	 * @location (eg: sydney,Melbourne etc)
	 */
	public void fetchAPIData(String location, WeatherReportVO reportVO) {
		log.info("Method fetchAPIData Start");
		try {
			String apiKey = reportVO.getApiKey();
			String urlPart2 = reportVO.getUrlPart2();
			String constructURL = DataConstants.URL_PART1 + apiKey
					+ urlPart2 + location + ".json";
			URL url = new URL(constructURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			else {
				log.info("Connection Success");
			}
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader((conn.getInputStream())));
			String output;
			String content = "";
			while ((output = buffReader.readLine()) != null) {
				content += output + "\n";
			}
			buffReader.close();
			conn.disconnect();
			JSONObject jsonOutObj = buildJSON(content);
			populateRequiredFields(jsonOutObj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Method fetchAPIData End");
	}

	/*
	 * Retrieve the values to be displayed in the report from the json object
	 * response from the web service call.
	 * 
	 * @jsonOutObj
	 */
	public void populateRequiredFields(JSONObject jsonOutObj) {
		log.info("Method populateRequiredFields Start");
		if(jsonOutObj.length() != 0){
			JSONObject currentObsObj = jsonOutObj
					.getJSONObject(DataConstants.CURRENT_OBSERVATION);
			JSONObject displayLocObj = currentObsObj
					.getJSONObject(DataConstants.DISPLAY_LOCATION);
			String concatFields = "";
			String city = displayLocObj.getString(DataConstants.CITY);
			String position = displayLocObj.getString(DataConstants.LATITUDE) + ","
					+ displayLocObj.getString(DataConstants.LONGITUDE) + ","
					+ displayLocObj.getString(DataConstants.ELEVATION);
			Instant instant = Instant.now();
			String condition = currentObsObj.getString(DataConstants.WEATHER);
			float temperature = currentObsObj.getLong(DataConstants.TEMPERATURE);
			String pressure = currentObsObj.getString(DataConstants.PRESSURE);
			String humidity = currentObsObj.getString(DataConstants.HUMIDITY);
			concatFields = city + "|" + position + "|" + instant + "|" + condition
					+ "|" + temperature + "|" + pressure + "|" + humidity;
			reportDat.append(concatFields);
			reportDat.append(System.getProperty(DataConstants.SEPARATOR));
		}
		else{
			log.info("JSON object response is empty");
		}
		log.info("Method populateRequiredFields End");
	}

	/*
	 * Method to write the output string to weather report data text file.
	 * 
	 * @outputLine
	 * @reportVO
	 */
	public void writeWeatherReportIntoFile(StringBuilder outputLine, WeatherReportVO reportVO) {
		log.info("Method writeWeatherReportIntoFile Start");
		try {
			String outputFile = reportVO.getOutputFileName();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.append(outputLine);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Method writeWeatherReportIntoFile End");
	}

	/*
	 * Build JSON object from the content string
	 * 
	 * @content
	 */
	public JSONObject buildJSON(String content) {
		log.info("Method buildJSON Start");
		JSONObject jsonOutput = new JSONObject(content);
		return jsonOutput;
	}
}
