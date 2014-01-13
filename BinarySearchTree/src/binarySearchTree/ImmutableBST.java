package binarySearchTree;

import java.util.Arrays;
import java.util.List;

public class ImmutableBST<V extends Comparable<V>> extends BST<V> {
	int size;
	String asString;
	
	public ImmutableBST() {
		size = 0;
		asString = "";
	}
	
	public ImmutableBST(BST<? extends V> tree) {
		this(tree.getInOrderTraversal(TraversalType.PREFIX));
	}
	
	public ImmutableBST(List<? extends V> values) {
		for(V value : values) {
			super.insert(value);
		}
		
		size = values.size();
		asString = super.toString();
	}
	
	public ImmutableBST(V[] values) {
		this(Arrays.asList(values));
	}
	
	public boolean insert(V val) {
		throw new UnsupportedOperationException();
	}
	
	public boolean delete(V val) {
		throw new UnsupportedOperationException();
	}
	
	public void balance() {
		throw new UnsupportedOperationException();
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		return asString;
	}
}
