/**
 * Singzon, Ryan
 * 260397455
 * 
 * COMP 251 - Algorithms and Data Structures
 * Winter 2014
 */

package a1posted;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *   Here is the posted code for Assignment 1 in COMP 251  Winter 2014.
 * 
 *   Written by Michael Langer.
 *   This basic heap implementation is a modified version of Wayne and Sedgewick's code 
 *   (from their book, see link from their Coursera Algorithms course website).
 *   See other comments at the top of the Heap.java class.
 */

public class IndexedHeap{   

	private ArrayList<Double>    priorities;
	private ArrayList<String>  	 names;     //   Think of this as a map:  indexToNames

	/*  
	 * 	This is not just a heap;  it is an indexed heap!  To index directly into the heap,
	 *  we need a map. 
	 */
	
	private HashMap<String,Integer>  nameToIndex;    

	// constructor

	public IndexedHeap(){
		
		//  A node in the heap keeps track of a object name and the priority of that object. 
		
		names = new ArrayList<String>();
		priorities = new ArrayList<Double>();

		/*
		 * Fill the first array slot (index 0) with dummy values, so that we can use usual 
		 * array-based heap parent/child indexing.   See my COMP 250 notes if you don't know 
		 * what that means.
		 */
								   
		names.add( null );    	
		priorities.add( 0.0 );      

		//  Here is the map that we'll need when we want to change the priority of an object.
		
		nameToIndex  = new HashMap<String,Integer>();
	}

	private int parent(int i){     
		return i/2;
	}
	    		
	private int leftChild(int i){ 
	    return 2*i;
	}
	
	private int rightChild(int i){ 
	    return 2*i+1;
	}
	
	private boolean is_leaf(int i){
		return (leftChild(i) >= priorities.size()) && (rightChild(i) >= priorities.size());
	}
	
	private boolean oneChild(int i){ 
	    return (leftChild(i) < priorities.size()) && (rightChild(i) >= priorities.size());
	}
	
	/* 
	 *  The upHeap and downHeap methods use the swap method which you need to implement.
	 */
	
	private void upHeap(int i){
		if (i > 1) {   // min element is at 1, not 0
			if ( priorities.get(i) < priorities.get(parent(i)) ) {
				swap(parent(i),i);
				upHeap(parent(i));
			}
		}
	}

	private void downHeap(int i){

		// If i is a leaf, heap property holds
		if ( !is_leaf(i)){

			// If i has one child...
			if (oneChild(i)){
				//  check heap property
				if ( priorities.get(i) > priorities.get(leftChild(i)) ){
					// If it fails, swap, fixing i and its child (a leaf)
					swap(i, leftChild(i));
				}
			}
			else	// i has two children...

				// check if heap property fails i.e. we need to swap with min of children

				if  (Math.min( priorities.get(leftChild(i)), priorities.get(rightChild(i))) < priorities.get(i)){ 

					//  see which child is the smaller and swap i's value into that child, then recurse

					if  (priorities.get(leftChild(i)) < priorities.get(rightChild(i))){
						swap(i,   leftChild(i));
						downHeap( leftChild(i) );
					}
					else{
						swap(i,  rightChild(i));
						downHeap(rightChild(i));
					}
				}
		}
	}	

	public boolean contains(String name){
		if (nameToIndex.containsKey( name ))
			return true;
		else
			return false;
	}
	
	public int sizePQ(){
		return priorities.size()-1;   //  not to be confused with the size() of the underlying ArrayList, which included a dummy element at 0
	}

	public boolean isEmpty(){
		return sizePQ() == 0;   
	}
	
	public double getPriority(String name){
		if  (!contains( name ))
			throw new IllegalArgumentException("nameToIndex map doesn't contain key " + String.valueOf(name));
		return priorities.get( nameToIndex.get(name) );	
	}
	
	public double getMinPriority(){
		return priorities.get( 1 );	
	}

	public String nameOfMin(){
		return names.get(1);
	}

	/*
	 *   Implement all methods below
	 */
	
	/*
	 *   swap( i, j) swaps the values in the nodes at indices i and j in the heap.   
	 */

	private void swap(int i, int j){

		//Swap the indices corresponding to the names
		String tempName = names.get(i);
		nameToIndex.put(names.get(j), i);
		nameToIndex.put(tempName, j);
		
		//Swap the names of the objects
		String nameToSwap = names.get(j);
		names.set(j,  names.get(i));
		names.set(i, nameToSwap);
		
		//Then swap their priorities
		double tempPriority;
		tempPriority = priorities.get(j);
		priorities.set(j, priorities.get(i));
		priorities.set(i, tempPriority);
	}

	
	//  returns (and removes) the name of the element with lowest priority value, and updates the heap	
	
	public String removeMin(){
		
		//Get the name of the highest priority node to return
		String nodeToRemove = names.get(1);
		nameToIndex.remove(nodeToRemove);
		
		//Remove the node from the heap
		names.remove(nodeToRemove);
		priorities.remove(priorities.get(1));
		
		//If the heap is not empty, find the last node and swap it into the top of the node
		if(names.size() > 1){
			names.add(1, names.get(names.size()-1));
			names.remove(names.size()-1);
			priorities.add(1, priorities.get(priorities.size()-1));
			priorities.remove(priorities.size()-1);

			//Bubble down the top node
			downHeap(1);
		}
		

		return nodeToRemove;
	}	

	/*
	 * There are two add methods.  The first assumes a specific priority.  That's the one
	 * you need to implement.   The second gives a default priority of Double.POSITIVE_INFINITY	  
	 */
	
	public void  add(String name, double priority){

		if  (contains( name ))
			throw new IllegalArgumentException("Trying to add " + String.valueOf(name) + ", but its already there.");

		//Add the name into the ArrayList and HashMap
		names.add(name);
		nameToIndex.put(name, sizePQ()+1);
		
		//Add the priority
		priorities.add(priority);
		
		//Bubble the new key up to the top
		upHeap(sizePQ());
	}
	
	public void  add(String name){
		add(name, Double.POSITIVE_INFINITY);
	}

	/*
	 *   If new priority is different from the current priority then change the priority (and possibly modify the heap). 
	 *   If the name is not there, then throw an exception.
	 */
	
	public void changePriority(String name, double priority){

		if  (!contains( name ))
			throw new IllegalArgumentException("Trying to change priority of " + String.valueOf(name) + ", but its not there.");

		
		//Get the index of the object to change
		int index = nameToIndex.get(name);

		//Swap priorities
		double oldPriority = priorities.get(index);
		priorities.set(index, priority);
		
		//Compare the new priority
		if(priority < oldPriority){
			//New priority has higher priority, bubble up (ie. new priority is smaller) 
			upHeap(index);
		}
		
		else{
			//New priority has lower priority, bubble down (ie. new priority is larger)
			downHeap(index);
		}
	}
	
}
