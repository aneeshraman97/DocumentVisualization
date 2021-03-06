package data_processing.multi_query_processing;

import api.reader.LuceneIndexReader;
import common.results.MultiQueryResults;
import common.results.QueryResults;
import data_processing.FixedNodeGenerator;
import data_processing.data.D3ConvertibleJson;
import data_processing.data.FixedNode;
import data_processing.data.Link;
import data_processing.multi_query_processing.data.DocumentNode;
import internal.static_util.FullTextExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts multi-query results to a JSON object
 * Created by chris on 11/22/15.
 */
public class MultiQueryConverter {

    public static final double CONSTANT = 1.0/3000.0;

    public static final int MAX_SIZE = 30;

    public static final String[] colors = {"red", "blue", "green"};

    /**
     * Convert a list of MultiQueryResults to a D3ConvertibleJson object
     *
     * @param results The list of results to convert into the view
     * @return Object that represents the JSON object that can be read by D3.
     */
    public static D3ConvertibleJson convertToLinksAndNodes(List<MultiQueryResults> results) {
        // Generate the JSON Object
        D3ConvertibleJson jsonObject = new D3ConvertibleJson();

        // Get a list of the unique terms within the results set.
        ArrayList<String> terms = new ArrayList<>();
        results.stream().forEach(result -> result.terms.stream()
                .filter(term -> !terms.contains(term))
                .forEach(terms::add));

        Map<String, Integer> termIndexes = new HashMap<>();
        ArrayList<FixedNode> fixedNodes = new ArrayList<>();
        FixedNodeGenerator.generateFixedNodes(termIndexes,
                                              fixedNodes,
                                              terms.toArray(new String[terms.size()]));

        // Adding the fixed nodes
        fixedNodes.forEach(jsonObject.nodes::add);
        boolean firstItr = true;
        double maxScore = 0;

        for (MultiQueryResults result : results) {
            // We know values are sorted, so this is the max score.
            if(firstItr){
                firstItr = false;
                maxScore = result.score;
            }

            String documentName = "doc" + result.docId;

            double nodeSize = determineNodeSize(result);
            String nodeColor = determineColor(result, maxScore);

            jsonObject.nodes.add(DocumentNode.of(documentName, result.docId, nodeColor,
                    result.docId, nodeSize, result.score));

            int myIndex = jsonObject.nodes.size() - 1;
            for (QueryResults qResult : result.getQueryResults()) {
                int sourceIndex = termIndexes.get(qResult.query);
                double linkPower = qResult.score;
                if (linkPower >= .001) {
                    jsonObject.links.add(Link.of(sourceIndex, myIndex, linkPower));
                }
            }
        }

        return jsonObject;
    }

    // TODO: This needs to implement something based on the indexes we assign to the RGB assets

    /**
     * Determines the colors of the nodes
     *
     * @param result Result to convert to a color
     * @return The color of the result
     */
    public static String determineColor(MultiQueryResults result, double maxScore) {
        int val; // Lightness value
        if(result.score == 0){
            val = 100;
        }else{
            val = 100 - (int)(100*(result.score / maxScore));
        }

        String hslString = "hsl(0,0%," + Integer.toString(val) + "%)";
        return hslString;
    }

    public static double determineNodeSize(MultiQueryResults result){
        String fullText = FullTextExtractor
                .extractText(LuceneIndexReader.getInstance().getReader(), result.docId);

        int length = fullText.length();

        double size = (length * CONSTANT) + 5;

        return size > 30 ? MAX_SIZE : size;
    }
}
