package model;

import com.google.common.base.Strings;
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

    private static MultiLanguageString CardAbilityTypeRest = new MultiLanguageString("Resting", "Ausruhen");
    private static MultiLanguageString CardAbilityTypeActivate = new MultiLanguageString("Activate", "Aktiviert");
    private static MultiLanguageString CardAttributNone = new MultiLanguageString("No attribute", "Ohne Attribut");

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

    private MultiLanguageString proccessString (MultiLanguageString string) {
        Pattern pRest = Pattern.compile("<img src=\"https://www.fowsystem.com/de/img/spossa.jpg\">");
        Matcher mRest = pRest.matcher(string.en);
        while (mRest.find()) {
            String replace = "{{Ability:" + cardAbilityType(CardAbilityTypeRest) + "}}";
            string.en = string.en.replace(mRest.group(0), replace);
            string.de = string.de.replace(mRest.group(0), replace);
        }

        Pattern pAttr = Pattern.compile("<img[^>]*>");
        Matcher mAttr = pAttr.matcher(string.en);
        while (mAttr.find()) {
            String replace = "";
            for (Integer val : processAttributeImg(mAttr.group(0))) {
                replace += "{{Attribute:" + val.toString() + "}}";
            }
            string.en = string.en.replace(mAttr.group(0), replace).trim();
            string.de = string.de.replace(mAttr.group(0), replace).trim();
        }

        Pattern pAttrNone = Pattern.compile("<span class=\"circle-text\">(\\d+)<\\/span>");
        Matcher mAttrNone = pAttrNone.matcher(string.en);
        while (mAttrNone.find()) {
            String replace = Strings.repeat(
                    "{{Attribute:" + cardAttribute(CardAttributNone) + "}}",
                    Integer.parseInt(mAttrNone.group(1)));

            string.en = string.en.replace(mAttrNone.group(0), replace).trim();
            string.de = string.de.replace(mAttrNone.group(0), replace).trim();
        }

        Pattern pAbility = Pattern.compile("<[^<>]+>([^<>\\d\\/]+)<\\/[^<>]+>");
        Matcher mAbility_en = pAbility.matcher(string.en);
        while (mAbility_en.find()) { // && (mAbility_en.start() == 0 || mAbility_en.group(1).charAt(0) == '\n')) {
            Matcher mAbility_de = pAbility.matcher(string.de);
            if (mAbility_de.find()) { // && (mAbility_de.start() == 0 || mAbility_de.group(1).charAt(0) == '\n')) {
                String replace;

                List<CardSimpleProp> types = cardTypes.stream().filter(t -> t.value.en.equals(mAbility_en.group(1))).collect(Collectors.toList());
                if (types.size() > 0) {
                    replace = "{{Type:" + types.get(0).id + "}}";
                } else {
                    replace = "{{Ability:" + cardAbilityType(new MultiLanguageString(
                            mAbility_en.group(1),
                            mAbility_de.group(1)
                    )).toString() + "}}";
                }

                string.en = string.en.replace(mAbility_en.group(0), replace).trim();
                string.de = string.de.replace(mAbility_de.group(0), replace).trim();
            }
        }

        Pattern pMuell = Pattern.compile("<[^>]+>([^<>]+)<\\/[^<>]+>");
        Matcher mMuell = pMuell.matcher(string.en);
        while (mMuell.find()) {
            string.en = string.en.replace(mMuell.group(0), "\"" + mMuell.group(1).trim() + "\"");
        }
        mMuell = pMuell.matcher(string.de);
        while (mMuell.find()) {
            string.de = string.de.replace(mMuell.group(0), "\"" + mMuell.group(1).trim() + "\"");
        }

        return string;
    }

    private List<Integer> processAttributeImg(String string) {
        ArrayList<Integer> ret = new ArrayList<>();

        Document doc = Jsoup.parse(string);
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
                case "oscurita":
                    typeVal = new MultiLanguageString("Darkness", "Finsternis");
                    break;
                case "vento":
                    typeVal = new MultiLanguageString("Wind", "Wind");
                    break;
                case "acqua":
                    typeVal = new MultiLanguageString("Water", "Wasser");
                    break;
                default:
                    System.out.println("processAttributeImg Not found: \"" + type + "\"");
                    typeVal = CardAttributNone;
                    break;
            }

            ret.add(cardAttribute(typeVal));
        }
        return ret;
    }

    private List<CardCostProp> processCardCost(String cost) {
        List<CardCostProp> ret = new ArrayList<>();

        MultiLanguageString c = proccessString(new MultiLanguageString(cost, cost));

        Pattern pCost = Pattern.compile("\\{\\{Attribute:(\\d+)\\}\\}");
        Matcher mCost = pCost.matcher(c.en);
        while (mCost.find()) {
            int typeInt = Integer.parseInt(mCost.group(1));
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

        return ret;
    }

    private List<CardAbilityPropEntry> processCardAbility(MultiLanguageString ability) {
        List<CardAbilityPropEntry> ret = new ArrayList<>();
        ability = proccessString(ability);
        String[] entries_en = ability.en.split("\n");
        String[] entries_de = ability.de.split("\n");

        if (entries_de.length > entries_en.length) {
            String[] newArray = new String[entries_de.length];
            System.arraycopy(entries_en, 0, newArray, 0, entries_en.length);
            entries_en = newArray;
            for (int i = 0; i < entries_en.length; i++) {
                if (entries_en[i] == null) {
                    entries_en[i] = "";
                }
            }
        }
        if (entries_en.length > entries_de.length) {
            String[] newArray = new String[entries_en.length];
            System.arraycopy(entries_de, 0, newArray, 0, entries_de.length);
            entries_de = newArray;
            for (int i = 0; i < entries_de.length; i++) {
                if (entries_de[i] == null) {
                    entries_de[i] = "";
                }
            }
        }


        for (int i = 0; i < entries_en.length; i++) {
            if (entries_en[i] != null && entries_en[i].length() > 0) {
                CardAbilityPropEntry prop = new CardAbilityPropEntry() {{
                    type = 0;
                }};

                Pattern pAbility = Pattern.compile("\\{\\{Ability:(\\d+)\\}\\}");
                Matcher mAbility = pAbility.matcher(entries_en[i]);
                if (mAbility.find() && mAbility.start() == 0) {
                    prop.type = Integer.parseInt(mAbility.group(1));
                    entries_en[i] = entries_en[i].replace(mAbility.group(0), "").trim();
                    entries_de[i] = entries_de[i].replace(mAbility.group(0), "").trim();
                }

                Pattern pAttribute = Pattern.compile("\\{\\{Attribute:(\\d+)\\}\\}");
                Matcher mAttribute = pAttribute.matcher(entries_en[i]);
                if (mAttribute.find() && mAttribute.start() == 0) {
                    prop.type = cardAbilityType(CardAbilityTypeActivate);
                    prop.cost = processCardCost(entries_en[i]);
                    do {
                        entries_en[i] = entries_en[i].replace(mAttribute.group(0), "").trim();
                        entries_de[i] = entries_de[i].replace(mAttribute.group(0), "").trim();
                        mAttribute = pAttribute.matcher(entries_en[i]);
                    } while (mAttribute.find() && mAttribute.start() == 0);
                }

                if (entries_en[i].length() > 0 && entries_de[i].length() > 0) {
                    prop.value = new MultiLanguageString(entries_en[i], entries_de[i]);
                }
                ret.add(prop);
            } else {
                System.out.println("processCardAbility: Empty entry?!?");
            }
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
