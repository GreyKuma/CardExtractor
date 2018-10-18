import org.json.JSONObject;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CardJsonWriter {
    private String path;
    private RandomAccessFile file;

    CardJsonWriter(String path) throws IOException {
        this.path = path;
        this.open();
    }

    private void appendString(String str) throws IOException {
        appendString(str, 0);
    }
    private void appendString(String str, int offset) throws IOException {
        byte[] strBytes = str.getBytes("UTF-8");
        file.getChannel().map(
                FileChannel.MapMode.READ_WRITE,
                file.length() + offset, strBytes.length)
                .put(strBytes);
    }

    private void appendBytes(byte[] bytes) throws IOException {
        file.getChannel().map(
                FileChannel.MapMode.READ_WRITE,
                file.length(), bytes.length)
                .put(bytes);
    }

    public void append(Card card) throws IOException {
        JSONObject obj = new JSONObject();

        obj.put("idNumeric", card.idNumeric);
        obj.put("idStr", card.idStr);
        obj.put("name", new JSONObject().put("en", card.name.en).put("de", card.name.de));
        obj.put("edition", new JSONObject().put("en", card.edition.en).put("de", card.edition.de));
        obj.put("type", new JSONObject().put("en", card.type.en).put("de", card.type.de));
        obj.put("race", new JSONObject().put("en", card.race.en).put("de", card.race.de));
        obj.put("attribute", new JSONObject().put("en", card.attribute.en).put("de", card.attribute.de));
        obj.put("ability", new JSONObject().put("en", card.ability.en).put("de", card.ability.de));
        obj.put("flavor", new JSONObject().put("en", card.flavor.en).put("de", card.flavor.de));
        obj.put("rarity", card.rarity);
        obj.put("cost", card.cost);
        obj.put("atk", card.atk);
        obj.put("def", card.def);
        obj.put("imageUrl", card.imageUrl);

        appendString(obj.toString(1));

        appendString(",\n");
    }

    public void close() throws IOException {
        appendString("]\n}", -2);
        file.close();
    }

    public void open() throws IOException {
        file = new RandomAccessFile(this.path, "rw");
        appendString("{\n cards: [\n");
    }
}
