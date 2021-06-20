import java.util.*;

public class Main {
    static Random rand = new Random();
    static List<Integer> randomTopologicalSort(DirectedGraph graph){
        final int V = graph.nodeSize();
        int[] d = new int[V];
        for(DirectedGraph.Edge e: graph.getAllEdges()){
            d[e.getTo()]++;
        }

        ArrayList<Integer> top = new ArrayList<>();
        List<Integer> ans = new ArrayList<>();
        for(int v=0; v<V; v++)if(d[v]==0)top.add(v);

        while(!top.isEmpty()){
            int r = rand.nextInt(top.size());
            int u = top.get(r);
            ans.add(u);
            top.set(r, top.get(top.size()-1));
            top.remove(top.size()-1);
            for(DirectedGraph.Edge e: graph.outEdges(u)){
                d[e.getTo()]--;
                if(d[e.getTo()]==0) top.add(e.getTo());
            }
        }
        if(ans.size() < V) return null;
        else return ans;
    }
    public static void main(String[] args){
        final long TIME_LIMIT = 3000 - 500;
        ContestScanner sc = new ContestScanner();
        ContestPrinter pr = new ContestPrinter();

        final int N = sc.nextInt(), M = sc.nextInt(), K = sc.nextInt();
        DirectedGraph graph = new DirectedGraph(N);
        for(int m=0; m<M; m++){
            graph.addEdge(sc.nextInt()-1, sc.nextInt()-1);
        }

        final long startTime = System.currentTimeMillis();
        
        Set<List<Integer>> ans = new HashSet<>();

        while(System.currentTimeMillis() - startTime < TIME_LIMIT && ans.size() < K){
            List<Integer> a = randomTopologicalSort(graph);
            if(a != null) ans.add(a);
        }

        if(ans.size() < K) pr.println(-1);
        else{
            for(List<Integer> a: ans){
                var array = a.toArray(new Integer[N]);
                for(int n=0; n<N-1; n++){
                    pr.print(array[n]+1);
                    pr.print(" ");
                }
                pr.println(array[N-1]+1);
            }
        }
        pr.close();
    }
}

class DirectedGraph{
    private static long INF = 1L << 60;
    
    class Edge implements Comparable<Edge>{
        int from, to;
        long length;
        public Edge(int from, int to, long len){
            this.from = from;
            this.to = to;
            this.length = len;
        }
        public int getFrom(){return from;}
        public int getTo(){return to;}
        public long getLength(){return length;}

        public int compareTo(Edge o){
            return java.util.Comparator.comparingLong(Edge::getLength)
                   .thenComparingInt(Edge::getFrom)
                   .thenComparingInt(Edge::getTo).compare(this, o);
        }
        public String toString(){
            return String.format("[%d->%d, %d]", from,to, length);
        }
    }

    private int V;
    private int E;
    private java.util.List<Edge>[] in, out;
    private java.util.List<Edge> allEdge;

    public DirectedGraph(int V){
        this.V = V;
        this.E = 0;
        in = new java.util.ArrayList[V];
        out = new java.util.ArrayList[V];
        for(int v=0; v<V; v++) in[v] = new java.util.ArrayList<>();
        for(int v=0; v<V; v++) out[v] = new java.util.ArrayList<>();
        allEdge = new java.util.ArrayList<>();
    }

    public void addEdge(int from, int to, long length){
        Edge e = new Edge(from, to, length);
        out[from].add(e);
        in[to].add(e);
        allEdge.add(e);
        E++;
    }
    public void addEdge(int from, int to){
        addEdge(from, to, 1);
    }
    public int nodeSize(){return V;}
    public java.util.List<Edge> outEdges(int v){
        return out[v];
    }
    public java.util.List<Integer> outNeighbours(int v){
        java.util.ArrayList<Integer> ans = new java.util.ArrayList<>();
        for(Edge e: outEdges(v)) ans.add(e.getTo());
        return ans;
    }

    public java.util.List<Edge> getAllEdges(){return allEdge;}

    public long[] dijkstra(int start){
        long[] ans = new long[V];
        java.util.Arrays.fill(ans, INF);
        java.util.PriorityQueue<Pair<Long,Integer>> queue = new java.util.PriorityQueue<>();
        queue.add(new Pair<>(0L, start));

        while(!queue.isEmpty()){
            Pair<Long,Integer> top = queue.poll();
            long d = top.getFirst();
            int v = top.getSecond();
            
            if(d > ans[v]) continue;

            ans[v] = d;
            for(Edge e: outEdges(v)){
                if(d + e.getLength() < ans[e.getTo()]){
                    queue.add(new Pair<>(d + e.getLength(), e.getTo()));
                }                
            }
        }
        return ans;
    }

