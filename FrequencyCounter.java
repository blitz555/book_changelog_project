import java.util.ArrayList;

public class FrequencyCounter extends Constants{
	//protected ArrayList<String> types
	protected ArrayList<ArrayList<String>> tokensmodified = new ArrayList<ArrayList<String>>();//change to 2D arraylist
	protected ArrayList<ArrayList<Integer>> tokentypes = new ArrayList<ArrayList<Integer>>();
	public int[] tokentypes_summary = new int[41];//token types 0 - 40
	
	String[] types = new String[41];
	public FrequencyCounter(){
		types[NOTYPE] = "no-type token";
		types[KEY_PART] = "keyword: part";
		types[KEY_CHAPTER] = "keyword: chapter";
		types[KEY_APPENDIX] = "keyword: appendix";
		types[NUM_NOTYPE] = "number: no type";
		types[NUM_PART] = "number: part";
		types[NUM_CHAPTER] = "number: chapter";
		types[NUM_APPENDIX] = "number: appendix";
		types[NUM_SUBCHAPTER] = "number: subchapter";
		types[PAGE_REGULAR] = "page number: regular";
		types[PAGE_PART] = "page number: part";
		types[PAGE_CHAPTER] = "page number: chapter";
		types[PAGE_APPENDIX] = "page number: appendix";
		types[PAGE_ENDMATTERS] = "page number: end matters";
		types[WORD_REGULAR] = "word INCLUDE \",:;'-\"";
		types[WORD_D1] = "word + \".\"";
		types[D1] = "delimiter \".\"";
		types[WORD_FRONTMATTERS] = "word (+D) in front matters";
		types[WORD_ENDMATTERS] = "word (+D) in end matters";
		types[NOTYPE_2TOKENS] = "2 no type tokens in line"; 
	};
		
	private String name = null;
	private int partWordCount = 0;
	private int chapterWordCount = 0;
	private int appendixWordCount = 0;
	
	private int partNumCount = 0;//number count increases if there is number beside keyword
	private int chapterNumCount = 0;//number count increases if there is number beside keyword
	private int appendixNumCount = 0;//number count increases if there is number beside keyword
	
	protected boolean partexists = false;
	protected boolean chapterexists = false;
	protected boolean appendixexists = false;
	protected boolean frontmattersexists = false;
	protected boolean endmattersexists = false;
	
