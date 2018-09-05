import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * @author sachin bhalekar 
 *
 *The below local search algorithm is used to find solution for Max2SAT problem.
 *The algorithm uses breakcount (The number of clauses that become false after changing the value of a variable) value for each variable and considers variable having minimum break count
 *to search for neighours. If the variables with minimum break count do not yield a better solution after flipping values
 *then the input is chosen randomly and the algorithm starts local search with the new input instance. 
 * */

public class Project3 {

	
	static HashMap<Integer,ArrayList<Integer>> hmpClauseKeyOrValue=new HashMap<Integer,ArrayList<Integer>>();
	//static HashMap<Integer,Integer> hmpMakeCount=new HashMap<Integer,Integer>();
	static HashMap<Integer,Integer> hmpBreakCount=new HashMap<Integer,Integer>();
	//static HashMap<Integer,Integer> hmpDifMakeCount_BreakCount=new HashMap<Integer,Integer>();
	static Set<Integer> variableSet=new HashSet<Integer>();
	static int noOfSatisfiableClause=0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File file = new File("input3.txt");
			Scanner sc = new Scanner(file);
			
			//Fetch all points and store in arrPoints Arraylist
			
			while(sc.hasNext())
			{
				int currClauseVal1=sc.nextInt();
				int currClauseVal2=sc.nextInt();
				ArrayList<Integer> arrTemp=hmpClauseKeyOrValue.get(currClauseVal1)==null?new ArrayList<Integer>():hmpClauseKeyOrValue.get(currClauseVal1);
				arrTemp.add(currClauseVal2);
				hmpClauseKeyOrValue.put(currClauseVal1,arrTemp);
				
				if(hmpClauseKeyOrValue.get(-currClauseVal1)==null)
				{
					hmpClauseKeyOrValue.put(-currClauseVal1,new ArrayList<Integer>());
				}
				
				
				
				arrTemp=hmpClauseKeyOrValue.get(currClauseVal2)==null?new ArrayList<Integer>():hmpClauseKeyOrValue.get(currClauseVal2);
				arrTemp.add(currClauseVal1);
				hmpClauseKeyOrValue.put(currClauseVal2,arrTemp);
				
				if(hmpClauseKeyOrValue.get(-currClauseVal2)==null)
				{
					hmpClauseKeyOrValue.put(-currClauseVal2,new ArrayList<Integer>());
				}
				
				
				currClauseVal1=Integer.signum(currClauseVal1)==-1?-currClauseVal1:currClauseVal1;
				currClauseVal2=Integer.signum(currClauseVal2)==-1?-currClauseVal2:currClauseVal2;
				
				variableSet.add(currClauseVal1);
				variableSet.add(currClauseVal2);
				
			}
			
					
			boolean[] inputInstance=generateRandomInputInstance(variableSet.size());
			
			
			/*Get Number of Satisfiable Clauses with the initial configuration of input
			All the clauses that have outcome as true as per current variable configuration are satisfiable*/
			
			noOfSatisfiableClause=getNoOfSatisfiableClauseWithCurrentConfig(inputInstance);
			
			
			/*Get Break count for each variable with respect to 
			initial configuration. The Breakcount for a variable is the number of clauses that will become false if the value
			of the variable was to be changed*/
			getBreakCount(inputInstance);
			
			/*Get Make count for each variable with respect to initial configuration
			 *  The makecount for a variable is the number of clauses that will become true if the value
			of the variable was to be changed*/
			//getMakeCount(inputInstance);
			
			
			/* for (Entry<Integer, Integer> entryMake: hmpMakeCount.entrySet()) {  // Iterate through hashmap
				 hmpDifMakeCount_BreakCount.put(entryMake.getKey(), entryMake.getValue()-hmpBreakCount.get(entryMake.getKey())); //update the difference of makecount and break count for each variable
		        }*/
			
			
			
