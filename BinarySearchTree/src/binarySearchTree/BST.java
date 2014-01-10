package binarySearchTree;

import java.util.ArrayList;
import java.util.List;

public class BST<V extends Comparable<V>> { // Note: Since generalizing BST to work with Comparable, I have opted to refrain from using V.equals(V o) in favor of the more consistent (with Comparables) V.compareTo(V o) == 0.
	private TreeNode root;
	private final EmptyTreeNode empty = new EmptyTreeNode();
	
	public BST() {
		root = empty;
	}
	
	public synchronized boolean exists(V val) {
		return root.exists(val);
	}
	
	public synchronized void insert(V val) {
		root = root.insert(val);
	}
	
	public synchronized void delete(V val) {
		root = root.delete(val);
	}
	
	public synchronized List<V> getInOrderTraversal() {
		return root.traverse();
	}
	
	public synchronized int size() {
		return root.size();
	}
	
	public synchronized void refresh() {
		NodeList nodes = root.traverseNodes();
		root.disconnect();
		root = nodes.remove(nodes.size()/2); // I use (nodes.size()/2) because this way, an even number of nodes
		root.recursiveReorganize(nodes);	 // will default to being "balanced" on the left.
	}
	
	public synchronized String toString() {
		/*
		 * Ugh. This method is a bit of a mess. It's like the grimy underbelly of the 
		 * elegant and beautiful recursion city. But it works.
		 * 
		 * Mostly.
		 */
		StringBuilder sb = new StringBuilder(), spaceBuilder = new StringBuilder(), lineBuilder = new StringBuilder();
		NodeList nodes = new NodeList();
		nodes.add(root);
		NodeList next = new NodeList();
		int len = 1, size, index;
		String space, line;
		for(TreeNode node : nodes) {
			size = node.toString().length();
			if(size > len) len = size;
		}
		for(int i = 0; i < len; i++) {
			spaceBuilder.append(' ');
			lineBuilder.append('_');
		}
		space = spaceBuilder.toString();
		line = lineBuilder.toString();
		
		while(!nodes.isEmpty()) {
			sb.append(' ');
			for(TreeNode node : nodes) {
				next.add(node.left);
				next.add(node.right);
				for(int i = 0; i < node.left.left.size(); i++) sb.append(space);
				for(int i = 0; i < node.left.right.size(); i++) sb.append(line);
				sb.append(node.toString());
				if(node.left != empty) {
					index = sb.lastIndexOf("[");
					sb.insert(index, line);
					index = sb.lastIndexOf(" _");
					sb.replace(index, index+2, "  ");
					index = sb.lastIndexOf("_[");
					sb.replace(index, index+2, "/[");
				}
				for(int i = 0; i < node.right.left.size(); i++) sb.append(line);
				for(int i = 0; i < node.right.right.size(); i++) sb.append(space);
				sb.append(space);
				if(node.right != empty) {
					index = sb.lastIndexOf("]")+1;
					sb.insert(index, line);
					index = sb.lastIndexOf("_ ");
					sb.replace(index, index+2, "  ");
					index = sb.lastIndexOf("]_");
					sb.replace(index, index+2, "]\\");
				}
			}
			nodes = next;
			next = new NodeList();
			index = sb.lastIndexOf(space);
			sb.replace(index, index+space.length(), "");
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private class TreeNode implements Comparable<TreeNode> {
		private TreeNode left;
		private TreeNode right;
		private V value;
		
		protected TreeNode(V val) {
			this(val, empty, empty);
		}
		
		protected TreeNode(V val, TreeNode left, TreeNode right) {
			this.value = val;
			this.left = left;
			this.right = right;
		}
		
		protected int size() {
			return left.size() + right.size() + 1;
		}
		
		protected boolean exists(V val) {
			if(val.compareTo(value) < 0) {
				return left.exists(val);
			} else if(val.compareTo(value) > 0) {
				return right.exists(val);
			}
			return true;
		}
		
		protected TreeNode insert(V val) {
			if(val.compareTo(value) < 0) {
				left = left.insert(val);
			} else if(val.compareTo(value) > 0) {
				right = right.insert(val);
			}
			return this;
		}
		
		protected TreeNode delete(V val) {
			/*
			 * I don't usually explain my code, but I'm proud of this method.
			 * 
			 * The reason this method returns a TreeNode is because, if the node does need to be deleted,
			 * then all references to and from it must be removed (that is, either made null or replaced)
			 * in order to facilitate garbage collection and prevent memory/security leakage. 
			 * 
			 * Removing the references from the node is easy; it can be achieved by just assigning null 
			 * (or empty in this case, so nothing in the orphaned node accidentally chokes on a NullPointerException
			 * before garbage collection) to both children before delete() returns.
			 * 
			 * Removing the references *to* the node, on the other hand, is a bit trickier: the only real reference
			 * to it is in its parent's right or left field, so we could always test for value equality from the parent
			 * rather than the node itself, but in order to have a nice, clean, context-independent recursive method, 
			 * we can't just test the child nodes' values from the parent or else we'd have forests of if-else's to navigate.
			 * 
			 * And we don't want that.
			 * 
			 * So instead, I'm having the node return its own replacement if it needs to be deleted, and if not, it
			 * simply assigns whatever the appropriate child's delete() returns to that child's reference, then returns itself.
			 * No node needs to worry about what its children's values are, and every node is responsible for its own deletion,
			 * or lack thereof; no node needs to have its parent clean up after it.
			 * 
			 * Simple, clean, and context-free.
			 * System.out.println("aww yiss");
			 */
			if(val.compareTo(value) < 0) {
				left = left.delete(val);
				return this;
			} else if(val.compareTo(value) > 0) {
				right = right.delete(val);
				return this;
			} else {
				if(left == empty || right == empty) { 
					TreeNode o = (left == empty ? right : left); 		// If either of this node's children are empty, we can just return the other one and remove all 
					left = empty; 										// references to and from this node. Again, this is to ensure that no memory or security leaks are 
					right = empty;	  		 							// caused by this "orphaned" node doing anything unsavory to whatever nodes used to be its children.		 
					return o;
				}														// 						...also, that's the creepiest comment I've written since "ThreadDeath kills threads *silently*." O_o
				
				/*
				 * If, however, both children are non-empty, it's a bit trickier.
				 * 
				 * On the plus side, we know that left can't be empty, so we don't have to go mucking
				 * about with testing for null and edge cases when initializing that while loop down there.
				 * 
				 * Basically, I say that "largest" is the node retrieved by starting with largest = left 
				 * and continuing to assign largest.right to largest until largest.right is empty.
				 * (In other words, largest is the right-most child under this node's left-hand child.)
				 * 
				 * Now, the value of largest is *guaranteed* (due to the properties of BST's) to be
				 * less than the value of this node, and by extension less than the value of this node's
				 * right-hand child.
				 * 
				 * largest is the right-most child of this node's left-hand child, so the value of largest
				 * is guaranteed to be greater than the value of that node as well.
				 * 
				 * Since largest's value must be less than that of this node's right-hand child,
				 * and greater than that of this node's left-hand child, this node can be replaced by
				 * largest without breaking the rules of BST's, and because only a single node is being
				 * moved, the tree isn't wildly unbalanced like it was in my first solution (which was,
				 * essentially, put this.right at largest.right and then return this.left).
				 * 
				 * Also, I'm creating a new node with the same value and putting it in the same place, 
				 * rather than moving the same node, so that I can remove all prior references to it 
				 * with a recursive delete() call instead of having to explicitly define its parent.
				 */

				TreeNode largest;
				for(largest = left; largest.right != empty; largest = largest.right) {}
				
				largest = new TreeNode(largest.value, left.delete(largest.value), right);
				/*
				 * Since the original largest by definition cannot have a right-hand child,
				 * it's impossible for this to run into an infinite recursive loop.
				 */
				return largest;
			}
		}
		
		protected List<V> traverse() {
			return traverse(TraversalType.INFIX); //Default is infix.
		}
		
		protected List<V> traverse(TraversalType type) {
			List<V> o = new ArrayList<V>();
			List<V> leftList = left.traverse(type);
			List<V> rightList = right.traverse(type);
			o.addAll(left.traverse(type));
			o.addAll(right.traverse(type));

			int i = 0;
			
			switch(type) {
				case POSTFIX:
					i += rightList.size();
				case INFIX:
					i += leftList.size();
				case PREFIX:
					break;
			}
			
			o.add(i, value);
			return o;
		}
		
		protected NodeList traverseNodes() {
			return traverseNodes(TraversalType.INFIX); //Again, default is infix.
		}
		
		protected NodeList traverseNodes(TraversalType type) {
			NodeList o = new NodeList();
			NodeList leftList = left.traverseNodes(type);
			NodeList rightList = right.traverseNodes(type);
			o.addAll(left.traverseNodes(type));
			o.addAll(right.traverseNodes(type));

			int i = 0;
			
			switch(type) {
				case POSTFIX:
					i += rightList.size();
				case INFIX:
					i += leftList.size();
				case PREFIX:
					break;
			}
			
			o.add(i, this);
			return o;
		}
		
		protected void disconnect() {
			left.disconnect();
			right.disconnect();
			left = empty;
			right = empty;
		}
		
		protected void recursiveReorganize(NodeList nodes) {
			int size = nodes.size(), half = (size+1)/2;
			NodeList leftList = nodes.subList(0, half), rightList = nodes.subList(half, size);
			left = leftList.remove(leftList.size()/2);
			right = rightList.remove(rightList.size()/2);
			left.recursiveReorganize(leftList);
			right.recursiveReorganize(rightList);
		}
		
		public String toString() {
			return String.format("[%1s]", value.toString());
		}
		
		public int compareTo(TreeNode o) {
			return value.compareTo(o.value);
		}
	}
	

	private final class EmptyTreeNode extends TreeNode {
		
		public EmptyTreeNode() {
			super(null, null, null);
			((TreeNode)this).left = this;
			((TreeNode)this).right = this;
		}
		
		protected int size() {
			return 0;
		}
		
		protected boolean exists(V val) {
			return false;
		}
		
		protected TreeNode insert(V val) {
			return new TreeNode(val);
		}
		
		protected TreeNode delete(V val) {
			return this;
		}
		
		protected List<V> traverse() {
			return new ArrayList<V>();
		}
		
		protected List<V> traverse(TraversalType type) {
			return traverse();
		}
		
		protected NodeList traverseNodes() {
			return new NodeList();
		}
		
		protected NodeList traverseNodes(TraversalType type) {
			return traverseNodes();
		}
		
		protected void disconnect() {
			return;
		}
		
		protected void recursiveReorganize(NodeList nodes) {
			if(!nodes.isEmpty()) throw new IllegalStateException();
			return;
		}
		
		public String toString() {
			return "";
		}
	}
	
	private class NodeList extends ArrayList<TreeNode> {
		private static final long serialVersionUID = 8720120906378313941L;
		
		public NodeList() {
			super();
		}
		
		public NodeList(List<TreeNode> l) {
			super(l);
		}

		public TreeNode remove(int index) {
			if(isEmpty()) return empty;
			return super.remove(index);
		}
		
		public boolean isEmpty() {
			for(TreeNode node : this) {
				if(node != empty) return false;
			}
			return true;
		}
		
		public NodeList subList(int fromIndex, int toIndex) {
			return new NodeList(super.subList(fromIndex, toIndex));
		}
	}
	
	private static enum TraversalType {PREFIX, INFIX, POSTFIX};
}