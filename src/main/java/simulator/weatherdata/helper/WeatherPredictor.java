package simulator.weatherdata.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;
import org.joda.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;

import simulator.weatherdata.util.DataConstants;
import simulator.weatherdata.util.WeatherReportVO;

public class WeatherPredictor {

	static Logger log = Logger.getLogger(WeatherPredictor.class.getName());
	private static String weatherDataFromResp = "";
	private static double currentYearTemp = 0;
	private static double currentYearPress = 0;
	private static double currentYearHumid = 0;
	WeatherDataHelper wdHelper = new WeatherDataHelper();
	StringBuilder reportData = new StringBuilder();

	/*
	 * Core method that invokes child methods which do the logic to calculate the weather conditions.
	 * 
	 * @predictionDate
	 * @reptVO
	 */
	public void predictWeather(String predictionDate, WeatherReportVO reptVO) {
		log.info("Method predictWeather Start");
		// Reading the locations from property file and storing it in a string array
		Properties propertyFile = reptVO.getLocationproperties();
		String[] cityAndStates = propertyFile.getProperty(
				DataConstants.CITY_AND_STATE).split(",");
		String apiKey = propertyFile.getProperty(DataConstants.API_KEY);
		reptVO.setApiKey(apiKey);
		String histUrlPart1 = propertyFile
				.getProperty(DataConstants.HIST_URL_PART1);
		reptVO.setHistUrlPart1(histUrlPart1);
		String histUrlPart2 = propertyFile
				.getProperty(DataConstants.HIST_URL_PART2);
		reptVO.setHistUrlPart2(histUrlPart2);
		String urlpart2 = propertyFile.getProperty(DataConstants.URL_PART2);
		reptVO.setUrlPart2(urlpart2);
		reptVO.setPredictionDate(predictionDate);
		HashMap<String, JSONObject> dataMap = new HashMap<String, JSONObject>();
		HashMap<String, HashMap<String, JSONObject>> dataForAllCities = new HashMap<String, HashMap<String, JSONObject>>();
		for (int i = 0; i < cityAndStates.length; i++) {
			dataMap = fetchAPIHistData(cityAndStates[i], reptVO);
			dataForAllCities.put(cityAndStates[i], dataMap);
		}
		fetchJSONAndCalculateParameters(dataForAllCities, reptVO);
		reptVO.setOutputFileName(DataConstants.OUTPUT_FILE);
		wdHelper.writeWeatherReportIntoFile(reportData,reptVO);
		log.info("Method predictWeather End");
	}

