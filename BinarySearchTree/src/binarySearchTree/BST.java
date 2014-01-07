package binarySearchTree;

public class BST {
	TreeNode root;
	
	public BST() {
		this.root = null;
	}
	
	public boolean exists(int val) {
		if(root == null) return false;
		return root.exists(val);
	}
	
	void insert(int val) {
		if(root == null) {
			root = new TreeNode(val);
		} else if(exists(val)) {
			return;
		} else {
			root.insert(val);
		}
	}
	
	void delete(int val) {
		if(root == null || !exists(val)) {
			return;
		} else {
			root = root.delete(val);
		}
	}
	
	public void printInfix() {
		root.printInfix();
	}
	
	public void printTree() {
	}
	
	public int length() {
		return root.length();
	}
	
	/*public String toString() {
		
	}*/
	
	@SuppressWarnings("unused")
	private TreeNode get(int val) {
		if(exists(val)) return root.get(val);
		else return null;
	}
	
	class TreeNode {
		TreeNode left;
		TreeNode right;
		int value;
		
		public int length() {
			return (1 + (left != null ? left.length() : 0) + (right != null ? right.length() : 0));
		}
		
		public TreeNode(int val) {
			this(val, null, null);
		}
		
		public TreeNode(int val, TreeNode left, TreeNode right) {
			value = val;
			this.left = left;
			this.right = right;
		}
		
		boolean exists(int val) {
			if(value == val) return true;
			TreeNode n = (val < value ? left : right);
			if(n == null) return false;
			return n.exists(val);
		}
		
		void insert(int val) {
			if(val < value) {
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
		
		TreeNode delete(int val) {
			if(val == value) {
				if(left == null) return right;
				if(right == null) return left;
				
				TreeNode largest = left;
				while(largest.right != null) largest = largest.right;
				largest.right = right;
				return left;
			} else {
				if(val < value) {
					left = left.delete(val);
				} else {
					right = right.delete(val);
				}
				return this;
			}
		}
		
		TreeNode get(int val) {
			if(value == val) return this;
			else if(val < value) {
				return left.get(val);
			} else {
				return right.get(val);
			}
		}
		
		public String toString() {
			return Integer.toString(value);
		}
		
		void printInfix() {
			if(left != null) {
				left.printInfix();
				System.out.print(", ");
			}
			System.out.print(this.toString());
			if(right != null) {
				System.out.print(", ");
				right.printInfix();
			}
		}
	}
}