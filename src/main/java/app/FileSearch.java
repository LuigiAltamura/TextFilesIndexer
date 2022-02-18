package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class FileSearch {

    private final Directory indexDirectory;
    private final StandardAnalyzer analyzer;

    public FileSearch(Directory fsDirectory, StandardAnalyzer analyzer) {

        this.indexDirectory = fsDirectory;
        this.analyzer = analyzer;
    }


    /**
     * This method permits to search a file from a sinle word or a sentence.
     * @param queryString is the query that the user typed
     * @return returns the list of documents that produced a match
     */
    public List<Document> searchFiles(String queryString) {
        try {
            Query query = new QueryParser("contents", analyzer).parse(QueryParser.escape(queryString));

            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, 10);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException | ParseException e) {
            System.out.println("Error searching " + queryString + " : " + e.getMessage());
        }
        return null;

    }

}
