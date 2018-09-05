import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Project3_random {

	
	static HashMap<Integer,ArrayList<Integer>> hmpClauseKeyOrValue=new HashMap<Integer,ArrayList<Integer>>();
	static Set<Integer> variableSet=new HashSet<Integer>();
	static int noOfSatisfiableClause=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File file = new File("src/input3.txt");
			Scanner sc = new Scanner(file);
			
			//Fetch all points and store in arrPoints Arraylist
		
			while(sc.hasNext())
			{
				int currClauseVal1=sc.nextInt();
				int currClauseVal2=sc.nextInt();
				ArrayList<Integer> arrTemp=hmpClauseKeyOrValue.get(currClauseVal1)==null?new ArrayList<Integer>():hmpClauseKeyOrValue.get(currClauseVal1);
				arrTemp.add(currClauseVal2);
				hmpClauseKeyOrValue.put(currClauseVal1,arrTemp);
				
				arrTemp=hmpClauseKeyOrValue.get(currClauseVal2)==null?new ArrayList<Integer>():hmpClauseKeyOrValue.get(currClauseVal2);
				arrTemp.add(currClauseVal1);
				hmpClauseKeyOrValue.put(currClauseVal2,arrTemp);
				
				
				currClauseVal1=Integer.signum(currClauseVal1)==-1?-currClauseVal1:currClauseVal1;
				currClauseVal2=Integer.signum(currClauseVal2)==-1?-currClauseVal2:currClauseVal2;
				
				variableSet.add(currClauseVal1);
				variableSet.add(currClauseVal2);
				
			}
			
		
			
			boolean[] inputInstance=new boolean[variableSet.size()];
			
			Arrays.fill(inputInstance, true);
			inputInstance[0]=false;
			inputInstance[1]=false;
			inputInstance[2]=false;
			noOfSatisfiableClause=getNoOfSatisfiableClauseWithCurrentConfig(inputInstance);
			
			//System.out.println(noOfSatisfiableClause);
			
			int exitVar=inputInstance.length*inputInstance.length;
			for(int i=0;i<exitVar;i++)
			{
				int flipIdx=getVariableToFlip(inputInstance.length);
			
				
				int changeInSatisfiability=getChangeInSatisfiability(inputInstance, flipIdx);
				if(changeInSatisfiability>=0)
				{
					inputInstance[flipIdx]=inputInstance[flipIdx]?false:true;
					noOfSatisfiableClause=noOfSatisfiableClause+changeInSatisfiability;
					//System.out.println(noOfSatisfiableClause  + "    "+ Arrays.toString(inputInstance));
				}
				else
				{
					
					//System.out.println("No gain in Satisfiability by changing X" + flipIdx);
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
		}

}
	
	
	
	public static int getChangeInSatisfiability(boolean[] inputInstance, int flipBit)
	{
		int ChangeInSatisfiability=0;
		
		//IF the bit is to be changed from true to false
		if(inputInstance[flipBit])
		{
			ArrayList<Integer> arrClause=hmpClauseKeyOrValue.get(flipBit+1);
				
			//Eliminating all the clauses which were true and which have changed to false due to changing variable corresponding to flipBit from true to false
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
			//Including all the clauses which were false and which have changed to true due to changing variable corresponding to flipBit from true to false	
				arrClause=hmpClauseKeyOrValue.get(-(flipBit+1));
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
		
		
		
		}
		
		else //IF the bit is to be changed from false to true
			if(!inputInstance[flipBit])
			{
				ArrayList<Integer> arrClause=hmpClauseKeyOrValue.get(-(flipBit+1));
					
				//Eliminating all the clauses which were true and which have changed to false due to changing variable corresponding to flipBit from false to true	
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
					
					arrClause=hmpClauseKeyOrValue.get((flipBit+1));
					//Including all the clauses which were false and which have changed to true due to changing variable corresponding to flipBit from false to true	
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
			
			
			
			}
		
		
		return ChangeInSatisfiability ;
	}
	
	
	
	
	
	
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
	
	
	
	
	
	
	
	public static int getVariableToFlip(int sizeOfInput)
	{
		int flipVariable=0;
		Random rand = new Random();
		flipVariable=rand.nextInt(sizeOfInput);
		
		return flipVariable;
	}
	
	
}
