

import model.CardRaw;

import java.io.File;
import java.io.IOException;
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
            json = new CardJsonWriter("newWalhalla.json");
            fire = null; //new CardFirebaseWriter("fowtest-f30af-service-account.json");

            CardListRequest cardListRequest = new CardListRequest();
            List<CardRequest> cardRequests = null;

            do {
                System.out.println(" Request page " + cardListRequest.page);
                List<CardRaw> cardRaws = new CardRequester().request(
                        cardRequests = new CardListRequester().request(
                                cardListRequest
                        )
                );

                System.out.println("  Got " + cardRaws.size() + " cardRaws:");

                for(int i = 0; i < cardRaws.size(); i++) {
                    if (csv != null) {
                        csv.append(cardRaws.get(i));
                    }
                    if (json != null) {
                        json.append(cardRaws.get(i));
                    }
                    if(fire != null) {
                        long time = fire.append(cardRaws.get(i));
                        System.out.println("   [" + i + "]:{" + Long.toString(time) + "} \"" + cardRaws.get(i).name.de + "\" | \"" +
                                cardRaws.get(i).name.en + "\"");
                    } else {

                        System.out.println("   [" + i + "]: \"" + cardRaws.get(i).name.de + "\" | \"" +
                                cardRaws.get(i).name.en + "\"");
                    }
                    // database.getRoot().push().setValue(cardRaws.get(i), null);
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
                try {
                    json.close();
                } catch(Exception e){}
            }
            if (fire != null) {
                try {
                    fire.close();
                } catch(Exception e){}
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


