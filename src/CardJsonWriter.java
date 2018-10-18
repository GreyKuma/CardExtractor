import model.Card;
import model.CardRaw;
import model.ModelHelper;
import org.json.JSONObject;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CardJsonWriter {
    private String path;
    private RandomAccessFile file;
    private ModelHelper helper;

    CardJsonWriter(String path) throws IOException {
        this.path = path;
        this.helper = new ModelHelper();
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

        obj.put("edition", card.edition);
        obj.put("type", card.type);
        obj.put("race", card.race);

        obj.put("attribute", card.attribute);
        obj.put("ability", card.ability);

        if (card.flavor != null) {
            obj.put("flavor", new JSONObject().put("en", card.flavor.en).put("de", card.flavor.de));
        }
        obj.put("rarity", card.rarity);
        obj.put("cost", card.cost);
        obj.put("atk", card.atk);
        obj.put("def", card.def);
        obj.put("imageUrl", card.imageUrl);

        appendString(obj.toString(1));

        appendString(",\n");
    }

    public void append(CardRaw cardRaw) throws IOException {
        append(helper.processCardRaw(cardRaw));
        /*
        JSONObject obj = new JSONObject();

        obj.put("idNumeric", cardRaw.idNumeric);
        obj.put("idStr", cardRaw.idStr);
        obj.put("name", new JSONObject().put("en", cardRaw.name.en).put("de", cardRaw.name.de));
        obj.put("edition", new JSONObject().put("en", cardRaw.edition.en).put("de", cardRaw.edition.de));
        obj.put("type", new JSONObject().put("en", cardRaw.type.en).put("de", cardRaw.type.de));
        obj.put("race", new JSONObject().put("en", cardRaw.race.en).put("de", cardRaw.race.de));
        obj.put("attribute", new JSONObject().put("en", cardRaw.attribute.en).put("de", cardRaw.attribute.de));
        obj.put("ability", new JSONObject().put("en", cardRaw.ability.en).put("de", cardRaw.ability.de));
        obj.put("flavor", new JSONObject().put("en", cardRaw.flavor.en).put("de", cardRaw.flavor.de));
        obj.put("rarity", cardRaw.rarity);
        obj.put("cost", cardRaw.cost);
        obj.put("atk", cardRaw.atk);
        obj.put("def", cardRaw.def);
        obj.put("imageUrl", cardRaw.imageUrl);

        appendString(obj.toString(1));

        appendString(",\n");*/
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
