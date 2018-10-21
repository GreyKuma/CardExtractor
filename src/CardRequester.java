import model.CardRaw;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CardRequester {
    public CardRaw request (CardRequest request) throws IOException {
        CardRaw ret = new CardRaw();
        ret.idNumeric = request.id;

        Document doc = Jsoup.parse(new URL(request.toUrl()).openStream(), "UTF-8", request.toUrl()); /// Jsoup.connect(request.toUrl()).get();
        Element head_en = doc.select("#right_dettcarta #inglese").first();
        Element head_de = doc.select("#right_dettcarta #local").first();

        Element id = doc.selectFirst("#left_dettcarta .tit_carta");
        if (id != null) {
            ret.idStr = id.text();
        }

        Element img = doc.selectFirst("#left_dettcarta img");
        if (img != null) {
            ret.imageUrl = img.attr("src");
        }

        if (head_en != null && head_de != null) {
            final String title = "tit_carta";
            final String desc = "desc_carta";

            Element last_data_en = head_en.selectFirst("p").children().first(), cur_data_en = last_data_en;
            Element last_data_de = head_de.selectFirst("p").children().first(), cur_data_de = last_data_de;

            do {
                if (cur_data_en.text().toLowerCase().startsWith("name")) {
                    ret.name.en = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("edition")) {
                    ret.edition.en = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("type")) {
                    ret.type.en = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("race")) {
                    ret.race.en = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("cost")) {
                    ret.cost = (cur_data_en = cur_data_en.nextElementSibling()).html();
                } else if (cur_data_en.text().toLowerCase().startsWith("atk")) {
                    ret.atk = Integer.parseInt((cur_data_en = cur_data_en.nextElementSibling()).text());
                } else if (cur_data_en.text().toLowerCase().startsWith("def")) {
                    ret.def = Integer.parseInt((cur_data_en = cur_data_en.nextElementSibling()).text());
                } else if (cur_data_en.text().toLowerCase().startsWith("attribute")) {
                    ret.attribute.en = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("rarity")) {
                    ret.rarity = (cur_data_en = cur_data_en.nextElementSibling()).text();
                } else if (cur_data_en.text().toLowerCase().startsWith("ability")) {
                    cur_data_en = cur_data_en.parent().nextElementSibling().children().first();
                    do {
                        if (ret.ability.en.length() > 0 && cur_data_en.html().length() > 0) {
                            ret.ability.en += "\n";
                        }
                        ret.ability.en += cur_data_en.html();
                    } while (
                        (
                            cur_data_en.nextElementSibling() != null &&
                            !cur_data_en.nextElementSibling().text().toLowerCase().contains("flavor") &&
                            (cur_data_en = cur_data_en.nextElementSibling()) != null
                        ) || (
                            cur_data_en.parent().nextElementSibling() != null &&
                            !cur_data_en.parent().nextElementSibling().text().toLowerCase().contains("flavor") &&
                            (cur_data_en = cur_data_en.parent().nextElementSibling().children().first()) != null
                        )
                    );

                    /*img = (cur_data_en = cur_data_en.parent().nextElementSibling()).selectFirst("img");
                    if (img != null) {
                        ret.ability.en += "<IMG>" + img.attr("src") + "</IMG>";
                    }
                    ret.ability.en += cur_data_en.text();
                    while (
                            cur_data_en.nextElementSibling() != null &&
                            !cur_data_en.nextElementSibling().text().toLowerCase().contains("flavor")) {
                        img = (cur_data_en = cur_data_en.nextElementSibling()).selectFirst("img");
                        if (img != null) {
                            ret.ability.en += "\n<IMG>" + img.attr("src") + "</IMG>\n";
                        } else {
                            ret.ability.en += "\n";
                        }
                        ret.ability.en += cur_data_en.text();
                    }
                    cur_data_en = cur_data_en.children().last();*/
                } else if (cur_data_en.text().toLowerCase().startsWith("flavor")) {
                    ret.flavor.en = (cur_data_en = cur_data_en.parent().nextElementSibling().children().last()).text();
                    while (cur_data_en.parent().nextElementSibling() != null) {
                        ret.flavor.en += "\n" + (cur_data_en = cur_data_en.parent().nextElementSibling()).text();
                    }
                }

                last_data_en = cur_data_en;
            } while (
                (cur_data_en = cur_data_en.nextElementSibling()) != null || (
                    last_data_en.parent().nextElementSibling() != null && (
                        (
                            last_data_en.parent().nextElementSibling().children().size() == 0 &&
                            last_data_en.parent().nextElementSibling().nextElementSibling() != null &&
                            (cur_data_en = last_data_en.parent().nextElementSibling().nextElementSibling().children().first()) != null
                        ) || (
                            last_data_en.parent().nextElementSibling() != null &&
                            (cur_data_en = last_data_en.parent().nextElementSibling().children().first()) != null
                        )
                    )
                )
            );

            do {
                if (cur_data_de.text().toLowerCase().startsWith("kartenname")) {
                    ret.name.de = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("set")) {
                    ret.edition.de = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("kartentyp")) {
                    ret.type.de = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("eigenschaften")) {
                    ret.race.de = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("kosten")) {
                    ret.cost = (cur_data_de = cur_data_de.nextElementSibling()).html();
                } else if (cur_data_de.text().toLowerCase().startsWith("atk")) {
                    ret.atk = Integer.parseInt((cur_data_de = cur_data_de.nextElementSibling()).text());
                } else if (cur_data_de.text().toLowerCase().startsWith("def")) {
                    ret.def = Integer.parseInt((cur_data_de = cur_data_de.nextElementSibling()).text());
                } else if (cur_data_de.text().toLowerCase().startsWith("attribut")) {
                    ret.attribute.de = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("seltenheit")) {
                    ret.rarity = (cur_data_de = cur_data_de.nextElementSibling()).text();
                } else if (cur_data_de.text().toLowerCase().startsWith("fähigkeit")) {
                    cur_data_de = cur_data_de.parent().nextElementSibling().children().first();
                    do {
                        if (ret.ability.de.length() > 0 && cur_data_de.html().length() > 0) {
                            ret.ability.de += "\n";
                        }
                        ret.ability.de += cur_data_de.html();
                    } while (
                        (
                            cur_data_de.nextElementSibling() != null &&
                            !cur_data_de.nextElementSibling().text().toLowerCase().contains("stimmungstext") &&
                            (cur_data_de = cur_data_de.nextElementSibling()) != null
                        ) || (
                            cur_data_de.parent().nextElementSibling() != null &&
                            !cur_data_de.parent().nextElementSibling().text().toLowerCase().contains("stimmungstext") &&
                            (cur_data_de = cur_data_de.parent().nextElementSibling().children().first()) != null
                        )
                    );

                    /*
                        while (cur_data_de.is("img")) {
                        ret.ability.de += "<IMG>" + img.attr("src") + "</IMG>\n";
                        cur_data_de = cur_data_de.nextElementSibling();
                    }
                    ret.ability.de += cur_data_de.text();
                    while (
                        cur_data_de.nextElementSibling() != null &&
                        !cur_data_de.nextElementSibling().text().toLowerCase().contains("stimmungstext")) {
                        img = (cur_data_de = cur_data_de.nextElementSibling()).selectFirst("img");
                        if (img != null) {
                            ret.ability.de += "\n<IMG>" + img.attr("src") + "</IMG>";
                        } else {
                            ret.ability.de += "\n";
                        }
                        ret.ability.de += cur_data_de.text();
                    }
                    cur_data_de = cur_data_de.children().last(); */
                } else if (cur_data_de.text().toLowerCase().startsWith("stimmungstext")) {
                    ret.flavor.de = (cur_data_de = cur_data_de.parent().nextElementSibling()).text();
                    while (cur_data_de.nextElementSibling() != null) {
                        ret.flavor.de += "\n" + (cur_data_de = cur_data_de.nextElementSibling()).text();
                    }
                    cur_data_de = cur_data_de.children().first();
                }

                last_data_de = cur_data_de;
            } while (
                (cur_data_de = cur_data_de.nextElementSibling()) != null || (
                    last_data_de.parent().nextElementSibling() != null && (
                        (
                            last_data_de.parent().nextElementSibling().children().size() == 0 &&
                            last_data_de.parent().nextElementSibling().nextElementSibling() != null &&
                            (cur_data_de = last_data_de.parent().nextElementSibling().nextElementSibling().children().first()) != null
                        ) || (
                            last_data_de.parent().nextElementSibling() != null &&
                            (cur_data_de = last_data_de.parent().nextElementSibling().children().first()) != null
                        )
                    )
                )
            );
        } else {
            throw new IOException("No card-data found.");
        }
        return ret;
    }

    public List<CardRaw> request (List<CardRequest> requests) throws IOException {
        ArrayList<CardRaw> ret = new ArrayList<CardRaw>();
        for (CardRequest request: requests) {
            try {
                ret.add(request(request));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
