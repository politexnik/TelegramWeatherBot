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
        String lastTown = null;
        String lastQuestion = null;
        ResultSet resultSet = DBConnect.executeSelectQuery(String.format("SELECT UserID, LastTown, LastQuestion " +
                "FROM Users WHERE UserID = %d;", userID));
        try {
            if (!resultSet.next()){
                DBConnect.executeUpdateQuery(String.format("INSERT INTO Users VALUES (%d, '%s', '');", userID, user.getUserName()));
            } else {
                lastTown = resultSet.getString("LastTown");
                lastQuestion = resultSet.getString("LastQuestion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        SendMessage sendMessage = new SendMessage();
        String outString = null;
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
                    outString = "Привет, " + user.getUserName() + ". Введите город или выберите настройки";
                    break;
                case "\\subscribe":
                    outString = "На какой город оформить подписку?";
                    setUserIDLastQuestion(userID, outString);
                    break;
                case "\\unsubscribe":
                    outString = "Отменяю все подписки!";
                    setUserIDLastQuestion(userID, outString);
                    DBConnect.executeUpdateQuery(String.format("DELETE FROM Subsription WHERE UserID = %d", userID));
                    break;
                case "\\cancel":
                    setUserIDLastQuestion(userID, "");
                    break;
                default:
                    //Если это ответ на вопрос
                    if (lastQuestion != null && lastQuestion.equals("На какой город оформить подписку?")){
                        //проверяем, есть ли уже такая подписка
                        resultSet = DBConnect.executeSelectQuery(String.format("SELECT UserID, Town " +
                                "FROM Subscription WHERE UserID = %d AND Town = '%s';", userID, messageString));
                        try {
                            if (resultSet.next()){
                                outString = "У вас уже есть такая подписка.";
                                setUserIDLastQuestion(userID, "");
                            } else //Если город известен - добавляем подписку в базу
                                if (Weather.isKnownCity(messageString)){
                                    DBConnect.executeUpdateQuery(String.format("INSERT INTO Subscription (UserID, Town) VALUES (%d, '%s');",
                                            userID, messageString));
                                    //обнуляем последний вопрос пользователю
                                    setUserIDLastQuestion(userID,"");
                                    new Thread(new SubscriptionHandler()).start();  //запуск потоков обработки подписок
                                    outString = "Оформление подписки";
                                } else
                                    outString = "Город неизвестен. Введите еще раз или наберите \\cancel";
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bot.lastTown = messageString;
                        //Обновляем последний город у пользователя в базе
                        DBConnect.executeUpdateQuery(String.format("UPDATE Users SET LastTown = '%s' WHERE UserID = %d;", messageString, userID));
                        bot.setButtons(sendMessage);
                        outString = Weather.getWeather(messageString, bot.typeForecast);
                    }
            }
            //отправляем сообщение в чат пользователю
            bot.sendMsg(sendMessage, inMessage.getChatId().toString(), outString);
        } else if (inMessage.hasLocation()) {
            outString = Weather.getWeather(inMessage.getLocation(), bot.typeForecast);
            bot.sendMsg(sendMessage, inMessage.getChatId().toString(), outString);
        }
    }

    //Обнуление последнего вопроса пользователя
    private void setUserIDLastQuestion(int userID, String question){
        DBConnect.executeUpdateQuery(String.format("UPDATE Users SET LastQuestion = '%s' WHERE UserID = %d;", question, userID));
    }

    private String getHelpMessage(){
        return "HELP message:" +
                "Здравствуй! Я предоставляю прогноз погоды по городам и геолокации. Набери название города или отправь" +
                "геолокацию - и я предоставлю прогноз";
    }
}
