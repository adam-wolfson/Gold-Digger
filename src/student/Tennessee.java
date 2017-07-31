package student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import game.EscapeState;
import game.ExploreState;
import game.Explorer;
import game.Node;
import game.NodeStatus;

public class Tennessee extends Explorer {
	
	private HashSet<Long> visited = new HashSet<Long>(); // track visited nodes
	private List<NodeStatus> traveled = new ArrayList<NodeStatus>(); //path traveled
	private HashMap<Long, Long> tracker = new HashMap<Long, Long>(); // be able to move back
	
    /** Get to the orb in as few steps as possible. Once you get there, 
     * you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToOrb() in ExploreState.
     * You know you are standing on the orb when distanceToOrb() is 0.
     * 
     * Use function moveTo(long id) in ExploreState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.*/
    @Override public void getOrb(ExploreState state) {
        //TODO : Get the orb
    	//write dfs outside of this
    	//MAKE DFS RECURSIVE
    	
    	
    	
    	while(state.distanceToOrb()!= 0){
    		for(NodeStatus node : state.neighbors()){
    			state.moveTo(node.getId());
    			dfs(node, visited, state);
    		}
    	}
    	return;
    	
    }
    

   
    /** visit all unvisited nodes that can be reached by Tennesse*/
    private void dfs(NodeStatus node, HashSet<Long> visited, ExploreState state){
    	if(state.distanceToOrb() == 0){
    		return;
    	}
    	List<NodeStatus> target = (List<NodeStatus>) state.neighbors();
    	
    	
    	int distance = Integer.MAX_VALUE;
    	NodeStatus movenode = null;
    	long move = target.get(0).getId();
    	for(NodeStatus nodes : target){
    		if(nodes.getDistanceToTarget() == 0){
    			state.moveTo(nodes.getId()); 
    			return;
    		}
    		else if(visited.contains(nodes.getId())){
    			continue;
    		}
    		else if(nodes.getDistanceToTarget()<distance){
    			distance = nodes.getDistanceToTarget();
    			move = nodes.getId();
    		}
    		
    		
    	}
    	
  
    	if(visited.contains(move)){
    		List<Long> neighborID = new ArrayList<Long>();
    		for(NodeStatus nodes : state.neighbors()){
    			neighborID.add(nodes.getId());
    		}
    		if(visited.containsAll(neighborID)){

    			while(visited.containsAll(neighborID)){
    				if(state.distanceToOrb() == 0) return;
    				visited.add(state.currentLocation());
    				state.moveTo(tracker.get(state.currentLocation()));
    				
    				neighborID.clear();
    				for(NodeStatus nodes : state.neighbors()){
    	    			neighborID.add(nodes.getId());
    	    		}
    			}
    			
    		}
   
    		return;
    	}
    	Long previous = state.currentLocation();
       	state.moveTo(move);    
    	visited.add(previous);
    	tracker.put(state.currentLocation(), previous);
    	
    	for(NodeStatus nodes : state.neighbors()){
    		dfs(nodes, visited, state);
    	}
    }

    /** Get out the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS get out before time runs
     * out, and this should be prioritized above collecting gold.
     * 
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * currentNode() and getExit() will return Node objects of interest, and getNodes()
     * will return a collection of all nodes on the graph. 
     * 
     * Note that the cavern will collapse in the number of steps given by stepsRemaining(),
     * and for each step this number is decremented by the weight of the edge taken. You can use
     * stepsRemaining() to get the time still remaining, seizeGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * 
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * 
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold. For this reason, using 
     * Dijkstra's to plot the shortest path to the exit is a good starting solution. */
    @Override public void getOut(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
    	// about 7,000: find node with most gold on it that you can get to and also leave
    	// about 10,000: find path to a gold node with most gold on it that you can get to and also leave
    	// about 17,000 - find path with most gold on it, and before leaving apply it again until you can still get out 
   
    	
    	Collection<Node> allnodes = state.getNodes();
    	allnodes.toArray();
    	int a = 0;
    	Node mostgold = null;
    	for(Node node : allnodes){
    		if(node.getTile().getGold()> a){
    			a = node.getTile().getGold();
    			mostgold = node;
    		}
    	}
    	List<Node> togold = Paths.dijkstra(state.currentNode(), mostgold);
    	List<Node> escape = Paths.dijkstra(mostgold, state.getExit());
    	while(state.stepsRemaining()> Paths.pathLength(togold)+Paths.pathLength(escape)){
    		traverse2(state);
    		Collection<Node> nodes = state.getNodes();
        	allnodes.toArray();
        	int b = 0;
        	Node newmostgold = null;
        	for(Node node : allnodes){
        		if(node.getTile().getGold()> b){
        			b = node.getTile().getGold();
        			mostgold = node;
        		}
        	}
        	List<Node> gold = Paths.dijkstra(state.currentNode(), mostgold);
        	List<Node> leave = Paths.dijkstra(mostgold, state.getExit());
        	togold = gold;
        	escape = leave;
    	}
    	simpletraverse(state);
    }
    
    /** just get out as fast as you can and pick up whatever gold is on the way*/
    private void simpletraverse(EscapeState state){
    	List<Node> path = Paths.dijkstra(state.currentNode(), state.getExit());
    	for(Node nodes : path){
    		try{
    			state.seizeGold();
    		}
    		catch (IllegalStateException e){
    			
    		}
    		if(nodes.getId() == state.currentNode().getId()){
    			
    		}
    		else{
    			state.moveTo(nodes);
    		}
    		
    	}
    	return;
    	
    }
    
    
    /** go to node with most gold on it and then get out*/
    private void traverse2(EscapeState state){
    	Collection<Node> allnodes = state.getNodes();
    	allnodes.toArray();
    	int a = 0;
    	Node mostgold = null;
    	for(Node node : allnodes){
    		if(node.getTile().getGold()> a){
    			a = node.getTile().getGold();
    			mostgold = node;
    		}
    	}
    	List<Node> pathToGold = Paths.dijkstra(state.currentNode(), mostgold);
    	for(Node node : pathToGold){
    		try{
    			state.seizeGold();
    		}
    		catch (IllegalStateException e){
    			
    		}
    		if(node.getId() == state.currentNode().getId()){
    			
    		}
    		else{
    			state.moveTo(node);
    		}
    		
    	}
    }

    
}
