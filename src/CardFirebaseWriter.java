import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class CardFirebaseWriter {
    private static final String DATABASE_URL = "https://fowtest-f30af.firebaseio.com/";
    private static Firestore database;
    private static CollectionReference collection;

    CardFirebaseWriter(String pathToServiceAccountKey) throws IOException {
        FileInputStream serviceAccount = new FileInputStream(pathToServiceAccountKey);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(DATABASE_URL)
                .build();

        FirebaseApp.initializeApp(options);
        database = FirestoreClient.getFirestore();

        collection = database.collection("CardImportTest_" + new SimpleDateFormat(
                "yyMMdd"
        ).format(new Date()));
    }

    public void append(Card card) {
        collection.document(card.idStr).set(card);
    }

}
