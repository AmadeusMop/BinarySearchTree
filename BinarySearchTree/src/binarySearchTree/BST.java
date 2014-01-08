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
	
	public int[] getInOrderTraversal() {
		String[] infix = root.getInfix().split(", ");
		int l = infix.length;
		int[] output = new int[l];
		for(int i = 0; i < l; i++) {
			output[i] = Integer.parseInt(infix[i]);
		}
		return output;
	}
	
	public String getInfix() {
		return root.getInfix();
	}
	
	public void printTree() {
		root.printTree(0);
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
	
	protected class TreeNode {
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
		
		void printTree(int depth) {
			if(right != null) right.printTree(depth+1);
			
			for(int i = 0; i < depth; i++) {
				System.out.print("  ");
			}
			System.out.println(value);
			
			if(left != null) left.printTree(depth+1);
		}
		
		String getInfix() {
			String o = Integer.toString(value);
			if(left != null) {
				o = left.getInfix() + ", " + o;
			}
			if(right != null) {
				o = o +  ", " + right.getInfix();
			}
			return o;
		}
	}
}