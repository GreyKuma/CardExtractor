package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {
    public int idNumeric;
    public String idStr;

    public MultiLanguageString name;
    public MultiLanguageString flavor;

    public List<CardAbilityPropEntry> ability;

    public String rarity;
    public String imageUrl;

    public List<CardCostProp> cost;
    public int atk;
    public int def;

    public Integer edition;

    public List<Integer> type;
    public List<Integer> race;
    public List<Integer> attribute;

    public Card() {
        /*
        name = new MultiLanguageString();
        flavor = new MultiLanguageString();
        type = new ArrayList<>();
        race = new ArrayList<>();
        attribute = new ArrayList<>();*/
    }
}
