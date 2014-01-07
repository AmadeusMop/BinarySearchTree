package binarySearchTree;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BST tree = new BST();
		int[] l = new int[] {0, 1, 5, 2, 7, 3, 2, 9, 4};
		for(int i : l) {
			tree.insert(i);
		}
		tree.printInfix();
	}

}
