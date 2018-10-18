

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class main {
    // private static DatabaseReference database;

    public static void main(String[] args) throws IOException{
        boolean all = true;

        CardCsvWriter csv = null;
        CardJsonWriter json = null;
        CardFirebaseWriter fire = null;

        try {
            System.out.println("Starting:");

            File f = new File("newWalhalla.csv");
            if (f.exists()) {
                f.delete();
            }
            f = new File("newWalhalla.json");
            if (f.exists()) {
                f.delete();
            }

            csv = null;
            json = null; // new CardJsonWriter("newWalhalla.json");
            fire = new CardFirebaseWriter("fowtest-f30af-service-account.json");

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

                    if (csv != null) {
                        csv.append(cards.get(i));
                    }
                    if (json != null) {
                        json.append(cards.get(i));
                    }
                    if(fire != null) {
                        fire.append(cards.get(i));
                    }
                    // database.getRoot().push().setValue(cards.get(i), null);
                }

                cardListRequest.page++;
            } while(all && cardRequests != null && cardRequests.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                csv.close();
            }
            if (json != null) {
                json.close();
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


