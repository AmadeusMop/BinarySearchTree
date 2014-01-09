package binarySearchTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		BST<Integer> tree = new BST<Integer>();
		tree.insert(5);
		int[] l = new int[] {0, 1, 2, 7, 3, 9, 4};
		for(int i : l) {
			tree.insert(i);
		}
		tree.delete(5);
		tree.refresh();
		System.out.println(tree.getInOrderTraversal().toString());
		Arrays.sort(l);
		System.out.println(Arrays.toString(l));
		tree.delete(7);
		System.out.println(tree);
	}

}
