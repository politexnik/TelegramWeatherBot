package WEATHER.JSON_WEATHER_MODEL;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherPoint {
    private Date date;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM kk:mm");
    private double temperature;
    private double pressure;
    private double humidity;
    private String description;
    private byte cloudCoverage;
    private double windSpeed;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(simpleDateFormat.format(date) + "\n")
                .append(String.format("temp: %.1f°C, pres: %.0f гПа, humidity: %.1f%%\n",temperature, pressure, humidity))
                .append(String.format("%s, cloud: %d%%, wind: %.1f м/с ",description, cloudCoverage, windSpeed));
        return stringBuilder.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte getCloudCoverage() {
        return cloudCoverage;
    }

    public void setCloudCoverage(byte cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
