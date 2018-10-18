import java.io.Serializable;

public class Card implements Serializable {
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

    public int cost;
    public int atk;
    public int def;

    public Card() {
        name = new MultiLanguageString();
        edition = new MultiLanguageString();
        type = new MultiLanguageString();
        race = new MultiLanguageString();
        attribute = new MultiLanguageString();
        ability = new MultiLanguageString();
        flavor = new MultiLanguageString();
    }
}