	public void setName(String theName){
		name = theName;
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * turn tokens containing section defining delimiters into subtokens, in tokensmodified
	 *@param String array tokens, unmodified
	 * does not return an object
	 */
	public void checkdelimiters(String[] tokens){
		for(int i = 0; i < tokens.length; i++ ){
			ArrayList<String> atoken = new ArrayList<String>();//store subtokens in atoken arraylist
			
			if( containsDelimiterAlphanumeric(tokens[i]) ){//check if token has delimiters
				String temp = null;//locally stores 1 character long string for comparison
				String temptoken = ""; //modified token string with space separating alphanumeric sequence and delimiter
				
				for(int j = 0; j < tokens[i].length(); j++){//add spaces between alphanumeric chars and delimiters in token
					temp = Character.toString( tokens[i].charAt(j) );
					if( temp.matches("[\\.]") ){//if delimiter is "." 
						temptoken = temptoken.concat(" . ");//add a space and delimiter "." to string
					}
					else{//if not delimiter
						temptoken = temptoken.concat(temp);//add temp to string
					}
				}//end of for loop j
				
				String[] subtokens = temptoken.split(" ");//stores tokens in temptoken into array of string
				
				for(int k = 0; k < subtokens.length; k++){//add subtokens into atoken arraylist
					atoken.add( subtokens[k] );
				}//end of for loop k
			}//end of if
			else{
				atoken.add( tokens[i] );//copy single token to arraylist
			}
			this.tokensmodified.add( atoken );//add atoken to 2d arraylist
		}//end of for loop i
	}
	
	/**
	 * get frequency o keywords part, chapter, appendix and corresponding number in a FrequencyCounter
	 * @param none
	 * does not return an object
	 * may or may not modify tokensmodified
	 */
	public void checkkeywords(){
		int pagenumber = 0; //keep temp count of page number
		int previousPageNumberIndex = -1;//keeps index of previous page number, start with -1 to precede 0
	
		//iterate through all tokens in tokens copy
		for(int i = 0; i < this.tokensmodified.size(); i++ ){
			ArrayList<Integer> atokentype = new ArrayList<Integer>();//starts a list with token type (and subtype(s))
			
			if( isNumeric( this.tokensmodified.get(i).get(0) ) ){//first token in line has numbers only
				if( this.tokensmodified.get(i).size() == 1 ){//1 token in line, classify by preceding keyword type 
					//keyword numbers
					if( this.tokentypes.get(i-1).get(0) == KEY_PART ){//preceded by keyword part
						atokentype.add(NUM_PART);
						this.tokentypes_summary[NUM_PART]++;
					}
					else if( this.tokentypes.get(i-1).get(0) == KEY_CHAPTER ){//preceded by keyword chapter
						atokentype.add(NUM_CHAPTER);
						this.tokentypes_summary[NUM_CHAPTER]++;
					}
					else if( this.tokentypes.get(i-1).get(0) == KEY_APPENDIX ){//preceded by keyword appendix
						atokentype.add(NUM_APPENDIX);
						this.tokentypes_summary[NUM_APPENDIX]++;
					}
					//page number starts
					else if( Integer.parseInt( this.tokensmodified.get(i).get(0) ) - pagenumber > -1 ){//greater or equal to previous page number
						if( this.tokentypes.get(previousPageNumberIndex + 1).get(0) == KEY_PART ){//previous is keyword part
							atokentype.add(PAGE_PART);
							this.tokentypes_summary[PAGE_PART]++;
						}
						else if( this.tokentypes.get(previousPageNumberIndex + 1).get(0) == KEY_CHAPTER ){//previous is keyword chapter
							atokentype.add(PAGE_CHAPTER);//type page number part
							this.tokentypes_summary[PAGE_CHAPTER]++;
						}
						else if( this.tokentypes.get(previousPageNumberIndex + 1).get(0) == KEY_APPENDIX ){//previous is keyword appendix
							atokentype.add(PAGE_APPENDIX);//type page number appendix
							this.tokentypes_summary[PAGE_APPENDIX]++;
						}
						else if( this.tokentypes.get(previousPageNumberIndex + 1).get(0) != NUM_SUBCHAPTER ){//previous is also not subchapter number
							atokentype.add(PAGE_ENDMATTERS);//type page number end matters
							this.tokentypes_summary[PAGE_ENDMATTERS]++;
						}
						else{//previous is also not subchapter number
							atokentype.add(PAGE_REGULAR);//type page number regular
							this.tokentypes_summary[PAGE_REGULAR]++;
						}//end of if else
						pagenumber = Integer.parseInt( this.tokensmodified.get(i).get(0) );//update page number
						previousPageNumberIndex = i;//update index of previous page number
					}//end of change in page number
					else{//no type
						atokentype.add(NUM_NOTYPE);//type unclassified number
						this.tokentypes_summary[NUM_NOTYPE]++;
					}
				}//end of if else 1 token case
				else if( this.tokensmodified.get(i).size() == 2 ){//2 tokens in line
					if( this.tokensmodified.get(i).get(1).matches(".*[\\.].*") ){//second token is ".", assume chapter number
						atokentype.add(NUM_CHAPTER);//type chapter number, first token
						atokentype.add(D1);//type delimiter "."
						this.tokentypes_summary[NUM_CHAPTER]++;
						this.tokentypes_summary[D1]++;
					}
					else{//second number is not "."
						atokentype.add(NOTYPE_2TOKENS);//2 no type tokens, first token
						atokentype.add(NOTYPE);//type no type token
						this.tokentypes_summary[NOTYPE_2TOKENS]++;
						this.tokentypes_summary[NOTYPE]++;
					}
				}//end of if else 2 tokens case
				else{//3+ tokens in line, assume only numbers and delimiters
					atokentype.add(NUM_SUBCHAPTER);//type subchapter number, first token
					this.tokentypes_summary[NUM_SUBCHAPTER]++;
					
					//for loop j checks tokens with index >1 
					for(int j = 1; j < this.tokensmodified.get(i).size(); j++){//in case of first token being number, iterate till end of line
						if( isNumeric( this.tokensmodified.get(i).get(j) ) ){//type unclassified number
							atokentype.add(NUM_NOTYPE);
							this.tokentypes_summary[NUM_NOTYPE]++;
						}
						else if( isDelimiter( this.tokensmodified.get(i).get(j) ) ){//type delimiter "."
							atokentype.add(D1);
							this.tokentypes_summary[D1]++;
						}
						else{//unclassified, NOTYPE
							atokentype.add(NOTYPE);
							this.tokentypes_summary[NOTYPE]++;
						}//end of if else 
					}//end of for loop j	
				}//end of else 3+ tokens case
			}//end of if else is number
			else if( isDelimiter( this.tokensmodified.get(i).get(0) ) ){//token is delimiter . 
				atokentype.add(D1);//assign type delimiter
				this.tokentypes_summary[D1]++;	
			}//end of if else is delimiter
			else if( this.tokensmodified.get(i).get(0).equalsIgnoreCase("part")){//token is part word
				this.partWordCount++;
				if( isNumeric( this.tokensmodified.get(i+1).get(0) ) ){//check and update part number next to part word
					this.partNumCount = Integer.parseInt( this.tokensmodified.get(i+1).get(0) );//update partNumCount
					atokentype.add(KEY_PART);//assign type part keyword
					this.tokentypes_summary[KEY_PART]++;	
				}
				else{//ordinary word
					atokentype.add(WORD_REGULAR);//assign WORD REGULAR 
					this.tokentypes_summary[WORD_REGULAR]++;	
				}
			}//end of if else is part keyword
			else if( this.tokensmodified.get(i).get(0).equalsIgnoreCase("chapter") ){//token is chapter word
				this.chapterWordCount++;
				if(isNumeric( this.tokensmodified.get(i+1).get(0) )){//check and update chapter number
					this.chapterNumCount = Integer.parseInt( this.tokensmodified.get(i+1).get(0) );//update chapterNumCount
					atokentype.add(KEY_CHAPTER);//assign type chapter keyword
					this.tokentypes_summary[KEY_CHAPTER]++;
				}
				else{//ordinary word
					atokentype.add(WORD_REGULAR);//assign typP WORD REGULAR
					this.tokentypes_summary[WORD_REGULAR]++;	
				}
			}//end of if else is chapter keyword
			else if( this.tokensmodified.get(i).get(0).equalsIgnoreCase("appendix") ){//token is appendix word
				this.appendixWordCount++;
				if( isNumeric( this.tokensmodified.get(i+1).get(0) ) ){//check and update appendix number
					this.appendixNumCount = Integer.parseInt( this.tokensmodified.get(i+1).get(0) );//update appendixNumCount
					atokentype.add(KEY_APPENDIX);//assign type appendix keyword
					this.tokentypes_summary[KEY_APPENDIX]++;
				}
				else if( this.tokensmodified.get(i+1).get(0).length() == 1 && Character.isLetter( this.tokensmodified.get(i+1).get(0).charAt(0) )){//if numbering is letter
					if( Character.isUpperCase( this.tokensmodified.get(i+1).get(0).charAt(0) ) ){
						this.appendixNumCount = this.tokensmodified.get(i+1).get(0).charAt(0) - 'A' + 1;
						//this.tokentypes_summary[NUM_APPENDIX]++;//update appendix count if letter
					}
					else if ( Character.isLowerCase( this.tokensmodified.get(i+1).get(0).charAt(0) ) ){
						this.appendixNumCount = this.tokensmodified.get(i+1).get(0).charAt(0) - 'a' - 1;
						//this.tokentypes_summary[NUM_APPENDIX]++;//update appendix count if number
					}
					atokentype.add(KEY_APPENDIX);//assign type appendix keyword
					this.tokentypes_summary[KEY_APPENDIX]++;
				}
				else{//ordinary word
					atokentype.add(WORD_REGULAR);//assign type WORD_REGULAR
					this.tokentypes_summary[WORD_REGULAR]++;	
				}
			}//end of if-else is appendix keyword
			else if ( this.tokensmodified.get(i).size() > 1 && this.tokensmodified.get(i).get(1).matches(".*[\\.].*") ){
				//assume 2 tokens in line, where first token is not a number, and second token is a delimiter "."
				if( isfrontmatters() ){
					atokentype.add(WORD_FRONTMATTERS);//assign type words FrontMATTERS
					tokentypes_summary[WORD_FRONTMATTERS]++;
				}
				else if( isendmatters( this.tokensmodified.get(i).get(0) ) ){
					atokentype.add(WORD_ENDMATTERS);//assign type words EndMATTERS
					tokentypes_summary[WORD_ENDMATTERS]++;
				}
				else{
					atokentype.add(WORD_D1);//assign type words REGULAR
					tokentypes_summary[WORD_D1]++;
				}
				atokentype.add(D1);//second token delimiter
				tokentypes_summary[D1]++;
			}//end of if-else is 2 tokens, word + "."
			else if( isWord( this.tokensmodified.get(i).get(0) ) ){//1 token word and ,:;'-
				if( isfrontmatters() ){
					atokentype.add(WORD_FRONTMATTERS);//assign type words FEMATTERS
					tokentypes_summary[WORD_FRONTMATTERS]++;
				}
				else if( isendmatters( this.tokensmodified.get(i).get(0) ) ){
					atokentype.add(WORD_ENDMATTERS);//assign type words FEMATTERS
					tokentypes_summary[WORD_ENDMATTERS]++;
				}
				else{
					atokentype.add(WORD_REGULAR);//assign type words REGULAR
					tokentypes_summary[WORD_REGULAR]++;
				}
			}//end of if-else is word regular
			else{
				atokentype.add(NOTYPE);//assign type NOTYPE TOKENS
				tokentypes_summary[NOTYPE]++;
			}//end of if-else NOTYPE
			this.tokentypes.add(atokentype);//add atokentype to tokentypes
		}//end of for loop
		
		//set part, chapter, appendix to true if number count is non zero and matches word count
		if( this.tokentypes_summary[NUM_PART] > 0){
			partexists = true;
		}
		if( this.tokentypes_summary[NUM_CHAPTER] > 0){
			chapterexists = true;
		}
		if( this.tokentypes_summary[NUM_APPENDIX] > 0){
			appendixexists = true;
		}
	}//end of FrequencyCounter
	
	/**
	 * print keyword summary in Frequency counter
	 * @param Frequency counter a
	 */
	public void FrequencyCounterPrinter(){
		System.out.println("\nFrequencyCounterPrinter " + this.getName() );
		System.out.printf("%20s%15s%15s%15s%15s%15s\n", "", "Part", "Chapter", "Appendix", "Frontmatters", "Endmatters");
		System.out.printf("%20s%15d%15d%15d\n", "WordCount:", this.partWordCount, this.chapterWordCount, this.appendixWordCount);
		System.out.printf("%20s%15d%15d%15d\n", "NumCount:", this.partNumCount, this.chapterNumCount, this.appendixNumCount);
		System.out.printf("%20s%15b%15b%15b%15b%15b\n", "Division exists:", this.partexists, this.chapterexists, this.appendixexists, this.frontmattersexists, this.endmattersexists);
	}//end of frequency counter printer
	
	/**
	 * check if string is numeric
	 * @param str
	 * @return true/false
	 */
	public boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    int d = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}//end of isNumeric
	