			// The runtime of the algorithm can be adjusted by controlling the value of exit variable
			int exitVar=inputInstance.length*inputInstance.length;
			for(int i=0;i<exitVar;i++)
			{
				int flipIdx=getVariableToFlip(inputInstance.length,inputInstance);
			
				
			
				if(flipIdx!=-1)
				{
				//Update the new breakCount for All the variables if flipIdx variable value is flipped
				updateBreakCount( flipIdx,inputInstance);
				inputInstance[flipIdx-1]=inputInstance[flipIdx-1]?false:true;
				}	
				else
				{
					//Reset the input instance randomly	if none of the minimum break count variables help in 
					//improving the result 
					
					
					boolean[] newInputInstance=generateRandomInputInstance(inputInstance.length);
					
					int noOfSatisfiableClauseWithNewInput=getNoOfSatisfiableClauseWithCurrentConfig(newInputInstance);
					//if the new randomly generated result gives value as good as the previous input or better than previous then accept the new 
					//input for further local searching
					if(noOfSatisfiableClauseWithNewInput>=noOfSatisfiableClause)
					{
						noOfSatisfiableClause=noOfSatisfiableClauseWithNewInput;
						inputInstance=newInputInstance;
						getBreakCount(inputInstance);
					}
					
					
					
				}
				
			}
			
			
		StringBuffer strInput=new StringBuffer();	
		for(int i=0;i<inputInstance.length;i++)
		{
			if(inputInstance[i])
			{
				strInput.append('T');
			}
			else
			{
				strInput.append('F');
			}
		}
		
		System.out.println(noOfSatisfiableClause+" "+strInput);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}

}
	

	//This method is used to generate a random inputInstance for local searching
	
	/**
	 * @param length
	 * @return
	 */
	public static boolean[] generateRandomInputInstance(int length)
	{
		boolean[] inputInstance=new boolean[length];
		Arrays.fill(inputInstance, false);
		Random randomObj=new Random();
		
		for(int i=0;i<length;i++)
		{
			int idx=randomObj.nextInt(length);
			inputInstance[idx]=!inputInstance[idx];
		}
		
		return inputInstance;
		
	}
	
	
	/**
	 * @param inputInstance
	 */
	//This method is used to initialize the break count of each variable based upon the current input configuration
	public static void getBreakCount(boolean[] inputInstance)
	{
		ArrayList<Integer> arrClause=null;
		//Calculating Break count for every variable
		//as a count of all the clauses which were true and which have changed to false due to changing variable corresponding to i	
		for(int i=0;i<inputInstance.length;i++)
		{
			hmpBreakCount.put(i+1, 0);
			
			if(inputInstance[i])
			{
				//IF the bit is to be changed from true to false
				arrClause=hmpClauseKeyOrValue.get(i+1);
				
			}
			else
			{
				//IF the bit is to be changed from false to true
				arrClause=hmpClauseKeyOrValue.get(-(i+1));
			}
					
				
					for(int i1=0;i1<arrClause.size();i1++)
					{
						int seconVariableInClause=arrClause.get(i1);
						//if OR with a complement seconVariableInClause and value of seconVariableInClause is true increment breakcount
						if(Integer.signum(seconVariableInClause)==-1 && inputInstance[(-seconVariableInClause)-1])
						{
							Integer count=hmpBreakCount.get(i+1);
							count=count==null?0:count;
							count++;
							hmpBreakCount.put(i+1, count);
						}
						//if OR with a seconVariableInClause and value of seconVariableInClause is false increment breakcount
						else if(Integer.signum(seconVariableInClause)!=-1 && !inputInstance[seconVariableInClause-1])
						{
							Integer count=hmpBreakCount.get(i+1);
							count=count==null?0:count;
							count++;
							hmpBreakCount.put(i+1, count);
						}
						
					}
				
		
		}
		
		
	}
	
	
	
	
	
	/**
	 * @param inputInstance
	 * @param flipBit
	 * @return
	 */
	//This method returns positive value if flipping flipBit improves satisfiability and negative value if satisfiability decreases
	public static int getChangeInSatisfiability(boolean[] inputInstance, int flipBit)
	{
		int ChangeInSatisfiability=0;
		
		
		ArrayList<Integer> arrClause=null;
		
		
		//Eliminating all the clauses which were true and which have changed to false due to changing variable corresponding to flipBit
		if(inputInstance[flipBit])
		{
			//IF the bit is to be changed from true to false
			arrClause=hmpClauseKeyOrValue.get(flipBit+1);
			
		}
		else
		{
			//IF the bit is to be changed from false to true
			arrClause=hmpClauseKeyOrValue.get(-(flipBit+1));
		}
				
			
				for(int i=0;i<arrClause.size();i++)
				{
					int seconVariableInClause=arrClause.get(i);
					if(Integer.signum(seconVariableInClause)==-1 && inputInstance[(-seconVariableInClause)-1])
					{
						ChangeInSatisfiability--;
					}
					else if(Integer.signum(seconVariableInClause)!=-1 && !inputInstance[seconVariableInClause-1])
					{
						ChangeInSatisfiability--;
					}
					
				}
			
				
				
		//Including all the clauses which were false and which have changed to true due to changing variable corresponding to flipBit	
				
			
				if(inputInstance[flipBit])
				{
					//IF the bit is to be changed from true to false
					arrClause=hmpClauseKeyOrValue.get(-(flipBit+1));
					
				}
				else
				{
					//IF the bit is to be changed from false to true
					arrClause=hmpClauseKeyOrValue.get((flipBit+1));
				}
				
				
				for(int i=0;i<arrClause.size();i++)
				{
					int seconVariableInClause=arrClause.get(i);
					if(Integer.signum(seconVariableInClause)==-1 && inputInstance[(-seconVariableInClause)-1])
					{
						ChangeInSatisfiability++;
					}
					else if(Integer.signum(seconVariableInClause)!=-1 && !inputInstance[seconVariableInClause-1])
					{
						ChangeInSatisfiability++;
					}
					
				}
		
		return ChangeInSatisfiability ;
	}
	
	
	
	
	
	
	/**
	 * @param inputInstance
	 * @return
	 */
	//This method returns the number of clauses that are true as per the current input configuration
	public static int getNoOfSatisfiableClauseWithCurrentConfig(boolean[] inputInstance)
	{
		int currentNoOfSatisfiable=0;
		int doubleCountedClauses=0;
		
		for(int i=0;i<inputInstance.length;i++)
		{
			int fetchClauseFor=inputInstance[i]?i+1:-(i+1);
			
					ArrayList<Integer> arrClause=hmpClauseKeyOrValue.get(fetchClauseFor);
					currentNoOfSatisfiable=currentNoOfSatisfiable+arrClause.size();
					
					for(int j=0;j<arrClause.size();j++)
					{
						int seconVariableInClause=arrClause.get(j);
						
						if(Integer.signum(seconVariableInClause)==-1 && !inputInstance[(-seconVariableInClause)-1])
						{
							doubleCountedClauses++;
						}
						else if(Integer.signum(seconVariableInClause)!=-1 && inputInstance[seconVariableInClause-1])
						{
							doubleCountedClauses++;
						}
								
						
					}
				
		}
		
		
		return currentNoOfSatisfiable - doubleCountedClauses/2;
	}
	
	
	
	
	
	
	
	
	/*This function returns a variable having minimum break count and flipping this variable improves the result.
	 * If the variable having minimum break count does not improve the result then the function returns -1
	 * 
*/	/**
 * @param sizeOfInput
 * @param inputInstance
 * @return
 */
