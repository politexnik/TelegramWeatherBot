import WEATHER.Weather;
import WEATHER.TypeForecast;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
            new Thread(new messageHandle(update.getMessage())).start();
        } else if(update.hasCallbackQuery()){
            String query = update.getCallbackQuery().getData();
            if (query.startsWith("\\")) {
                typeForecast = typeForecastMap.get(query.toLowerCase());
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
        setButtons(sendMessage);
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
        keyboardFirstRow.add(new KeyboardButton("\\start"));
        keyboardFirstRow.add(new KeyboardButton("\\help"));
        keyboardFirstRow.add(new KeyboardButton("\\setup"));
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private void setInline(){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("Сейчас").setCallbackData("\\current"));
        buttons1.add(new InlineKeyboardButton().setText("24ч").setCallbackData("\\hourly"));
        buttons1.add(new InlineKeyboardButton().setText("5 дней").setCallbackData("\\daily"));
        buttons.add(buttons1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
    }

    private class messageHandle implements Runnable{
        Message message;
        public messageHandle(Message message){
            this.message = message;
        }

        @Override
        public void run() {
            if (message.hasText()) {
                String messageString = message.getText();
                    switch (messageString) {
                        case "\\setup":


                    }
                    String returnWeather = Weather.getWeather(messageString, typeForecast);
                    sendMsg(message.getChatId().toString(), returnWeather);
            } else if (message.hasLocation()) {
                String returnWeather = Weather.getWeather(message.getLocation(), typeForecast);
                sendMsg(message.getChatId().toString(), returnWeather);
            }
        }
    }


    public String getBotUsername() {
        return "WeatherPolitexnikBot";
    }

    public String getBotToken() {
        return "734570323:AAEPEakF_UUoonwl6oib6QZjl0LAbZKerr4";
    }
}
