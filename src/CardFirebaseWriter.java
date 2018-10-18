import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseReference;
import model.Card;
import model.CardRaw;
import model.CardSimpleProp;
import model.ModelHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CardFirebaseWriter {
    private static final String DATABASE_URL = "https://fowtest-f30af.firebaseio.com/";
    private static String name;
    private static Firestore database;
    private static CollectionReference collection;
    private static DatabaseReference ref;

    private static ModelHelper modelHelper;

    CardFirebaseWriter(String pathToServiceAccountKey) throws IOException {
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(pathToServiceAccountKey);
        } catch (IOException e) {
            throw new IOException("Make sure to have a ServiceAccountFile.\n" +
                    "Get one from the console:\n" +
                    "See https://firebase.google.com/docs/admin/setup#initialize_the_sdk", e);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(DATABASE_URL)
                .build();

        FirebaseApp.initializeApp(options);
        database = FirestoreClient.getFirestore();

        name = "ImportTest_" + new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        collection = database.collection(name);

        modelHelper = new ModelHelper();
    }

    public long append(CardRaw cardRaw) throws ExecutionException, InterruptedException {
        Card card = modelHelper.processCardRaw(cardRaw);
        ApiFuture<WriteResult> result = collection
                .document("data")
                .collection("Cards")
                .document(card.idStr)
                .set(card);

        return result.get().getUpdateTime().getSeconds();
    }

    public void close() throws Exception {
        System.out.println("  Insert CardTypes:");
        for (CardSimpleProp cardSimpleProp : modelHelper.cardTypes) {
            System.out.println("   [" + cardSimpleProp.id + "]:{" +
            collection
                    .document("data")
                    .collection("CardType")
                    .document(Integer.toString(cardSimpleProp.id))
                    .set(cardSimpleProp.value).get().getUpdateTime() +
                    "} \"" + cardSimpleProp.value.de + "\" | \"" + cardSimpleProp.value.en + "\"");
        }

        System.out.println("  Insert CardEdition:");
        for (CardSimpleProp cardSimpleProp : modelHelper.cardEdition) {
            System.out.println("   [" + cardSimpleProp.id + "]:{" +
            collection
                    .document("data")
                    .collection("CardEdition")
                    .document(Integer.toString(cardSimpleProp.id))
                    .set(cardSimpleProp.value).get().getUpdateTime() +
                    "} \"" + cardSimpleProp.value.de + "\" | \"" + cardSimpleProp.value.en + "\"");
        }

        System.out.println("  Insert CardAttributes:");
        for (CardSimpleProp cardSimpleProp : modelHelper.cardAttribute) {
            System.out.println("   [" + cardSimpleProp.id + "]:{" +
                collection
                        .document("data")
                        .collection("CardAttribute")
                        .document(Integer.toString(cardSimpleProp.id))
                        .set(cardSimpleProp.value).get().getUpdateTime() +
                    "} \"" + cardSimpleProp.value.de + "\" | \"" + cardSimpleProp.value.en + "\"");
        }

        System.out.println("  Insert CardRace:");
        for (CardSimpleProp cardSimpleProp : modelHelper.cardRace) {
            System.out.println("   [" + cardSimpleProp.id + "]:{" +
            collection
                    .document("data")
                    .collection("CardRace")
                    .document(Integer.toString(cardSimpleProp.id))
                    .set(cardSimpleProp.value).get().getUpdateTime() +
                    "} \"" + cardSimpleProp.value.de + "\" | \"" + cardSimpleProp.value.en + "\"");
        }

        System.out.println("  Insert CardAbilityType:");
        for (CardSimpleProp cardSimpleProp : modelHelper.cardAbilityType) {
            System.out.println("   [" + cardSimpleProp.id + "]:{" +
                    collection
                            .document("data")
                            .collection("CardRace")
                            .document(Integer.toString(cardSimpleProp.id))
                            .set(cardSimpleProp.value).get().getUpdateTime() +
                    "} \"" + cardSimpleProp.value.de + "\" | \"" + cardSimpleProp.value.en + "\"");
        }

        database.close();
    }
}
