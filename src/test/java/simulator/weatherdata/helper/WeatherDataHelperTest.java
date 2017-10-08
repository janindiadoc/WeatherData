package simulator.weatherdata.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONObject;

import simulator.weatherdata.util.DataConstants;
import simulator.weatherdata.util.WeatherReportVO;
import junit.framework.TestCase;

public class WeatherDataHelperTest extends TestCase {

	WeatherDataHelper wdHelper = new WeatherDataHelper();
	WeatherReportVO wReportVO = new WeatherReportVO();

	/*
	 * Test API connection with the correct API key and location.Should succeed
	 */
	public void testFetchAPIDataSuccess() {
		wReportVO.setApiKey("5a747aee9dc6150a");
		wReportVO.setUrlPart2("/conditions/q/AU/");
		wdHelper.fetchAPIData("Sydney", wReportVO);
		assertTrue(true);
	}

	/*
	 * Test API connection with the wrong API key and correct location.Should
	 * fail
	 */
	public void testFetchAPIDataInvalidApikey() {
		wReportVO.setApiKey("123");
		wReportVO.setUrlPart2("/conditions/q/AU/");
		wdHelper.fetchAPIData("Melbourne", wReportVO);
		assertTrue(false);
	}

	/*
	 * Test API connection with the correct API key and wrong location.Should
	 * fail
	 */
	public void testFetchAPIDataInvalidLocation() {
		wReportVO.setApiKey("5a747aee9dc6150a");
		wReportVO.setUrlPart2("/conditions/q/AU/");
		wdHelper.fetchAPIData("aaa", wReportVO);
		assertTrue(false);
	}

	/*
	 * Test writing the output to file with correct property file and
	 * content.Should succeed
	 */
	public void testWriteWeatherReportIntoFileSuccess() throws IOException {
		StringBuilder finalOutput = new StringBuilder();
		WeatherReportVO reportVO = new WeatherReportVO();
		Properties property = new Properties();
		InputStream fileInput;
		try {
			fileInput = new FileInputStream(DataConstants.LOCATION_PROPERTIES);
			property.load(fileInput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reportVO.setLocationproperties(property);
		reportVO.setOutputFileName("WeatherReportTest.txt");
		finalOutput
				.append("Sunshine|-35.31000137,149.13000488,611.1|2017-10-07T01:20:46.637Z|Cloudy|16.0|1022|20%");
		wdHelper.writeWeatherReportIntoFile(finalOutput, reportVO);
		assertTrue(true);
	}

	/*
	 * Test writing the output to file with wrong property file and
	 * content.Should fail
	 */
	public void testWriteWeatherReportIntoFileFail() throws IOException {
		StringBuilder finalOutput = new StringBuilder();
		WeatherReportVO reportVO = new WeatherReportVO();
		Properties property = new Properties();
		InputStream fileInput;
		try {
			fileInput = new FileInputStream(
					DataConstants.LOCATION_PROPERTIES_NOT_EXISTS);
			property.load(fileInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		reportVO.setLocationproperties(property);
		reportVO.setOutputFileName("WeatherReportTest.txt");
		finalOutput
				.append("Darwin|-35.31000137,149.13000488,611.1|2017-10-07T01:20:46.637Z|Rainy|16.0|1026|60%");
		wdHelper.writeWeatherReportIntoFile(finalOutput, reportVO);
		assertTrue(false);
	}
	
	/*
	 * Test for a json object passed as null. Should fail
	 */
	public void testpopulateRequiredFields(){
		JSONObject json = null;
		wdHelper.populateRequiredFields(json);
		assertTrue(false);
	}
}
