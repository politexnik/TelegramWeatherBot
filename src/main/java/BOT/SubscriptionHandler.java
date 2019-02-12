package BOT;

import WEATHER.TypeForecast;
import WEATHER.Weather;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.TimerTask;

public class SubscriptionHandler extends TimerTask {
    private String chatID;
    private String cityName;
    private Bot bot;
    private SendMessage sendMessage;
    @Override
    public void run() {
        String outString = Weather.getWeather(cityName, TypeForecast.HOURLY);
        bot.sendMsg(sendMessage, chatID, outString);
    }

    SubscriptionHandler(Bot bot, SendMessage sendMessage, String chatID, String cityName) {
        this.chatID = chatID;
        this.cityName = cityName;
        this.bot = bot;
        this.sendMessage = sendMessage;
    }

}
