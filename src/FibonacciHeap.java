//todo-delete import- added to make heapPrint work
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

//feras baransi, 211757133, ferasbaransi
//mohammed qaiss, 208196857, mohammedq

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{

    //static Heap heap;
    //static FibonacciHeap fibonacciHeap;

    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        int m = 2;
        HeapNode[] nodesPointers = new HeapNode[m+1];
        for (int k = m - 1; k >= -1; k--) {
            heap.insert(k);
            nodesPointers[k+1] = heap.head;
        }

        print(heap, true);
        System.out.println("after insert");

        heap.deleteMin();

        System.out.println("after delete min");
        print(heap, true);
        for (int j = m; j >= 2; j--) {
            int i = (int) (Math.log(j)/Math.log(2));
            int index = (int) (m - Math.pow(2,i)+1);
                heap.decreaseKey(nodesPointers[index], m + 1);

        }

        System.out.println("after delete min");
        print(heap, true);
    }

    public static int totalLinks = 0;
    public static int totalCuts = 0;

    public int numOfTrees = 0;
    public int numOfMarks = 0;

    private int heapSize;

    private HeapNode head;
    private HeapNode tail;
    private HeapNode minHeapNode;
    static final PrintStream stream = System.out;

    public FibonacciHeap() { } // constrctor for null FibonacciHeap


    public FibonacciHeap(HeapNode x) { // constrctor for FibonacciHeap
        this.head = x;
        this.tail = x;
        this.minHeapNode = x;

        this.head.setNext(head);
        this.head.setPrev(head);

    }

    /**
     * public boolean isEmpty()
     *
     * precondition: none
     *
     * The method returns true if and only if the heap
     * is empty.
     *
     */
    public boolean isEmpty()
    {
        return this.heapSize == 0; // true if the heapSize is 0 else false
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     *
     * Returns the new node created.
     */
    public HeapNode insert(int key)
    {
        HeapNode heapNodeToAdd; //the new heapNode to add to the FibonacciHeap

        if (isEmpty()) { // FibonacciHeap is empty

            heapNodeToAdd = new HeapNode(key); // creats new HeapNode

            heapNodeToAdd.setNext(heapNodeToAdd); // setNext to heapNodeToAdd
            heapNodeToAdd.setPrev(heapNodeToAdd); // setPrev to heapNodeToAdd

            updatePointersAfterInsert(heapNodeToAdd); // update pointer after insert

        }
        else { // FibonacciHeap is not empty

            heapNodeToAdd = new HeapNode(key, this.tail, this.head);  // creats new HeapNode with next and prev that initialize

            this.head.setPrev(heapNodeToAdd); // new prev to head
            this.tail.setNext(heapNodeToAdd); // new next to tail

            updatePointersAfterInsert(heapNodeToAdd); // update pointer after insert

        }
        this.heapSize++; // update heapSize by one
        this.numOfTrees++; // update numOfTrees by one

        return heapNodeToAdd; // Returns the new node created
    }

    /*
     * private void updatePointersAfterInsert(HeapNode heapNodeToAdd)
     *
     * update the pointers of the fibonacciHeap after insert to the head
     *
     * O(1)
     */
    private void updatePointersAfterInsert(HeapNode heapNodeToAdd)
    {
        if (this.minHeapNode == null) { // when the fibonacciHeap is empty
            this.head = heapNodeToAdd;
            this.tail = heapNodeToAdd;
            this.minHeapNode = heapNodeToAdd;

        } else { // !(when the fibonacciHeap is empty)
            if (this.minHeapNode.getKey() > heapNodeToAdd.getKey()) this.minHeapNode = heapNodeToAdd; // update the minHeapNode
            this.head = heapNodeToAdd; // update the head

        }

    }

    /**
     * public void deleteMin()
     *
     * Delete the node containing the minimum key.
     *
     * n = fibonacciHeap.Size()
     * WC - O(n)
     * Amort - O(log(n))
     *
     */
    public void deleteMin()
    {
        if (!isEmpty()) {
            if (this.minHeapNode.getRank() == 0) { // minHeapNode is with rank = 0

                if (size() == 1) updatePointersAfterDeleteMin(1); // size is 1
                else updatePointersAfterDeleteMin(2); // size of the fibonacciHeap > 0

            } else { // minHeapNode is with rank > 0
                if (!(this.minHeapNode.getNext().equals(this.minHeapNode))) { // case3 = this.head != this.tail
                    updatePointersAfterDeleteMin(3);

                } else { // case4 = this.head == this.tail
                    updatePointersAfterDeleteMin(4);

                }
            }
        }

    }

    private void updatePointersAfterDeleteMin(int i)
    {
        switch(i) {
            case 1: // minHeapNode is with rank = 0 && fibonacciHeap is with size 1
                resetFibonacciHeap(); // null pointers
                break;

            case 2: // minHeapNode is with rank = 0 && fibonacciHeap is with size > 1
                if (this.minHeapNode == this.head) // minHeapNode is the head
                    this.head = this.minHeapNode.next;

                if (this.minHeapNode == this.tail) // minHeapNode is the tail
                    this.tail = this.minHeapNode.prev;

                // update the pointers of minHeapNode.getPrev() and minHeapNode.getNext()
                this.minHeapNode.getPrev().setNext(this.minHeapNode.getNext());
                this.minHeapNode.getNext().setPrev(this.minHeapNode.getPrev());

                this.heapSize--; // descend the heapSize by 1
                this.numOfTrees--;

                Consolidating();
                break;

            case 3: // minHeapNode.getNext() != minHeapNode
                if (this.minHeapNode == this.head) { // minHeapNode is the head of the fibonacci
                    updateHead(); // update the head and delete the minHeapNode
                    Consolidating();
                    this.heapSize--; // descend the heapSize by 1
                    break;

                } else if (this.minHeapNode == this.tail) { // minHeapNode is the tail of the fibonacci
                    updateTail(); // update the tail and delete the minHeapNode
                    Consolidating();

                    this.heapSize--; // descend the heapSize by 1
                    break;

                } else { // minHeapNode is not the tail or the head of the fibonacci
                    updateAfterDelete(); // update the pointers of minHeapNode.getPrev() and minHeapNode.getNext() and delete the minHeapNode
                    Consolidating();

                    this.heapSize--; // descend the heapSize by 1
                    break;

                }
            case 4: // minHeapNode.getNext() == minHeapNode
                update(); // update the tail and the head ,and delete the minHeapNode
                Consolidating();

                this.heapSize--; // descend the heapSize by 1
                break;
        }


    }

    /*
     * private void update()
     *
     * update the pointers after delete the minNide
     * O(k) <-> k is the number if children to the minNode
     *
     */
    private void update()
    {
        HeapNode childOfMinHeapNode = this.minHeapNode.getChild(); // child of the minNode
        int countmarks = 0; // counter to the marked HeapNode that changed after delete minHeapNode

        if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
        childOfMinHeapNode.mark = 0;
        childOfMinHeapNode.setParent(null);

        this.head = childOfMinHeapNode; // update the head pointer

        HeapNode lastChild = childOfMinHeapNode;

        // update the parent for the children of the minHeapNode to be null
        for (int i = 2; i <= this.minHeapNode.rank; i++) {

            childOfMinHeapNode = childOfMinHeapNode.getNext();

            if (childOfMinHeapNode.mark == 1) countmarks++;
            childOfMinHeapNode.mark = 0;
            childOfMinHeapNode.setParent(null);

            lastChild = childOfMinHeapNode;
        }

        this.tail = lastChild; // update the tail pointer
        //this.tail.setNext(head);
        //this.head.setPrev(tail);

        this.numOfMarks -= countmarks; // descend the numOfMarks by countmarks
        this.numOfTrees = this.numOfTrees -1 +this.minHeapNode.getRank(); // update the num of the trees after delet minHeapNode
    }

    /*
     * private void updateAfterDelete()
     *
     * update the pointers after delete the minHeapNode
     * O(k) <-> k is the number if children to the minHeapNode
     */
    private void updateAfterDelete()
    {

        // initialize pointers to the prev\next\child heapNode of the minHeapNode
        HeapNode prevOfMinHeapNode = this.minHeapNode.getPrev();
        HeapNode nextOfMinHeapNode = this.minHeapNode.getNext();
        HeapNode childOfMinHeapNode = this.minHeapNode.getChild();
        int countmarks = 0; // counter to the marked HeapNode that changed after delete minHeapNode

        if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
        childOfMinHeapNode.mark = 0;
        childOfMinHeapNode.setParent(null);

        childOfMinHeapNode.setPrev(prevOfMinHeapNode);
        prevOfMinHeapNode.setNext(childOfMinHeapNode);

        HeapNode lastChild = childOfMinHeapNode;

        // update the parent for the children of the minNide to be null
        for (int i = 2; i <= this.minHeapNode.rank; i++) {

            childOfMinHeapNode = childOfMinHeapNode.getNext();

            if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
            childOfMinHeapNode.mark = 0;
            childOfMinHeapNode.setParent(null);

            lastChild = childOfMinHeapNode;
        }

        // Link the new pointers
        lastChild.setNext(nextOfMinHeapNode);
        nextOfMinHeapNode.setPrev(lastChild);

        this.numOfMarks -= countmarks; // descend the numOfMarks by countmarks
        this.numOfTrees = this.numOfTrees -1 +this.minHeapNode.getRank(); // update the num of the trees after delet minHeapNode

    }

    /*
     * private void updateTail()
     *
     * update the pointers after delete the minHeapNode
     * O(k) <-> k is the number if children to the minHeapNode
     */
    private void updateTail()
    {
        // initialize pointers to the prev\child heapNode of the minHeapNode
        HeapNode prevOfMinHeapNode = this.minHeapNode.getPrev();
        HeapNode childOfMinHeapNode = this.minHeapNode.getChild();
        int countmarks = 0; // counter to the marked HeapNode that changed after delete minHeapNode

        if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
        childOfMinHeapNode.mark = 0;
        childOfMinHeapNode.setParent(null);

        // Link the new pointers
        childOfMinHeapNode.setPrev(prevOfMinHeapNode);
        prevOfMinHeapNode.setNext(childOfMinHeapNode);


        HeapNode lastChild = childOfMinHeapNode;
        // update the parent for the children of the minNide to be null
        for (int i = 2; i <= this.minHeapNode.rank; i++) {

            childOfMinHeapNode = childOfMinHeapNode.getNext();

            if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
            childOfMinHeapNode.mark = 0;
            childOfMinHeapNode.setParent(null);

            lastChild = childOfMinHeapNode;
        }

        // Update the new pointers
        this.tail = lastChild;
        lastChild.setNext(this.head);
        this.head.setPrev(lastChild);

        this.numOfMarks -= countmarks; // descend the numOfMarks by countmarks
        this.numOfTrees = this.numOfTrees -1 +this.minHeapNode.getRank(); // update the num of the trees after delet minHeapNode

    }

    /*
     * private void updateHead()
     *
     * update the pointers after delete the minHeapNode
     * O(k) <-> k is the number if children to the minHeapNode
     */
    private void updateHead()
    {
        // initialize pointers to the next\child heapNode of the minHeapNode
        HeapNode nextOfMinHeapNode = this.minHeapNode.getNext();
        HeapNode childOfMinHeapNode = this.minHeapNode.getChild();
        int countmarks = 0; // counter to the marked HeapNode that changed after delete minHeapNode

        if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked
        childOfMinHeapNode.mark = 0;
        childOfMinHeapNode.setParent(null);

        // Update the new pointers
        this.head = childOfMinHeapNode;
        childOfMinHeapNode.setPrev(tail);
        this.tail.setNext(childOfMinHeapNode);


        HeapNode lastChild = childOfMinHeapNode;
        // update the parent for the children of the minNide to be null
        for (int i = 2; i <= this.minHeapNode.rank; i++) {
            childOfMinHeapNode = childOfMinHeapNode.getNext();

            if (childOfMinHeapNode.mark == 1) countmarks++; // child of the minHeapNode is marked

            childOfMinHeapNode.mark = 0;
            childOfMinHeapNode.setParent(null);

            lastChild = childOfMinHeapNode;
        }

        // Update the new pointers
        nextOfMinHeapNode.setPrev(lastChild);
        lastChild.setNext(nextOfMinHeapNode);

        this.numOfMarks -= countmarks; // descend the numOfMarks by countmarks
        this.numOfTrees = this.numOfTrees -1 +this.minHeapNode.getRank(); // update the num of the trees after delet minHeapNode

    }

    /*
     * private void Consolidating()
     *
     * insert the the Roots and the children of the MinHeapNode to an array Depending on the ranks
     * after that biuld the new fibonacciHeap from the ranks in our array
     *
     * n = fibonacciHeap.Size()
     * WC - O(n)
     * Amort - O(log(n))
     */
    private void Consolidating()
    {
        HeapNode[] f = toBuckets();
        fromBuckets(f);

    }

    /*
     * private void fromBuckets(HeapNode[] f)
     *
     * biuld the new fibonacciHeap from the ranks in our array
     *
     * WC - O(log(n))
     */
    private void fromBuckets(HeapNode[] f)
    {
        // initialize the pointers to null
        this.head = null;
        this.tail = null;
        this.minHeapNode = null;

        HeapNode pos = null;
        // for loop th build the new fibonacciHeap
        for (int i = 0; i < f.length; i++) {

            if (this.head != null && f[i] != null) { // this.head != null && f[i] != null --> update the pointers
                pos.setNext(f[i]);
                f[i].setPrev(pos);
                this.head.setPrev(f[i]);
                f[i].setNext(this.head);
                this.tail = f[i];

                if (this.minHeapNode.getKey() > f[i].getKey()) this.minHeapNode = f[i]; // update the minHeapNode

                pos = f[i];
            }

            if (this.head == null && f[i] != null) { // this.head == null && f[i] != null --> update the pointers
                this.head = f[i];
                this.tail = f[i];
                this.minHeapNode = f[i];
                this.head.setNext(head);
                this.head.setPrev(head);

                pos = this.head;
            }


        }

    }

    /*
     * private HeapNode[] toBuckets()
     *
     * insert the the Roots and the children of the MinHeapNode to an array Depending on the ranks
     *
     * n = fibonacciHeap.Size()
     * WC - O(n)
     * Amort - O(log(n))
     */
    private HeapNode[] toBuckets()
    {
        HeapNode[] f = new HeapNode[(int)(Math.log(this.heapSize) / Math.log(2)) +1]; // initialize null HeapNode array
        HeapNode pos = this.head; // pointer to the first of the fibonacci

        // we insrt the HeapNode dependent in it's rank
        // when HeapNode in inserted to f[i] while f[i]==null --> no need for Link
        // when HeapNode in inserted to f[i] while f[i]!=null --> need for Link

        f[pos.getRank()] = pos;

        HeapNode posHelper = pos.getNext() ; // new pointer to the next HeapNode of the head HeapNode
        pos.setNext(null);
        pos.setPrev(null);

        while (posHelper != this.head) { // As long as we have not made a full round
            if (f[posHelper.getRank()] == null) { // no link
                f[posHelper.getRank()] = posHelper;

                pos = posHelper;
                posHelper = posHelper.getNext();
                //if (pos.getParent() != null) System.out.println("bug");
                pos.setNext(null);
                pos.setPrev(null);
            }

            else { // need Link
                boolean T = true;
                pos = posHelper;
                posHelper = posHelper.getNext();
                pos.setNext(null);
                pos.setPrev(null);
                HeapNode current = f[pos.getRank()];
                f[pos.getRank()] = null;
                while (T) { // HeapNode in inserted to f[i] while f[i]!=null --> need for Link


                    if (f[pos.getRank() +1] == null) { // HeapNode in inserted to f[i+1] while f[i]==null --> need for extra one Link lonly
                        f[pos.getRank()] = null;
                        f[pos.getRank() +1] = Link(pos, current); // link
                        T = false;

                    } else { // HeapNode in inserted to f[i+1] while f[i]!=null --> need for another Link
                        pos = Link(pos, current); // Link
                        current = f[pos.getRank()];
                        f[pos.getRank()] = null;
                        //if (pos.getRank() +1 == f.length) T = false;
                    }
                }
            }
        }

        return f; // arrat returned
    }


    /*
     * private HeapNode Link(HeapNode heapNode, HeapNode pos)
     *
     * RankheapNode = Rankpos = k
     * Linked to HeaoNodes to one with rank k+1
     *
     * O(1)
     */
    private HeapNode Link(HeapNode heapNode, HeapNode pos) {

        // pos is a new child to heapNode
        if (heapNode.getKey() < pos.getKey()) {
            if (heapNode.getRank() > 0) { // heapNode is with rank =0
                HeapNode heapNodeChild = heapNode.getChild();

                // update the pointers of heapNode child && posParent
                heapNode.setChild(pos);
                pos.setParent(heapNode);
                pos.setNext(heapNodeChild);
                pos.setPrev(heapNodeChild.getPrev());
                heapNodeChild.getPrev().setNext(pos);
                heapNodeChild.setPrev(pos);

            } else { // heapNode is with rank >0

                // update the pointers of heapNode child && posParent
                heapNode.setChild(pos);
                pos.setParent(heapNode);
                pos.setNext(pos);
                pos.setPrev(pos);
            }

            heapNode.setRank(heapNode.getRank() +1); // update the heapNodeRank
            this.numOfTrees--; // update the num of trees by 1
            totalLinks++; // update the num of totalLinks by 1

            return heapNode;

        } else { // heapNode.getKey() > pos.getKey()
            if (pos.getRank() > 0) { // pos is with rank =0
                HeapNode posChild = pos.getChild();

                // update the pointers of pos child && heapNodeParent
                pos.setChild(heapNode);
                heapNode.setParent(pos);
                heapNode.setNext(posChild);
                heapNode.setPrev(posChild.getPrev());
                posChild.getPrev().setNext(heapNode);
                posChild.setPrev(heapNode); //

            } else { // pos is with rank >0

                // update the pointers of pos child && heapNodeParent
                pos.setChild(heapNode);
                heapNode.setParent(pos);
                heapNode.setNext(heapNode);
                heapNode.setPrev(heapNode);
            }

            pos.setRank(pos.getRank() +1); // update the posRank
            this.numOfTrees--; // update the num of trees by 1
            totalLinks++; // update the num of totalLinks by 1

            return pos;
        }
    }

    /*
     * private void resetFibonacciHeap()
     *
     * reset the FibonacciHeap pointers to null
     * O(1)
     */
    private void resetFibonacciHeap()
    {
        this.head = null;
        this.tail = null;
        this.minHeapNode = null;

        this.heapSize = 0;
        this.numOfTrees = 0;

    }

    /**
     * public HeapNode findMin()
     *
     * Return the node of the heap whose key is minimal.
     *
     */
    public HeapNode findMin()
    {
        return this.minHeapNode; // Return the node of the heap whose key is minimal
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Meld the heap with heap2
     * O(1)
     */
    public void meld (FibonacciHeap heap2)
    {
        if (this.isEmpty()) update(heap2);	// this is empty
        else if (heap2.isEmpty()) return; // heap2 is empty
        else { // !(this is empty && heap2 is empty)

            // Linked the two FibonacciHeaps
            this.tail.next = heap2.head;
            heap2.head.prev = this.tail;

            updatePointersAfterMeld(heap2); // update the fields of this
        }

    }

    /*
     * private void updatePointersAfterMeld(FibonacciHeap heap2)
     *
     * update the fields for the FibonacciHeap
     * O(1)
     */
    private void updatePointersAfterMeld(FibonacciHeap heap2)
    {
        this.head.prev = heap2.tail;
        heap2.tail.next = this.head;

        this.tail = heap2.tail;
        this.heapSize += heap2.heapSize;
        this.numOfTrees += heap2.numOfTrees;
        if (heap2.minHeapNode.getKey() < this.minHeapNode.getKey()) this.minHeapNode = heap2.minHeapNode;

    }

    /*
     * private void update(FibonacciHeap heap2)
     *
     * is case that we do meld but empty == true
     * O(1)
     */
    private void update(FibonacciHeap heap2)
    {
        this.head = heap2.head;
        this.tail = heap2.tail;
        this.heapSize = heap2.heapSize;
        this.minHeapNode = heap2.minHeapNode;

    }

    /**
     * public int size()
     *
     * Return the number of elements in the heap
     * O(1)
     */
    public int size()
    {
        return this.heapSize;
    }

    /**
     * public int[] countersRep()
     *
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     *
     */
    public int[] countersRep()
    {
        if (size() != 0) { // FibonacciHeap is not empty

            int[] arr = new int[(int)((Math.log(size()) / Math.log(2)) +1)];
            HeapNode pos = this.head; // pointer to the head of the FibonacciHeap

            arr[pos.getRank()] = 1;
            pos = pos.next;

            while (pos != this.head) { // As long as we have not made a full round
                arr[pos.getRank()] += 1; // Update the appropriate place in the array
                pos = pos.next;
            }

            return arr; //Return a counters array
        }
        return new int [0]; //Return a counters array
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * first step - decreasekey(x,-infinity) && change x.ketKey to -infinity
     * second step - deleteMin()
     *
     *
     */
    public void delete(HeapNode x)
    {
        x.setKey(Integer.MIN_VALUE);
        this.decreaseKey(x, 0);
        this.deleteMin();

    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        if (x.getParent() == null) { // decrease a root HeapNode
            x.setKey(x.getKey() -delta);
            if (x.getKey() < this.minHeapNode.getKey()) this.minHeapNode = x;

        } else if (x.getKey() -delta >= x.getParent().getKey()) { // no cut needed
            x.setKey(x.getKey() -delta);

        } else { // (x.getKey() -delta < x.getParent().getKey())
            x.setKey(x.getKey() -delta);//
            cascadingCut(x, x.getParent());
        }
        return; // should be replaced by student code
    }

    /*
     * private void cascadingCut(HeapNode x, HeapNode parent)
     *
     * cut x HeapNode from his parent and insert x to the begining if the fibonacci heap
     *
     * O(n) WC
     * O(1) Amort
     */
    private void cascadingCut(HeapNode x, HeapNode parent)
    {
        // update the counter of totalCuts by one
        totalCuts++;
        cut(x, parent);


        if (parent.getParent() != null) { // parent not is a root
            if (parent.getMark() == 0) { // parent not marked
                parent.setMark(1);
                numOfMarks++;

            }
            else { // parent marked
                cascadingCut(parent, parent.getParent());

            }
        }

    }

    /*
     * private void cut(HeapNode x, HeapNode parent)
     *
     * cut x from his parent and insert to the head of the fibonacci heap
     */
    private void cut(HeapNode x, HeapNode parent)
    {

        // update the pointers
        x.setParent(null);
        if (x.getMark() == 1) this.numOfMarks--;
        x.setMark(0);
        parent.setRank(parent.getRank()-1);

        if (parent.getRank() == 0) { // parent has one child
            parent.setChild(null);

            x.addSibling(this.head, this.tail); // add x to the head of the fibonacci heap
            updatePointersAfterCut(x); // update the fields pointers after cut


        } else if (parent.getChild() == x) { // x is the first child to parent
            parent.setChild(x.getNext());
            x.getNext().setPrev(x.getPrev());
            x.getPrev().setNext(x.getNext());

            x.addSibling(this.head, this.tail); // add x to the head of the fibonacci heap
            updatePointersAfterCut(x); // update the fields pointers after cut


        } else { // x is not the first child to parent
            x.getNext().setPrev(x.getPrev());
            x.getPrev().setNext(x.getNext());

            x.addSibling(this.head, this.tail); // add x to the head of the fibonacci heap
            updatePointersAfterCut(x); // update the fields pointers after cut

        }

    }

    /*
     * private void updatePointersAfterCut(HeapNode cur)
     *
     * update the fields after cut
     * O(1)
     */
    private void updatePointersAfterCut(HeapNode cur)
    {
        this.head = cur;

        if (cur.getKey() < this.minHeapNode.getKey()) this.minHeapNode = cur;
        this.numOfTrees += 1;

    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        return this.numOfTrees + 2*this.numOfMarks; // should be replaced by student code
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the run-time of the program.
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * in its root.
     */
    public static int totalLinks()
    {
        return totalLinks; // should be replaced by student code
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return totalCuts; // should be replaced by student code
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k minimal elements in a binomial tree H.
     * The function should run in O(k*deg(H)).
     * You are not allowed to change H.
     *
     * in this founction we use a field similar ,to help as to get the children of all node
     * with out that we chanfe the source fibonacci heap
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        if (H == null || H.isEmpty() || k <= 0)
            return new int[0];

        int[] arr = new int[k];

        FibonacciHeap heap = new FibonacciHeap(); // initialize a new fibonacci heap
        heap.insert(H.findMin().getKey()); // inserted the H.findMin() in new HeapNode object - copy from the H.findMin()
        heap.head.similar = H.findMin(); // update the similar pointer of heap.head.similar to point to H.findMin()


        for(int i = 0; i < k; i++) { // k iterations

            arr[i] = heap.minHeapNode.getKey(); // We will put the next minimal organ in the heap
            HeapNode pos = heap.minHeapNode.similar.getChild(); // get a new pointer to the children of the heap.minHeapNode
            heap.deleteMin();

            if(pos != null) {

                // while we do not complete a full rotation
                // inserted the pos in new HeapNode object - copy from the pos
                // pos = pos.next

                HeapNode cur = pos;
                do {

                    heap.insert(pos.getKey());
                    heap.head.similar = pos;
                    pos = pos.next;
                } while (pos != cur);
            }

        }


        return arr; // returned the sorted array Keys from the min to the max
    }

    /*
     * public int getNumOfTrees()
     *
     * return the numbers of the trees in the fibonacci heap
     */
    public int getNumOfTrees() {
        return this.numOfTrees;
    }
    //todo-heapPrint method
    static void printIndentPrefix(ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        for (int i = 0; i < size - 1; ++i) {
            stream.format("%c   ", hasNexts.get(i).booleanValue() ? '│' : ' ');
        }
    }

    static void printIndent(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        printIndentPrefix(hasNexts);

        stream.format("%c── %s\n",
                hasNexts.get(size - 1) ? '├' : '╰',
                heapNode == null ? "(null)" : String.valueOf(heapNode.getKey())
        );
    }

    static String repeatString(String s,int count){
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    static void printIndentVerbose(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        if (heapNode == null) {
            printIndentPrefix(hasNexts);
            stream.format("%c── %s\n", hasNexts.get(size - 1) ? '├' : '╰', "(null)");
            return;
        }

        Function<Supplier<FibonacciHeap.HeapNode>, String> keyify = (f) -> {
            FibonacciHeap.HeapNode node = f.get();
            return node == null ? "(null)" : String.valueOf(node.getKey());
        };
        String title  = String.format(" Key: %d ", heapNode.getKey());
        List<String> content =  Arrays.asList(
                String.format(" Rank: %d ", heapNode.getRank()),
                String.format(" Marked: %b ", heapNode.getMark()),
                String.format(" Parent: %s ", keyify.apply(heapNode::getParent)),
                String.format(" Next: %s ", keyify.apply(heapNode::getNext)),
                String.format(" Prev: %s ", keyify.apply(heapNode::getPrev)),
                String.format(" Child: %s", keyify.apply(heapNode::getChild))
        );

        /* Print details in box */
        int length = Math.max(
                title.length(),
                content.stream().map(String::length).max(Integer::compareTo).get()
        );
        String line = repeatString("─", length);
        String padded = String.format("%%-%ds", length);
        boolean hasNext = hasNexts.get(size - 1);

        //print header row
        printIndentPrefix(hasNexts);
        stream.format("%c── ╭%s╮%n", hasNext ? '├' : '╰', line);

        //print title row
        printIndentPrefix(hasNexts);
        stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', title);

        // print separator
        printIndentPrefix(hasNexts);
        stream.format("%c   ├%s┤%n", hasNext ? '│' : ' ', line);

        // print content
        for (String data : content) {
            printIndentPrefix(hasNexts);
            stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', data);
        }

        // print footer
        printIndentPrefix(hasNexts);
        stream.format("%c   ╰%s╯%n", hasNext ? '│' : ' ', line);
    }

    static void printHeapNode(FibonacciHeap.HeapNode heapNode, FibonacciHeap.HeapNode until, ArrayList<Boolean> hasNexts, boolean verbose) {
        if (heapNode == null || heapNode == until) {
            return;
        }
        hasNexts.set(
                hasNexts.size() - 1,
                heapNode.getNext() != null && heapNode.getNext() != heapNode && heapNode.getNext() != until
        );
        if (verbose) {
            printIndentVerbose(heapNode, hasNexts);
        } else {
            printIndent(heapNode, hasNexts);
        }

        hasNexts.add(false);
        printHeapNode(heapNode.getChild(), null, hasNexts, verbose);
        hasNexts.remove(hasNexts.size() - 1);

        until = until == null ? heapNode : until;
        printHeapNode(heapNode.getNext(), until, hasNexts, verbose);
    }

    public static void print(FibonacciHeap heap, boolean verbose) {
        if (heap == null) {
            stream.println("(null)");
            return;
        } else if (heap.isEmpty()) {
            stream.println("(empty)");
            return;
        }

        stream.println("╮");
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        printHeapNode(heap.getFirst(), null, list, verbose);
    }
    //todo-heapPrint ends here-delete afterwards.

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in
     * another file
     *
     */
    public class HeapNode{

        public int key;
        private int rank;
        private int mark;

        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;

        private HeapNode similar;

        public HeapNode(int key) { // constructor a new HeapNode with the default Except for key
            this.key = key;
        }

        public HeapNode(int key, HeapNode prev, HeapNode next) { // constructor a new HeapNode with the default Except for key ,prev and next
            this.key = key;
            this.prev = prev;
            this.next = next;

        }

        public HeapNode(int key, HeapNode child, int rank) { // constructor a new HeapNode with the default Except for key ,child and rank
            this.key = key;
            this.child = child;
            this.rank = rank;
        }
        public int getKey() { // get key
            return this.key;
        }
        public void setKey(int key) { // set key
            this.key = key;
        }

        public int getRank() { // get rank
            return this.rank;
        }
        public void setRank(int rank) { // set rank
            this.rank = rank;
        }

        public int getMark() { // get mark
            return this.mark;
        }
        public void setMark(int mark) { // set mark
            this.mark = mark;
        }

        public HeapNode getChild() { // get child
            return this.child;
        }
        public void setChild(HeapNode child) { // set child
            this.child = child;
        }

        public HeapNode getNext() { // get next
            return this.next;
        }
        public void setNext(HeapNode next) { // set next
            this.next = next;
        }

        public HeapNode getPrev() { // get prev
            return this.prev;
        }
        public void setPrev(HeapNode prev) { // set prev
            this.prev = prev;
        }

        public HeapNode getParent() { // get parent
            return this.parent;
        }
        public void setParent(HeapNode parent) { // set parent
            this.parent = parent;
        }

        /*
         * public void addSibling(HeapNode sibling1, HeapNode sibling2)
         *
         * update the next and prev for this(HeapNode)
         *
         * O(1)
         */
        public void addSibling(HeapNode sibling1, HeapNode sibling2) {
            this.next = sibling1;
            this.prev = sibling2;
            sibling1.prev = this;
            sibling2.next = this;


        }

    }


    /*
     * public HeapNode getFirst()
     *
     * returned the head of the fibonacci heap
     * o(1)
     */
    public HeapNode getFirst() {
        // TODO Auto-generated method stub
        return this.head;
    }


}
