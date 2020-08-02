import java.util.ArrayList;

/**
 * Compare class hold information about two books in two FrequencyCounter objects and methods for comparing objects
 * @author Z
 *
 */
public class Compare extends Constants{
	protected FrequencyCounter keywordCounter_new = new FrequencyCounter();//keywordCounter of new book
	protected FrequencyCounter keywordCounter_old = new FrequencyCounter();//keywordCounter of old book
	
	Section bookhead_new = new Section();//head of new book
	Section bookhead_old = new Section();//head of old book
	
	protected ArrayList<ArrayList<Section>> booknew = new ArrayList<ArrayList<Section>>();//stores section references by level
	protected ArrayList<ArrayList<Section>> bookold = new ArrayList<ArrayList<Section>>();//stores section references by level
	
	public Compare(){} 
	
	/**
	 * step 1 process input string array into token types, and PRINT tokens tokentypes
	 * @param book_array_new unprocessed, input string array
	 */
	public void processTokens(String name, String[] book_array_new, FrequencyCounter counter){
		//process new book currently
		counter.checkdelimiters(book_array_new);//separate tokens containing delimiters into subtokens
		counter.checkkeywords();
		counter.checkkeywords2();
		counter.setName(name);
		//counter.printTokenAndTypes();
		
		counter.FrequencyCounterPrinter();//print keywordCounter summary
		//counter.printTokenTypesSummary();//print tokentypes summary
	}
	
	/**
	 * step 2 check existing division types to determine construct method
	 * contains a printTable call
	 * @param counter
	 */
	public Section scan(String bookname, FrequencyCounter counter, Section bookhead){
		//basic format, chapter division exists
		if(counter.chapterexists == true && counter.partexists == false){//contains division by chapter
			
			if(counter.tokentypes_summary[D1] > 
			2*counter.tokentypes_summary[NUM_CHAPTER]){//2 levels subchapters exist in chapters
				bookhead = construct1(bookname, counter);
				//this.bookhead_new.printSectionslevel( this.bookhead_new.lowerlevel );//print lower level
				//bookhead.printTable("   ");//3 space keys separate levels
			}
			//add no subchapter case
		}
		return bookhead;
	}
	
	/**
	 * step 3 part of step 2, construct multilists containing chapter and subchapters, return head
	 * @param counter a frequency counter containing tokens
	 * @return head a list containing sections
	 */
	public Section construct1(String bookname, FrequencyCounter counter){
		//add page number method
		Section head = new Section();//creates head object
		//System.out.println("\nBook head created.");
		head.settitle( bookname );//set head title 
		Section previous = head;//keeps track of previous section reference
		//head.setupperlevelnum( counter.tokentypes_summary[NUM_CHAPTER] );//top level is chapter
		//head.setlowerlevelnum( counter.tokentypes_summary[WORD_D1] );//bottom level is subchapter
		//System.out.println("upper level num: " + head.upperlevel.size());
		//System.out.println("lower level num: " + head.lowerlevel.size());
		
		int level = 0;//current level, of head
		int tokensindex = 0;//current tokens index
		for(int i = 0; i < counter.tokentypes_summary[ NUM_CHAPTER ]; i++){//for loop i iterate through chapter level
			if( counter.tokentypes.get( tokensindex ).get(0) == NUM_CHAPTER ){//num chapter marks start of chapter title
				//System.out.println("\nNEW chapter: found chapter token " + i);
				previous = head;//reset previous pointer to head for next chapter
				level = head.getlevel() + 1;//update level for chapter
				
				Section current = new Section();//update section to newly created section 
				int order = Integer.parseInt( counter.tokensmodified.get( tokensindex ).get(0) ) - 1;
				//convert token to chapter number or replace with index i
				tokensindex++;//move to next token (line), ignore delimiter
				
				tokensindex = current.addinfo(tokensindex, level, order, previous, counter);
				//add level, title, startpage, upperlevel reference, and update tokenscount
				previous.addlowerlevelsection(current);//add to upper level section
				
				previous = current;//update previous reference to current chapter section
				//look for start of next section
				if( counter.tokentypes.get(tokensindex).get(0) == NUM_SUBCHAPTER ){//subchapter numbers, process tokens in line
					level++;//update to next level
					for(int j = 0; j < counter.tokensmodified.get( tokensindex ).size(); j++){//process third token in line
						if(  counter.tokentypes.get(tokensindex).get(j) == NUM_NOTYPE ){//found subchapter order
							order = Integer.parseInt( counter.tokensmodified.get( tokensindex ).get(j) );//save order
							System.out.println("found subchapter order " + order);
							continue;
						}
					}
				}
				else if( counter.tokentypes.get(tokensindex).get(0) == NUM_CHAPTER ){//next chapter starts, restart loop
					tokensindex++;
					System.out.println("moved to next chapter");
					continue;
				}//end of if else find next title
				
				//System.out.println("checking current tokentype " + counter.tokensmodified.get(tokensindex).get(0));
				
				level = previous.getlevel() + 1;//update level for next level 
				order = 0;//restart order at zero for next level
				//need while loop to iterate through all subsections, stop condition is a chapter number or index moved past bound
				while( tokensindex < counter.tokensmodified.size() && counter.tokentypes.get( tokensindex ).get(0) != NUM_CHAPTER ){
					if( counter.tokentypes.get( tokensindex ).get(0) == WORD_ENDMATTERS ){ break; }//stop check for endmatters
					current = new Section();//creates new section for next level
					tokensindex = current.addinfo(tokensindex, level, order, previous, counter);//add section info
					previous.addlowerlevelsection(current);//add subsection to chapter lowerlevel
					head.addupperlevelsection(current);//add subsection to head upperlevel
					order++;
				}//end of while subsection
			}//end of if chapter number
			else{
				//handle other cases where first token is not a chapter number,
			}
			
			if(tokensindex == counter.tokensmodified.size()){//STOP CHECK
				break;
			}
		}//end of for loop i
		
		//System.out.println("upper level num: " + head.upperlevel.size());
		//System.out.println("lower level num: " + head.lowerlevel.size());
		return head;
	}//end of construct1
	
	
}//end of compare class