	/**
	 * check for . delimiter in token
	 * @param in_str
	 * @return true/false 
	*/ 
	public static boolean isDelimiter(String in_str){
		if( in_str.matches(".*[\\.].*") ){//checks . delimiter in token
			return true;
		}
		return false;
	}
	
	/**
	 * check string contains only letters and punctuations and at most 1 unrecognized char
	 * @param str
	 * @return true/false
	 */
	public static boolean isWord(String str)  
	{  
		int n = 0;
		for(int i = 0; i < str.length(); i++){//check each character in string is letter or punctuation
			String test = str.substring(i, i+1);
			if( test.matches(".*[a-zA-Z,:;'-].*") ){//punctuations ,:;'-
				n++;
			}
		}//end of for loop i
		if( str.length() - n < 2){//if str contains at most 1 unrecognized char, is word
			return true;
		}
		return false;
	}//end of isWord
	
	/**
	 * check for alphanumeric and delimiter combinations in String
	 * @param in_str
	 * @return true/false 
	 */
	public static boolean containsDelimiterAlphanumeric(String in_str){
		if( ( in_str.matches(".*[0-9a-zA-Z].*") ) && in_str.matches(".*[\\.].*") ){//need to satisfy both conditions, separately
			//checks for alphanumeric char and . combination in token
			return true;
		}
		return false;
	}
	
