package Words;

import Config.Config;

import java.time.LocalDateTime;
import java.util.Date;

import static java.lang.Thread.sleep;

public abstract class Timer {

    public Timer(long time){
        startTread(time);

    }
    public abstract void end();
    private void startTread(long time){
        Thread ntread = new Thread(()->{
            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end();
                startTread(86400000);
            });
        ntread.start();
    }
}
