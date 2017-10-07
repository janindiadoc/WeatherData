# WeatherData
Repository created for development of a weather data simulator
This is a prototype of a program which simulates the weather and outputs weather data in a standard format taking into account things like atmosphere, topography, geography,oceanography, or similar) that evolves over time. The following measurements has been fetched for different cities which is the input of this program.

1. Location
2. Position is a comma-separated triple containing latitude, longitude, and elevation
3. Local time is an ISO8601 date time
4. Conditions is either Snow, Rain, Sunny,
5. Temperature is in °C,
6. Pressure is in hPa, and
7. Relative humidity is a %.

Weather Underground is the application used to fetch the conditions mentioned above. Website is https://www.wunderground.com/ . The API which provides all the above factors is retrived by using a REST Webservice call providing the city and api key as the input. The api key is generated from the website.
REST call is made for a particular city to wunderground which provides the weather condition factors as response  in JSON format.Necessary fields has been retrieved from the response which is displayed in the standard format as requested. 

The cities are provied in a property file called location.properties
The output is written in a file called WeatherReport.txt

This application requires below
JDK1.7

JSON ( for read the data from Rest API )

Joda (Used Instant class to retrive the local time format)

Junit (for junit test cases)

FileInputStream ,BufferedWriter( to write the output in to a file )

Wunderground RestAPI ( to download weather data  weather underground application)
