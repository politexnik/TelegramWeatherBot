import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.logging.Level;

public class WeatherBot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) { //проверка на наличие текста
            String message = update.getMessage().getText();
            sendMsg(update.getMessage().getChatId().toString(), message);
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимот отправить в качестве сообщения.
     */

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try{
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "WeatherPolitexnikBot";
    }

    public String getBotToken() {
        return "734570323:AAEPEakF_UUoonwl6oib6QZjl0LAbZKerr4";
    }
}
