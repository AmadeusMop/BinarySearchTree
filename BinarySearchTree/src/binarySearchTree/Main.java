package binarySearchTree;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		BST<Integer> tree = new BST<Integer>();
		tree.insert(50);
		int[] l = new int[] {0, 10, 20, 70, 30, 90, 40};
		for(int i : l) {
			tree.insert(i);
		}
		tree.delete(50);
		System.out.println(tree.getInOrderTraversal().toString());
		Arrays.sort(l);
		System.out.println(Arrays.toString(l));
	}

}
