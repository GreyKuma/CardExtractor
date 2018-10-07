import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSV{

    private String path;
    private PrintWriter printWriter;
    private StringBuilder stringBuilder = new StringBuilder();
    private int width;
    private int currentColumn = 0;

    public CSV(String path, int width) throws FileNotFoundException{
        this.open(path);
        this.width = width;
    }

    public CSV(String path) throws FileNotFoundException{
        this(path, 1);
    }



    public void append(String str){
        str = "\"" + str + "\"";
        stringBuilder.append(str);
        if(currentColumn == width - 1){
            currentColumn = (currentColumn + 1)%width;
            stringBuilder.append("\n");
            return;
        }
        currentColumn++;
        stringBuilder.append(',');
    }

    public void append(String[] strArray){
        for(String data:strArray){
            append(data);
        }

    }

    public void commit(){
        printWriter.write(stringBuilder.toString());
        stringBuilder = new StringBuilder();
    }

    public void close(){
        commit();
        printWriter.close();
    }

    public void open() throws FileNotFoundException{
        printWriter = new PrintWriter(new File(this.path));
    }

    public void open(String newPath) throws FileNotFoundException {
        this.path = newPath;
        open();
    }

    public static void main(String[]args) throws FileNotFoundException{
        CSV csv = new CSV("test.csv", 2);
        String[] strArray = {"id", "Name", "1", "test"};

        csv.append(strArray);
        csv.close();
        System.out.println("Done");



//        PrintWriter pw = new PrintWriter(new File("test.csv"));
//        StringBuilder sb = new StringBuilder();
//        sb.append("id");
//        sb.append(',');
//        sb.append("Name");
//        sb.append('\n');
//
//        sb.append("1");
//        sb.append(',');
//        sb.append("Prashant Ghimire");
//        sb.append('\n');
//
//        pw.write(sb.toString());
//        pw.close();
//        System.out.println("done!");
    }
}
