import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardListRequester {
    List<CardRequest> request(CardListRequest request) throws IOException {
        return request(request, false);
    }

    List<CardRequest> request(CardListRequest request, boolean all) throws IOException {
        ArrayList<CardRequest> ret = new ArrayList<CardRequest>();
        do {
            Document doc = Jsoup.connect(request.toUrl()).get();
            Elements cards = doc.select("a.preview");

            if (cards.size() == 0) {
                break;
            }

            for (Element card : cards) {
                CardRequest cardRequest = new CardRequest();
                String[] href = card.attr("href").split("/");

                cardRequest.name = href[href.length - 1];
                cardRequest.id = Integer.parseInt(href[href.length - 2]);

                ret.add(cardRequest);
            }

            if (all) {
                request.page++;
            }
        } while (all);

        return ret;
    }
}