public static int getVariableToFlip(int sizeOfInput, boolean[] inputInstance)
	{
		int flipVariable=-1;
		
        /*//Check for variables having zero break count i.e. variable whose value change will not break any clause.
		If no variable found with zero break count the find the variable with minimum break count*/
		Entry<Integer, Integer> entryDifMakeCountBreakCount=null;
        Entry<Integer, Integer> entryMinBreakCount=null;
		int minValueBreakCount=(Collections.min(hmpBreakCount.values()));  // This will return min value in the Hashmap
        
		
		ArrayList<Integer> arrMinBreakCount=new ArrayList<>();
		
		for (Entry<Integer, Integer> entry: hmpBreakCount.entrySet()) {  // Iterate through hashmap
            if (entry.getValue()==minValueBreakCount) {
            	entryMinBreakCount=entry;     // entry with min breakcount value
            	
            	arrMinBreakCount.add(entry.getKey()); // Store all the variables having minimum break count
            }
        }
				
		for(int i=0;i<arrMinBreakCount.size();i++)
		{
			/* if more than one variable having minimum break count then randomly choose one of the variables */
			Random rand = new Random();
			int randMinBreakCOuntVar=rand.nextInt(arrMinBreakCount.size());	 	
			
			/*Now check if flipping the value of the randomly chosen variable having minimum break count increases the number of satisfiable clauses
			 * If it does improve the result then flip the variable, update the noOfSatisfiableClause and break from this loop
			 *  */
			int changeInSatisfiability=getChangeInSatisfiability(inputInstance, arrMinBreakCount.get(randMinBreakCOuntVar)-1);
			if(changeInSatisfiability>=0)
			{
				noOfSatisfiableClause=noOfSatisfiableClause+changeInSatisfiability;
				flipVariable=arrMinBreakCount.get(randMinBreakCOuntVar);
				break;
					
			}
			else
			{
				//if the chosen minimum break count variable does not improve the result remove it from consideration for next iteration.
				arrMinBreakCount.remove(randMinBreakCOuntVar);
			}
		
		}
		
        
		//return the variable to be flipped to update in inputInstance . If the value returned is -1 then it means
		// that flipping the values of the variables having 
		return flipVariable;
	}
	
	
	
	
