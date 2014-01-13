package binarySearchTree;

/*import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;*/

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BST<V extends Comparable<V>> { // Note: Since generalizing BST to work with Comparable, I have opted to refrain from using V.equals(V o) in favor of the more consistent (with Comparables) V.compareTo(V o) == 0.
	private final EmptyTreeNode empty = new EmptyTreeNode();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private int cachedSize = 0;
	private boolean cacheValid = true;
	
	private TreeNode root = empty;
	
	public BST() {}
	
	public BST(V value) {
		insert(value);
	}
	
	public BST(V[] values) {
		for(V value : values) {
			insert(value);
		}
	}
	
	/*
	 * Methods specified in the assignment
	 */
	
	public boolean exists(V val) {
		/*
		 * Returns a boolean representing whether the tree contains the given value.
		 * 
		 * Returns false if the value is null.
		 * 
		 * Runs in O(log n) if the tree is balanced; otherwise, runs in O(n).
		 */
		
		if(val == null) return false;
		
		lock.readLock().lock();
		try {
			return root.exists(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void insert(V val) {
		/*
		 * Takes the given value and inserts it into the tree at the appropriate place using recursion.
		 * 
		 * Does nothing if the given value is null or already exists in the tree.
		 * 
		 * Note: root.insert() is called whether or not the given value already exists, since
		 * existence is not tested before the insert() call. This is because exists() and insert()
		 * have the same computational complexity, so testing for existence would be inefficient.
		 * 
		 * Runs in O(log n) if tree is balanced; otherwise, runs in O(n).
		 * 
		 */
		
		if(val == null) return;
		
		lock.writeLock().lock();
		try {
			root = root.insert(val);
			cacheValid = false;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void delete(V val) {
		/*
		 * Removes the given value from the tree.
		 * 
		 * Does nothing if the given value is null or does not exist in the tree.
		 * 
		 * Like insert(), existence of val is not tested before the root.delete() call.
		 * 
		 * Runs in O(log n) if tree is balanced; otherwise, runs in O(n).
		 */
		
		if(val == null) return;
		
		lock.writeLock().lock();
		try {
			root = root.delete(val);
			cacheValid = false;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/*
	 * Other methods
	 */
	
	public List<V> getInOrderTraversal() {
		/*
		 * Returns an ordered list of values in the tree by retrieving the values with a recursive infix traversal.
		 * 
		 * Returns an empty list if the tree is empty.
		 * 
		 * Note: This method uses a list that is passed down in the traverse() call from node to child, then modified in the node.
		 * I would have preferred to simply have each traverse() call return a list, then concatenate those lists, but since that
		 * requires iterating over at least one of the lists for every traverse() call, that would bump up the complexity to O(2^n)!
		 * And we do NOT want that. I suppose I could write an immutable List implementation using nodes (I would call it TraversalList)
		 * that supports construction via two other TraversalLists that would concatenate the lists in O(1) time...
		 * 
		 * Runs in O(n).
		 */
		
		lock.readLock().lock();
		try {
			List<V> out = new ArrayList<V>();
			root.traverse(out);
			return out;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public int size() {
		/*
		 * Returns the size of the tree (i.e., the total number of non-empty nodes the tree contains).
		 * 
		 * Uses a cached value of size to minimize delay.
		 * 
		 * Runs in O(1) if the cached value is valid; otherwise, runs in O(n);
		 */
		
		if(!cacheValid) {
			lock.readLock().lock();
			try {
				cachedSize = root.size();
				cacheValid = true;
			} finally {
				lock.readLock().unlock();
			}
		}
		
		return cachedSize;
	}
	
	public void balance() {
		/*
		 * "Balances" the tree (i.e., reorganizes the tree into the smallest height possible).
		 * 
		 * Does nothing if the tree is empty.
		 * 
		 * Note: Although balance() needs to acquire the write lock, since it reorganizes the internal structure of the tree,
		 * it does not modify the data within the tree, and as such, it does not invalidate cachedSize.
		 * 
		 * Runs in O(n).
		 */
		
		lock.writeLock().lock();
		try {
			NodeList nodes = traverseNodes();
			root.disconnect();
			root = nodes.remove(nodes.size()/2); // I use (nodes.size()/2) because, with an even number of nodes,
			root.recursiveReorganize(nodes);	 // it will result in the tree being "balanced" on the left.
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private NodeList traverseNodes() {
		/*
		 * traverseNodes() is almost exactly the same as getInOrderTraversal(), but traverseNodes()
		 * returns a List of the nodes themselves rather than just their values.
		 * 
		 * See getInOrderTraversal for more information.
		 * 
		 * Note: No read lock is needed, since traverseNodes() is private and thus will always be used within another method that locks.
		 */
		
		NodeList out = new NodeList();
		root.traverseNodes(out);
		return out;
	}
	
	public String toString() {
		/*
		 * Returns a String representation of the tree.
		 * 
		 * Returns an empty String if the tree is empty.
		 * 
		 * Ugh. This method is a bit of a mess. It's like the grimy underbelly of this 
		 * elegant and beautiful recursion city. But it works.
		 * 
		 * Mostly.
		 * 
		 * Runs in O(n^2). (I think.)
		 */

		lock.readLock().lock();
		try {
			StringBuilder sb = new StringBuilder(), spaceBuilder = new StringBuilder(), lineBuilder = new StringBuilder();
			NodeList nodes = new NodeList();
			nodes.add(root);
			NodeList next = new NodeList();
			NodeList allNodes = traverseNodes();
			int len = 1, size, index;
			boolean nodesLeft = (allNodes.size() > 0);
			String space, line, nodeString;
			
			for(TreeNode node : allNodes) {
				size = node.toString().length();
				if(size > len) len = size;
			}
			
			for(int i = 0; i < len; i++) {
				spaceBuilder.append(' ');
				lineBuilder.append('_');
			}
			
			space = spaceBuilder.toString();
			line = lineBuilder.toString();
			
			while(nodesLeft) {
				sb.append(' ');
				nodesLeft = false;
				for(TreeNode node : nodes) {
					if(node == empty) {
						next.add(empty);
						sb.append(space);
						continue;
					}
					nodesLeft = true;
					next.add(node.left);
					next.add(node.right);
					nodeString = node.toString();
					for(int i = 0; i < node.left.left.size(); i++) sb.append(space);
					for(int i = 0; i < node.left.right.size(); i++) sb.append(line);
					sb.append(nodeString);
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
					if(node != empty) {
						index = sb.lastIndexOf("[")+1;
						for(int i = (len - nodeString.length()+1)/2; i > 0; i--) sb.insert(index, ' ');

						index = sb.lastIndexOf("]");
						for(int i = (len - nodeString.length())/2; i > 0; i--) sb.insert(index, ' ');
					}
				}
				nodes = next;
				next = new NodeList();
				index = sb.lastIndexOf(space);
				sb.replace(index, index+space.length(), "");
				sb.append("\n");
			}
			return sb.toString();
			
		} finally {
			lock.readLock().unlock();
		}
	}
	
	private class TreeNode implements Comparable<V> {
		/*
		 * I have endeavored to make the recursive methods in TreeNode be as "blind" as possible - that is,
		 * with as little testing for "special cases" (such as val being null) as is necessary.
		 * Part of this is having each node have no control or access to its parent. No node may see "up" the
		 * tree: nodes may only see "down". Each recursive method loop ends when either the escape condition
		 * (usually compareTo(val) == 0) is met or when it reaches empty, which cannot recurse.
		 */

		private final V value;
		private TreeNode left;
		private TreeNode right;
		
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
			if(compareTo(val) > 0) {
				return left.exists(val);
			} else if(compareTo(val) < 0) {
				return right.exists(val);
			}
			return true;
		}
		
		protected TreeNode insert(V val) {
			if(compareTo(val) > 0) {
				left = left.insert(val);
			} else if(compareTo(val) < 0) {
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
			 * Simple, clean, and blind.
			 * System.out.println("aww yiss");
			 */
			
			if(compareTo(val) > 0) {
				left = left.delete(val);
				return this;
			} else if(compareTo(val) < 0) {
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
				 * Yes, I'm calling delete() for all three possibilities of compareTo(),
				 * but delete() isn't called if either of the node to be deleted's children are empty,
				 * and since largest by definition cannot have a right-hand child, the left.delete()
				 * statement here cannot run into an infinite recursive loop, as it will stop recursing
				 * when it reaches largest.
				 */
				return largest;
			}
		}
		
		protected void traverse(List<V> in) {
			traverse(in, TraversalType.INFIX); //Default is infix.
		}
		
		protected void traverse(List<V> in, TraversalType type) {
			if(type == TraversalType.PREFIX) in.add(value);
			left.traverse(in, type);
			if(type == TraversalType.INFIX) in.add(value); 
			right.traverse(in, type);
			if(type == TraversalType.POSTFIX) in.add(value); 
		}
		
		protected void traverseNodes(NodeList in) {
			traverseNodes(in, TraversalType.INFIX); //Again, default is infix.
		}
		
		protected void traverseNodes(NodeList in, TraversalType type) {
			if(type == TraversalType.PREFIX) in.add(this);
			left.traverseNodes(in, type);
			if(type == TraversalType.INFIX) in.add(this); 
			right.traverseNodes(in, type);
			if(type == TraversalType.POSTFIX) in.add(this); 
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
		
		public int compareTo(V val) {
			return value.compareTo(val);
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
		
		protected void traverse(List<V> in, TraversalType type) {
			return;
		}
		
		protected void traverseNodes(NodeList in, TraversalType type) {
			return;
		}
		
		protected void disconnect() {
			return;
		}
		
		protected void recursiveReorganize(NodeList nodes) {
			if(!nodes.isEmpty()) throw new IllegalStateException();
			return;
		}
		
		public int compareTo(V val) {
			throw new IllegalStateException();
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
	
	/*
	 * ...Okay, I did make a TraversalList. I might actually put it in the BST later.
	 * Don't even bother reading it all right now. It's still in-progress and there's nothing below it but an enum.
	 */
	
	/*
	private class TraversalList<E> extends java.util.AbstractList<E> {
		private TraversalNode first;
		private TraversalNode last;
		private int size;
		
		public TraversalList(E element) {
			TraversalNode node = new TraversalNode(element);
			first = node;
			last = node;
		}
		
		public TraversalList(TraversalList<E> l1, TraversalList<E> l2, E element, TraversalType type) {
			TraversalNode node = new TraversalNode(element);
			size = l1.size() + l2.size() + 1;
			switch(type) {
				case PREFIX:
					first = node;
					node.next = l1.first;
					node.next.prev = node;
					last = l2.last;
					l1.last.next = l2.first;
					l2.first.prev = l1.last.next;
					break;
				case INFIX:
					first = l1.first;
					last = l2.last;
					node.prev = l1.last;
					node.next = l2.first;
					node.next.prev = node;
					node.prev.next = node;
					break;
				case POSTFIX:
					last = node;
					node.prev = l2.last;
					node.prev.next = node;
					first = l1.first;
					l1.last.next = l2.first;
					l2.first.prev = l1.last.next;
					break;
			}
			l1.clear();
			l2.clear();
		}
		
		private class TraversalNode {
			private E value;
			private TraversalNode next;
			private TraversalNode prev;
			
			public TraversalNode(E value) {
				this.value = value;
				this.next = null;
				this.prev = null;
			}
		}
		
		public void clear() {
			first = null;
			last = null;
			size = 0;
		}
		
		public Iterator<E> iterator() {
			return listIterator();
		}

		public ListIterator<E> listIterator() {
			return new TraversalListIterator();
		}

		public ListIterator<E> listIterator(int index) {
			return new TraversalListIterator(index);
		}

		public int size() {
			return size;
		}
		
		public E get(int index) {
			return getNode(index).value;
		}
		
		private TraversalNode getNode(int index) {
			if(index >= size) throw new IndexOutOfBoundsException(Integer.toString(index));
			TraversalNode node;
			if(size/2 > index) {
				node = first;
				for(int i = 0; i < index; i++) node = node.next;
			} else {
				node = last;
				for(int i = size-1; i > index; i--) node = node.prev;
			}
			return node;
		}
		
		private class TraversalListIterator implements ListIterator<E> {
			private TraversalNode node;
			private int index;
			
			public TraversalListIterator(int index) {
				this.index = index;
				node = getNode(index);
			}
			
			public TraversalListIterator() {
				this.index = 0;
				node = first;
			}

			public void add(E e) {
				throw new UnsupportedOperationException();
			}

			public boolean hasNext() {
				return node != null;
			}

			public boolean hasPrevious() {
				return node.prev != null;
			}

			public E next() {
				if(!hasNext()) throw new NoSuchElementException();
				E element = node.value;
				node = node.next;
				index++;
				return element;
			}

			public int nextIndex() {
				return index;
			}

			public E previous() {
				if(!hasPrevious()) throw new NoSuchElementException();
				if(!hasNext()) node = last;
				else node = node.prev;
				return node.value;
			}

			public int previousIndex() {
				return index - 1;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
				
			}

			@Override
			public void set(E e) {
				throw new UnsupportedOperationException();
				
			}
			
		}
	}*/

	private static enum TraversalType {PREFIX, INFIX, POSTFIX};
}