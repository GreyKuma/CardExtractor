
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CardCsvWriter {
    private String path;
    private RandomAccessFile file;

    public CardCsvWriter(String path) throws IOException {
        this.path = path;
        this.open();
        this.appendHeader();
    }

    private void appendString(String str) throws IOException {
        byte[] strBytes = str.getBytes("UTF-8");
        file.getChannel().map(
                FileChannel.MapMode.READ_WRITE,
                file.length(), strBytes.length)
                .put(strBytes);
    }

    private void appendHeader() throws IOException {
        appendString("idNumeric;idStr;name_en;name_de;edition_en;edition_de;type_en;type_de;race_en;race_de;attribute_en;attribute_de;ability_en;ability_de;flavor_en;flavor_de;rarity;cost;atk;dev;imageUrl\n");
    }

    public void append(Card card) throws IOException {
        appendString(new StringBuilder()
                .append(card.idNumeric).append(";\"")
                .append(card.idStr).append("\";\"")
                .append(card.name.en).append("\";\"")
                .append(card.name.de).append("\";\"")
                .append(card.edition.en).append("\";\"")
                .append(card.edition.de).append("\";\"")
                .append(card.type.en).append("\";\"")
                .append(card.type.de).append("\";\"")
                .append(card.race.en).append("\";\"")
                .append(card.race.de).append("\";\"")
                .append(card.attribute.en.replace("\"", "\\\"")).append("\";\"")
                .append(card.attribute.de.replace("\"", "\\\"")).append("\";\"")
                .append(card.ability.en.replace("\"", "\\\"")).append("\";\"")
                .append(card.ability.de.replace("\"", "\\\"")).append("\";\"")
                .append(card.flavor.en.replace("\"", "\\\"")).append("\";\"")
                .append(card.flavor.de.replace("\"", "\\\"")).append("\";\"")
                .append(card.rarity).append("\";")
                .append(card.cost).append(';')
                .append(card.atk).append(';')
                .append(card.def).append(";\"")
                .append(card.imageUrl).append("\"\n")
        .toString());
    }

    public void close() throws IOException {
        file.close();
    }

    public void open() throws IOException {
        file = new RandomAccessFile(this.path, "rw");
    }
}
