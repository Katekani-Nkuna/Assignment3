/**
 * A B+ tree generic node
 * Abstract class with common methods and data. Each kind of node implements this class.
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
abstract class BPTreeNode<TKey extends Comparable<TKey>, TValue> {
	
	protected Object[] keys;
	protected int keyTally;
	protected int m;
	protected BPTreeNode<TKey, TValue> parentNode;
	protected BPTreeNode<TKey, TValue> leftSibling;
	protected BPTreeNode<TKey, TValue> rightSibling;
	protected static int level = 0; // do not modify this variable's value as it is used for printing purposes. You can create another variable with a different name if you need to store the level.
	

	protected BPTreeNode() 
	{
		this.keyTally = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() 
	{
		return this.keyTally;
	}
	
	@SuppressWarnings("unchecked")
	public TKey getKey(int index) 
	{
		return (TKey)this.keys[index];
	}

	public void setKey(int index, TKey key) 
	{
		this.keys[index] = key;
	}

	public BPTreeNode<TKey, TValue> getParent() 
	{
		return this.parentNode;
	}

	public void setParent(BPTreeNode<TKey, TValue> parent) 
	{
		this.parentNode = parent;
	}	
	
	public abstract boolean isLeaf();
	
	/**
	 * Print all nodes in a subtree rooted with this node
	 */
	@SuppressWarnings("unchecked")
	public void print(BPTreeNode<TKey, TValue> node)
	{
		level++;
		if (node != null) {
			System.out.print("Level " + level + " ");
			node.printKeys();
			System.out.println();

			// If this node is not a leaf, then 
        		// print all the subtrees rooted with this node.
        		if (!node.isLeaf())
			{	BPTreeInnerNode inner = (BPTreeInnerNode<TKey, TValue>)node;
				for (int j = 0; j < (node.m); j++)
    				{
        				this.print((BPTreeNode<TKey, TValue>)inner.references[j]);
    				}
			}
		}
		level--;
	}

	/**
	 * Print all the keys in this node
	 */
	protected void printKeys()
	{
		System.out.print("[");
    		for (int i = 0; i < this.getKeyCount(); i++)
    		{
        		System.out.print(" " + this.keys[i]);
    		}
 		System.out.print("]");
	}


	////// You may not change any code above this line. You may add extra variables if need be //////

	////// Implement the functions below this line //////
	
	
	
	/**
	 * Search a key on the B+ tree and return its associated value using the index set. If the given key 
	 * is not found, null should be returned.
	 */
	public TValue search(TKey key) 
	{
		BPTreeNode<TKey, TValue> node = getLeafNode(key);
		//find key on the leaf node
		for (int i = 0; i < node.getKeyCount(); i++) {
			if (key.equals(node.getKey(i))) {
				return ((BPTreeLeafNode<TKey, TValue>) node).getValue(i);
			}
		}

		return null;
	}



	/**
	 * Insert a new key and its associated value into the B+ tree. The root node of the
	 * changed tree should be returned.
	 */
	public BPTreeNode<TKey, TValue> insert(TKey key, TValue value) 
	{
		// Search for leaf node key
		final int MAX = m-1;

		BPTreeNode<TKey, TValue> node = getLeafNode(key);
		BPTreeNode<TKey, TValue> danglingNode = null;
		while (true){
			//leaf node not full
			if (node.getKeyCount() < m){
				//insert Key and increment keyTally;
				if (node.isLeaf())
					((BPTreeLeafNode<TKey, TValue>)node).leafInsert(key,value);
				else
					((BPTreeInnerNode<TKey, TValue>)node).innerInsert(key);
				return this;
			}
			else{
				/*split node into node1 and node2;
				node1 = node, node2 is new;
				distribute keys and references evenly between node1 and node2 and
				initialize properly their keyTally’s;*/
				BPTreeNode<TKey, TValue> newNode = null;
				if (node.isLeaf()){
					newNode = ((BPTreeLeafNode<TKey, TValue>)node).leafSplit(key,value);
					key = newNode.getKey(0);
				}
				else {
					newNode = ((BPTreeInnerNode<TKey, TValue>)node).innerSplit(key,danglingNode);
				}

				BPTreeInnerNode<TKey, TValue> parent = (BPTreeInnerNode<TKey, TValue>)node.getParent();
				//if node is root
				if (parent == null){
					BPTreeInnerNode<TKey, TValue> newRoot = new BPTreeInnerNode<>(m);
					newRoot.setKey(0, key);
					newRoot.keyTally = 1;

					newRoot.setChild(0,node);
					newRoot.setChild(1, newNode);

					node.setParent(newRoot);
					newNode.setParent(newRoot);

					return newRoot;
				}
				else {

					//Attach newNode on an appropriate place
					danglingNode = parent.attacheNewNode(newNode);
					node = parent;
				}
			}


		}

	}



	/**
	 * Delete a key and its associated value from the B+ tree. The root node of the
	 * changed tree should be returned.
	 */
	public BPTreeNode<TKey, TValue> delete(TKey key) 
	{
		// Your code goes here
		return null;
	}



	/**
	 * Return all associated key values on the B+ tree in ascending key order using the sequence set. An array
	 * of the key values should be returned.
	 */
	@SuppressWarnings("unchecked")
	public TValue[] values() 
	{
		BPTreeNode<TKey, TValue> temp = this;

		//go to the leftmost node;
		while (!temp.isLeaf())
			temp = ((BPTreeInnerNode<TKey, TValue>)temp).getChild(0);

		int n = 0;
		BPTreeNode<TKey, TValue> current = temp;

		//Count total number of keys through the last level linked list
		while (temp != null){

			n += temp.getKeyCount();
			temp = temp.rightSibling;
		}

		TValue [] values = (TValue[]) new Object[n];

		//Collect all values
		while (current != null){
			for (int i = 0; i < current.getKeyCount(); i++) {
				values[i] =  ((BPTreeLeafNode<TKey, TValue>) current).getValue(i);
			}

			current = current.rightSibling;
		}

		return values;
	}


	/*===============================================================================================================
	============================================= Helper Functions =================================================
	================================================================================================================*/

	private BPTreeNode<TKey, TValue> getLeafNode(TKey key){

		BPTreeNode<TKey, TValue> current = this;

		//find leaf node where the key is
		while(!current.isLeaf()){
			//Search for key path
			int i;
			for (i = 0; i < current.keyTally; i++)
			{
				if (key.compareTo(current.getKey(i)) < 0)
					break;
			}

			//get the next index node on path to key from children (references)
			current = ((BPTreeInnerNode<TKey, TValue>) current).getChild(i);
		}

		return current;
	}

}