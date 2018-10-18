import java.io.Serializable;

public class MultiLanguageString implements Serializable {
    public String de;
    public String en;

    MultiLanguageString(){
        this("","");
    }
    MultiLanguageString(String en, String de){
        this.en = en;
        this.de = de;
    }

}
