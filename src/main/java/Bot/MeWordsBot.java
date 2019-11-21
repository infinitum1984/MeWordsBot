package Bot;

import Config.Config;
import Firebase.FirebaseUser;
import Firebase.MyFirebase;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MeWordsBot extends TelegramLongPollingBot {
    private final String BOT_TOKEN=System.getenv("BOT_TOKEN");//main
    private final String BOT_TOKEN_TEST=System.getenv("BOT_TOKEN_TEST");//test
    private final String BOT_USER_NAME="Me Words Bot";

    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery())
            processCallBacks(update.getCallbackQuery());


        if (!(update.hasMessage() && update.getMessage().hasText()))
            return;

        String msg = update.getMessage().getText();
        String usrId= String.valueOf(update.getMessage().getChatId());

        System.out.println("--------------\n"+"usr: "+usrId+"\n"+"msg: "+msg+"\n"+"--------------\n");
        switch (msg){
            case BotRequestMsgs.ON_START :
                onStartRequest(usrId);
                break;

            case BotRequestMsgs.ON_HELP:
                sendHelpMsg(usrId);
                break;

            case BotRequestMsgs.SHOW_USR_WORDS:
                showUserWords(usrId);
                break;
            default:
                sendMessage(usrId, "I don't know what do you want");
                break;

        }
    }

    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public String getBotToken() {
        if (Config.IS_TEST) return BOT_TOKEN_TEST;

        return BOT_TOKEN;
    }
    private void sendMessage(String usr_id, String text){

        SendMessage message = new SendMessage()
                .setChatId(usr_id)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //processing requests
    private void onStartRequest(String usrId){
        if (!MyFirebase.get().userExist(usrId)) {
            MyFirebase.get().create_user(usrId);
            sendMessage(usrId, "You are registered in our system and now you will receive daily new words.\n" +
                    "Now every day at 7 am you will receive notifications with new words");
            sendControlKeyboard(usrId);
        }else {
            sendMessage(usrId,"Welcome back!!");
            sendControlKeyboard(usrId);

        }
    }

    private void sendHelpMsg(String usr_id){
        sendMessage(usr_id,
                "Show my words - show words what you know."
        );

    }


    private void sendControlKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Use keyboard to control.");

        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row1 = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row1.add(BotRequestMsgs.SHOW_USR_WORDS);
        // Add the first row to the keyboard
        keyboard.add(row1);
        // Create another keyboard row
        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotRequestMsgs.ON_HELP);
        keyboard.add(row2);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);

        try {
            // Send the message
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendNewWordsToUser(String usr_id, String [] words){


        SendMessage message = new SendMessage()
                .setChatId(usr_id)
                .setText("Your new words \n" +
                        "\n" +
                        words[0] + "\n" +
                        words[1] + "\n" +
                        words[2] + "\n");
        try {
           execute(message);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText("i know").setCallbackData("update_msg_text"));
            // Set the keyboard to the markup
            rowsInline.add(rowInline);
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //callbacks processing
    private void processCallBacks(CallbackQuery callbackQuery){
        String call_data = callbackQuery.getData();
        long chat_id =callbackQuery.getMessage().getChatId();

        if (call_data.contains("show_usr_words_id_")){
            int st = call_data.indexOf("id_");
            int id_s=Integer.parseInt(call_data.substring(st+3,call_data.length()));
            showUserWords(id_s-1,String.valueOf(chat_id));
        }
    }
    private void showUserWords(int id_start, String usr_id){
        FirebaseUser firebaseUser = MyFirebase.get().getUser(usr_id);
        String msg ="Your words:";
        ArrayList<String> usr_words = (ArrayList<String>) firebaseUser.getMyWords();
        int id_end=id_start-50;
        if (id_start-50<0){
            id_end=0;
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("next").setCallbackData("show_usr_words_id_"+id_end));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        keyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(usr_id);
        message.setText(msg+usr_words_arr_string((ArrayList<String>) firebaseUser.getMyWords(),id_start,id_end));
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void showUserWords(String usr_id){
        FirebaseUser firebaseUser = MyFirebase.get().getUser(usr_id);
        ArrayList<String> usr_words = (ArrayList<String>) firebaseUser.getMyWords();
        showUserWords(usr_words.size()-1,usr_id);
    }


    private String usr_words_arr_string(ArrayList<String> array, int id_start, int id_end){
        String out= "";

        for (int i = id_start; i >=id_end; i--) {
            out=out+"\n"+String.valueOf(i+1)+") "+array.get(i)+"\n";
        }
        return out;
    }
}
