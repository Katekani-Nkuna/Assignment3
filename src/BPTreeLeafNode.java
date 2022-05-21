
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

	public void leafInsert(TKey key, TValue value){

		int i = this.getKeyCount()-1;

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
		int mid = (int) Math.ceil(m/2);

		for (int i = 0; i < mid; i++){
			this.leafInsert(temp.getKey(i),temp.getValue(i));
		}

		for (int i = mid; i < temp.getKeyCount(); i++ ){
			node2.leafInsert(temp.getKey(i),temp.getValue(i));
		}

		node2.rightSibling = this.rightSibling;
		this.rightSibling = node2;

		return node2;
	}

}
