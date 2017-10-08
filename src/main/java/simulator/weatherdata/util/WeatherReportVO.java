package simulator.weatherdata.util;

import java.util.Properties;

public class WeatherReportVO {

	private Properties locationproperties;
	private String outputFileName;
	private String outputFileLocation;
	private String apiKey;
	private String urlPart2;

	public Properties getLocationproperties() {
		return locationproperties;
	}

	public void setLocationproperties(Properties locationproperties) {
		this.locationproperties = locationproperties;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getOutputFileLocation() {
		return outputFileLocation;
	}

	public void setOutputFileLocation(String outputFileLocation) {
		this.outputFileLocation = outputFileLocation;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUrlPart2() {
		return urlPart2;
	}

	public void setUrlPart2(String urlPart2) {
		this.urlPart2 = urlPart2;
	}

}
