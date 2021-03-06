package WEATHER;

import WEATHER.JSON_WEATHER_MODEL.HourlyModel;
import WEATHER.JSON_WEATHER_MODEL.WeatherPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.common.Coordinate;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.query.*;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Класс прогноза погоды
 */

public class Weather {
    private static final String API_KEY = "8593aa46effd9bf8dc172c21c1e9121b";
    private static final DataWeatherClient client = new UrlConnectionDataWeatherClient(API_KEY);

    public static String getWeather(String cityName, TypeForecast typeForecast) {
        switch (typeForecast) {
            case CURRENT:
                return getCurrentWeather(cityName);
            case HOURLY:
                return getHourlyWeather(cityName);
            case DAILY:
                return getDailyWeather(cityName);
            default:
                return null;
        }
    }

    public static String getWeather(Location location, TypeForecast typeForecast) {
        //получаем погоду по выбраному методу и городу либо локации
        switch (typeForecast) {
            case CURRENT:
                return getCurrentWeather(location);
            case HOURLY:
                return getHourlyWeather(location);
            case DAILY:
                return getDailyWeather(location);
            default:
                return null;
        }
    }

    //прогноз текущей погоды
    private static String getCurrentWeather(String cityName) {
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

    //прогноз погоды по часам
    private static String getHourlyWeather(String cityName) {
        return getJSONForeCast("http://api.openweathermap.org/data/2.5/forecast?q=" + encodeCityName(cityName) +
                ",RU&units=metric&lang=ru&appid=" + API_KEY, TypeForecast.HOURLY);
    }

    private static String getCurrentWeather(Location location) {
        CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
                .currentWeather()
                .oneLocation()
                .byGeographicCoordinates(new Coordinate(String.valueOf(location.getLongitude()),
                        String.valueOf(location.getLatitude())))
                .language(Language.RUSSIAN)
                .unitFormat(UnitFormat.METRIC)
                .build();
        CurrentWeather currentWeather = client.getCurrentWeather(currentWeatherOneLocationQuery);
        return adaptedPrint(currentWeather);
    }

    private static String getHourlyWeather(Location location) {
        return getJSONForeCast("http://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() + "&lon="
                + location.getLongitude() + "&units=metric&lang=ru&appid=" + API_KEY, TypeForecast.HOURLY);
    }

    public static boolean isKnownCity(String cityName) {
        String russianTextEncode;
        russianTextEncode = URLEncoder.encode(cityName, StandardCharsets.UTF_8);   //В случае, если русское название города
            // из 2х слов без перекодировки не получится
        String urlString = "http://api.openweathermap.org/data/2.5/forecast?q=" + russianTextEncode +
                ",RU&units=metric&lang=ru&appid=" + API_KEY;

        return (getJSONFromUrl(urlString) != null); //если вернулся null - город неизвестен
    }

    //Получение и парсинг JSON ответа по URL (5-ти дневный 3х часовой запрос по геолокации либо по названию города)
    private static String getJSONForeCast(String urlString, TypeForecast typeForecast) {
        HourlyModel hourlyModel = new HourlyModel();
        JSONObject jObject;
        try {
            //Парсим JSON из API из OpenWeatherMap (см package.json).
            jObject = getJSONFromUrl(urlString);
            if (jObject == null) {
                return "Не найден";
            }

            JSONObject jCity = jObject.getJSONObject("city");
            hourlyModel = new HourlyModel();
            hourlyModel.setCityName(jCity.getString("name"));
            hourlyModel.setCityID(jCity.getInt("id"));
            JSONObject jCoord = jCity.getJSONObject("coord");
            hourlyModel.setCoordinatesLatitude(jCoord.getDouble("lat"));
            hourlyModel.setCoordinatesLongitude(jCoord.getDouble("lon"));
            hourlyModel.setPointsNumber((byte) jObject.getInt("cnt"));
            hourlyModel.setWeatherPointList(new ArrayList<>(hourlyModel.getPointsNumber()));

            JSONArray jsonArray = jObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONObject main = obj.getJSONObject("main");

                WeatherPoint weatherPoint = new WeatherPoint();
                weatherPoint.setTemperature(main.getDouble("temp"));
                weatherPoint.setPressure(main.getDouble("pressure"));
                weatherPoint.setHumidity(main.getDouble("humidity"));
                weatherPoint.setDescription(obj.getJSONArray("weather").getJSONObject(0).getString("description"));
                weatherPoint.setCloudCoverage((byte) obj.getJSONObject("clouds").getInt("all"));
                weatherPoint.setWindSpeed(obj.getJSONObject("wind").getDouble("speed"));
                //напоследок парсим дату
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                weatherPoint.setDate(simpleDateFormat.parse(obj.getString("dt_txt")));
                hourlyModel.getWeatherPointList().add(weatherPoint);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return typeForecast == TypeForecast.HOURLY ? hourlyModel.toString() : hourlyModel.toStringDaily();
    }


    //Печать для объекта CurrentWeather
    private static String adaptedPrint(CurrentWeather currentWeather) {
        return String.format(
                "Current weather in %s(%s):\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                currentWeather.getCityName(), currentWeather.getSystemParameters().getCountry(),
                currentWeather.getMainParameters().getTemperature(),
                currentWeather.getMainParameters().getHumidity(),
                currentWeather.getMainParameters().getPressure()
        );
    }

    //прогноз погоды по дням
    private static String getDailyWeather(String cityName) {
        return getJSONForeCast("http://api.openweathermap.org/data/2.5/forecast?q=" + encodeCityName(cityName) +
                ",RU&units=metric&lang=ru&appid=" + API_KEY, TypeForecast.DAILY);
    }

    private static String getDailyWeather(Location location) {
        return getJSONForeCast("http://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() + "&lon="
                + location.getLongitude() + "&units=metric&lang=ru&appid=" + API_KEY, TypeForecast.DAILY);
    }

    private static JSONObject getJSONFromUrl(String urlString){
        JSONObject jObject = null;

        try {
            //читаем из URL JSON объект
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            if (((HttpURLConnection)urlConnection).getResponseCode() == 404) {
                return null;    //если город не найден
            }
            InputStream urlInputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
            }
            //Парсим JSON из API из OpenWeatherMap (см package.json)
            jObject = new JSONObject(stringBuilder.toString());

//            String codeResult = jObject.getString("cod");
//            //Если не найден город
//            if (codeResult.equals("404")) {
//                return null;
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jObject;
    }

    private static String encodeCityName(String cityName){
        String russianTextEncode;
        russianTextEncode = URLEncoder.encode(cityName, StandardCharsets.UTF_8);   //В случае, если русское название города
            // из 2х слов без перекодировки не получится
        return russianTextEncode;
    }
}