    public long[] bellmanford(int start) throws Exception{
        long[] distance = new long[V];
        java.util.Arrays.fill(distance, INF);
        distance[start] = 0;
        for(int i=0; i<V; i++){
            for(Edge e: allEdge){
                int u = e.getFrom(), v = e.getTo();
                long l = e.getLength();
                if(distance[v] > distance[u] + l) distance[v] = distance[u] + l;
            }
        }

        for(Edge e: allEdge){
            int u = e.getFrom(), v = e.getTo();
            long l = e.getLength();
            if(distance[v] > distance[u] + l) throw new Exception("Bellman-Ford algorithm: negative-weight cycle found");
        }

        return distance;
    }

    public long[] bfs01(int start){
        long[] ans = new long[V];
        java.util.Arrays.fill(ans, INF);
        ans[start] = 0;
 
        java.util.LinkedList<Integer> queue = new java.util.LinkedList<>();
        queue.add(start);
        while(!queue.isEmpty()){
            int v = queue.pollFirst();
            for(Edge e: outEdges(v)) if(ans[e.getTo()] > ans[v] + e.getLength()){
                ans[e.getTo()] = ans[v] + e.getLength();
                if(e.getLength() > 0) queue.addLast(e.getTo());
                else queue.addFirst(e.getTo());
            }
        }
        return ans;
    }

    public long[][] warshallfloyd(){
        long[][] ans = new long[V][V];
        for(int i=0; i<V; i++)for(int j=0; j<V; j++){
            ans[i][j] = INF;
        }
        for(Edge e: allEdge) ans[e.getFrom()][e.getTo()] = e.getLength();

        for(int k=0; k<V; k++)for(int i=0; i<V; i++)for(int j=0; j<V; j++){
            ans[i][j] = Math.min(ans[i][j], ans[i][k]+ans[k][j]);
        }
        return ans;
    }
    public String toString(){return java.util.Arrays.toString(out);}
}

class DSU {
	private int n;
	private int[] parentOrSize;

	public DSU(int n) {
		this.n = n;
		this.parentOrSize = new int[n];
		java.util.Arrays.fill(parentOrSize, -1);
	}

	int merge(int a, int b) {
		if (!(0 <= a && a < n))
			throw new IndexOutOfBoundsException("a=" + a);
		if (!(0 <= b && b < n))
			throw new IndexOutOfBoundsException("b=" + b);

		int x = leader(a);
		int y = leader(b);
		if (x == y) return x;
		if (-parentOrSize[x] < -parentOrSize[y]) {
			int tmp = x;
			x = y;
			y = tmp;
		}
		parentOrSize[x] += parentOrSize[y];
		parentOrSize[y] = x;
		return x;
	}

	boolean same(int a, int b) {
		if (!(0 <= a && a < n))
			throw new IndexOutOfBoundsException("a=" + a);
		if (!(0 <= b && b < n))
			throw new IndexOutOfBoundsException("b=" + b);
		return leader(a) == leader(b);
	}

	int leader(int a) {
		if (parentOrSize[a] < 0) {
			return a;
		} else {
			parentOrSize[a] = leader(parentOrSize[a]);
			return parentOrSize[a];
		}
	}

	int size(int a) {
		if (!(0 <= a && a < n))
			throw new IndexOutOfBoundsException("" + a);
		return -parentOrSize[leader(a)];
	}

	java.util.ArrayList<java.util.ArrayList<Integer>> groups() {
		int[] leaderBuf = new int[n];
		int[] groupSize = new int[n];
		for (int i = 0; i < n; i++) {
			leaderBuf[i] = leader(i);
			groupSize[leaderBuf[i]]++;
		}
		java.util.ArrayList<java.util.ArrayList<Integer>> result = new java.util.ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			result.add(new java.util.ArrayList<>(groupSize[i]));
		}
		for (int i = 0; i < n; i++) {
			result.get(leaderBuf[i]).add(i);
		}
		result.removeIf(java.util.ArrayList::isEmpty);
		return result;
	}
}
class Pair<S extends Comparable<S>, T extends Comparable<T>> implements Comparable<Pair<S,T>>{
    S first;
    T second;

