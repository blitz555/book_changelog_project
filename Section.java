//this part also use JLabels
//section contains all information stored in a line of table of content
//it is a generic class of all possible section levels, part, chapter, sub chapter, appendix, etc.
//this is a singly/doubly linked list? to be determined
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class Section extends Constants{
	private int level = 0;//level 0 for head, 1 for part, 2, for chapter...
	private int order = 0;//order of section, compared to other sections of this level
	private int overall_order = 0;//order in table of content
	private int startpage = 0;//start page of this section >=0
	private int endpage = 0;//end page of this section >=0
	private int pagelength = 0;//number of pages in this section = endpage - startpage, updated by startpage/endpage
	
	public int startindex = 0;//record index in tokensmodified
	public int endindex = 0;
	static int overall_ordercount = 0;
	
	private String title = "";//title of section
	protected ArrayList<Section> upperlevel = new ArrayList<Section>();//equivalent to previous pointer
	protected ArrayList<Section> lowerlevel = new ArrayList<Section>();//equivalent to next pointer
	
	private int upperlevelnum = 0;//number references to upper level, 1 for all sections except head
	private int lowerlevelnum = 0;//number of references to lower level, n for all nodes, 0 for all leafs

	protected Boolean matchfound = false;//keeps track of whether exact or similar section match found
	protected double similarityscore = 0;//keeps score of match
	protected Section match = null;//reference to matching section
	protected JLabel label = null;//label for view
	
	public Section(){
		//System.out.println("\nCreating new section");
	}
	
	/**
	 * set title, set display label to show title
	 * @param str
	 */
	public void settitle(String str){
		this.title = str;
	}
	
	/**
	 * set label, by combining level, order and title
	 */
	public void setlabel(){
		String str = new String();//contains text to write to panel
		//System.out.println(this.getlevel() + " " + this.getorder() + " " + this.gettitle());
		if(this.getlevel()!=0 && this.gettitle()!=null){
			for(int i = 1; i < this.getlevel(); i++){
				str+= "        ";//add one tab for each level larger than 1
			}
			str += this.getorder()+1;//add index by 1 and concatenate
			str += " " + this.gettitle();//add spacing between numbering and title, and title
			this.label = new JLabel(str);
		}
		else{
			System.out.println("Label not updated. Key parameters missing.");
			return;
		}
	}
	/**
	 * get title
	 * @return
	 */
	public String gettitle(){
		return this.title;
	}
	
	/**
	 * set level of object section, reserve level 0 for head of list, whole numbers for others
	 * @param n level in book
	 */
	public void setlevel(int n){
		assert n > -1;// need enable assertions, 
		this.level = n;
		//System.out.println("level set to " + n);
	}
	
	/**
	 * get level of object section
	 * @return level
	 */
	public int getlevel(){
		return this.level;
	}
	
	/**
	 * set order number of object section, reserve level 0 for head of list, whole numbers for others
	 * @param n order
	 */
	public void setorder(int n){
		assert n > -1;// need enable assertions, 
		this.order = n;
		//System.out.println("order set to " + n);
	}
	
	/**
	 * set overall_order number of object section, starting at 0
	 * @param n overall_order
	 */
	public void setoverallorder(int n){
		assert n > -1;// need enable assertions, 
		this.overall_order = n;
		//System.out.println("order set to " + n);
	}
	
	/**
	 * get overall_order number of object section
	 * @return overall_order
	 */
	public int getoverallorder(){
		return this.overall_order;
	}
	
	/**
	 * get order number of object section
	 * @return order
	 */
	public int getorder(){
		return this.order;
	}
	
	/**
	 * set startpage, larger than 0
	 * @param n startpage
	 */
	public void setstartpage(int n){
		assert n > 0;// need enable assertions, 
		this.startpage = n;
		if(startpage != 0 && endpage !=0 && endpage - startpage > -1){
			this.setpagelength();
			System.out.println("order set to " + n);
		}
	}
	
	/**
	 * get startpage
	 * @return startpage
	 */
	public int getstartpage(){
		return this.startpage;
	}
	
	/**
	 * set endpage, larger than 0
	 * @param n endpage
	 */
	public void setendpage(int n){
		assert n > 0;// need enable assertions, 
		this.endpage = n;
		if(startpage != 0 && endpage !=0 && endpage - startpage > -1){
			this.setpagelength();
		}
	}
	
	/**
	 * get endpage
	 * @return endpage
	 */
	public int getendpage(){
		return this.endpage;
	}
	
	/**
	 * set section page length
	 */
	public void setpagelength(){
		this.pagelength = this.endpage - this.startpage;//section length formula
		//System.out.println("Page length updated to " + this.pagelength);
	}
	
	/**
	 * set lowerlevelnum n
	 * @param n number of sections
	 */
	public void setlowerlevelnum(int n){
		if(n < 0){ 
			System.out.println("Lower level sections was not updated because input size is " + n);
			return;
		}
		this.lowerlevelnum = n;
	}
	
	/**
	 * set upperlevelnum n and initialize size of upperlevel array n
	 * @param n number of sections
	 */
	public void setupperlevelnum(int n){
		if(n < 0){ 
			System.out.println("Lower level array was not updated because input size is " + n);
			return;
		}
			this.upperlevelnum = n;
	}
	
	/**
	 * getter lowerlevelnum
	 * @return lowerlevelnum
	 */
	public int getlowerlevelnum(){
		return lowerlevelnum;
	}
	
	/**
	 * getter upperlevelnum
	 * @return upperlevelnum
	 */
	public int getupperlevelnum(){
		return upperlevelnum;
	}
	
	/**getter lowerlevel section
	 * n is valid if n is whole number within existing bound
	 * @param n index of object section
	 * @return object section, else null
	 */
	public Section getlowerlevelsection(int n){
		if( n < 0 || n - this.lowerlevel.size() > -1 ){
			System.out.println("Input is an invalid number because lowerlevel size is " + this.lowerlevel.size() + " and your input is " + n);
			System.out.println("Returning null object");
			return null;
		}
		Section target = this.lowerlevel.get(n);
		if( target == null){
			System.out.println("Target section is empty");
		}
		return target;
	}//end of getlowerlevelsection
	
	/**getter upperlevel section
	 * n is valid if n is whole number within existing bound
	 * @param n index of object section
	 * @return object section, else null
	 */
	public Section getupperlevelsection(int n){
		if( n < 0 || n - this.upperlevel.size() > -1 ){
			System.out.println("Input is an invalid number because upperlevel size is " + this.upperlevel.size() + " and your input is " + n);
			System.out.println("Returning null object");
			return null;
		}
		Section target = this.upperlevel.get(n);
		if( target == null){
			System.out.println("Target section is empty");
		}
		return target;
	}//end of getupperlevelsection
	
	/**add lowerlevel section
	 * @param newsection new section object/reference
	 */
	public void addlowerlevelsection(Section newsection){
		this.lowerlevel.add(newsection);
		this.lowerlevelnum++;
		//System.out.println("New section is added to lower level");
	}//end of setlowerlevelsection
	
	/**add upperlevel section
	 * initialize upperlevel array index n-1 to new object section
	 * @param newsection new section object/reference
	 */
	public void addupperlevelsection(Section newsection){
		this.upperlevel.add(newsection);
		this.upperlevelnum++;
		//System.out.println("New section is added to upper level");
	}//end of setupperlevelsection
	
	/**
	 * add level, order, title and startpage num if it exists
	 * return last index plus 1 if index moved out of bound
	 * @param startindex
	 * @param counter
	 * @return tokensindex current token index
	 */
	public int addinfo(int startindex, int level, int order, Section previous, FrequencyCounter counter){
		this.setlevel(level);//add level
		this.setorder(order);//update section order
		this.addupperlevelsection(previous);//add previous reference, previous pointer
		//System.out.println( "previous title is: " + previous.gettitle() );//print previous title
		int tokensindex = startindex;
		int testtype = counter.tokentypes.get(tokensindex).get(0);//tokentype number
		String titletemp = "";
		
		while( testtype == NUM_NOTYPE || testtype == WORD_REGULAR || testtype == WORD_D1 ){
			//next token is notype number, regular word, or word plus delimiter
			titletemp = titletemp.concat( counter.tokensmodified.get(tokensindex).get(0) );//concat first token
			titletemp = titletemp.concat(" ");//add whitespace between tokens
			if( testtype == WORD_D1 ){//first token followed by second token, a delimiter,
				break;//finish appending to string, exit loop
			}
			tokensindex++;//move to next token (line)
			if(tokensindex == counter.tokensmodified.size()){//return tokensindex if index moved past bound, size = max index + 1
				return tokensindex;
			}
			
			testtype = counter.tokentypes.get(tokensindex).get(0);
		}//end of while loop
		tokensindex++;//move to next token (line), after title
		
		titletemp = titletemp.trim();//removing ending whitespace
		this.settitle(titletemp);//add title
		
		if(tokensindex == counter.tokensmodified.size()){//return tokensindex if index moved past bound, size = max index + 1
			return tokensindex;
		}
		
		testtype = counter.tokentypes.get(tokensindex).get(0);//update testtype 
		
		if( testtype - PAGE_REGULAR > -1 && testtype - PAGE_ENDMATTERS < -1 ){
			//20 <= testtype <= 24 next token is a page number, add startpage number
			int pagenum = Integer.parseInt( counter.tokensmodified.get( tokensindex ).get(0) );//get page number
			this.setstartpage( pagenum );//set start page number
		}//end of if page num present
		
		return tokensindex;
	}//end of addinfo
	
	/**
	 * print all section titles in level
	 * @param a upper or lower level sections arraylist
	 */
	public void printSectionslevel(ArrayList<Section> a){
		for(int i = 0; i < a.size(); i++){
			System.out.println( a.get(i).gettitle() );
		}
	}
	
	/**
	 * recursive method for printing title of all sections
	 * @param space is string for incrementing spacing between levels
	 */
	public void printTable(String space){//printing head section line should be optional
		if( this.level == 0){//head section
			System.out.printf("%s%s\n", space, this.gettitle() );//visit part
		}
		else if( this.match!=null){//have matching title
			System.out.printf("%s%d %s %s %f %s\n", space, this.getorder() + 1, this.gettitle(), this.matchfound.toString(), this.similarityscore, this.match.gettitle() );//visit part
		}
		else{//no matching title
			System.out.printf("%s%d %s %s %f\n", space, this.getorder() + 1, this.gettitle(), this.matchfound.toString(), this.similarityscore);//visit part
		}
		space+= space;
		if( this.lowerlevel.size() > 0){//traverse to lowerlevel because lower level is filled
			for(int i = 0;  i < this.lowerlevel.size(); i++ ){
				Section nextlevel = lowerlevel.get(i);
				nextlevel.printTable(space);
			}
		}
		else{//end recursive method
			return;
		}//end of if-else traverse lowerlevel
	}//end of printTable
	
	/**
	 * recursive method to put all sections in all lowerlevels in a list, call format label  
	 * @param thelist arraylist of all sections in all lowerlevels
	 * @return thelist arraylist accumulating sections
	 */
	public ArrayList<Section> createonelist(ArrayList<Section> thelist){//visit-left recursion order
		if( this.getlevel() != 0){//not head section, visit branch
			thelist.add(this);//add node section to list in visit-left order
			this.setlabel();//format label
		}
		
		if( this.lowerlevel.size() > 0){//traverse to lowerlevel because lower level is filled
			for(int i = 0;  i < this.lowerlevel.size(); i++ ){
				Section nextlevel = this.lowerlevel.get(i);//next section in lower level
				nextlevel.createonelist(thelist);//recursive method call
			}//end of for loop i add sections in lower level
		}
		return thelist;//returns arraylist of all sections
	}//end of createonelist
	
	/**
	 * transfer all labels in sections to a list of labels, and number a label in the section it belongs 
	 * set label colour according to similarity
	 * @param storage vector of all labels
	 * @param similaritylevel1 similarity threshold for high probability match
	 * @return
	 */
	public ArrayList<JLabel> transferAllLabels(ArrayList<JLabel> storage, double similaritylevel1, double similaritylevel2){//printing head section line should be optional
		if( this.level == 0){//head section
			overall_ordercount = 0;//set static variable to 0 at start of each head node
		}
		else{//visit node, add label
			this.setoverallorder(overall_ordercount);//save label index in section
			overall_ordercount++;//increment counter 
			if( this.similarityscore > similaritylevel1){
				this.label.setBackground(Color.GREEN);
			}
			else if( this.similarityscore > similaritylevel2){
				this.label.setBackground(Color.YELLOW);
			}
			else{
				this.label.setBackground(Color.RED);
			}
			if(this.level==1){
				this.label.setBorder(BorderFactory.createLineBorder(Color.black));//set black line border around chapter titles
			}
			storage.add(this.label);
		}
		if( this.lowerlevel.size() > 0){//traverse to lowerlevel because lower level is filled
			for(int i = 0;  i < this.lowerlevel.size(); i++ ){
				Section nextlevel = lowerlevel.get(i);
				nextlevel.transferAllLabels(storage, similaritylevel1, similaritylevel2);
			}
		}
		else{//end recursive method
			return storage;
		}//end of if-else traverse lowerlevel
		return storage;//this line may not do anything, just to keep compiler happy
	}//end of printTable
	
	/**
	 * save matching sections' indices in a 2d array
	 * @param arr storage array for matching indices
	 * @return arr updated 2d arr
	 */
	public int[][] getmatchinglabelindices(int[][] arr){
		if( this.level == 0){//head section
			//do nothing
		}
		else{//visit node, add label indices
			arr[this.overall_order][0] = this.overall_order;//save index of this section
			//System.out.print( this.overall_order+ " " + this.title);
			
			if( this.matchfound ){//has matching section title, save current index
				arr[this.overall_order][1] = this.match.overall_order;//save index of matching section's order
				//System.out.println(" " + this.match.overall_order );
			}
			else{
				arr[this.overall_order][1] = -1;//set negative values for those sections that have no matching section
				//System.out.println(" -1");
			}
		}
		
		if( this.lowerlevel.size() > 0){//traverse to lowerlevel because lower level is filled
			for(int i = 0;  i < this.lowerlevel.size(); i++ ){
				Section nextlevel = lowerlevel.get(i);
				nextlevel.getmatchinglabelindices(arr);
			}
		}
		else{//end recursive method
			return arr;
		}//end of if-else traverse lowerlevel
		return arr;//this line may not do anything, just to keep compiler happy
	}
	
	
}//end of section class
