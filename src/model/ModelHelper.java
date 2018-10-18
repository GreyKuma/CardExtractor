package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModelHelper {
    public List<CardSimpleProp> cardTypes;
    public List<CardSimpleProp> cardEdition;
    public List<CardSimpleProp> cardRace;
    public List<CardSimpleProp> cardAttribute;
    public List<CardSimpleProp> cardAbilityType;

    public ModelHelper() {
        cardTypes = new ArrayList<>();
        cardEdition = new ArrayList<>();
        cardRace = new ArrayList<>();
        cardAttribute = new ArrayList<>();
        cardAbilityType = new ArrayList<>();

        cardAbilityType.add(new CardSimpleProp(){{
            id = 0;
            value = new MultiLanguageString("Always", "Dauerhaft");
        }});
        cardAbilityType.add(new CardSimpleProp(){{
            id = 1;
            value = new MultiLanguageString("Resting", "Ausruhen");
        }});
        cardAbilityType.add(new CardSimpleProp(){{
            id = 2;
            value = new MultiLanguageString("Activate", "Aktiviert");
        }});
    }

    Integer cardProp(MultiLanguageString valuee, List<CardSimpleProp> src) {
        CardSimpleProp ret;

        if (valuee == null || valuee.en.equals("")) {
            return null;
        }

        List<CardSimpleProp> res = src
                .stream().filter(cardType -> valuee.en.equals(cardType.value.en))
                .collect(Collectors.toList());

        if (res.size() == 0) {
            ret = new CardSimpleProp(){{
                id = src.size();
                value = new MultiLanguageString(valuee.en, valuee.de);
            }};
            src.add(ret);
        } else {
            ret = res.get(0);
        }

        return ret.id;
    }

    Integer cardType(MultiLanguageString value) {
        return cardProp(value, cardTypes);
    }
    Integer cardEdition(MultiLanguageString value) {
        return cardProp(value, cardEdition);
    }
    Integer cardRace(MultiLanguageString value) {
        return cardProp(value, cardRace);
    }
    Integer cardAttribute(MultiLanguageString value) {
        return cardProp(value, cardAttribute);
    }
    Integer cardAbilityType(MultiLanguageString value) {
        return cardProp(value, cardAbilityType);
    }

    private List<MultiLanguageString> extractValue(String en, String de) {
        List<String> list_en = List.of(en.split("/"));
        List<String> list_de = List.of(de.split("/"));
        List<MultiLanguageString> list = new ArrayList<>();
        for (int i = 0; i < list_en.size(); i++) {
            list.add(new MultiLanguageString(list_en.get(i), list_de.get(i)));
        }
        return list;
    }

    private List<CardCostProp> processCardCost(String cost) {
        List<CardCostProp> ret = new ArrayList<>();


        if (cost.matches("-?\\d+(\\.\\d+)?")) {
            int typeInt = cardAttribute(new MultiLanguageString("Any", "Beliebig"));
            ret.add(new CardCostProp() {{
                type = typeInt;
                count = Integer.parseInt(cost);
            }});
            return ret;
        }

        Document doc = Jsoup.parse(cost);
        Elements eles = doc.select("img");
        for (Element img : eles) {
            String[] srcPath = img.attr("src").split("/");
            String type = srcPath[srcPath.length-1].replace(".png", "");
            MultiLanguageString typeVal;

            switch (type) {
                case "luce":
                    typeVal = new MultiLanguageString("Light", "Licht");
                    break;
                case "fuoco":
                    typeVal = new MultiLanguageString("Fire", "Feuer");
                    break;
                default:
                    System.out.println("processCardCost Not found: \"" + type + "\"");
                    typeVal = new MultiLanguageString("Any", "Beliebig");
                    break;
            }

            int typeInt = cardAttribute(typeVal);
            List<CardCostProp> already = ret.stream().filter(r -> r.type == typeInt).collect(Collectors.toList());
            if (already.size() == 0) {
                ret.add(new CardCostProp() {{
                    type = typeInt;
                    count = 1;
                }});
            } else {
                already.get(0).count++;
            }
        }

        eles = doc.select("span");
        for (Element ele : eles) {
            int typeInt = cardAttribute(new MultiLanguageString("Any", "Beliebig"));
            List<CardCostProp> already = ret.stream().filter(r -> r.type == typeInt).collect(Collectors.toList());
            if (already.size() == 0) {
                ret.add(new CardCostProp() {{
                    type = typeInt;
                    count = Integer.parseInt(ele.text());
                }});
            } else {
                already.get(0).count++;
            }
        }

        return ret;
    }

    private List<CardAbilityPropEntry> processCardAbility(MultiLanguageString ability) {
        List<CardAbilityPropEntry> ret = new ArrayList<>();
        String[] entries_en = ability.en.split("\n");
        String[] entries_de = ability.de.split("\n");

        if (entries_de.length > entries_en.length) {
            String[] newArray = new String[entries_de.length];
            System.arraycopy(entries_en, 0, newArray, 0, entries_en.length);
            entries_en = newArray;
        }
        if (entries_en.length > entries_de.length) {
            String[] newArray = new String[entries_en.length];
            System.arraycopy(entries_de, 0, newArray, 0, entries_de.length);
            entries_de = newArray;
        }

        for (int i = 0; i < entries_en.length; i++) {
            CardAbilityPropEntry prop = new CardAbilityPropEntry(){{
                type = 0;
            }};

            Pattern pActivate = Pattern.compile("<span class=\"circle-text\">(\\d+)<\\/span>.?\\s*");
            Matcher mActivate = pActivate.matcher(entries_en[i]);
            if (mActivate.find()) {
                prop.type = 2;
                prop.cost = processCardCost(mActivate.group(1));
                entries_en[i] = entries_en[i].replace(mActivate.group(0), "");
                entries_de[i] = entries_de[i].replace(mActivate.group(0), "");
            } else {
                Pattern pRest = Pattern.compile("<img src=\"https://www.fowsystem.com/de/img/spossa.jpg\">.?\\s*");
                Matcher mRest = pRest.matcher(entries_en[i]);
                if (mRest.find()) {
                    prop.type = 1;
                    entries_en[i] = entries_en[i].replace(mRest.group(0), "");
                    entries_de[i] = entries_de[i].replace(mRest.group(0), "");
                } else {
                    Pattern pCost = Pattern.compile("<img src=\"https://www.fowsystem.com/de/img/([^/>]+).jpg\">.?\\s*");
                    Matcher mCost = pCost.matcher(entries_en[i]);
                    if (mCost.find()) {
                        prop.cost = processCardCost(mCost.group(1));
                        entries_en[i] = entries_en[i].replace(mCost.group(0), "");
                        entries_de[i] = entries_de[i].replace(mCost.group(0), "");
                    } else {
                        Pattern pAddition = Pattern.compile("<[^>]+>(.+)<\\/[^<>]+>\\s*");
                        Matcher mAddition_en = pAddition.matcher(entries_en[i]);
                        if (mAddition_en.find() && (
                            mAddition_en.group(0).length() == entries_en[i].length() ||
                            mAddition_en.start() == 0
                        )) {
                            Matcher mAddition_de = pAddition.matcher(entries_de[i]);
                            if (mAddition_de.find()) {
                                prop.type = cardAbilityType(new MultiLanguageString(
                                        mAddition_en.group(1),
                                        mAddition_de.group(1)));
                                if (mAddition_en.group(0).length() == entries_en[i].length()) {
                                    if (entries_en.length < i+1) {
                                        i++;
                                    } else {
                                        break;
                                    }
                                } else {
                                    entries_en[i] = entries_en[i].replace(mAddition_en.group(0), "");
                                    entries_de[i] = entries_de[i].replace(mAddition_de.group(0), "");
                                }
                            }
                        }
                    }
                }
            }

            Pattern pMuell = Pattern.compile("<[^>]+>(.+)<\\/[^<>]+>");
            Matcher mMuell = pMuell.matcher(entries_en[i]);
            while (mMuell.find()) {
                entries_en[i] = entries_en[i].replace(mMuell.group(0), "\"" + mMuell.group(1) + "\"");
            }
            mMuell = pMuell.matcher(entries_de[i]);
            while (mMuell.find()) {
                entries_de[i] = entries_de[i].replace(mMuell.group(0), "\"" + mMuell.group(1) + "\"");
            }

            prop.value = new MultiLanguageString(entries_en[i], entries_de[i]);

            ret.add(prop);
        }

        return ret;
    }

    public Card processCardRaw(CardRaw raw) {
        Card card = new Card(){{
            idNumeric = raw.idNumeric;
            idStr = raw.idStr;
            rarity = raw.rarity;
            imageUrl = raw.imageUrl;
            name = raw.name;
            atk = raw.atk;
            def = raw.def;
            flavor = raw.flavor;
        }};

        if (card.flavor != null && card.flavor.en.equals("")) {
            card.flavor.en = null;
        }
        if (card.flavor != null && card.flavor.de.equals("")) {
            card.flavor.de = null;
        }
        if(card.flavor == null || (card.flavor.en == null && card.flavor.de == null)) {
            card.flavor = null;
        }

        card.edition = cardEdition(raw.edition);

        card.type = new ArrayList<>();
        extractValue(raw.type.en, raw.type.de).forEach(t -> card.type.add(cardType(t)));
        if (card.type.stream().noneMatch(Objects::nonNull)) {
            card.type = null;
        }

        card.race = new ArrayList<>();
        extractValue(raw.race.en, raw.race.de).forEach(t -> card.race.add(cardRace(t)));
        if (card.race.stream().noneMatch(Objects::nonNull)) {
            card.race = null;
        }

        card.attribute = new ArrayList<>();
        extractValue(raw.attribute.en, raw.attribute.de).forEach(t -> card.attribute.add(cardAttribute(t)));
        if (card.attribute.stream().noneMatch(Objects::nonNull)) {
            card.attribute = null;
        }

        if (raw.ability.en.length() > 0 || raw.ability.de.length() > 0) {
            card.ability = processCardAbility(raw.ability);
        }

        if (raw.cost != null && raw.cost.length() > 0) {
            card.cost = processCardCost(raw.cost);
        }

        return card;
    }
}
