package WEATHER;

import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.common.Coordinate;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.daily.DailyForecast;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.openweathermap.api.query.Language;
import org.openweathermap.api.query.QueryBuilderPicker;
import org.openweathermap.api.query.UnitFormat;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.openweathermap.api.query.forecast.hourly.ByGeographicCoordinates;

public class WeatherLocationShort {
    private static final String API_KEY = "8593aa46effd9bf8dc172c21c1e9121b";

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
        return PrintWeather.adaptedPrint(currentWeather);
    }

    private static String getDailyWeatherByLocation(double lat, double lon) {
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        org.openweathermap.api.query.forecast.daily.ByGeographicCoordinates byGeographicCoordinates =
                QueryBuilderPicker.pick()
                        .forecast()                                         // get forecast
                        .daily()                                           // it should be daily forecast
                        .byGeographicCoordinates(new Coordinate(String.valueOf(lon), String.valueOf(lat)))
                        .language(Language.RUSSIAN)
                        .unitFormat(UnitFormat.METRIC)
                        .build();
        ForecastInformation<DailyForecast> forecastInformation = client.getForecastInformation(byGeographicCoordinates);
        return PrintWeather.adaptedPrint(forecastInformation);
    }

    private static String getWeatherHourlyByLocation(double lat, double lon) {
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        ByGeographicCoordinates byGeographicCoordinates = QueryBuilderPicker.pick()
                .forecast()                                         // get forecast
                .hourly()                                           // it should be hourly forecast
                .byGeographicCoordinates(new Coordinate(String.valueOf(lon), String.valueOf(lat)))
                .language(Language.RUSSIAN)
                .unitFormat(UnitFormat.METRIC)
                .build();
        ForecastInformation<HourlyForecast> forecastInformation = client.getForecastInformation(byGeographicCoordinates);
        return PrintWeather.adaptedPrint(forecastInformation);
    }
}
