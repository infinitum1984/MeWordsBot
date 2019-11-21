package Words;

import Bot.MeWordsBot;
import Config.Config;
import Firebase.FirebaseUser;
import Firebase.MyFirebase;


import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

public class NewWordsSender {
    public void startSending(MeWordsBot bot, long notification_time){
        new Timer(countTimeForTimer(notification_time)) {
            @Override
            public void end() {
                List<String> users_list= MyFirebase.get().getUsers();
                for (String user_id:users_list){
                    FirebaseUser user = MyFirebase.get().getUser(user_id);
                    bot.sendNewWordsToUser(user_id,user.generateWords());
                    MyFirebase.get().updateUser(user_id,user);
                }

            }
        };
    }
    private long countTimeForTimer(long time){
        if (Config.IS_TEST){return 200;}

        long wake_up_time=time;

        LocalDateTime now= LocalDateTime.now(TimeZone.getTimeZone("Europe/Kiev").toZoneId());
        long now_hour=now.getHour();
        long now_minute=now.getMinute();
        long now_time =(now_hour*3600000)+(now_minute*60000);
        if(now_time<=wake_up_time){
            return wake_up_time-now_time;
        }
        long out=(86400000-now_time)+wake_up_time;
        return out;
    }
}
