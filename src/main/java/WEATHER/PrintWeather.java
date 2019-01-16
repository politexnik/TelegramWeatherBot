package WEATHER;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.model.forecast.Forecast;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.daily.DailyForecast;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

/**
 * Класс для печати сообщений
 */
public class PrintWeather {

    //Печать текущего прогноза
    static String adaptedPrint(CurrentWeather currentWeather) {
        return String.format(
                "Current weather in %s(%s):\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                currentWeather.getCityName(), currentWeather.getSystemParameters().getCountry(),
                currentWeather.getMainParameters().getTemperature(),
                currentWeather.getMainParameters().getHumidity(),
                currentWeather.getMainParameters().getPressure()
        );
    }

    //Печать прогноза часа
    static String adaptedPrint(HourlyForecast hourlyForecast) {
        return String.format(
                "Forecast for %s:\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                hourlyForecast.getDateTime().toString(),
                hourlyForecast.getMainParameters().getTemperature(),
                hourlyForecast.getMainParameters().getHumidity(),
                hourlyForecast.getMainParameters().getPressure()
        );
    }

    //Печать прогноза по суткам и часового. Собран в один метод, т.к. при перегрузке методы
    // ForecastInformation<HourlyForecast> и ForecastInformation<DailyForecast> коллапсируют
    static <T extends Forecast> String adaptedPrint(ForecastInformation<T> forecastInformation) {
        //достаем параметризованный класс
        Class actualClass = forecastInformation.getClass();
        ParameterizedType type = (ParameterizedType)actualClass.getGenericSuperclass();
        Class parameter = (Class)type.getActualTypeArguments()[0];



        StringBuilder stringBuilder = new StringBuilder(forecastInformation.getCity().toString());
        if (HourlyForecast.class.equals(parameter)) {
            ForecastInformation<HourlyForecast> forecastHourlyInformation = (ForecastInformation<HourlyForecast>) forecastInformation;
            for (HourlyForecast hourlyForecast : forecastHourlyInformation.getForecasts()) {
                stringBuilder.append("\n" + PrintWeather.adaptedPrint(hourlyForecast));
            }
        } else if (forecast instanceof DailyForecast) {
            ForecastInformation<DailyForecast> forecastDailyInformation = (ForecastInformation<DailyForecast>) forecastInformation;
            for (DailyForecast dailyForecast : forecastDailyInformation.getForecasts()) {
                stringBuilder.append(String.format("\nTemperature on %s will be: %s",
                        dailyForecast.getDateTime().toString(), dailyForecast.getTemperature().toString()));
            }
        }
        return stringBuilder.toString();
    }


}
