package binarySearchTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Main {
	
	private static int pow2(int power) {
		int o = 1;
		for(int i = 0; i < power; i++) o *= 2;
		return o;
	}

	public static void main(String[] args) {
		BST<Integer> tree;
		tree = new BST<Integer>();
		/*
		tree.insert(5); 
		tree.insert(-20);
		tree.insert(6);
		
		int[] l = new int[] {0, 1, 2, 7, 3, 9, 4};
		for(int i : l) {
			tree.insert(i);
		}
		tree.delete(5);
		tree.balance();
		System.out.println(tree.getInOrderTraversal().toString());
		Arrays.sort(l);
		System.out.println(Arrays.toString(l));
		tree.delete(7);
		tree.balance();*/
		
		int m = pow2(7);
		for(int i = 1; i < m; i++) {
			tree.insert(i);
		}
		
		System.out.println(tree);
		tree.balance();
		System.out.println(tree);
	}

}