    public Pair(S s, T t){
        first = s;
        second = t;
    }
    public S getFirst(){return first;}
    public T getSecond(){return second;}
    public boolean equals(Object another){
        if(this==another) return true;
        if(!(another instanceof Pair)) return false;
        Pair otherPair = (Pair)another;
        return this.first.equals(otherPair.first) && this.second.equals(otherPair.second);
    }
    public int compareTo(Pair<S,T> another){
        java.util.Comparator<Pair<S,T>> comp1 = java.util.Comparator.comparing(Pair::getFirst);
        java.util.Comparator<Pair<S,T>> comp2 = comp1.thenComparing(Pair::getSecond);
        return comp2.compare(this, another);
    }
    public int hashCode(){
        return first.hashCode() * 10007 + second.hashCode();
    }
    public String toString(){
        return String.format("(%s, %s)", first, second);
    }
}

class ContestScanner {
    private final java.io.InputStream in;
    private final byte[] buffer = new byte[1024];
    private int ptr = 0;
    private int buflen = 0;

    private static final long LONG_MAX_TENTHS = 922337203685477580L;
    private static final int LONG_MAX_LAST_DIGIT = 7;
    private static final int LONG_MIN_LAST_DIGIT = 8;

    public ContestScanner(java.io.InputStream in){
        this.in = in;
    }
    public ContestScanner(java.io.File file) throws java.io.FileNotFoundException{
        this.in = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
    }
    public ContestScanner(){
        this(System.in);
    }
 
    private boolean hasNextByte() {
        if (ptr < buflen) {
            return true;
        }else{
            ptr = 0;
            try {
                buflen = in.read(buffer);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            if (buflen <= 0) {
                return false;
            }
        }
        return true;
    }
    private int readByte() { 
        if (hasNextByte()) return buffer[ptr++]; else return -1;
    }
    private static boolean isPrintableChar(int c) {
        return 33 <= c && c <= 126;
    }
    public boolean hasNext() {
        while(hasNextByte() && !isPrintableChar(buffer[ptr])) ptr++;
        return hasNextByte();
    }
    public String next() {
        if (!hasNext()) throw new java.util.NoSuchElementException();
        StringBuilder sb = new StringBuilder();
        int b = readByte();
        while(isPrintableChar(b)) {
            sb.appendCodePoint(b);
            b = readByte();
        }
        return sb.toString();
    }
 
    public long nextLong() {
        if (!hasNext()) throw new java.util.NoSuchElementException();
        long n = 0;
        boolean minus = false;
        int b = readByte();
        if (b == '-') {
            minus = true;
            b = readByte();
        }
        if (b < '0' || '9' < b) {
            throw new NumberFormatException();
        }
        while (true) {
            if ('0' <= b && b <= '9') {
                int digit = b - '0';
                if (n >= LONG_MAX_TENTHS) {
                    if (n == LONG_MAX_TENTHS) {
                        if (minus) {
                            if (digit <= LONG_MIN_LAST_DIGIT) {
                                n = -n * 10 - digit;
                                b = readByte();
                                if (!isPrintableChar(b)) {
                                    return n;
                                } else if (b < '0' || '9' < b) {
                                    throw new NumberFormatException(
                                        String.format("%d%s... is not number", n, Character.toString(b))
                                    );
                                }
                            }
                        } else {
                            if (digit <= LONG_MAX_LAST_DIGIT) {
                                n = n * 10 + digit;
                                b = readByte();
                                if (!isPrintableChar(b)) {
                                    return n;
                                } else if (b < '0' || '9' < b) {
                                    throw new NumberFormatException(
                                        String.format("%d%s... is not number", n, Character.toString(b))
                                    );
                                }
                            }
                        }
                    }
                    throw new ArithmeticException(
                        String.format("%s%d%d... overflows long.", minus ? "-" : "", n, digit)
                    );
                }
                n = n * 10 + digit;
            }else if(b == -1 || !isPrintableChar(b)){
                return minus ? -n : n;
            }else{
                throw new NumberFormatException();
            }
            b = readByte();
        }
    }
    public int nextInt() {
        long nl = nextLong();
        if (nl < Integer.MIN_VALUE || nl > Integer.MAX_VALUE) throw new NumberFormatException();
        return (int) nl;
    }
    public double nextDouble() {
        return Double.parseDouble(next());
    }
 
    public long[] nextLongArray(int length){
        long[] array = new long[length];
        for(int i=0; i<length; i++) array[i] = this.nextLong();
        return array;
    }
    public long[] nextLongArray(int length, java.util.function.LongUnaryOperator map){
        long[] array = new long[length];
        for(int i=0; i<length; i++) array[i] = map.applyAsLong(this.nextLong());
        return array;
    }
    public int[] nextIntArray(int length){
        int[] array = new int[length];
        for(int i=0; i<length; i++) array[i] = this.nextInt();
        return array;
    }
    public int[] nextIntArray(int length, java.util.function.IntUnaryOperator map){
        int[] array = new int[length];
        for(int i=0; i<length; i++) array[i] = map.applyAsInt(this.nextInt());
        return array;
    }
    public double[] nextDoubleArray(int length){
        double[] array = new double[length];
        for(int i=0; i<length; i++) array[i] = this.nextDouble();
        return array;
    }
    public double[] nextDoubleArray(int length, java.util.function.DoubleUnaryOperator map){
        double[] array = new double[length];
        for(int i=0; i<length; i++) array[i] = map.applyAsDouble(this.nextDouble());
        return array;
    }
 
    public long[][] nextLongMatrix(int height, int width){
        long[][] mat = new long[height][width];
        for(int h=0; h<height; h++) for(int w=0; w<width; w++){
            mat[h][w] = this.nextLong();
        }
        return mat;
    }
    public int[][] nextIntMatrix(int height, int width){
        int[][] mat = new int[height][width];
        for(int h=0; h<height; h++) for(int w=0; w<width; w++){
            mat[h][w] = this.nextInt();
        }
        return mat;
    }
    public double[][] nextDoubleMatrix(int height, int width){
        double[][] mat = new double[height][width];
        for(int h=0; h<height; h++) for(int w=0; w<width; w++){
            mat[h][w] = this.nextDouble();
        }
        return mat;
    }
 
    public char[][] nextCharMatrix(int height, int width){
        char[][] mat = new char[height][width];
        for(int h=0; h<height; h++){
            String s = this.next();
            for(int w=0; w<width; w++){
                mat[h][w] = s.charAt(w);
            }
        }
        return mat;
    }
}

class ContestPrinter extends java.io.PrintWriter{
    public ContestPrinter(java.io.PrintStream stream){
        super(stream);
    }
    public ContestPrinter(){
        super(System.out);
    }
    public ContestPrinter(java.io.File file) throws java.io.FileNotFoundException{
        super(file);
    }
    
