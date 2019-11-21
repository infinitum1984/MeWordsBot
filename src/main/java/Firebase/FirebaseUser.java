package Firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class FirebaseUser {
    private List<String> allWords;
    private List<String> myWords;
    private String id;

    public FirebaseUser(String usr_id) {
        allWords = new ArrayList<>();
        myWords = new ArrayList<>();
        id = usr_id;
    }


    public String[] generateWords() {
        int max = allWords.size() - 1;
        String[] new_words = new String[3];
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        Random randomGenerator = new Random();
        while (numbers.size() < 3) {

            int random = randomGenerator.nextInt(max);
            if (!numbers.contains(random)) {
                numbers.add(random);


            }
        }
        if (numbers.get(0) < numbers.get(1)) {
            int i = numbers.get(0);
            numbers.set(0, numbers.get(1));
            numbers.set(1, i);
        }
        if (numbers.get(1) < numbers.get(2)) {
            int i = numbers.get(1);
            numbers.set(1, numbers.get(2));
            numbers.set(2, i);
        }
        int j = 0;
        for (int i : numbers) {
            new_words[j] = allWords.get(i);
            myWords.add(allWords.get(i));

            allWords.remove(i);
            j++;
        }

        return new_words;

    }


    public void setAllWords(List<String> allWords) {
        this.allWords = allWords;
    }

    public void setMyWords(List<String> myWords) {
        this.myWords = myWords;
    }

    public List<String> getAllWords() {
        return allWords;
    }

    public List<String> getMyWords() {
        return myWords;
    }

    public String getId() {
        return id;
    }
}
