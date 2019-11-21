import Bot.MeWordsBot;
import Words.NewWordsSender;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    private static MeWordsBot meWordsBot;
    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        meWordsBot =new MeWordsBot();
        try {
            telegramBotsApi.registerBot(meWordsBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        var wordsSender=new NewWordsSender();

        var notification_time =25200000l;//7 hours

        wordsSender.startSending(meWordsBot,notification_time);

    }



}