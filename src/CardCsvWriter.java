
import model.CardRaw;

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

    public void append(CardRaw cardRaw) throws IOException {
        appendString(new StringBuilder()
                .append(cardRaw.idNumeric).append(";\"")
                .append(cardRaw.idStr).append("\";\"")
                .append(cardRaw.name.en).append("\";\"")
                .append(cardRaw.name.de).append("\";\"")
                .append(cardRaw.edition.en).append("\";\"")
                .append(cardRaw.edition.de).append("\";\"")
                .append(cardRaw.type.en).append("\";\"")
                .append(cardRaw.type.de).append("\";\"")
                .append(cardRaw.race.en).append("\";\"")
                .append(cardRaw.race.de).append("\";\"")
                .append(cardRaw.attribute.en.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.attribute.de.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.ability.en.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.ability.de.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.flavor.en.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.flavor.de.replace("\"", "\\\"")).append("\";\"")
                .append(cardRaw.rarity).append("\";")
                .append(cardRaw.cost).append(';')
                .append(cardRaw.atk).append(';')
                .append(cardRaw.def).append(";\"")
                .append(cardRaw.imageUrl).append("\"\n")
        .toString());
    }

    public void close() throws IOException {
        file.close();
    }

    public void open() throws IOException {
        file = new RandomAccessFile(this.path, "rw");
    }
}
