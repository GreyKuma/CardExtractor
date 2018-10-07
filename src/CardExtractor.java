import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CardExtractor {

    public static void main(String[] args) throws IOException{
        String walhallaUrl = "https://www.fowsystem.com/de/Kartendatenbank?order=insasc&page=&CERCA=cerca&cardname=&block=ALL&edition=Die+Wiedergeburt+von+Walhalla&REGATT=or&cardnumber=&ABILITYTEXT=&ATKMIN=0&ATKMAX=2500&DEFMIN=0&DEFMAX=2500";
        String walhallaStarterDecksUrl = "https://www.fowsystem.com/de/Kartendatenbank?order=insasc&page=&CERCA=cerca&cardname=&block=ALL&edition=Neu-Walhalla-Starterdeck&REGATT=or&cardnumber=&ABILITYTEXT=&ATKMIN=0&ATKMAX=2500&DEFMIN=0&DEFMAX=2500";
//        Document doc = Jsoup.connect(walhallaUrl).get();
        Document doc = Jsoup.connect(walhallaStarterDecksUrl).get();
        System.out.println(doc.title());

        String nameQuery = ".box_nomecarta span";
        String elementQuery = ".preview";
        String idQuery = ".box_idcarta";
        Elements cardNames = doc.select(elementQuery);
//        for (Element cardName : cardNames) {
//            log("%s: %s", cardName.select(idQuery).text(),cardName.select(nameQuery).text());
//        }
        ArrayList<String> data = new ArrayList<>();
        for (Element card : cardNames) {
            data.add(card.select(nameQuery).text());
            data.add(card.select(idQuery).text());
            data.add("https://www.fowsystem.com" + card.attr("href"));
        }

        CSV csv = new CSV("newWalhalla.csv", 3);
        String[] headers = {"Kartenname", "ID", "Link"};
        csv.append(headers);
        System.out.println("Adding data...");
        csv.append(data.toArray(headers));
        csv.close();
        System.out.println("Done!");

    }
}


