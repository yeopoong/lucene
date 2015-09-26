package test.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** Index all text files under a directory. See http://lucene.apache.org/java/3_1/demo.html. */
public class LogFileIndexer {
  
  private LogFileIndexer() {}

  /** Index all text files under a directory. */
  public static void main(String[] args) {
//    String indexPath = "c:\\test\\lucene\\index";
//    String String indexPath = "/gibs/test/lucene/index";
      String indexPath = "index";
      String logFileDir = "logs";
      
    boolean create = true;

    
    

    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Date start = new Date();
      
      Directory dir = FSDirectory.open(new File(indexPath));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);

      File indexFile = new File(indexPath,"segments.gen");
      ///새로 생성 또는 기존 파일 update //////////////////////////////
  		if(indexFile.exists()){
  			create=false; //update mode 입니다.
  			System.out.println("CREATE_OR_APPEND mode>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
  		}
      
      if (create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      iwc.setRAMBufferSizeMB(256.0);

      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, new File(logFileDir));

      // NOTE: if you want to maximize search performance,
      // you can optionally call optimize here.  This can be
      // a costly operation, so generally it's only worth
      // it when your index is relatively static (ie you're
      // done adding documents to it):
      //
       writer.optimize();

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  static void indexDocs(IndexWriter writer, File file)
    throws IOException {
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(writer, new File(file, files[i]));
          }
        }
      } else {
    	  System.out.println("file>>>>>>>>>>>>>>>>>>>"+file);
    	  if(!file.getName().endsWith(".txt")){
        	  System.out.println("file SKIP >>>>>>>>>>>>>"+file);
        	  return;
    	  }
        FileInputStream fis;
        BufferedReader br =null;
        String line=null;
        try {
          fis = new FileInputStream(file);
          br = new BufferedReader(new InputStreamReader(fis));
          while((line=br.readLine())!= null){
        	  System.out.println("data######"+line);
        	  Document doc = makeIndex(line);
              writer.addDocument(doc);
          }
          
          
        } catch (Exception e) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help
        	e.printStackTrace();
          return;
        }

        try {
        	          
        } finally {
          fis.close();
          br.close();
        }
      }
    }
  }

  private static Document makeIndex(String line) {
    Document doc = new Document();
//    Field pathField = new Field("logdata", line, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
    Field dataField = new Field("logdata", line, Field.Store.YES, Field.Index.ANALYZED);
    dataField.setOmitTermFreqAndPositions(true);
    doc.add(dataField);
//이전 소스는 이렇게 되어 있었다..          
//	doc.add(new Field("messageLog",logString, Field.Store.YES, Field.Index.TOKENIZED));
	return doc;
  }
  
  
  
}
