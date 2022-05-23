/**
 * A B+ tree internal node
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
class BPTreeInnerNode<TKey extends Comparable<TKey>, TValue> extends BPTreeNode<TKey, TValue> {
	
	protected Object[] references; 
	
	public BPTreeInnerNode(int order) {
		this.m = order;
		// The strategy used here first inserts and then checks for overflow. 
		// Thus an extra space is required i.e. m instead of m-1/m+1 instead of m.
		// You can change this if needed. 
		this.keys = new Object[m];
		this.references = new Object[m + 1];
	}
	
	@SuppressWarnings("unchecked")
	public BPTreeNode<TKey, TValue> getChild(int index) {
		return (BPTreeNode<TKey, TValue>)this.references[index];
	}

	public void setChild(int index, BPTreeNode<TKey, TValue> child) {
		this.references[index] = child;
		if (child != null)
			child.setParent(this);
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	////// You should not change any code above this line //////

	////// Implement functions below this line //////

	/*==================================================================================================================
	  =================================== Insertion Helper Functions ===================================================
	  =================================== =============================================================================*/
	public BPTreeNode<TKey, TValue> innerInsert(TKey key){

		final int MAX = m-1;

		int i = this.getKeyCount()-1;

		//find place to insert key
		while (i >= 0 && key.compareTo(this.getKey(i)) < 0){
			this.setKey(i+1, this.getKey(i));
			i--;
		}

		this.setKey(i+1, key);
		this.keyTally++;

		return this;
	}

	public BPTreeNode<TKey, TValue> innerSplit(TKey key, BPTreeNode<TKey, TValue> danglingNode){
		BPTreeInnerNode<TKey, TValue> node2 = new BPTreeInnerNode<>(m);
		BPTreeInnerNode<TKey, TValue> temp = new BPTreeInnerNode<>(m+1);
		final int MAX = m-1;

//		if (danglingNode != null)
//			danglingNode = this.attacheNewNode(danglingNode);

		//Copy node keys and values to temp which is 1 space bigger than m to accomodate new key,value
		//Only used for sorting purpose
		for (int i = 0; i < this.getKeyCount(); i++){
			temp.innerInsert(this.getKey(i));
			//clear node so it can have an equal redistribution
			this.setKey(i,null);
		}

		//insert new value on temp
		temp.innerInsert(key);
		this.keyTally = 0;
		int mid = m/2;

		for (int i = 0; i < mid; i++){
			this.innerInsert(temp.getKey(i));
		}

		this.setChild(mid,this.getChild(mid));

		int index = 0;
		for (int i = mid+1; i < temp.getKeyCount(); i++ ){

			node2.innerInsert(temp.getKey(i));

			//redistribute references
			node2.setChild(index++, this.getChild(i));
			//clear that child from leftNode
			this.setChild(i,null);
		}

		node2.setChild(index,danglingNode);

		return node2;
	}

	public BPTreeNode<TKey, TValue> attacheNewNode(BPTreeNode<TKey, TValue> newNode){
		int i = this.getKeyCount();
		final int MAX = m-1;

		BPTreeNode<TKey, TValue> danglingNode = null;
		//if the parent node is full, then either it's last ref or previous
		if (i == MAX){
			if (newNode.getKey(0).compareTo(this.getChild(i).getKey(0)) < 0){
				danglingNode = this.getChild(i);
			}
			else {
				return newNode;
			}
		}

		while (i >= 0 && newNode.getKey(0).compareTo( this.getChild(i).getKey(0)) < 0){
			this.setChild(i+1, this.getChild(i));
			i--;
		}

		this.setChild(i+1, newNode);

		return danglingNode;
	}

	TKey getMiddleKey(TKey key){
		BPTreeInnerNode<TKey, TValue> temp = new BPTreeInnerNode<>(m+1);
		for (int i = 0; i < this.getKeyCount(); i++){
			temp.innerInsert(this.getKey(i));
		}

		//insert new value on temp
		temp.innerInsert(key);

		int mid = m/2;

		return temp.getKey(mid);
	}


	/*==================================================================================================================
	  =================================== Deletion Helper Functions ===================================================
	  =================================== =============================================================================*/
	public BPTreeNode<TKey, TValue> removeAtIndex(int index){

		//shift all keys from index to the left
		//shift all children from index to the left
		if (index > 0)
			this.getChild(index-1).rightSibling = this.getChild(index).rightSibling;


		for (int i = index; i < this.getKeyCount()-1; i++, index++){
			this.setKey(i, this.getKey(i+1));
			this.setChild(i, this.getChild(i+1));
		}

		//shift the last child to it's predecessor
		this.setChild(keyTally-1,this.getChild(keyTally));

		this.setKey(keyTally-1, null);
		this.setChild(keyTally,null);

		keyTally--;
		return this;
	}

	@Override
	public void distribute(BPTreeNode<TKey, TValue> rightNode, int index) {
		BPTreeNode<TKey, TValue> parent = this.getParent();
		//BPTreeLeafNode temp
		BPTreeInnerNode<TKey, TValue> temp = new BPTreeInnerNode<>(m+1);

		//Copy node keys and values to temp which is 1 space bigger than m to accomodate new key,value
		int numKeys = this.getKeyCount();

		for (int i = 0; i < numKeys; i++){
			temp.innerInsert(this.getKey(i));
			this.setKey(i, null);
		}

		numKeys = rightNode.getKeyCount();

		for (int i = 0; i < numKeys; i++){
			temp.innerInsert(rightNode.getKey(i));
			rightNode.setKey(i,null);
		}

		this.keyTally = 0;
		rightNode.keyTally = 0;

		//distribute keys evenly
		int mid = temp.getKeyCount()/2;
		for (int i = 0; i < mid; i++){
			this.innerInsert(temp.getKey(i));
		}

		for (int i = mid; i<temp.getKeyCount(); i++){
			((BPTreeInnerNode<TKey, TValue>)rightNode).innerInsert(temp.getKey(i));
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
			this.innerInsert(rightNode.getKey(i));
		}

		int index = getIndexOfNode(this);

		//Delete seperator key and Shift right sibblings to the left
		parent.removeAtIndex(index); //this function performs the above comment
		return parent;
	}

	public BPTreeNode<TKey, TValue> removeSeperatorKey(int index){
		//shift all keys from index to the left
		//shift all children from index to the left

			this.getChild(index).rightSibling = this.getChild(index).rightSibling.rightSibling;


		for (int i = index; i < this.getKeyCount()-1; i++){
			this.setKey(i, this.getKey(i+1));
		}

		for (int i = index+1; i < this.getKeyCount(); i++){
			this.setChild(i, this.getChild(i+1));
		}


		this.setKey(keyTally-1, null);
		this.setChild(keyTally,null);

		keyTally--;
		return this;
	}

}