//This method is used to update the break point of all the variables if the value of variable flipVariable is changed
	/**
	 * @param flipVariable
	 * @param inputInstance
	 */
	public static void updateBreakCount(int flipVariable, boolean[] inputInstance)
	{
		
		ArrayList<Integer> arrClauseP=hmpClauseKeyOrValue.get(flipVariable);
		ArrayList<Integer> arrClauseN=hmpClauseKeyOrValue.get(-flipVariable);
			
			
			for(int i=0;i<arrClauseP.size();i++)
			{
				int seconVariableInClause=arrClauseP.get(i);
				if(Integer.signum(seconVariableInClause)==-1 && !inputInstance[(-seconVariableInClause)-1])
				{
					int currBreakCount=hmpBreakCount.get(-seconVariableInClause);
					
					                                
					currBreakCount=inputInstance[flipVariable-1]?currBreakCount+1:currBreakCount-1;
					hmpBreakCount.put(-seconVariableInClause,currBreakCount);
				}
				
				else if(Integer.signum(seconVariableInClause)!=-1 && inputInstance[seconVariableInClause-1])
				{
					int currBreakCount=hmpBreakCount.get(arrClauseP.get(i));
					currBreakCount=inputInstance[flipVariable-1]?currBreakCount+1:currBreakCount-1;
					hmpBreakCount.put(arrClauseP.get(i),currBreakCount);
				}
				else 
				{
					int currBreakCount=hmpBreakCount.get(flipVariable);
					
					currBreakCount=!inputInstance[flipVariable-1]?currBreakCount+1:currBreakCount-1;
					hmpBreakCount.put(flipVariable,currBreakCount);
					
				}
				
				
			}
			
			for(int i=0;i<arrClauseN.size();i++)
			{
				
				int seconVariableInClause=arrClauseN.get(i);
				if(Integer.signum(seconVariableInClause)==-1 && !inputInstance[(-seconVariableInClause)-1])
				{
					int currBreakCount=hmpBreakCount.get(-seconVariableInClause);
					currBreakCount=inputInstance[flipVariable-1]?currBreakCount-1:currBreakCount+1;
					hmpBreakCount.put(-seconVariableInClause,currBreakCount);
				}
				
				else if(Integer.signum(seconVariableInClause)!=-1 && inputInstance[seconVariableInClause-1])
				{
					int currBreakCount=hmpBreakCount.get(arrClauseN.get(i));
					currBreakCount=inputInstance[flipVariable-1]?currBreakCount-1:currBreakCount+1;
					hmpBreakCount.put(arrClauseN.get(i),currBreakCount);
				}
				else 
				{
					int currBreakCount=hmpBreakCount.get(flipVariable);
					currBreakCount=!inputInstance[flipVariable-1]?currBreakCount-1:currBreakCount+1;
					hmpBreakCount.put(flipVariable,currBreakCount);
					
				}
				
				
			}
			
		
	}
	
	
	
	
	
	/*public static void getMakeCount(boolean[] inputInstance)
	{
		ArrayList<Integer> arrClause=null;
		//Calculating Make count for every variable
		//as a count of all the clauses which were false and which have changed to true due to changing variable corresponding to i	
		for(int i=0;i<inputInstance.length;i++)
		{
			if(inputInstance[i])
			{
				//IF the bit is to be changed from true to false
				arrClause=hmpClauseKeyOrValue.get(-(i+1));
				
			}
			else
			{
				//IF the bit is to be changed from false to true
				arrClause=hmpClauseKeyOrValue.get((i+1));
			}
			
			
			for(int i1=0;i1<arrClause.size();i1++)
			{
				int seconVariableInClause=arrClause.get(i1);
				
				if(Integer.signum(seconVariableInClause)==-1 && inputInstance[(-seconVariableInClause)-1])
				{
					Integer count=hmpMakeCount.get(i);
					count=count==null?0:count;
					count++;
					hmpMakeCount.put(i, count);
					
				}
				else if(Integer.signum(seconVariableInClause)!=-1 && !inputInstance[seconVariableInClause-1])
				{
					Integer count=hmpMakeCount.get(i);
					count=count==null?0:count;
					count++;
					hmpMakeCount.put(i, count);
				}
				
			}
	
	
		}
		
		
	}
	*/
	
}
