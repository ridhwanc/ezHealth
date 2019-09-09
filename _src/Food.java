package ezHealth;

//Class that implements the Food Object 
public class Food implements Comparable<Food> {

	// private variables used to store the food's name and average price
	private String name;
	private double avg_price;
	
	//Constructor for the Food object
	public Food(String name,double a_price) {
		this.name = name;
		this.avg_price = a_price;
	}
	
	//accessor for the food's name
	public String getName() {
		return this.name;
	}
	
	//accessor for the food's price
	public double getPrice() {
		return this.avg_price;
	}
	
	//mutator that returns the food's and price into string
	public String toString() {
		return this.name + "," + this.avg_price;
	}

	//compares two food objects and returns 1,-1,0 depending on result 
	@Override
	public int compareTo(Food food) {
		if(this.avg_price < food.avg_price) return -1;
		if(this.avg_price > food.avg_price) return 1;
		return 0;
	}

}
