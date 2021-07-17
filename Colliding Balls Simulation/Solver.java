import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Solver
{
    private int minCount;
    private Board[] orderedAnswer;

    public Solver(Board initial)
    {
        final MinPQ<Node> pq;
        final MinPQ<Node> pqTwin;
        if (initial == null)
            throw new IllegalArgumentException("IllegalArgumentException");
        pq = new MinPQ<Node>();
        pqTwin = new MinPQ<Node>();
        Node root = new Node(initial, 0, null);
        pq.insert(root);
        Node rootTwin = new Node(initial.twin(), 0, null);
        pqTwin.insert(rootTwin);

        boolean turn = true;
        while (true)
        {
            if (turn)
            {
                Node x = workOneQueue(pq);
                if (x != null)
                {
                    minCount = x.moves;
                    makeAnswer(x);
                    break;
                }
                turn = false;
            }
            else
            {
                Node x = workOneQueue(pqTwin);
                if (x != null)
                {
                    minCount = -1;
                    makeAnswer(null);
                    break;
                }
                turn = true;
            }
        }
    }
    private Node workOneQueue(MinPQ<Node> pq)
    {
        Node x = pq.delMin();
        if (x.board.hamming() == 0)
            return x;
        Iterator<Board> it = x.board.neighbors().iterator();
        while (it.hasNext())
        {
            Board board = it.next();
            if (checkBack(x.prev, board))
                continue;
            Node toInsert = new Node(board, x.moves + 1, x);
            pq.insert(toInsert);
        }
        return null;
    }
    private boolean checkBack(Node a, Board b)
    {
        if (a == null)
            return false;
        return a.board.equals(b);
    }
    private void makeAnswer(Node x)
    {
        if (x == null)
        {
            orderedAnswer = new Board[1];
            orderedAnswer[0] = null;
        }
        orderedAnswer = new Board[minCount + 1];
        for (int i = minCount; i >= 0; i--)
        {
            orderedAnswer[i] = x.board;
            x = x.prev;
        }
    }

    public boolean isSolvable()
    {
        return minCount != -1;
    }
    public int moves()
    {
        return minCount;
    }
    public Iterable<Board> solution()
    {
        if (!isSolvable())
            return null;
        return new arrayAsIterable();
    }
    private class arrayAsIterable implements Iterable<Board>
    {
        public Iterator<Board> iterator()
        {
            return new arrayAsIterator();
        }
        private class arrayAsIterator implements Iterator<Board>
        {
            private int current = 0;
            public boolean hasNext()
            {
                return current <= minCount;
            }
            public void remove()
            {
                throw new UnsupportedOperationException("UnsupportedOperationException");
            }
            public Board next()
            {
                if (current > minCount)
                    throw new NoSuchElementException("ReachedEnd");
                Board board = orderedAnswer[current];
                current++;
                return board;
            }

        }
    }

    private class Node implements Comparable<Node>
    {
        public final Board board;
        public final int moves;        //number of moves to reach here
        public Node prev;
        public final int priorityNumber;


        public Node(Board board, int moves, Node prev)
        {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
            priorityNumber = this.moves + board.manhattan();
        }

        public int compareTo(Node o)
        {
            if (this == o)
                return 0;
            return this.priorityNumber - o.priorityNumber;
        }
    }

    public static void main(String[] args)
    {
        /*int n = 3;
        //int[][] tiles = {{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        //int[][] tiles = {{1, 2, 3}, {4, 5, 6}, {0, 7, 8}};
        int[][] tiles = {{0, 1, 3}, {4, 2, 5}, {7, 8, 6}};
        Board b = new Board(tiles);
        Solver solver = new Solver(b);
        System.out.println(solver.moves());
        Iterator<Board> it = solver.solution().iterator();
        while (it.hasNext())
        {
            Board board = it.next();
            System.out.println(board.toString());
        }*/

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