	/*
	 * Retrieve the values to be displayed in the report from the json object and calculate the weather parameters.
	 *  
	 * @predictionDate
	 * @reptVO
	 */
	private void fetchJSONAndCalculateParameters(
			HashMap<String, HashMap<String, JSONObject>> dataForAllCities, WeatherReportVO reptVO) {

		log.info("Method fetchJSONAndCalculateParameters Start");
		Iterator<Entry<String, HashMap<String, JSONObject>>> it = dataForAllCities.entrySet().iterator();
		String concatFields = "";
		while (it.hasNext()) {
			Map.Entry pair = it.next();
			weatherDataFromResp = populateWeatherDataFromJSON((Map<String, JSONObject>) pair.getValue());
			int indexPi = weatherDataFromResp.indexOf('|');
			int indexComma = weatherDataFromResp.indexOf(',');
			int indexDoubleColon = weatherDataFromResp.indexOf(':');
			String variationForTemp = weatherDataFromResp.substring(0 , indexPi);
			String variationForPress = weatherDataFromResp.substring(indexPi + 1, indexComma);
			String variationForHumid = weatherDataFromResp.substring(indexComma + 1, indexDoubleColon);
			String condition = weatherDataFromResp.substring(indexDoubleColon+1, weatherDataFromResp.length());
			long predictedTemp = Math.round(currentYearTemp
					+ Double.parseDouble(variationForTemp));
			long predictedPress = Math.round(currentYearPress
					+ Double.parseDouble(variationForPress));
			long predictedHumid = Math.round(currentYearHumid
					+ Double.parseDouble(variationForHumid));
			String location = pair.getKey().toString();
			String position = wdHelper.fetchAPIData(location, reptVO);
			Instant predDate = Instant.parse(reptVO.getPredictionDate());
			int indexYear = reptVO.getPredictionDate().indexOf('-');
			int yeartoPredict = Integer.parseInt(reptVO.getPredictionDate().substring(0, indexYear));
			if (yeartoPredict >= (Calendar.getInstance().get(Calendar.YEAR)) + 2) {
				predictedTemp = Math.round(predictedTemp
						+ Double.parseDouble(variationForTemp));
				predictedPress = Math.round(predictedPress
						+ Double.parseDouble(variationForPress));
				predictedHumid = Math.round(predictedHumid
						+ Double.parseDouble(variationForHumid));
			}
			concatFields = location + "|" + position + "|" + predDate + "|" + condition
					+ "|" + predictedTemp + "|" + predictedPress + "|" + predictedHumid+"%";
			reportData.append(concatFields);
			reportData.append(System.getProperty(DataConstants.SEPARATOR));
		}

	}
	/*
	 * Populate the data needed for calculating the parameters required for prediction.
	 *  
	 * @mapWithHisData
	 */
	public String populateWeatherDataFromJSON(Map<String,JSONObject> mapWithHisData) {
		log.info("Method populateWeatherDataFromJSON Start");
		if (null != mapWithHisData) {
			Iterator<Entry<String, JSONObject>> mapIt = mapWithHisData
					.entrySet().iterator();
			List<Double> tempList = new ArrayList<Double>();
			List<Double> pressList = new ArrayList<Double>();
			List<Double> humidlist = new ArrayList<Double>();
			int rainCount = 0;
			int snowCount = 0;
			int sunnyCount = 0;
			while (mapIt.hasNext()) {
				Map.Entry pair = mapIt.next();
				int year = Integer.parseInt(pair.getKey().toString());
				JSONObject obj = (JSONObject) pair.getValue();
				JSONObject currentObsObj = obj
						.getJSONObject(DataConstants.HISTORY);
				JSONArray dailySummObj = currentObsObj
						.getJSONArray(DataConstants.DAILY_SUMMARY);
				double meanTemp = 0;
				double meanPressure = 0;
				double meanHumidity = 0;
				for (int n = 0; n < dailySummObj.length(); n++) {
					JSONObject object = dailySummObj.getJSONObject(n);
					meanTemp = object.getDouble(DataConstants.TEMP);
					meanPressure = object.getDouble(DataConstants.PRESS);
					meanHumidity = (object.getDouble(DataConstants.MX_HUMID) + object
							.getDouble(DataConstants.MN_HUMID)) / 2;
					int rainValue = object.getInt(DataConstants.RAIN);
					int snowValue = object.getInt(DataConstants.SNOW_1);
					if (rainValue > 0) {
						rainCount++;
					} else if (snowValue > 0) {
						snowCount++;
					} else {
						sunnyCount++;
					}
					if (year == Calendar.getInstance().get(Calendar.YEAR)) {
						currentYearTemp = meanTemp;
						currentYearPress = meanPressure;
						currentYearHumid = meanHumidity;
					}
					tempList.add(meanTemp);
					pressList.add(meanPressure);
					humidlist.add(meanHumidity);
				}
			}

			String condition = rainCount > snowCount && rainCount > sunnyCount ? DataConstants.RAINY
					: snowCount > rainCount && snowCount > sunnyCount ? DataConstants.SNOW
							: sunnyCount > snowCount && sunnyCount > rainCount ? DataConstants.SUNNY
									: "No value";
			double variedTemp = findVariation(tempList.size(), tempList);
			double variedPressure = findVariation(pressList.size(), pressList);
			double variedHumidity = findVariation(humidlist.size(), humidlist);
			return variedTemp + "|" + variedPressure + "," + variedHumidity
					+ ":" + condition;
		}
		log.info("Method populateWeatherDataFromJSON End");
		return null;
	}

	/*
	 * Find the difference between the values in the list
	 * @size
	 * @parameterList
	 */
	public double findVariation(int size, List<Double> parameterList) {
		double meanValue = 0;
		double[] difference = new double[size];
		difference[0] = 0;
		for (int i = 0; i < parameterList.size() - 1; i++) {
			difference[i + 1] = parameterList.get(i + 1) - parameterList.get(i);
		}
		for (int j = 0; j < difference.length; j++) {
			meanValue += difference[j];
		}
		return (meanValue / size);
	}

	/*
	 * Fetch the historical data for the supplied date. Web service call made to weather underground application to fetch
	 * the data by supplying the apikey, location and date.
	 * 
	 * @location
	 * @reptVO
	 */
	public HashMap<String, JSONObject> fetchAPIHistData(String location,
			WeatherReportVO reptVO) {
		log.info("Method fetchAPIHistData Start");
		HashMap<String, JSONObject> histData = new HashMap<String, JSONObject>();
		try {
			String apiKey = reptVO.getApiKey();
			String histUrlPart1 = reptVO.getHistUrlPart1();
			String histUrlPart2 = reptVO.getHistUrlPart2();
			String predictionDate = reptVO.getPredictionDate();
			int indexYear = predictionDate.indexOf('-');
			int indexMonth = predictionDate.indexOf('-', indexYear+1);
			int yeartoPredict = Integer.parseInt(predictionDate.substring(0, indexYear));
			int yearForData = yeartoPredict - 5;
			String monthToPredict = predictionDate.substring(indexYear+1, indexMonth);
			String dayToPredict = predictionDate.substring(indexMonth+1, predictionDate.length());
			for (int i = yearForData; i < yeartoPredict; i++) {
				String formDate = String.valueOf(i) + monthToPredict
						+ dayToPredict;
				String constructURL = DataConstants.URL_PART1 + apiKey
						+ histUrlPart1 + formDate + histUrlPart2 + location
						+ ".json";
				System.out.println(constructURL);
				URL url = new URL(constructURL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				} else {
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
				histData.put(String.valueOf(i), jsonOutObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Method fetchAPIHistData End");
		return histData;
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