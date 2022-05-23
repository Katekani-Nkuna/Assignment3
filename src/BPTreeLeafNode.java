
/**
 * A B+ tree leaf node
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
class BPTreeLeafNode<TKey extends Comparable<TKey>, TValue> extends BPTreeNode<TKey, TValue> {
	
	protected Object[] values;
	
	public BPTreeLeafNode(int order) {
		this.m = order;
		// The strategy used here first inserts and then checks for overflow. 
		// Thus an extra space is required i.e. m instead of m-1.
		// You can change this if needed.
		this.keys = new Object[m];
		this.values = new Object[m];
	}

	@SuppressWarnings("unchecked")
	public TValue getValue(int index) {
		return (TValue)this.values[index];
	}

	public void setValue(int index, TValue value) {
		this.values[index] = value;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	////// You should not change any code above this line //////

	////// Implement functions below this line //////

	/*==================================================================================================================
	  =================================== Insertion Helper Functions ===================================================
	  =================================== =============================================================================*/

	public void leafInsert(TKey key, TValue value){

		int i = this.getKeyCount()-1;
		final int MAX = m-1;

		//find place to insert key and value
		while (i >= 0 && key.compareTo(this.getKey(i)) < 0){
			this.setKey(i+1, this.getKey(i));
			this.setValue(i+1, this.getValue(i));
			i--;
		}

		this.setKey(i+1, key);
		this.setValue(i+1, value);

		this.keyTally++;
	}

	public BPTreeNode<TKey, TValue> leafSplit(TKey key, TValue value){

		final int MAX = m-1;
		BPTreeLeafNode<TKey, TValue> node2 = new BPTreeLeafNode<>(m);
		BPTreeLeafNode<TKey, TValue> temp = new BPTreeLeafNode<>(m+1);

		//Copy node keys and values to temp which is 1 space bigger than m to accomodate new key,value
		//Only used for sorting purpose
		for (int i = 0; i < this.getKeyCount(); i++){
			temp.leafInsert(this.getKey(i), this.getValue(i));
			//clear node so it can have an equal redistribution
			this.setValue(i,null);
			this.setKey(i,null);
		}

		//insert new value on temp
		temp.leafInsert(key, value);
		this.keyTally = 0;
		int mid = m/2;

		for (int i = 0; i < mid; i++){
			this.leafInsert(temp.getKey(i),temp.getValue(i));
		}

		for (int i = mid; i < temp.getKeyCount(); i++ ){
			node2.leafInsert(temp.getKey(i),temp.getValue(i));
		}

		//update left and right pointers
		node2.rightSibling = this.rightSibling;
		if (node2.rightSibling != null){
			node2.rightSibling.leftSibling = node2;
		}
		this.rightSibling = node2;
		node2.leftSibling = this;

		return node2;
	}

	/*==================================================================================================================
	  =================================== Deletion Helper Functions ===================================================
	  =================================== =============================================================================*/
	public BPTreeNode<TKey, TValue> removeAtIndex(int index){

		//shift all keys from index to the left
		for (int i = index; i < this.getKeyCount()-1; i++, index++){
			this.setKey(i, this.getKey(i+1));
			this.setValue(i,this.getValue(i+1));
		}

		this.setKey(keyTally-1, null);
		this.setValue(keyTally-1, null);

		keyTally--;
		return this;
	}

	@Override
	public void distribute(BPTreeNode<TKey, TValue> rightNode, int index) {
		BPTreeNode<TKey, TValue> parent = this.getParent();
		//BPTreeLeafNode temp
		BPTreeLeafNode<TKey, TValue> temp = new BPTreeLeafNode<>(m+1);

		//Copy node keys and values to temp which is 1 space bigger than m to accomodate new key,value
		int numKeys = this.getKeyCount();

		for (int i = 0; i < numKeys; i++){
			temp.leafInsert(this.getKey(i), this.getValue(i));
			this.setKey(i, null);
			this.setValue(i, null);
		}

		numKeys = rightNode.getKeyCount();

		for (int i = 0; i < numKeys; i++){
			temp.leafInsert(rightNode.getKey(i),((BPTreeLeafNode<TKey, TValue>)rightNode).getValue(i));
			rightNode.setKey(i,null);
			((BPTreeLeafNode<TKey, TValue>)rightNode).setValue(i,null);
		}

		this.keyTally = 0;
		rightNode.keyTally = 0;

		//distribute keys evenly
		int mid = temp.getKeyCount()/2;
		for (int i = 0; i < mid; i++){
			this.leafInsert(temp.getKey(i), temp.getValue(i));
		}

		for (int i = mid; i<temp.getKeyCount(); i++){
			((BPTreeLeafNode<TKey, TValue>)rightNode).leafInsert(temp.getKey(i), temp.getValue(i));
		}

		//place successor
		TKey successor = rightNode.getKey(0);
		parent.setKey(index,successor);
	}

	@Override
	public BPTreeNode<TKey, TValue> merge(BPTreeNode<TKey, TValue> rightNode) {
		BPTreeNode<TKey, TValue> parent = this.getParent();
		//BPTreeLeafNode temp

		//Copy all rightNode keys to the left
		for (int i = 0; i < rightNode.getKeyCount(); i++){
			this.leafInsert(rightNode.getKey(i), ((BPTreeLeafNode<TKey, TValue> )rightNode).getValue(i));
		}

		int index = getIndexOfNode(this);

		//Remove seperator key and Shift right keys and sibblings to the left
		((BPTreeInnerNode<TKey,TValue>)parent).removeSeperatorKey(index); //this function performs the above comment
		return parent;
	}

	public BPTreeNode<TKey, TValue> rootMerge(BPTreeNode<TKey, TValue> rightNode){
		BPTreeNode<TKey, TValue> parent = this.getParent();
		BPTreeLeafNode<TKey, TValue> newRoot = new BPTreeLeafNode<>(m);

		for (int i = 0; i < this.getKeyCount(); i++){
			newRoot.leafInsert(this.getKey(i), this.getValue(i));
		}

		for (int i = 0; i < rightNode.getKeyCount(); i++){
			newRoot.leafInsert(rightNode.getKey(i), ((BPTreeLeafNode<TKey, TValue>)rightNode).getValue(i));
		}
		return newRoot;
	}
}
