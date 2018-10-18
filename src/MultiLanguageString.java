import java.io.Serializable;

public class MultiLanguageString implements Serializable {
    String de;
    String en;

    MultiLanguageString(){
        this("","");
    }
    MultiLanguageString(String en, String de){
        this.en = en;
        this.de = de;
    }

}
