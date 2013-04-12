
public class Fuzzy {

	
	
	public static double[] getIntersection(double set1[], double set2[]){
//		System.out.println("The set1 is");
//		printSet(set1);

		if(set1.length != set2.length){
			System.err.println("Cannot do intersection with sets of different sizes");
			return null;
		}
		
		double [] intersection = new double[set1.length];
		
		for(int i=0; i < set1.length; i++){
			
			if(set1[i] > set2[i]){
				intersection[i] = set2[i];
			}
			else{
				intersection[i] = set1[i];
			}
		}
		
//System.out.println("The intersection is");
//printSet(intersection);
		return intersection;
		
	}
	
	public static double[] getUnion(double set1[], double set2[]){
		
		if(set1.length != set2.length){
			System.err.println("Cannot do intersection with sets of different sizes");
			return null;
		}
		
		double [] union = new double[set1.length];
		
		for(int i=0; i < set1.length; i++){
			
			if(set1[i] > set2[i]){
				union[i] = set1[i];
			}
			else{
				union[i] = set2[i];
			}
		}
		
		return union;
		
	}
	
	
	
	
	
	public static double[] deffuzify(int list[]){
		int largest = 0;
		
		double [] newList = new double[list.length];
		
		for(int i=0; i < list.length; i++){
			
			if(list[i] > largest){
				largest = list[i];
			}			
		}
		
		for(int i=0; i < list.length; i++){
			if(largest == 0) newList[i] = (double)0;
			else newList[i] = (double)list[i]/largest;
			
		//	System.out.println(i + " " + (double)list[i]/largest);
		}
		
		return newList;
	}
	
	//turns a fuzzy set like 0 0.2 0 1 into 0 1 0 1
	public static double[] crisp(double[] list){
		
		double[] newList = new double[list.length];
		
		for(int i=0; i < list.length; i++){
			if(list[i] > 0){
				newList[i] = 1;
			}
			else{
				newList[i] = 0;
			}
		}
		
		return newList;
	}
	
	
	public static double[] setWeight(double[] list, double value){
		
		for(int i=0; i < list.length; i++){
			list[i] *= value;
		}
		
		return list;
	}
	
	
	
	public static double getMembership(double[] list, double index){
		
		
		double floor = Math.floor(index);
		double ceil = Math.ceil(index);
		
		if(floor == ceil){
			return list[(int)index];
		}
		else{
			return (list[(int) floor] + list[(int) ceil]) / 2;
		}
		
		
	}
	
	public static double[] rule (double value, double[] list1, double[] list2){
		return setWeight(list2, (getMembership(list1, value)));
	}
	
	
}
