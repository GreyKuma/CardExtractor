package model;

import java.io.Serializable;

public class CardRaw implements Serializable {
    public int idNumeric;
    public String idStr;

    public MultiLanguageString name;
    public MultiLanguageString edition;
    public MultiLanguageString type;
    public MultiLanguageString race;
    public MultiLanguageString attribute;
    public MultiLanguageString ability;
    public MultiLanguageString flavor;

    public String rarity;
    public String imageUrl;
    public String cost;

    public int atk;
    public int def;

    public CardRaw() {
        name = new MultiLanguageString();
        edition = new MultiLanguageString();
        type = new MultiLanguageString();
        race = new MultiLanguageString();
        attribute = new MultiLanguageString();
        ability = new MultiLanguageString();
        flavor = new MultiLanguageString();
    }
}
