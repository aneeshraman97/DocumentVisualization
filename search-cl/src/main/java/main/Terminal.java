package main;

import api.document_access.MetadataRetriever;
import common.Constants;
import common.data.ScoredDocument;
import api.indexer.PDFIndexer;
import api.reader.LuceneIndexReader;
import api.document_search.LuceneDocumentSearcher;
import api.document_search.DocumentSearcherFactory;
import api.exception.LuceneSearchException;

import java.util.List;
import java.util.Scanner;

/**
 * Created by chris on 10/5/15.
 */
public class Terminal {
    public static final String INDEX_OUT = "index";

    public static void main(String[] args) throws Exception {
        // Initialize the index
        PDFIndexer indexer = new PDFIndexer(INDEX_OUT, System.getenv(Constants.RESOURCE_FOLDER_VAR));
//        indexer.updateIndex();

        if (!LuceneIndexReader.getInstance().initializeIndexReader(INDEX_OUT)) {
            System.err.println("Initializer Error: Could Not Initialize IndexReader");
        }

        LuceneDocumentSearcher searcher = DocumentSearcherFactory
                .getDocumentSearcher(LuceneIndexReader.getInstance(), DocumentSearcherFactory.TokenizerType.WHITESPACE_TOKENIZER);
        MetadataRetriever retriever = new MetadataRetriever(LuceneIndexReader.getInstance());

        Scanner kb = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a search term: ");
            String search = kb.nextLine();
            if (search.equals("") || search.isEmpty()) break;
            List<ScoredDocument> docs = searcher.searchForTerm(search);
            docs.stream().forEach(doc -> {
                try {
                    System.out.println(doc.score + "\t" + retriever.getTitle(doc.docId));
                } catch (LuceneSearchException e) {
                    System.err.println("Error finding document title: " + e.toString());
                }
            });
        }
        kb.close();
    }
}
