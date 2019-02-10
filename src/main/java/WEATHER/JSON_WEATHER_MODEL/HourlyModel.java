package WEATHER.JSON_WEATHER_MODEL;

import java.util.List;

public class HourlyModel {
    private String cityName;
    private double coordinatesLatitude;
    private double coordinatesLongitude;
    private int cityID;
    private byte pointsNumber;
    private List<WeatherPoint> weatherPointList;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder().append("Прогноз на каждые 3 часа:\n")
            .append("Город: " + cityName + "\n");

        for (WeatherPoint wp: weatherPointList) {
            stringBuilder.append(wp.toString() + "\n");
        }
        return stringBuilder.toString();
    }

    public String toStringDaily(){
        //TODO
        StringBuilder stringBuilder = new StringBuilder().append("Прогноз на каждые 3 часа:\n")
                .append("Город: " + cityName + "\n");

        for (int i = 0; i < weatherPointList.size(); i++) {
            //if ()
        }


        return stringBuilder.toString();

    }

    public String getCityName() {
        return cityName;
    }

    public double getCoordinatesLatitude() {
        return coordinatesLatitude;
    }

    public double getCoordinatesLongitude() {
        return coordinatesLongitude;
    }

    public int getCityID() {
        return cityID;
    }

    public byte getPointsNumber() {
        return pointsNumber;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCoordinatesLatitude(double coordinatesLatitude) {
        this.coordinatesLatitude = coordinatesLatitude;
    }

    public void setCoordinatesLongitude(double coordinatesLongitude) {
        this.coordinatesLongitude = coordinatesLongitude;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public void setPointsNumber(byte pointsNumber) {
        this.pointsNumber = pointsNumber;
    }

    public List<WeatherPoint> getWeatherPointList() {
        return weatherPointList;
    }

    public void setWeatherPointList(List<WeatherPoint> weatherPointList) {
        this.weatherPointList = weatherPointList;
    }
}


