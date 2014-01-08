package binarySearchTree;

import java.util.Arrays;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BST tree = new BST();
		tree.insert(50);
		int[] l = new int[] {0, 10, 20, 70, 30, 90, 40};
		for(int i : l) {
			tree.insert(i);
		}
		tree.delete(50);
		System.out.println(Arrays.toString(tree.getInOrderTraversal()));
		Arrays.sort(l);
		System.out.println(Arrays.toString(l));
	}

}
