package ge.tsu.global_weather.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/*
this class DefaultWeatherListener runs immediately(specifically contextInitialized method is called) as program starts
and fetches information about Tbilisi weather and prints
as default value of application before user asks for other city
 */
@WebListener
public class DefaultWeatherListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(DefaultWeatherListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("ContextInitialized method has been invoked");

        String APIKey = "6720963e6dba4736bea125950231010";
        String defaultCityName = "tbilisi";
        String ApiUrl = "https://api.weatherapi.com/v1/current.json?key=" + APIKey + "&q=" + defaultCityName + "&aqi=no";
        try {
            URL url = new URL(ApiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == 200) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String inputLine;
                StringBuilder apiResponse = new StringBuilder();

                while ((inputLine = bufferedReader.readLine()) != null) {
                    apiResponse.append(inputLine);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();

                String jsonResponse = apiResponse.toString();

                JSONObject jsonObject = new JSONObject(jsonResponse);

                ServletContext servletContext = event.getServletContext();

                logger.info("API has been fetched for default value Tbilisi");


                String temp = jsonObject.getJSONObject("current").get("temp_c").toString();
                servletContext.setAttribute("temp", temp);

                String defaultCity = String.valueOf(jsonObject.getJSONObject("location").get("name"));
                servletContext.setAttribute("default-city", defaultCity);

               // String lastUpdatedTime = jsonObject.getJSONObject("current").get("last_updated").toString();
                servletContext.setAttribute("last-updated-time", getFormattedLastUpdatedTimeOfWeather(jsonObject));

                String humidity = jsonObject.getJSONObject("current").get("humidity").toString();
                servletContext.setAttribute("humidity", humidity);

                String windSpeed = jsonObject.getJSONObject("current").get("wind_kph").toString();
                servletContext.setAttribute("wind-speed", windSpeed);

                //checking which weather code has been retrieved and then selecting appropriate image of weather
                int weatherCode = jsonObject.getJSONObject("current").getJSONObject("condition").getInt("code");
                String weatherImagePath;
                switch (weatherCode) {
                    case 1000:
                        weatherImagePath = "Images/clear.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1003:
                    case 1006:
                        weatherImagePath = "Images/clouds.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1009:
                    case 1012:
                    case 1023:
                        weatherImagePath = "Images/rain.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1015:
                    case 1018:
                        weatherImagePath = "Images/drizzle.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1020:
                    case 1026:
                        weatherImagePath = "Images/snow.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1029:
                    case 1030:
                    case 1033:
                    case 1036:
                    case 1039:
                    case 1042:
                        weatherImagePath = "Images/mist.png";
                        servletContext.setAttribute("weather-image", weatherImagePath);
                        break;
                    default:
                        System.out.println("Unknown weather code");
                }
                logger.info("ContextInitialized has finished and values are passed to index.jsp");
            } else {
                // Handle the error based on the status code
                if (responseCode == 404) {
                    // Not Found error
                    throw new Exception("Resource not found");
                } else if (responseCode == 500) {
                    // Server error
                    throw new Exception("Internal Server Error");
                } else {
                    // Handle other status codes as needed
                    throw new Exception("Maybe your API Key has been expired, HTTP request failed with status code " + responseCode);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //this method gets jsonObject which is our API, then fetches date and formats it
    public String getFormattedLastUpdatedTimeOfWeather(JSONObject jsonObject) throws ParseException {
        //fetching date and time from api, shows users when was the last update of weather
        String updateTime = jsonObject.getJSONObject("current").get("last_updated").toString();
        // Create a SimpleDateFormat object for the input format (our date example-> 2023-09-29 13:41:46)
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // Parse the input string into a Date object
        Date date = inputDateFormat.parse(updateTime);
        // Create a SimpleDateFormat object for the desired output format (our desired format-> 29-09-2023 13:41)
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm");

        //out put will be like that-> 29-09-2023 13:41
        return outputDateFormat.format(date);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
