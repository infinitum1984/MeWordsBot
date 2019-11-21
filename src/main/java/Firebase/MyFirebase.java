package Firebase;

import Config.Config;
import Words.WordsBank;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
public class MyFirebase {
   static private String DATABASE_NAME="https://mewords-1d958.firebaseio.com/";
   private static MyFirebase myFirebase;
   private MyFirebase(){

       FirebaseOptions options = null;
       FileInputStream refreshToken = null;

       try {
           if (Config.IS_TEST){
               refreshToken = new FileInputStream("src/main/resources/fire_keys/mewords-test.json");//test

           }else {
               refreshToken = new FileInputStream("src/main/resources/fire_keys/mewords.json");//main

           }


       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       if (Config.IS_TEST){ DATABASE_NAME = "https://mewords-test.firebaseio.com/";}

       System.out.println(DATABASE_NAME);
       try {
           options = new FirebaseOptions.Builder()
                   .setCredentials(GoogleCredentials.fromStream(refreshToken))
                   .setDatabaseUrl(DATABASE_NAME)

                   .build();
       } catch (IOException e) {
           e.printStackTrace();
       }

       FirebaseApp.initializeApp(options);
   }

   public static MyFirebase get(){
       if (myFirebase==null)
           myFirebase = new MyFirebase();

       return myFirebase;

   }



    public FirebaseUser create_user(String usrID){
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> attend = new HashMap<>();
        attend.put("words_bank", Arrays.asList(WordsBank.get().getAllWords())) ;
        attend.put("notification",true);
        db.collection("users").document(usrID).set(attend);
        return getUser(usrID);
    }
    public boolean userExist(String usrID){
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<DocumentSnapshot> docIdRef = db.collection("users").document(usrID).get();                //MyFirebase.get().create_user(usrId);
        boolean exsit = false;
        try { exsit=docIdRef.get().exists();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return exsit;
    }

    public List<String> getUsers(){
        ArrayList<String> documentSnapshots = new ArrayList<>();

        Firestore db = FirestoreClient.getFirestore();
        Iterable<DocumentReference> usr_doc_ref = db.collection("users").listDocuments();
        for(DocumentReference doc : usr_doc_ref){
            ApiFuture<DocumentSnapshot> future = doc.get();
            String usr_document=null;
            try {
                usr_document=future.get().getId();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            documentSnapshots.add(usr_document);

        }
       return documentSnapshots;

    }
    public FirebaseUser getUser(String usrID){
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> future = db.collection("users").document(usrID).get();
        FirebaseUser usr=null;
        try {
            var doc = future.get();
            usr = new FirebaseUser(usrID);
            usr.setMyWords((List<String>) doc.get("my_words"));
            usr.setAllWords((List<String>) doc.get("words_bank"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return usr;
    }
    public void updateUser(String usr_id, FirebaseUser usr){

        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> attend = new HashMap<>();
        String[] all_w = new String[usr.getAllWords().size()];
        String[] m_w = new String[usr.getMyWords().size()];
        for (int i = 0; i < usr.getAllWords().size(); i++) {
            all_w[i] = usr.getAllWords().get(i);
        }
        for (int i = 0; i < usr.getMyWords().size(); i++) {
            m_w[i] =usr.getMyWords().get(i);
        }
        attend.put("my_words", Arrays.asList(m_w));
        db.collection("users").document(usr_id).set(attend);
        attend = new HashMap<>();
        attend.put("words_bank", Arrays.asList(all_w));
        db.collection("users").document(usr_id).update(attend);

    }
}
