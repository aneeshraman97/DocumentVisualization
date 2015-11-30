package data_processing.multi_query_processing.multi_query_json_data;

/**
 * Created by chris on 11/22/15.
 */
public class DocumentNode extends GenericNode {
    public final int docId;
    public final int size;
    public final double score;
    public DocumentNode(String name, int id, String color, int docId, int size, double score){
        super(false, name, id, color);
        this.docId = docId;
        this.size = size;
        this.score = score;
    }

    public static DocumentNode of(String name, int id, String color, int docId, int size, double score){
        return new DocumentNode(name, id, color, docId, size, score);
    }
}
