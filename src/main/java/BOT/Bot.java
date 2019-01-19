package BOT;

import WEATHER.Weather;
import WEATHER.TypeForecast;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    TypeForecast typeForecast = TypeForecast.CURRENT;
    String lastTown;

    //Мапа для установки типа погоды
    static Map<String, TypeForecast> typeForecastMap;

    static {
        typeForecastMap = new HashMap<>();
        typeForecastMap.put("\\current", TypeForecast.CURRENT);
        typeForecastMap.put("\\hourly", TypeForecast.HOURLY);
        typeForecastMap.put("\\daily", TypeForecast.DAILY);
    }


    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {  //проверка на сообщения
            new Thread(new MessageHandler(this, update.getMessage())).start();
        } else if (update.hasCallbackQuery()) {
            new Thread(new CallbackQueryHandler(this, update.getCallbackQuery())).start();
        }

    }

    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param chatId id чата
     * @param outString      Строка, которую необходимот отправить в качестве сообщения.
     */

    public synchronized void sendMsg(SendMessage sendMessage, String chatId, String outString) {
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(outString);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("\\start"));
        keyboardFirstRow.add(new KeyboardButton("\\help"));
        keyboardFirstRow.add(new KeyboardButton("\\setup"));
        keyboardFirstRow.add(new KeyboardButton(lastTown));
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    void setInlineTypeForecast(SendMessage sendMessage) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("Сейчас").setCallbackData("\\current"));
        buttons1.add(new InlineKeyboardButton().setText("24ч").setCallbackData("\\hourly"));
        buttons1.add(new InlineKeyboardButton().setText("5 дней").setCallbackData("\\daily"));
        buttons.add(buttons1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    public String getBotUsername() {
        return "WeatherPolitexnikBot";
    }

    public String getBotToken() {
        return "734570323:AAEPEakF_UUoonwl6oib6QZjl0LAbZKerr4";
    }
}
