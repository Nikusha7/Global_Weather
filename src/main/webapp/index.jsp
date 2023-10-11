<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Weather Hub</title>
    <link rel="stylesheet" href="CSS/styles.css">

<%--    <script>--%>
<%--        function validateCityName() {--%>
<%--            var cityNameInput = document.getElementById('city');--%>
<%--            var cityName = cityNameInput.value.trim(); // Remove leading and trailing spaces--%>

<%--            if (cityName === '') {--%>
<%--                alert("City name cannot be empty.");--%>
<%--                return false; // Prevent form submission--%>
<%--            } else if (cityName.includes(' ')) {--%>
<%--                alert("City name cannot contain spaces.");--%>
<%--                return false; // Prevent form submission--%>
<%--            }--%>

<%--            return true; // Allow form submission--%>
<%--        }--%>
<%--    </script>--%>

</head>

<body>

<%
    /*
    setting values to temperature, city, last update time, humidity, wind speed and weather icon.
    by default, it will be set for tbilisi(defaultWeatherListener is invoked)
    and after user enters other city weather servlet will be invoked
     */
    String temperature;
    if (request.getAttribute("temp") != null) {
        //setting values of city which user entered
        temperature = String.valueOf(request.getAttribute("temp"));
    } else {
        //setting default values of tbilisi
        temperature = String.valueOf(request.getServletContext().getAttribute("temp"));
    }

    String city;
    if (request.getAttribute("city") != null) {
        //setting values of city which user entered
        city = String.valueOf(request.getAttribute("city"));
    } else {
        //setting default values of tbilisi
        city = String.valueOf(request.getServletContext().getAttribute("default-city"));
    }

    String lastUpdateTime;
    if (request.getAttribute("last-updated-time") != null) {
        //setting values of city which user entered
        lastUpdateTime = String.valueOf(request.getAttribute("last-updated-time"));
    } else {
        //setting default values of tbilisi
        lastUpdateTime = String.valueOf(request.getServletContext().getAttribute("last-updated-time"));
    }

    String humidity;
    if (request.getAttribute("humidity") != null) {
        //setting values of city which user entered
        humidity = String.valueOf(request.getAttribute("humidity"));
    } else {
        //setting default values of tbilisi
        humidity = String.valueOf(request.getServletContext().getAttribute("humidity"));
    }

    String windSpeed;
    if (request.getAttribute("wind-speed") != null) {
        //setting values of city which user entered
        windSpeed = String.valueOf(request.getAttribute("wind-speed"));
    } else {
        //setting default values of tbilisi
        windSpeed = String.valueOf(request.getServletContext().getAttribute("wind-speed"));
    }

    String weatherImage;
    if (request.getAttribute("weather-image") != null) {
        //setting values of city which user entered
        weatherImage = String.valueOf(request.getAttribute("weather-image"));
    } else {
        //setting default values of tbilisi
        weatherImage = String.valueOf(request.getServletContext().getAttribute("weather-image"));
    }

    String errorMessage;
    if (request.getAttribute("error-message") != null) {
        errorMessage = String.valueOf(request.getAttribute("error-message"));
    } else if (request.getAttribute("error") != null) {
        errorMessage = String.valueOf(request.getAttribute("error"));
    } else {
        errorMessage = "";
    }
%>

<div class="card">
    <div class="search">
<%--       onsubmit="return validateCityName();"--%>
        <form method="post" action="weather">
            <input name="city" id="city" type="text" placeholder="Enter City Name" spellcheck="false">
            <button><img src="Images/search.png"></button>
        </form>
    </div>
    <div class="error">
        <p><%=errorMessage%>
        </p>
    </div>
    <div class="weather">
        <img class="weather-icon" src=<%=weatherImage%>>

        <h1 class="temperature"><%=temperature%>&degc</h1>
        <h2 class="city"><%=city%>
        </h2>
        <br/>
        <div class="lastUpdateTime">
            <p>Last updated at <%=lastUpdateTime%>
            </p>
        </div>
        <div class="details">

            <div class="col">
                <img src="Images/humidity.png">
                <div>
                    <p class="humidity"><%=humidity%>%</p>
                    <p>Humidity</p>
                </div>
            </div>
            <div class="col">
                <img src="Images/wind.png">
                <div>
                    <p class="wind"><%=windSpeed%> km/h</p>
                    <p>Wind Speed</p>
                </div>
            </div>

        </div>
    </div>
</div>


</body>
</html>