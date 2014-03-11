import java.io.*;
import java.util.Random;

class pitHoleException extends Exception
{
	public String toString()
	{
	return "In same state";
	}
}

public class DataOfMDP
{
	static InputStreamReader r;
	static BufferedReader input;

	double world[][],utility[][],initial_utility[][],error[][];
	int world_size_rows,world_size_columns;
	double DISCOUNT;

	String optimal_policy[][];

	final double INFINITY=9999999;
	final double SEED=INFINITY/10;
	final int UP=0;
	final int DOWN=1;
	final int LEFT=2;
	final int RIGHT=3;
	final int CORRECT=0;
	final double TRANSITION[]={0.8,0.1,0.1};

	final int TRANSIT_MATRIX[][]=	{
						{UP,-1,0},
						{DOWN,1,0},
						{LEFT,0,-1},
						{RIGHT,0,1}
					};
	final int ROW_TRANSFORM=1;
	final int COLUMN_TRANSFORM=2;
						



	DataOfMDP()throws IOException
	{
	r=new InputStreamReader(System.in);
	input=new BufferedReader(r);

		//echo("Welcome to the world of AI");
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	void driver()throws IOException
	{
		takeInput();
		computeUtilities();
		show_utility();
		show_policy();
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	void computeUtilities()throws IOException
	{
	int i=-1,j=-1; //classic rare error in the for loop; since out of j for loop;j gets deleted and compile error for if loop
		//try by dynamic initialization of i,j in the for loop

	double error_value=0,count=0;
	initial_utility=new double[world_size_rows][world_size_columns];

	double stop_condition=((0.0000001)*(1-DISCOUNT))/DISCOUNT;

	echo("=========== BEGINNING TO COMPUTE UTILITIES =============");
	
		do
		{
		copy_to_initial_utility();

			for(i=0;i<world_size_rows;i++)
			{
				for(j=0;j<world_size_columns;j++)
				{
				
					if(world[i][j]==(INFINITY/SEED)*(-1))
					continue;
	
					if(world[i][j]>SEED)
					utility[i][j]=(world[i][j]-INFINITY);//+(DISCOUNT)*maximum_Bell(i,j);
					else
					{
					utility[i][j]=world[i][j]+(DISCOUNT)*maximum_Bell(i,j);
				
					}

				}

			}


		count++;		
		}
		while(count<100/*error_value<stop_condition*/);


		
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	double maximum_Bell(int i,int j)throws IOException
	{
		//echo("inside maximum_Bell()");
	int index=-1;
	double max=(-1)*INFINITY;
	double sum=0;
	double correct_action[]=new double[4];

		for(int transition=UP;transition<=RIGHT;transition++)
		{
			if((correct_action[transition]=transit(i,j,transition))>max)
			{
			max=correct_action[transition];
			index=transition;
			optimal_policy[i][j]=map(transition);
			}
			
	
		}
		//-------------------------------------------------
		//char c;
		//echo("=========================== MAXIMUM BELL ===================================");
		//echo("The max value for state ("+i+","+j+") occurs for transtion "+ map(index)+" and is "+max);
		//echoc("Press any key to continue");
		//c=(char)input.read();
		//echo("============================================================================");

		//-------------------------------------------------

	return max;
	}

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	double transit(int i,int j,int CORRECT_ACTION)throws IOException
	{
	//echo("inside transit()");

	int OPPOSITE_ACTION,current_action;
	double sum=0;
	int index=CORRECT;

	OPPOSITE_ACTION=(CORRECT_ACTION%2)==0?CORRECT_ACTION+1:CORRECT_ACTION-1;

		//echo("CORRECT_ACTION ="+map(CORRECT_ACTION));
		//echo("OPPOSITE_ACTION ="+map(OPPOSITE_ACTION));

	current_action=CORRECT_ACTION;
	sum+=0.8*get_transition_utility(i,j,current_action);

		current_action=0;

			while(current_action<4)
			{
				if(current_action==OPPOSITE_ACTION||current_action==CORRECT_ACTION)
				{
				current_action++;
				continue;
				}
				
			sum+=(0.1*get_transition_utility(i,j,current_action));

			current_action++;
			}
	return sum;	
	}

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	double get_transition_utility(int i,int j,int current_action)throws IOException
	{
	double transit_utility=-1;
	int row_transform=-1,column_transform=-1;
	
			try
			{
			row_transform=i+TRANSIT_MATRIX[current_action][ROW_TRANSFORM];
			column_transform=j+TRANSIT_MATRIX[current_action][COLUMN_TRANSFORM];

			transit_utility=initial_utility[row_transform][column_transform];

			//-------------------------------------------------------
			//char c;
			//echoc("The action "+map(current_action)+" changes the state from ("+i+","+j+") ");
			//echoc("to ("+row_transform+","+column_transform+")\n");
			//echoc("Press any key to continue");
			//c=(char)input.read();

			//------------------------------------------------------

				if(transit_utility==((INFINITY/SEED)*(-1)))
				throw new pitHoleException();


				//if(transit_utility>SEED)
				//throw new pitHoleException();

				if(transit_utility>SEED)
				transit_utility-=INFINITY;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				//-------------------------------------------------
				//char c;
				//echo("=========================== TRANSITION  ===================================");
				//echo("The transit utility for "+map(current_action)+" is "+transit_utility);
				//echo("Press any key to continue");
				//c=(char)input.read();
				//echo("============================================================================");
				//-------------------------------------------------

			return initial_utility[i][j];
			}
			catch(pitHoleException e)
			{
				//-------------------------------------------------
				//char c;
				//echo("=========================== TRANSITION  ===================================");
				//echo("The transit utility for "+map(current_action)+" is "+transit_utility);
				//echo("Press any key to continue");
				//c=(char)input.read();
				//echo("============================================================================");
				//-------------------------------------------------

			return initial_utility[i][j];
			}
		//-------------------------------------------------
		//char c;
		//echo("=========================== TRANSITION  ===================================");
		//echo("The transit utility for "+map(current_action)+" is "+transit_utility);
		//echo("Press any key to continue");
		//c=(char)input.read();
		//echo("============================================================================");

		//-------------------------------------------------

	return transit_utility;
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	void copy_to_initial_utility()
	{
	double x;
		for(int i=0;i<world_size_rows;i++)
			for(int j=0;j<world_size_columns;j++)
			{
			x=utility[i][j];
			initial_utility[i][j]=x;
			}
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	double compute_error()
	{
	double error_value=0,sum=0;
	int N=world_size_rows*world_size_columns;

		for(int i=0;i<world_size_rows;i++)
			for(int j=0;j<world_size_columns;j++)
			{
				if(error[i][j]>0)
				sum+=error[i][j];
				else
				sum-=error[i][j];
			}
			

	error_value=sum/N;
	return error_value;
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	void takeInput()throws IOException
	{
	
	echo("Welcome to takeInput()");

		echo("Please enter the no of rows of the world");
		world_size_rows=Integer.parseInt(input.readLine());

		echo("Please enter the no of columns of the world");
		world_size_columns=Integer.parseInt(input.readLine());

	world=new double[world_size_rows][world_size_columns];
	utility=new double[world_size_rows][world_size_columns];
	error=new double[world_size_rows][world_size_columns];
	optimal_policy=new String[world_size_rows][world_size_columns];

	for(int i=0;i<world_size_rows;i++)
	for(int j=0;j<world_size_columns;j++)
	optimal_policy[i][j]=new String("null");


		echo("Does the universe have a fixed reward for all states....Y/N");
		if(input.readLine().equals("Y"))
		initialize_world(true);

		ask_and_set_dead_states();

			
			set_terminal_states();

		echo("Last question I promise\n");
		echo("Please enter the discount value");
		DISCOUNT=Double.parseDouble(input.readLine());

	echo("Well those were a lot of questions...Here is the world that you entered");
	show_world();
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	void initialize_world(boolean static_reward)throws IOException
	{
		if(static_reward)
		{
			echo("Please enter the reward value");
			double reward=Double.parseDouble(input.readLine());

			for(int i=0;i<world_size_rows;i++)
			for(int j=0;j<world_size_columns;j++)
			world[i][j]=reward;
		}
		else
		{
			for(int i=0;i<world_size_rows;i++)
				for(int j=0;j<world_size_columns;j++)
				{
				echo("Please enter the reward for state ["+i+","+j+"]");
				world[i][j]=Double.parseDouble(input.readLine());
				}			
		}
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	void set_terminal_states()throws IOException
	{
	int n=-1;

		echo("Please enter the number of terminal states");
		n=Integer.parseInt(input.readLine());

	int count=0;
	int row,column;
	double reward;

		while(count++<n)
		{
			echo("Please enter the row no of the terminal state");
			row=Integer.parseInt(input.readLine());

			echo("Please enter the column no of the terminal state");
			column=Integer.parseInt(input.readLine());

			echo("Please enter the reward for the terminal state");
			reward=Double.parseDouble(input.readLine());

			world[row][column]=reward+INFINITY;
			utility[row][column]=reward;
		}
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

	void ask_and_set_dead_states()throws IOException
	{
	int n=-1;

		echo("How many inaccesible (dead) states are present");
		n=Integer.parseInt(input.readLine());

			if(n>0)
			{
			int row,column;

				for(int i=0;i<n;i++)
				{
					echo("Enter the row no for dead state "+(i+1));
					row=Integer.parseInt(input.readLine());

					echo("Enter the column no for dead state "+(i+1));
					column=Integer.parseInt(input.readLine());

				world[row][column]=(INFINITY/SEED)*(-1);
				utility[row][column]=world[row][column];
				}
			}
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	String map(int x)
	{
		switch(x)
		{
		case 0:return "UP";
		case 1:return "DOWN";
		case 2:return "LEFT";
		case 3:return "RIGHT";
		}
	return "ERROR";
	}
	void show_world()
	{
		for(int i=0;i<world_size_rows;i++)
		{
			for(int j=0;j<world_size_columns;j++)
			{
				if(world[i][j]>SEED)
				echoc(""+(world[i][j]-INFINITY)+"\t");
				else
				echoc(""+world[i][j]+"\t");
			}
		echo("");
		}
	}

	void show_utility()
	{
	echo("\n\n=============== UTILITY MATRIX =======================\n");

		for(int i=0;i<world_size_rows;i++)
		{
			for(int j=0;j<world_size_columns;j++)
			{
			echoc(""+utility[i][j]+"\t\t");
			}
		echo("");
		}

	echo("===================================================");
	}

	void show_policy()
	{
	echo("\n\n============== OPTIMUM POLICY =======================\n");

		for(int i=0;i<world_size_rows;i++)
		{
			for(int j=0;j<world_size_columns;j++)
			{
			echoc(""+optimal_policy[i][j]+"\t");
			}
		echo("");
		}

	echo("===================================================");
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	void echo(String s)
	{
		System.out.println(s);
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	void echoc(String s)
	{
		System.out.print(s);
	}
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
}