	/**
	 * print tokens and types
	 */
	public void printTokenAndTypes(){
		int tokenscount = 0;
		for(int i = 0; i < this.tokensmodified.size(); i++ ){
			tokenscount += this.tokensmodified.get(i).size();
			System.out.printf( "%-10d", i );
			System.out.printf( "%-20s", this.tokensmodified.get(i) );
			for(int j = 0; j < this.tokentypes.get(i).size(); j++){
				System.out.printf( "%5s", this.tokentypes.get(i).get(j) );
			}
			System.out.print("\n");
		}
		System.out.println("	Total tokens in tokensmodified: " + tokenscount);
	}//end of printTokenTypesSummary
	
	/**
	 * print tokentypes_summary
	 */
	public void printTokenTypesSummary(){
		System.out.println("\n       tokentypes_summary");
		for(int i = 0; i < this.tokentypes_summary.length; i++ ){
			if(this.types[i] != null && this.tokentypes_summary[i]!=0 ){
				System.out.printf("%-30s", this.types[i]);
				System.out.printf("%5d %5d \n", i, this.tokentypes_summary[i]);
			}
		}
	}//end of printTokenTypesSummary
	
	/**
	 * check if any keyword or number have been scanned, update frontmatterexists true/false
	 * @return frontmatters true/false
	 */
	public boolean isfrontmatters(){
		boolean frontmatters = false; 
		int sum = 0;//keys sum of targeted token types
		for(int i = KEY_PART; i < KEY_APPENDIX + 1; i++){
			sum += this.tokentypes_summary[i];
		}//end of for loop i, keyword type category
		for(int j = NUM_PART; j < NUM_SUBCHAPTER + 1; j++){
			sum += this.tokentypes_summary[j];
		}//end of for loop j, keyword type category
		if(sum == 0){
			frontmatters = true; 
			this.frontmattersexists = true;
		}
		return frontmatters;
	}//end of isfrontmatters
	
