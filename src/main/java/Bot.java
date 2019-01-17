import WEATHER.Weather;
import WEATHER.TypeForecast;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private TypeForecast typeForecast = TypeForecast.CURRENT;

    //Мапа для установки типа погоды
    private static Map<String, TypeForecast> typeForecastMap;
    static {
        typeForecastMap = new HashMap<String, TypeForecast>();
        typeForecastMap.put("\\current", TypeForecast.CURRENT);
        typeForecastMap.put("\\hourly", TypeForecast.HOURLY);
        typeForecastMap.put("\\daily", TypeForecast.DAILY);
    }


    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){  //проверка на сообщения
            if (update.getMessage().hasText()) {
                String message = update.getMessage().getText();
                //устанавливаем тип прогноза
                if (message.startsWith("\\")) {
                    typeForecast = typeForecastMap.get(message.toLowerCase());
                } else {
                    String returnWeather = Weather.getWeather(message, typeForecast);
                    sendMsg(update.getMessage().getChatId().toString(), returnWeather);
                }
            } else if (update.getMessage().hasLocation()) {
                double lat = update.getMessage().getLocation().getLatitude();
                double lon = update.getMessage().getLocation().getLongitude();
                String returnWeather = Weather.getWeather(update.getMessage().getLocation(), typeForecast);
                sendMsg(update.getMessage().getChatId().toString(), returnWeather);
            }
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param chatId id чата
     * @param s      Строка, которую необходимот отправить в качестве сообщения.
     */

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
//            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Cейчас"));
        keyboardFirstRow.add(new KeyboardButton("24 часа"));
        keyboardFirstRow.add(new KeyboardButton("3 дня"));
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "WeatherPolitexnikBot";
    }

    public String getBotToken() {
        return "734570323:AAEPEakF_UUoonwl6oib6QZjl0LAbZKerr4";
    }
}
