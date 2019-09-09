package ezHealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
//add comments
public class Read {
	/**
	 * Write's to an text file and outputs it.
	 * 
	 * @param output The name of the file to write to.
	 * @param data   The data being written into the File.
	 */
	private static void writeFile(String output, String data) {
		FileWriter fw;
		try {
			fw = new FileWriter(new File(output));
			fw.write(data);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String[]> readin(String fileAddress, boolean ignoreHeader) {
		List<String[]> test = new ArrayList<String[]>();
		BufferedReader br = null;
		String data[];
		String line = "";
		try {
			br = new BufferedReader(new FileReader(fileAddress));
			if (ignoreHeader == true)
				br.readLine();
			while ((line = br.readLine()) != null) {
				data = line.split(",");
				test.add(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return test;
	}
	
	private static String[][] conv(List<String[]> arr){
		String [][] arrtemp= new String [arr.size()][5];
		int count =  0;
		for (String [] i : arr) {
			arrtemp [count][0] = i[3]+"_"+i[5];
			arrtemp [count][1] = i[11];
			arrtemp [count][2] = i[12];
			try {
				arrtemp [count][3] = i[18];
		      } catch(ArrayIndexOutOfBoundsException e) {
		    	  arrtemp [count][3] = "";
		      }
			try {
				arrtemp [count][4] = i[19];
		      } catch(ArrayIndexOutOfBoundsException e) {
		    	  arrtemp [count][4] = "";  
		      }
			count++;
		}
		return arrtemp;
	}

	private static void sortbyColumn(String arr[][], int col) 
    { 
        // Using built-in sort function Arrays.sort 
        Arrays.sort(arr, new Comparator<String[]>() { 
            
          @Override              
          // Compare values according to columns 
          public int compare(final String[] entry1,  
                             final String[] entry2) { 
  
            // To sort in descending order revert  
            // the '>' Operator 
            if (entry1[col].compareTo(entry2[col]) > 0) 
                return 1; 
            else if (entry1[col].compareTo(entry2[col]) < 0)
                return -1;
            else
            	return 0;
          } 
        });  // End of function call sort(). 
    }	
	
	private static List<String[]> filter(String fileAddress, boolean ignoreHeader) {
		List<String[]> test = new ArrayList<String[]>();
		List<String[]> e = readin(fileAddress, ignoreHeader);
		String [][] data = conv(e);
		sortbyColumn(data, 0);
		String templist [];
		int count = 0;
		float avg = 0;
		String unit = "";
		String unitper = "";
		for (String[] i : data) {
			if (count == 0) {
				for (String[] j : data) {
					if (i[0].equalsIgnoreCase(j[0])) {
						avg += (Float.parseFloat(i[1]) + Float.parseFloat(i[2]))/2;
						unit +=i[3]+",";
						unitper +=i[4]+",";
						count++;
					}
				}
			templist = new String [] {i[0], String.valueOf(avg/count), unit, unitper};
			test.add(templist);
			unit = "";
			avg =0;
			unitper = "";
			}
			count--;
		}
		return test;
	}
	
	private static String exist(String line) {
		String [] temp = line.split(","); 
		for (int k = 0; k < temp.length; k++) {
			if (!temp[k].isEmpty()) {
				return temp[k];
			}
		}
		return "";
	}
	
	private static float price(String p, String u) {
		if (u.isEmpty()) return Float.parseFloat(p);
		Float tot = Float.parseFloat(p)/Float.parseFloat(u);
		return tot;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Please wait for graph construction!");
		List<String[]> data = filter("_data/price_csv.csv", true);
		List<Food> food = new LinkedList<Food>();
		//FoodList newData = new FoodList(data.size());
		String output = "";
		for (String[] i : data) {
			//System.out.println(i[0] + " " + i[1] + " " + i[2] + " " + i[3]);
			Food temp = new Food(i[0], (price(i[1],exist(i[2]))));
			food.add(temp);
			output += "\n"+ i[0] + " ," + String.valueOf(price(i[1],exist(i[2])));
			if (!exist(i[3]).isEmpty()) {
				output += " ,"+exist(i[3]);
			}
		}
		writeFile("_data/price.csv", output);
		
		// Sort food list
		Collections.sort(food);
		
		// Code to create graph
		EdgeWeightedDigraph g = new EdgeWeightedDigraph(food.size());
		
		// Print sorted foods
		PrintStream foodStream = new PrintStream(new File("_data/foods.txt"));
		for (int i = 0; i < food.size(); i++) {
			foodStream.println(food.get(i).toString());
		}
		foodStream.close();
		
		// Algorithm to add edges to graph in DAG format (linearized DAG)
		double weight;
		int i = 0;
		int j = 0;
		while (i < food.size()) {
			for (j = i+1; j < food.size(); j++) {
				weight = food.get(j).getPrice() - food.get(i).getPrice();
				g.addEdge(new DirectedEdge(i, j, weight));
			}
			i++;
		}
		
		// Print edges
		PrintStream edgeStream = new PrintStream(new File("_data/edges.txt"));
		edgeStream.println(g.toString());
		edgeStream.close();
		
		// Notice
		System.out.println("Foods.txt, and edges.txt have been created!");
		
		// Get userBudget
		System.out.println("Please enter a positive valued budget for your grocery list: ");
		Scanner input = new Scanner(System.in);
		double userBudget = input.nextDouble();
		while (userBudget < 0) {
			System.out.println("Please enter a positive valued budget: ");
			userBudget = input.nextDouble();
		}
		input.close();
		
		// Find longest path and print longest path trace to traces.txt
		g.simpleLongestPath(userBudget, food);
		
		// Print longest path
		System.out.println("Here is the most varied & healthy wholesale food grocery list:");
		Scanner tracePrint = new Scanner(new File("_data/traces.txt"));
		String temp = "";
		while (tracePrint.hasNextLine()) {
			temp =  tracePrint.nextLine();
			System.out.println(temp);
		}
		tracePrint.close();
		System.out.println("\nResults saved in traces.txt.");
	}
}

 
