package WEATHER;

import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.daily.DailyForecast;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.openweathermap.api.query.Language;
import org.openweathermap.api.query.QueryBuilderPicker;
import org.openweathermap.api.query.UnitFormat;
import org.openweathermap.api.query.forecast.hourly.ByCityName;

public class WeatherTest {
    private static final String API_KEY = "8593aa46effd9bf8dc172c21c1e9121b";

    public static void main(String[] args) {
        DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);
        org.openweathermap.api.query.forecast.daily.ByCityName byCityNameForecast =
                QueryBuilderPicker.pick()
                .forecast()                                         // get forecast
                .daily()                                            // it should be dailt
                .byCityName("Kharkiv")                              // for Kharkiv city
                .countryCode("UA")                                  // in Ukraine
                .unitFormat(UnitFormat.METRIC)                      // in Metric units
                .language(Language.ENGLISH)                         // in English
                .build();
        ForecastInformation<DailyForecast> forecastInformation = client.getForecastInformation(byCityNameForecast);
        StringBuilder stringBuilder = new StringBuilder(forecastInformation.getCity().toString());
        for (DailyForecast forecast : forecastInformation.getForecasts()) {
            stringBuilder.append(String.format("\nTemperature on %s will be: %s",
                    forecast.getDateTime().toString(), forecast.getTemperature().toString()));
        }
    }


    private static String prettyPrint(HourlyForecast hourlyForecast) {
        return String.format(
                "Forecast for %s:\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                hourlyForecast.getDateTime().toString(),
                hourlyForecast.getMainParameters().getTemperature(),
                hourlyForecast.getMainParameters().getHumidity(),
                hourlyForecast.getMainParameters().getPressure()
        );
    }


}
