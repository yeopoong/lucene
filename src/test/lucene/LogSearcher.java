package test.lucene;

import java.io.File;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** Simple command-line based search demo. */
public class LogSearcher {

  private LogSearcher() {}

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
    String usage =
      "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/java/4_0/demo.html for details.";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }

//    String index = "c:\\test\\lucene\\index";
    String index = "index";
    String field = "logdata";
    String queryString = "javadoc";
    
    IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(index)));
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
    QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
    
  Query query = parser.parse(queryString);
  System.out.println("Searching for: " + query.toString(field));
    Date start = new Date();
    int numTotalHits=1000;
    ScoreDoc[] hits = searcher.search(query, numTotalHits).scoreDocs;
    System.out.println("matching Record Cound:"+ hits.length);
      for (int i = 0; i < hits.length; i++) {
    	  Document doc = searcher.doc(hits[i].doc);
          System.out.println(doc.get("logdata")); //filename이라는 필드명으로 필드가 들어가있어야함
      } 

    Date end = new Date();
    System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
    searcher.close();
  }

}
