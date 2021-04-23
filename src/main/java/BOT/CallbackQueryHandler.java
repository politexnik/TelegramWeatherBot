package BOT;

import WEATHER.TypeForecast;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

//Обработка CallbackQuery в сообщении
public class CallbackQueryHandler implements Runnable {
    CallbackQuery callbackQuery;
    Bot tbot;



    public CallbackQueryHandler(Bot bot, CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;
        tbot = bot;
    }

    @Override
    public void run() {
        String query = callbackQuery.getData();
        if (query.startsWith("\\")) {
            if (query.equalsIgnoreCase("\\current") || query.equalsIgnoreCase("\\hourly")
                    || query.equalsIgnoreCase("\\daily")) {
                tbot.typeForecast = tbot.typeForecastMap.get(query.toLowerCase());
            } else {

                switch (query) {
                    case "\\start":
                        break;
                }
            }
        }
    }
}
