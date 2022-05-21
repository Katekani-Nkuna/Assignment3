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
}