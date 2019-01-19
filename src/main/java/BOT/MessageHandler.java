package BOT;

import Database.DBConnect;
import WEATHER.Weather;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageHandler implements Runnable {
    Bot bot;
    Message inMessage;

    public MessageHandler(Bot bot, Message inMessage) {
        this.inMessage = inMessage;
        this.bot = bot;
    }

    @Override
    public void run() {
        //Проверка нового пользователя и занесение в базу
        User user = inMessage.getFrom();
        int userID = user.getId();
        ResultSet resultSet = DBConnect.executeSelectQuery(String.format("SELECT UserID FROM Users WHERE UserID = %d;", userID));
        try {
            if (!resultSet.next()){
                DBConnect.executeUpdateQuery(String.format("INSERT INTO Users VALUES (%d, '%s', '');", userID, user.getUserName()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        SendMessage sendMessage = new SendMessage();
        String outString;
        if (inMessage.hasText()) {
            String messageString = inMessage.getText();

            switch (messageString) {
                case "\\setup":
                    bot.setInlineTypeForecast(sendMessage);
                    outString = "Выберите тип прогноза?";
                    break;
                case "\\help":
                    outString = "HELP";
                    break;
                case "\\start":
                    bot.setButtons(sendMessage);
                    outString = "Введите город или выберите настройки";
                    break;
                default:
                    bot.lastTown = messageString;
                    bot.setButtons(sendMessage);
                    outString = Weather.getWeather(messageString, bot.typeForecast);
            }
            bot.sendMsg(sendMessage, inMessage.getChatId().toString(), outString);
        } else if (inMessage.hasLocation()) {
            outString = Weather.getWeather(inMessage.getLocation(), bot.typeForecast);
            bot.sendMsg(sendMessage, inMessage.getChatId().toString(), outString);
        }
    }
}
