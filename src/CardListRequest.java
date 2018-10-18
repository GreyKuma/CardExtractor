public class CardListRequest {
    public static final String CardListUrl = "https://www.fowsystem.com/de/Kartendatenbank";

    public int page = 1;
    public String order = "desc";
    public String regatt = "or";

    public String cardname = "";
    public String block = "ALL";
    public String edition = "ALL";
    public String cardnumber = "";
    public String abilitytext = "";

    public int atkMin = 0;
    public int atkMax = 2500;

    public int defMin = 0;
    public int defMax = 2500;

    public String toUrl() {
        return new StringBuilder(CardListUrl)
                .append("?page=").append(this.page)
                .append("&order=").append(this.order)
                .append("?regatt=").append(this.regatt)
                .append("&cardname=").append(this.cardname)
                .append("&block=").append(this.block)
                .append("&edition=").append(this.edition)
                .append("&cardnumber=").append(this.cardnumber)
                .append("&abilitytext=").append(this.abilitytext)
                .append("&atkMin=").append(this.atkMin)
                .append("&atkMax=").append(this.atkMax)
                .append("&defMin=").append(this.defMin)
                .append("&defMax=").append(this.defMax)
                .toString();
    }
}
