package math;

import java.util.ArrayList;

import adversary.MoveSet;
/* The following code was used from :
 * https://www.sanfoundry.com/java-program-implement-self-balancing-binary-search-tree/
 * with some slight modifications to store a movements set in a node and use a float 
 * instead of an int for evaulation
 */
/* Class SBBSTNode */
class SBBSTNode
{    
    SBBSTNode left, right;
    float data;
    int height;
    ArrayList<MoveSet> moveSets;
    /* Constructor */
    public SBBSTNode()
    {
        left = null;
        right = null;
        data = 0;
        height = 0;
    }
    /* Constructor */
    public SBBSTNode(float n,MoveSet moveSet)
    {
        left = null;
        right = null;
        data = n;
        height = 0;
        this.moveSets = new ArrayList<>();
        moveSets.add(moveSet);
    }     
    public void addMoveSet(MoveSet moveSet) {
        moveSets.add(moveSet);
    }
}

/* Class SelfBalancingBinarySearchTree */
public class SelfBalancingBinarySearchTree
{
    private SBBSTNode root;     
    public ArrayList<MoveSet> players;
    /* Constructor */
    public SelfBalancingBinarySearchTree()
    {
    	players = new ArrayList<>();
        root = null;
    }

    /* Function to check if tree is empty */
    public boolean isEmpty()
    {
        return root == null;
    }

    /* Make the tree logically empty */
    public void clear()
    {
        root = null;
    }
    /* Function to insert data */
    public void insert(float data,MoveSet moveSet)
    {
        root = insert(data,moveSet, root);
    }
    /* Function to get height of node */
    private int height(SBBSTNode t )
    {
        return t == null ? -1 : t.height;
    }
    /* Function to max of left/right node */
    private int max(int lhs, int rhs)
    {
        return lhs > rhs ? lhs : rhs;
    }
    /* Function to insert data recursively */
    private SBBSTNode insert(float x,MoveSet moveSet, SBBSTNode t)
    {
        if (t == null)
            t = new SBBSTNode(x,moveSet);
        else if (x < t.data)
        {
            t.left = insert( x,moveSet, t.left );
            if (height( t.left ) - height( t.right ) == 2)
                if (x < t.left.data)
                    t = rotateWithLeftChild( t );
                else
                    t = doubleWithLeftChild( t );
        }
        else if (x > t.data)
        {
            t.right = insert( x,moveSet, t.right );
            if (height( t.right ) - height( t.left ) == 2)
                if (x > t.right.data)
                    t = rotateWithRightChild( t );
                else
                    t = doubleWithRightChild( t );
        }
        else t.addMoveSet(moveSet);  // Duplicate eval;
        t.height = max( height( t.left ), height( t.right ) ) + 1;
        return t;
    }
    /* Rotate binary tree node with left child */     
    private SBBSTNode rotateWithLeftChild(SBBSTNode k2)
    {
        SBBSTNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = max( height( k1.left ), k2.height ) + 1;
        return k1;
    }

    /* Rotate binary tree node with right child */
    private SBBSTNode rotateWithRightChild(SBBSTNode k1)
    {
        SBBSTNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = max( height( k2.right ), k1.height ) + 1;
        return k2;
    }
    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child */
    private SBBSTNode doubleWithLeftChild(SBBSTNode k3)
    {
        k3.left = rotateWithRightChild( k3.left );
        return rotateWithLeftChild( k3 );
    }
    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child */      
    private SBBSTNode doubleWithRightChild(SBBSTNode k1)
    {
        k1.right = rotateWithLeftChild( k1.right );
        return rotateWithRightChild( k1 );
    }    
    /* Functions to count number of nodes */
    public int countNodes()
    {
        return countNodes(root);
    }
    private int countNodes(SBBSTNode r)
    {
        if (r == null)
            return 0;
        else
        {
            int l = 1;
            l += countNodes(r.left);
            l += countNodes(r.right);
            return l;
        }
    }
    /* Functions to search for an element */
    public boolean search(int val)
    {
        return search(root, val);
    }
    private boolean search(SBBSTNode r, int val)
    {
        boolean found = false;
        while ((r != null) && !found)
        {
            float rval = r.data;
            if (val < rval)
                r = r.left;
            else if (val > rval)
                r = r.right;
            else
            {
                found = true;
                break;
            }
            found = search(r, val);
        }
        return found;
    }
    /* Function for inorder traversal */
    public void inorder()
    {
        inorder(root);
    }
    private void inorder(SBBSTNode r)
    {
        if (r != null)
        {
            inorder(r.left);
            for(MoveSet moveSet : r.moveSets) {
                players.add(moveSet);
            }
            inorder(r.right);
        }
    }
    /* Function for preorder traversal */
    public void preorder()
    {
        preorder(root);
    }
    private void preorder(SBBSTNode r)
    {
        if (r != null)
        {
            System.out.print(r.data +" ");
            preorder(r.left);             
            preorder(r.right);
        }
    }
    /* Function for postorder traversal */
    public void postorder()
    {
        postorder(root);
    }
    private void postorder(SBBSTNode r)
    {
        if (r != null)
        {
            postorder(r.left);             
            postorder(r.right);
            System.out.print(r.data +" ");
        }
    }     
}