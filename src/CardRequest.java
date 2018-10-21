public class CardRequest {
    public static final String CardUrl = "https://www.fowsystem.com/de/Kartendatenbank/";

    public int id;
    public String name;

    public String toUrl() {
        return new StringBuilder(CardUrl)
                .append(id)
                .append('/')
                .append(name)
                .toString();
    }
}
