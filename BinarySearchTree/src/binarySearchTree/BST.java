package binarySearchTree;

import java.util.ArrayList;
import java.util.List;

public class BST<V extends Comparable<V>> {
	TreeNode root;
	
	public BST() {
		this.root = null;
	}
	
	public boolean exists(V val) {
		if(root == null) return false;
		return root.exists(val);
	}
	
	void insert(V val) {
		if(root == null) {
			root = new TreeNode(val);
		} else if(exists(val)) {
			return;
		} else {
			root.insert(val);
		}
	}
	
	void delete(V val) {
		if(root == null || !exists(val)) {
			return;
		} else {
			root = root.delete(val);
		}
	}

	public List<V> getPrefix();
	public List<V> getInfix();
	public List<V> getPostfix();
	
	public List<V> getInOrderTraversal() {
		return root.getInfix();
	}
	
	public int length() {
		return root.length();
	}
	
	/*public String toString() {
		
	}*/
	
	@SuppressWarnings("unused")
	private TreeNode get(V val) {
		if(exists(val)) return root.get(val);
		else return null;
	}
	
	protected class TreeNode {
		TreeNode left;
		TreeNode right;
		V value;
		
		protected int length() {
			return (1 + (left != null ? left.length() : 0) + (right != null ? right.length() : 0));
		}
		
		protected TreeNode(V val) {
			this(val, null, null);
		}
		
		protected TreeNode(V val, TreeNode left, TreeNode right) {
			value = val;
			this.left = left;
			this.right = right;
		}
		
		protected boolean exists(V val) {
			if(value == val) return true;
			TreeNode n = (val.compareTo(value) < 0 ? left : right);
			if(n == null) return false;
			return n.exists(val);
		}
		
		protected void insert(V val) {
			if(val.compareTo(value) < 0) {
				if(left == null) {
					left = new TreeNode(val);
				} else {
					left.insert(val);
				}
			} else {
				if(right == null) {
					right = new TreeNode(val);
				} else {
					right.insert(val);
				}
			}
		}
		
		protected TreeNode delete(V val) {
			if(val == value) {
				if(left == null || right == null) {
					TreeNode o = (left == null ? right : left);
					left = null;
					right = null;
					return o;
				}
				
				TreeNode largest = left, parent = this;
				while(largest.right != null) {parent = largest; largest = largest.right;}
				parent.right = largest.left;
				largest.left = left;
				largest.right = right;
				return largest;
			} else {
				if(val.compareTo(value) < 0) {
					left = left.delete(val);
				} else {
					right = right.delete(val);
				}
				return this;
			}
		}
		
		protected TreeNode get(V val) {
			if(value.compareTo(val) == 0) return this;
			else if(val.compareTo(value) < 0) {
				return left.get(val);
			} else {
				return right.get(val);
			}
		}
		
		protected List<V> getFix(int type) { //TODO: Make type an enum
			List<V> o = new ArrayList<V>();
			if(left != null) {
				o.addAll(left.getInfix());
			}
			o.add(value);
			if(right != null) {
				o.addAll(right.getInfix());
			}
			return o;
		}
		
		public String toString() {
			return "(" + value.toString() + ")";
		}
	}
}