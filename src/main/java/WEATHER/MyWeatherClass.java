package WEATHER;

import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.common.Coordinate;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.openweathermap.api.query.*;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.openweathermap.api.query.forecast.hourly.ByCityName;



public class MyWeatherClass {
    private static final String API_KEY = "8593aa46effd9bf8dc172c21c1e9121b";

    public static String getWeatherByCity(String cityName, TypeForecast typeForecast) {

        switch (typeForecast) {
            case CURRENT:
                return getCurrentWeatherByCity(cityName);
            case HOURLY:
                return getWeatherHourlyByCity(cityName);
            case DAILY:
                return getDailyWeatherByCity(cityName);
            default:
                return null;
        }
    }

    public static String getCurrentWeatherByCity(String cityName){
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
                .currentWeather()                   // get current weather
                .oneLocation()                      // for one location
                .byCityName(cityName)              // for cityName
                .countryCode("RU")                  // in Russia
                .type(Type.ACCURATE)                // with Accurate search
                .language(Language.RUSSIAN)         // in English language
                .responseFormat(ResponseFormat.JSON)// with JSON response format
                .unitFormat(UnitFormat.METRIC)      // in metric units
                .build();
        CurrentWeather currentWeather = client.getCurrentWeather(currentWeatherOneLocationQuery);
        return adaptedPrint(currentWeather);
    }

    private static String adaptedPrint(CurrentWeather currentWeather) {
        return String.format(
                "Current weather in %s(%s):\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                currentWeather.getCityName(), currentWeather.getSystemParameters().getCountry(),
                currentWeather.getMainParameters().getTemperature(),
                currentWeather.getMainParameters().getHumidity(),
                currentWeather.getMainParameters().getPressure()
        );
    }

    public static String getCurrentWeatherByLocation(double lat, double lon) {
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
                .currentWeather()
                .oneLocation()
                .byGeographicCoordinates(new Coordinate(String.valueOf(lon), String.valueOf(lat)))
                .language(Language.RUSSIAN)
                .unitFormat(UnitFormat.METRIC)
                .build();
        CurrentWeather currentWeather = client.getCurrentWeather(currentWeatherOneLocationQuery);
        return adaptedPrint(currentWeather);
    }

    public static String getWeatherHourlyByCity(String cityName){
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        ByCityName byCityNameForecast = QueryBuilderPicker.pick()
                .forecast()                                         // get forecast
                .hourly()                                           // it should be hourly forecast
                .byCityName(cityName)                              // for Kharkiv city
                .countryCode("RU")                                  // in Ukraine
                .unitFormat(UnitFormat.METRIC)                      // in Metric units
                .language(Language.ENGLISH)                         // in English
                .count(5)                                           // limit results to 5 forecasts
                .build();
        ForecastInformation<HourlyForecast> forecastInformation = client.getForecastInformation(byCityNameForecast);
        StringBuilder stringBuilder = new StringBuilder("Forecasts for " + forecastInformation.getCity() + ":");
        for (HourlyForecast forecast : forecastInformation.getForecasts()) {
            stringBuilder.append("\n" + adaptedPrint(forecast));
        }
        return stringBuilder.toString();
    }

    private static String adaptedPrint(HourlyForecast hourlyForecast) {
        return String.format(
                "Forecast for %s:\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                hourlyForecast.getDateTime().toString(),
                hourlyForecast.getMainParameters().getTemperature(),
                hourlyForecast.getMainParameters().getHumidity(),
                hourlyForecast.getMainParameters().getPressure()
        );
    }

    public static String getWeatherByLocation(double lat, double lon, TypeForecast typeForecast){
        switch (typeForecast) {
            case CURRENT:
                return getCurrentWeatherByLocation(lat, lon);
            case HOURLY:
                return getWeatherHourlyByLocation(lat, lon);
            case DAILY:
                return getDailyWeatherByLocation(lat, lon);
            default:
                return null;
        }
    }

    private static String getDailyWeatherByLocation(double lat, double lon) {
        return null;
    }

    private static String getWeatherHourlyByLocation(double lat, double lon) {
        return null;
    }

    public static String getDailyWeatherByCity(String cityName){
        return null;
    }
}
