/**
* Cuijun Wu cuijunwu
* John Keesling keesling
* Mami Hackl mami1203
* LING573 D3: Index contents 
*/

// include libraries
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * ContIndexer class 
 */
public class ContIndexer {

  private IndexWriter writer;
  private ArrayList<File> queue = new ArrayList<File>();

  /**
   * Constructor
   * @param indexDir the name of the folder in which the index should be created
   * @throws java.io.IOException
   */
  ContIndexer(String indexDir, StandardAnalyzer analyzer) throws IOException {
    writer = new IndexWriter(indexDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
  }

  /**
   * Indexes a file or directory
   * @param fileName the name of a text file or a folder we wish to add to the index
   * @throws java.io.IOException
   */
  public void indexFileOrDirectory(String fileName) throws IOException {
    //gets the list of files in a folder
    listFiles(new File(fileName));

    int originalNumDocs = writer.numDocs();
    //int newNumDocs = writer.numDocs();

    int counter = 0;
    for (File f : queue) {
      FileReader fr = null;
      try {

        // add contents of file
        fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Matcher matcher;

        // regular expressions to extract sentence from documents
        //Pattern header = Pattern.compile("Found (\\d+) hits.");
        //Pattern pattern = Pattern.compile("((``)?[A-Z].*[.?!](''))\\s+");

        Pattern pattern = Pattern.compile("(<P>|<TEXT>)");
        Pattern pattern2 = Pattern.compile("<[/]?[A-Z]+>");
        Pattern pattern3 = Pattern.compile("\\s+$");
        Pattern pattern4 = Pattern.compile("(?<=[a-z0-9]{2,}\\s?[)]?[.?!](['`]{2})?(?![,\\-\\w]+))[\\s\\n]*");
        Pattern pattern5 = Pattern.compile("^['\\s]+");
        Pattern pattern6 = Pattern.compile("(['`]{2}|\")");

        //Set set = new LinkedHashSet();

        while((line = br.readLine()) != null){
          matcher = pattern.matcher(line);
          // if beginning tag, skip
          if(matcher.find()){
            line = br.readLine();
            String cont = "";
            do{
                matcher = pattern2.matcher(line);
                // if any tags, skip
                if(matcher.find()){
                   continue;

                // contents found
                }else{
                   matcher = pattern4.matcher(line);

                   // line contains eos 
                   if(matcher.find()){
	             String[] temp = pattern4.split(line);

                     for (int i = 0; i < temp.length; i++){
                       String str = temp[i];
                       matcher = pattern4.matcher(str);
                       // end of the sentence. add to index 
                       if(matcher.find()){
                          cont = cont.concat(str);
                          // remove space at bos
                          matcher = pattern6.matcher(cont);
                          if (!matcher.find()){
                            cont = cont.replaceAll("^\\s+","");
                            addIndex(cont,counter,f.getAbsolutePath()); 
                            cont = "";
                            counter++;
                          }else{
                           cont = "";
                          }
                       // bos. start a new sentence 
                       }else{
                          matcher = pattern5.matcher(str);
                          if(!matcher.find()){
                            cont = str + ' ';
                          }
                       }
                     } // end of for-loop

                   // line doesn't contain eos
                   }else{
                     matcher = pattern3.matcher(line);
                     // there's space at the end of line
                     if(matcher.find()){
                        cont = cont.concat(line);
                     // add space to the end of line
                     }else{
                        cont = cont.concat(line + " ");
                     }
                   }
                }
            }while((line = br.readLine()) != null);
          }
        }

      } catch (Exception e) {
        System.out.println("Could not add: " + f);
      } finally {
        fr.close();
      }
    } // end for-loop

    //int newNumDocs = writer.numDocs();

    queue.clear();

    // return number of documents added
    //return newNumDocs - originalNumDocs;
  }


  /**
   * list files
   */
  private void addIndex(String str, int counter, String fileName)throws IOException {


    Document doc = new Document();

    //adding field contents
    doc.add(new Field("contents",str,
          Field.Store.YES,
          Field.Index.ANALYZED));

    //adding second field which contains the path of the file
    //String ctr = String.valueOf(counter);
    doc.add(new Field("path",fileName,
          Field.Store.YES,
          Field.Index.NOT_ANALYZED));

    //System.out.println("added: " + fileName);
    //System.out.println(str + ' ');
    writer.addDocument(doc);
   }


  /**
   * list files
   */
  private void listFiles(File file) {
    if (!file.exists()) {
      System.out.println(file + " does not exist.");
    }
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        listFiles(f);
      }
    } else {
      //index text files
      String filename = file.getName().toLowerCase();
      queue.add(file);
    }
  }

  /**
   * Close the index.
   * @throws java.io.IOException
   */
  public void closeIndex() throws IOException {
    writer.optimize();
    writer.close();
  }

}
