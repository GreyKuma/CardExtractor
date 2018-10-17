

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class main {
    // private static DatabaseReference database;

    public static void main(String[] args) throws IOException{
        CardCsvWriter csv = null;

        try {
            System.out.println("Starting:");
            // initFirebase();

            File f = new File("newWalhalla.csv");
            if (f.exists()) {
                f.delete();
            }

            csv = new CardCsvWriter("newWalhalla.csv");
            CardListRequest cardListRequest = new CardListRequest();
            List<CardRequest> cardRequests = null;

            do {
                System.out.println(" Request page " + cardListRequest.page);
                List<Card> cards = new CardRequester().request(
                        cardRequests = new CardListRequester().request(
                                cardListRequest
                        )
                );

                System.out.println("  Got " + cards.size() + " cards:");

                for(int i = 0; i < cards.size(); i++) {
                    System.out.println("   [" + i + "]: \"" + cards.get(i).name.de + "\" | \"" +
                            cards.get(i).name.en + "\"");

                    csv.append(cards.get(i));
                    // database.getRoot().push().setValue(cards.get(i), null);
                }

                cardListRequest.page++;
            } while(cardRequests != null && cardRequests.size() > 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                csv.close();
            }
        }

        System.out.println("Done!");

    }

    /*
    private static void initFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("fowtest-f30af-firebase-adminsdk-5m9ju-646ac26974.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fowtest-f30af.firebaseio.com/")
                .build();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance("CardsImport").getReference();
    }*/
}


