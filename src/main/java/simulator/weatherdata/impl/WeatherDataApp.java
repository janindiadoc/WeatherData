package simulator.weatherdata.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import simulator.weatherdata.helper.WeatherDataHelper;
import simulator.weatherdata.util.DataConstants;
import simulator.weatherdata.util.WeatherReportVO;

/**
 * A Simulator class for displaying the Weather Data for the given location in Australia!
 * 
 */
public class WeatherDataApp {
	static Logger log = Logger.getLogger(WeatherDataApp.class.getName());

	public static void main(String[] args) throws IOException {
		WeatherDataHelper dataHelper = new WeatherDataHelper();
		WeatherReportVO reportVO = new WeatherReportVO();
		Properties inputLocationProperty = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			InputStream fileInput = classLoader.getResourceAsStream(
					DataConstants.LOCATION_PROPERTIES);
			inputLocationProperty.load(fileInput);
			reportVO.setLocationproperties(inputLocationProperty);
			dataHelper.fetchDataFromWeatherUnderground(reportVO);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
