package binarySearchTree;

import java.util.ArrayList;
import java.util.List;

public class BST {
	TreeNode root;
	
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
	
	private TreeNode get(int val) {
		if(exists(val)) return root.get(val);
		else return null;
	}
	
	class TreeNode {
		TreeNode left;
		TreeNode right;
		int value;
		
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
		
		List<TreeNode> getTreeAsList() {
			List<TreeNode> l = new ArrayList<TreeNode>();
			l.add(this);
			if(left != null) {
				l = left.getTreeAsList().addAll(l);
			}
		}
		
		void add(TreeNode node) {
			if(node.value = value) throw new IllegalArgumentException();
			if(node.value < value) {
				if()
			}
		}
		
		TreeNode delete(int val) {
			if(val == value) {
				
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
	}
}