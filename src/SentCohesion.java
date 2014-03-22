import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.spell.EditDistance;
import com.aliasi.util.Distance;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentCohesion {
	    static final Distance<CharSequence> EDIT_DISTANCE = new EditDistance(false);
	    public static void main(String[] args) throws IOException {	
	        try{	        	
	        	File dir = new File("../output3/");	        	
	        	if(dir.isDirectory()){
	        		String[] files = dir.list();
	        		for(int i = 0; i < files.length; i++){
	        			File f = new File(dir, files[i]);
						if(!f.isDirectory()){						
		        				FileInputStream fis = new FileInputStream(f);
		    	            	BufferedInputStream bis = new BufferedInputStream(fis);
		    	            	DataInputStream dis = new DataInputStream(bis);
		    	            	String line = "";  
		    	            	String one_file = "";
		    	            	Pattern acronyms = Pattern.compile("\\(?[a-zA-Z]\\.[a-zA-Z]\\.");
		    	            	Pattern acronyms2 = Pattern.compile("[a-zA-Z][a-z]?\\.[A-Za-z]\\.\\,?");
		    	            	Pattern acronyms3 = Pattern.compile("[A-Z]\\.?[A-Za-z]\\.\\'s");
		    	            	Pattern numbers = Pattern.compile("\\$?\\d+\\.\\d+");
		    	            	Pattern numbers2 = Pattern.compile("\\(\\d+\\.\\d*\\)\\:");
		    	            	Pattern numbers3 = Pattern.compile("No\\.\\d+");
		    	            	Pattern aletter = Pattern.compile("[a-zA-Z]\\.");
					Pattern twoletters = Pattern.compile("[A-Z][a-zA-Z][a-z]?\\.\\-?");
		    	            	Pattern moreletters = Pattern.compile("[A-Z][a-zA-Z]+\\.\\,");
		    	            	Pattern ellipsis = Pattern.compile("\\.\\.\\.");
		    	            	
		    	            	// change to the format that is recognizable by the LingPipe API
		    	            	while((line = dis.readLine()) != null){	
		    	            			line = line.replaceAll("\\n", "");		    	            			
		    	            			//line = line.replaceAll("\\,", "=");
		    	            			String [] words = line.split("\\s+");
		    	            			for(int j=0; j<words.length; j++){
		    	            				Matcher usa = acronyms.matcher(words[j]);
		    	            				Matcher acr = acronyms2.matcher(words[j]);
		    	            				Matcher acr2 = acronyms3.matcher(words[j]);
		    	            				Matcher num = numbers.matcher(words[j]);
		    	            				Matcher num2 = numbers2.matcher(words[j]);
		    	            				Matcher num3 = numbers3.matcher(words[j]);
		    	            				Matcher one = aletter.matcher(words[j]);
								Matcher more = moreletters.matcher(words[j]);
		    	            				Matcher two = twoletters.matcher(words[j]);
		    	            				Matcher ell = ellipsis.matcher(words[j]);
		    	            				if(usa.matches() || acr.matches() || acr2.matches() || num.matches() || num2.matches() 
		    	            						|| num3.matches() || one.matches() || two.matches() || more.matches() || ell.matches()) {	    	            					
		    	            					words[j] = words[j].replaceAll("\\.", "\\#");	
		    	            					words[j] = words[j].replaceAll("\\'", "\\@");
		    	            				}
		    	            				words[j] = words[j].replaceAll("\\,", "=");
		    	            				one_file += words[j] + " ";
		    	            			}		    	            			
		    	            			one_file = one_file.replaceAll("\\.", "\\,");
		    	            			one_file = one_file.replaceAll("\\s+", "+");		    	            		
		    	            	}  
		    	            	// parse out input set
		    		        	Set<String> inputSet = new HashSet<String>();
		    	            	for (String s : one_file.split(",")) 	                    		
		    	            			inputSet.add(s);
		    	            
		    	            	// set up max distance
		    	           	    int maxDistance = args.length == 1 ? Integer.MAX_VALUE : Integer.MAX_VALUE;
		    	         
		    	            	// Complete-Link Clusterer
		    	           		HierarchicalClusterer<String> clClusterer
		    	           		    		= new CompleteLinkClusterer<String>(maxDistance, EDIT_DISTANCE);
		    	          		Set<Set<String>> clClustering = clClusterer.cluster(inputSet);	
		    	          		
		    	          		// output the files
		    	          	 	FileOutputStream out = new FileOutputStream("../output/" + files[i].substring(0,5) 
		    	            			+ ".M.250." + files[i].substring(5,6) + ".8");
		    	    	    	PrintStream p = new PrintStream(out);
		    	    	    	p.println(clClustering);
						}
	    	       }	        		
	        	}	          	 
	        } catch (IOException e){
	        	System.out.println(e.getMessage());
	        }
	    }
}
