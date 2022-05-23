public class Main {


	public static void main(String[] args)
	{
		//General tree
		BPTree<Integer, Integer> tree = new BPTree<Integer, Integer>(4); // A B+ Tree with order 4

		tree.insert(20, 5000);
		tree.insert(10, 1000);
		tree.insert(30, 9000);
		tree.insert(50, 8000);
		tree.insert(40, 2000);
		tree.insert(60, 7000);
		tree.insert(90, 3000);
		tree.insert(70, 6000);
		tree.insert(80, 4000);

		System.out.println("Structure of the constucted tree is: ");
		tree.print();

		Integer value = 70; // Delete leaf also internal
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 60; // Delete leaf
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 50; // Delete leaf, underflow, borrow left
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 40; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 10; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 20; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 80; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();

		value = 30; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();
		value = 90; // Delete leaf, underflow, borrow right
		tree.delete(value);
		System.out.println("Structure of the tree after delete of: " + value);
		tree.print();



	/* Expected Output:
	Structure of the constucted tree is:
	Level 1 [ 30 50 70]
	Level 2 [ 10 20]
	Level 2 [ 30 40]
	Level 2 [ 50 60]
	Level 2 [ 70 80 90]

	Structure of the tree after delete of: 70
	Level 1 [ 30 50 70]
	Level 2 [ 10 20]
	Level 2 [ 30 40]
	Level 2 [ 50 60]
	Level 2 [ 80 90]

	Structure of the tree after delete of: 60
	Level 1 [ 30 50 70]
	Level 2 [ 10 20]
	Level 2 [ 30 40]
	Level 2 [ 50]
	Level 2 [ 80 90]

	Structure of the tree after delete of: 50
	Level 1 [ 30 40 70]
	Level 2 [ 10 20]
	Level 2 [ 30]
	Level 2 [ 40]
	Level 2 [ 80 90]

	Structure of the tree after delete of: 40
	Level 1 [ 30 40 90]
	Level 2 [ 10 20]
	Level 2 [ 30]
	Level 2 [ 80]
	Level 2 [ 90]

	Search the tree for 80:
	Found key with value 4000
	Search the tree for 100:
	Key not found!
	Search the tree for 40:
	Key not found!

	Structure of the constucted index is:
	Level 1 [ 17012340]
	Level 2 [ 16094340 16230943]
	Level 2 [ 17012340 17248830]

	Search the index tree for student: 17248830
	Found key with value 2
	Return index tree values ordered by student id:
	Value 3
	Value 1
	Value 4
	Value 2

	Structure of the constucted index is :
	Level 1 [ Molefe]
	Level 2 [ Botha Evans]
	Level 2 [ Molefe Muller]

	Search the index tree for student: Botha
	Found key with value 3
	Return index tree values ordered by surname:
	Value 3
	Value 4
	Value 1
	Value 2
	*/
	}



}