package simulator.weatherdata.util;

import java.util.Properties;

public class WeatherReportVO {

	private Properties locationproperties;
	private String outputFileName;
	private String outputFileLocation;
	private String apiKey;
	private String urlPart2;
	private String histUrlPart1;
	private String histUrlPart2;
	private Properties historicalDataProps;
	private String predictionDate;

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

	public Properties getHistoricalDataProps() {
		return historicalDataProps;
	}

	public void setHistoricalDataProps(Properties historicalDataProps) {
		this.historicalDataProps = historicalDataProps;
	}

	public String getHistUrlPart1() {
		return histUrlPart1;
	}

	public void setHistUrlPart1(String histUrlPart1) {
		this.histUrlPart1 = histUrlPart1;
	}

	public String getHistUrlPart2() {
		return histUrlPart2;
	}

	public void setHistUrlPart2(String histUrlPart2) {
		this.histUrlPart2 = histUrlPart2;
	}

	public String getPredictionDate() {
		return predictionDate;
	}

	public void setPredictionDate(String predictionDate) {
		this.predictionDate = predictionDate;
	}

}
