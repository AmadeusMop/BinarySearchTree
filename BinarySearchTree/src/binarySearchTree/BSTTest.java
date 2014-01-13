package binarySearchTree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BSTTest {
	
	BST<Integer> tree;

	@Before
	public void setUp() throws Exception {
		tree = new BST<>();
	}

	@Test
	public void insertTest() {
		tree.insert(5);
		tree.insert(3);
		tree.insert(6);
		tree.insert(null); //Should not throw an exception here
	}
	
	@Test
	public void existsTest() {
		insertTest();
		assertTrue(tree.exists(5));
		assertTrue(tree.exists(6));
		assertFalse(tree.exists(7));
		assertFalse(tree.exists(8));
		tree.insert(7);
		assertTrue(tree.exists(7));
		assertFalse(tree.exists(8));
		assertFalse(tree.exists(null)); //Should not throw an exception here
	}
	
	@Test
	public void deleteTest() {
		insertTest();
		assertTrue(tree.exists(6));
		tree.delete(6);
		assertFalse(tree.exists(6));
		assertTrue(tree.exists(5));
		tree.delete(null); //Should not throw an exception here
	}
}
