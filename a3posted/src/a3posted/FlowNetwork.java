/**
 * COMP 251 - Data Structures and Algorithms
 * Winter 2014
 * Assignment 3
 * 
 * Singzon, Ryan
 * 260397455
 */


package a3posted;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;

public class FlowNetwork {

	//   The data structures follow what I presented in class.  Use three graphs which 
	//   represent the capacities, the flow, and the residual capacities.
	
	Graph capacities;      		// weights are capacities   (G)
	Graph flow;            		// weights are flows        (f)
	Graph residualCapacities;   // weights are determined by capacities (graph) and flow (G_f)
	
	//   Constructor.   The input is a graph that defines the edge capacities.
	
	public FlowNetwork(Graph capacities){
				
		this.capacities    = capacities;
		
		//  The flow and residual capacity graphs have the same vertices as the original graph.
		
		flow               = new Graph( capacities.getVertices() );
		residualCapacities = new Graph( capacities.getVertices() );
		
		//  Initialize the flow and residualCapacity graphs.   The flow is initialized to 0.  
		//  The residual capacity graph has only forward edges, with weights identical to the capacities. 

		for (String u : flow.getVertices()){
			for (String v : capacities.getEdgesFrom(u).keySet() ){
				
				//  Initialize the flow to 0 on each edge
				
				flow.addEdge(u, v, new Double(0.0));
				
				//	Initialize the residual capacity graph G_f to have the same edges and capacities as the original graph G (capacities).
				
				residualCapacities.addEdge(u, v, new Double( capacities.getEdgesFrom(u).get(v) ));
			}
		}
	}

	/*
	 * Here we find the maximum flow in the graph.    There is a while loop, and in each pass
	 * we find an augmenting path from s to t, and then augment the flow using this path.
	 * The beta value is computed in the augment method. 
	 */
	
	public void  maxFlow(String s,  String t){
		
		LinkedList<String> path;
		double beta;
		while (true){
			path = this.findAugmentingPath(s, t);
			if (path == null)
				break;
			else{
				beta = computeBottleneck(path);
				augment(path, beta);				
			}
			System.out.println("\nResidual graph after");
			System.out.println(residualCapacities.toString());
		}	
	}
	
	/*
	 *   Use breadth first search (bfs) to find an s-t path in the residual graph.    
	 *   If such a path exists, return the path as a linked list of vertices (s,...,t).   
	 *   If no path from s to t in the residual graph exists, then return null.  
	 */
	
	public LinkedList<String>  findAugmentingPath(String s, String t){
	
		//Create a temporary list to hold the reversed path and create the augmented path
		LinkedList<String> tempPath = new LinkedList<String>();
		LinkedList<String> augmentingPath = new LinkedList<String>();

		//Perform breadth first search on the residual graph to find valid paths
		residualCapacities.bfs(s);
		
		
		//Add the path from the terminus to the source
		tempPath.add(t);
		String currentNode = t;
		String parent = "";
		
		//Continue adding edges until the parent of the current node points to itself
		while(residualCapacities.getParent(currentNode) != null){
		 	parent = residualCapacities.getParent(currentNode);
			tempPath.add(residualCapacities.getParent(currentNode));
			currentNode = parent;
			if(parent.equals(s)) break;
		}
		
		while(!tempPath.isEmpty()){
			augmentingPath.add(tempPath.removeLast());
		}
		
		//If the node visited last is the source, then the path is valid
		if(currentNode.equals(s)){
			for(String node : augmentingPath){
				System.out.println(node);
			}
			return augmentingPath;
		}
		
		//Otherwise, there is not path from the terminus to the source
		else{
			return null;
		}
		
	}
	
	/*
	 *   Given an augmenting path that was computed by findAugmentingPath(), 
	 *   find the bottleneck value (beta) of that path, and return it.
	 */
	
	public double computeBottleneck(LinkedList<String>  path){

		double beta = Double.MAX_VALUE;

		//  Check all edges in the path and find the one with the smallest weight in the
		//  residual graph.   This will be the new value of beta.

		String currentNode;
		String nextNode;
		double residualCapacity;
		
		//Traverse the entire path and find the lowest weight
		for(int i = 0; i < path.size() - 1; i++){
			currentNode = path.get(i);
			residualCapacity = Double.MAX_VALUE;
			
			//Get the weight of the edge to the next node in the path 
			nextNode = path.get(i+1);
			residualCapacity = residualCapacities.getEdgesFrom(currentNode).get(nextNode);
				
			//Set beta to the smallest residual capacity 
			if(beta > residualCapacity){
				beta = residualCapacity;
			}
		}
		
		System.out.println("Bottleneck: "+beta);
		return beta;
	}
	
	//  Once we know beta for a path, we recompute the flow and update the residual capacity graph.

	public void augment(LinkedList<String>  path,  double beta){
		
		String currentNode;
		String nextNode;
		
		//Increase the flow at every edge in the path
		for(int i = 0; i < path.size()-1; i++){
			currentNode = path.get(i);
			nextNode = path.get(i+1);
			
			//Initialize the edges to change
			double newFlow = 0.0;
			double forwardEdge = 0.0;
			double backwardsEdge = 0.0;
			
			//Get the value of the current flow between the edges and augment the flow
			if(flow.getEdgesFrom(currentNode).containsKey(nextNode)){
				newFlow = flow.getEdgesFrom(currentNode).get(nextNode);
			}
			
			newFlow += beta;
			flow.addEdge(currentNode, nextNode, newFlow);

			//Calculate the forward and backward edges for the residual graph
			if(flow.getEdgesFrom(currentNode).containsKey(nextNode)){

				//Weight of backwards edge is flow(e)
				backwardsEdge = flow.getEdgesFrom(currentNode).get(nextNode);

				if(capacities.getEdgesFrom(currentNode).containsKey(nextNode)){
					
					//Weight of forwards edge is capacities(e) - flow(e)
					forwardEdge = capacities.getEdgesFrom(currentNode).get(nextNode) - flow.getEdgesFrom(currentNode).get(nextNode);
				}
			}

			//Add the edges for the forward and backwards edges
			residualCapacities.addEdge(currentNode, nextNode, forwardEdge);
			residualCapacities.addEdge(nextNode, currentNode, backwardsEdge);
			
			//Remove an edge if it is zero
			if(forwardEdge == 0.0){ 
				residualCapacities.getEdgesFrom(currentNode).remove(nextNode);
			}
			if(backwardsEdge == 0.0){
				residualCapacities.getEdgesFrom(nextNode).remove(currentNode);
			}
		}
	}

	//  This just dumps out the adjacency lists of the three graphs (original with capacities,  flow,  residual graph).
	
	public String toString(){
		return "capacities\n" + capacities + "\n\n" + "flow\n" + flow + "\n\n" + "residualCapacities\n" + residualCapacities;
	}
	
}