    private static String dtos(double x, int n) {
        StringBuilder sb = new StringBuilder();
        if(x < 0){
            sb.append('-');
            x = -x;
        }
        x += Math.pow(10, -n)/2;
        sb.append((long)x);
        sb.append(".");
        x -= (long)x;
        for(int i = 0;i < n;i++){
            x *= 10;
            sb.append((int)x);
            x -= (int)x;
        }
        return sb.toString();
    }

    @Override
    public void print(float f){
        super.print(dtos(f, 20));
    }
    @Override
    public void println(float f){
        super.println(dtos(f, 20));
    }
    @Override
    public void print(double d){
        super.print(dtos(d, 20));
    }
    @Override
    public void println(double d){
        super.println(dtos(d, 20));
    }
    
    

    public void printArray(int[] array, String separator){
        int n = array.length;
        if(n==0){
            super.println();
            return;
        }
        for(int i=0; i<n-1; i++){
            super.print(array[i]);
            super.print(separator);
        }
        super.println(array[n-1]);
    }
    public void printArray(int[] array){
        this.printArray(array, " ");
    }
    public void printArray(int[] array, String separator, java.util.function.IntUnaryOperator map){
        int n = array.length;
        if(n==0){
            super.println();
            return;
        }
        for(int i=0; i<n-1; i++){
            super.print(map.applyAsInt(array[i]));
            super.print(separator);
        }
        super.println(map.applyAsInt(array[n-1]));
    }
    public void printArray(int[] array, java.util.function.IntUnaryOperator map){
        this.printArray(array, " ", map);
    }

    public void printArray(long[] array, String separator){
        int n = array.length;
        if(n==0){
            super.println();
            return;
        }
        for(int i=0; i<n-1; i++){
            super.print(array[i]);
            super.print(separator);
        }
        super.println(array[n-1]);
    }
    public void printArray(long[] array){
        this.printArray(array, " ");
    }
    public void printArray(long[] array, String separator, java.util.function.LongUnaryOperator map){
        int n = array.length;
        if(n==0){
            super.println();
            return;
        }
        for(int i=0; i<n-1; i++){
            super.print(map.applyAsLong(array[i]));
            super.print(separator);
        }
        super.println(map.applyAsLong(array[n-1]));
    }
    public void printArray(long[] array, java.util.function.LongUnaryOperator map){
        this.printArray(array, " ", map);
    }    
}