	/**
	 * check token matches high frequency words in end matters, update endmattersexist true/false
	 * @param str token 
	 * @return true/false
	 */
	public boolean isendmatters(String str){
		boolean endmatters = false;
		String[] endwords = {"reference", "credit", "index", "glossary", "source", "answer", "appendices"};
		for(int i = 0; i < endwords.length; i++){
			if( str.toLowerCase().contains( endwords[i].toLowerCase() ) ){
				endmatters = true;
				this.endmattersexists = true;
				return endmatters;
			}
		}//end of for loop i
		return endmatters;
	}
	
	/**
	 * modified appendix letter to int num
	 */
	public void checkkeywords2(){
		boolean endmattersflag = false;
		for(int i = 0; i < this.tokensmodified.size() - 1; i++ ){
			int testtype = this.tokentypes.get(i).get(0);
			String nexttoken = this.tokensmodified.get(i+1).get(0);//limit index i+1 to within bound
			if( testtype == KEY_APPENDIX && Character.isLetter( nexttoken.charAt(0) ) ){//appendix numbering is letter
				int appendixnum = 0;
				if( Character.isUpperCase( nexttoken.charAt(0) ) ){
					appendixnum = this.tokensmodified.get(i+1).get(0).charAt(0) - 'A' + 1;
				}
				else if ( Character.isLowerCase( nexttoken.charAt(0) ) ){
					appendixnum = this.tokensmodified.get(i+1).get(0).charAt(0) - 'a' - 1;
				}
				nexttoken = Integer.toString(appendixnum);//convert token letter to number in string
				this.tokensmodified.get(i+1).set(0, nexttoken);//change letter to number
				this.tokentypes.get(i+1).set(0, NUM_APPENDIX);//change token type to num appendix
				
				tokentypes_summary[WORD_REGULAR]--;//revise token type summary
				tokentypes_summary[NUM_APPENDIX]++;
			}//end of if appendix lettering is letter
			if( testtype == WORD_ENDMATTERS ){//first end matters token reached
				endmattersflag = true;
			}
			if( endmattersflag == true){//change first token type to word_endmatters
				this.tokentypes.get(i+1).set(0, WORD_ENDMATTERS);
			}
		}//end of for loop i
	}//end of checkkeywords2
}//end of class
