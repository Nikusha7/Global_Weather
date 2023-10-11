package ge.tsu.global_weather.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/*
this servlet class is invoked when user enters city name,
here we are fetching API to get temperature, weather code for image, last update time(when was api updated),
humidity and wind speed in that specific city.
then sending all those values to index.jsp
 */
@WebServlet(name = "WeatherServlet", value = "/weather")
public class WeatherServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WeatherServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doPost method has been invoked");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/");

        String city = req.getParameter("city").replace(" ", "%20");

        if (req.getParameter("city") == null || req.getParameter("city").isEmpty()) {
            req.setAttribute("error-message", "Please enter the city name");
            requestDispatcher.forward(req, resp);
            return;
        }

        String APIKey = System.getenv("Weather_API_KEY");
        String ApiUrl = "https://api.weatherapi.com/v1/current.json?key=" + APIKey + "&q=" + city + "&aqi=no";
        try {
            logger.info("Started Api Fetch, API url: "+ApiUrl);
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

                logger.info("Api has been successfully fetched");

                String temp = jsonObject.getJSONObject("current").get("temp_c").toString();
                Double d = Double.valueOf(temp);
                DecimalFormat decimalFormat1 = new DecimalFormat("0");
                req.setAttribute("temp", decimalFormat1.format(d));

                String cityName = jsonObject.getJSONObject("location").getString("name");
                req.setAttribute("city", cityName);

                String lastUpdatedTime = jsonObject.getJSONObject("current").get("last_updated").toString();
                req.setAttribute("last-updated-time", getFormattedLastUpdatedTimeOfWeather(jsonObject));

                String humidity = jsonObject.getJSONObject("current").get("humidity").toString();
                req.setAttribute("humidity", humidity);

                String windSpeed = jsonObject.getJSONObject("current").get("wind_kph").toString();
                req.setAttribute("wind-speed", windSpeed);

                //checking which weather code has been retrieved and then selecting appropriate image of weather
                int weatherCode = jsonObject.getJSONObject("current").getJSONObject("condition").getInt("code");
                String weatherImagePath;
                switch (weatherCode) {
                    case 1000:
                        weatherImagePath = "Images/clear.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1003:
                    case 1006:
                        weatherImagePath = "Images/clouds.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1009:
                    case 1012:
                    case 1023:
                        weatherImagePath = "Images/rain.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1015:
                    case 1018:
                        weatherImagePath = "Images/drizzle.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1020:
                    case 1026:
                        weatherImagePath = "Images/snow.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    case 1029:
                    case 1030:
                    case 1033:
                    case 1036:
                    case 1039:
                    case 1042:
                        weatherImagePath = "Images/mist.png";
                        req.setAttribute("weather-image", weatherImagePath);
                        break;
                    default:
                        System.out.println("Unknown weather code");
                }

                requestDispatcher.forward(req, resp);

                logger.info("doPost method has finished and values are passed to index.jsp");
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
                    //throw new Exception("Maybe your API Key has been expired, HTTP request failed with status code " + responseCode);
                    req.setAttribute("error", "Invalid city name");
                    requestDispatcher.forward(req, resp);
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
}
