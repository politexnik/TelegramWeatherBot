package WEATHER;

import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.daily.DailyForecast;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.openweathermap.api.query.*;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.openweathermap.api.query.forecast.hourly.ByCityName;

public class WeatherCity {
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
        return PrintWeather.adaptedPrint(currentWeather);
    }

    public static String getWeatherHourlyByCity(String cityName){
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        ByCityName byCityNameForecast = QueryBuilderPicker.pick()
                .forecast()                                         // get forecast
                .hourly()                                           // it should be hourly forecast
                .byCityName(cityName)                              // for Kharkiv city
                .countryCode("RU")                                  // in Ukraine
                .unitFormat(UnitFormat.METRIC)                      // in Metric units
                .language(Language.RUSSIAN)                         // in English
                .count(5)                                           // limit results to 5 forecasts
                .build();
        ForecastInformation<HourlyForecast> forecastInformation = client.getForecastInformation(byCityNameForecast);
        return PrintWeather.adaptedPrint(forecastInformation, new HourlyForecast());
    }

    public static String getDailyWeatherByCity(String cityName){
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        org.openweathermap.api.query.forecast.daily.ByCityName byCityNameForecast =
                QueryBuilderPicker.pick()
                        .forecast()                                         // get forecast
                        .daily()                                            // it should be dailt
                        .byCityName(cityName)                              // for Kharkiv city
                        .countryCode("RU")                                  // in Ukraine
                        .unitFormat(UnitFormat.METRIC)                      // in Metric units
                        .language(Language.RUSSIAN)                         // in English
                        .build();
        ForecastInformation<DailyForecast> forecastInformation = client.getForecastInformation(byCityNameForecast);
        return PrintWeather.adaptedPrint(forecastInformation, new DailyForecast());
    }
}
