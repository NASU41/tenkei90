import java.util.*;
 
public class Main {
    private static long sum(long[] a){
        long r = 0;
        for(long e:a) r += e;
        return r;
    }
    public static void main(String[] args){
        ContestScanner sc = new ContestScanner();
        ContestPrinter pr = new ContestPrinter();
 
        int N = sc.nextInt();
        long W = sc.nextLong();
        
        final int s = N, t = N+1;
        MaxFlow flow = new MaxFlow(N+2);
 
        long[] A = sc.nextLongArray(N);
        for(int n=0; n<N; n++){
            int k = sc.nextInt();
            flow.addEdge(s, n, A[n]);
            flow.addEdge(n, t, W);
            int[] c = sc.nextIntArray(k, x -> x-1);
            for(int key: c) flow.addEdge(key,n, 1_000_000_000_000_000L);
        }
 
        pr.println(sum(A) - flow.maxFlow(s, t));
 
        pr.close();
    }
}
 
class MaxFlow {
    private static final class InternalCapEdge {
        final int to;
        final int rev;
        long cap;
        InternalCapEdge(int to, int rev, long cap) { this.to = to; this.rev = rev; this.cap = cap; }
    }
    public static final class CapEdge {
        public final int from, to;
        public final long cap, flow;
        CapEdge(int from, int to, long cap, long flow) { this.from = from; this.to = to; this.cap = cap; this.flow = flow; }
        @Override
        public boolean equals(Object o) {
            if (o instanceof CapEdge) {
                CapEdge e = (CapEdge) o;
                return from == e.from && to == e.to && cap == e.cap && flow == e.flow;
            }
            return false;
        }
        @Override public String toString(){
            return String.format("%d -> %d: %d/%d", from, to, flow, cap);
        }
    }
    private static final class IntPair {
        final int first, second;
        IntPair(int first, int second) { this.first = first; this.second = second; }
    }
 
    static final long INF = Long.MAX_VALUE;
 
    private final int n;
    private final java.util.ArrayList<IntPair> pos;
    private final java.util.ArrayList<InternalCapEdge>[] g;
 
    @SuppressWarnings("unchecked")
    public MaxFlow(int n) {
        this.n = n;
        this.pos = new java.util.ArrayList<>();
        this.g = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++) {
            this.g[i] = new java.util.ArrayList<>();
        }
    }
 
    public int addEdge(int from, int to, long cap) {
        rangeCheck(from, 0, n);
        rangeCheck(to, 0, n);
        nonNegativeCheck(cap, "Capacity");
        int m = pos.size();
        pos.add(new IntPair(from, g[from].size()));
        int fromId = g[from].size();
        int toId = g[to].size();
        if (from == to) toId++;
        g[from].add(new InternalCapEdge(to, toId, cap));
        g[to].add(new InternalCapEdge(from, fromId, 0L));
        return m;
    }
 
    private InternalCapEdge getInternalEdge(int i) {
        return g[pos.get(i).first].get(pos.get(i).second);
    }
 
    private InternalCapEdge getInternalEdgeReversed(InternalCapEdge e) {
        return g[e.to].get(e.rev);
    }
 
    public CapEdge getEdge(int i) {
        int m = pos.size();
        rangeCheck(i, 0, m);
        InternalCapEdge e = getInternalEdge(i);
        InternalCapEdge re = getInternalEdgeReversed(e);
        return new CapEdge(re.to, e.to, e.cap + re.cap, re.cap);
    }
 
    public CapEdge[] getEdges() {
        CapEdge[] res = new CapEdge[pos.size()];
        java.util.Arrays.setAll(res, this::getEdge);
        return res;
    }
 
    public void changeEdge(int i, long newCap, long newFlow) {
        int m = pos.size();
        rangeCheck(i, 0, m);
        nonNegativeCheck(newCap, "Capacity");
        if (newFlow > newCap) {
            throw new IllegalArgumentException(
                String.format("Flow %d is greater than the capacity %d.", newCap, newFlow)
            );
        }
        InternalCapEdge e = getInternalEdge(i);
        InternalCapEdge re = getInternalEdgeReversed(e);
        e.cap = newCap - newFlow;
        re.cap = newFlow;
    }
 
    public long maxFlow(int s, int t) {
        return flow(s, t, INF);
    }
 
    public long flow(int s, int t, long flowLimit) {
        rangeCheck(s, 0, n);
        rangeCheck(t, 0, n);
        long flow = 0L;
        int[] level = new int[n];
        int[] que = new int[n];
        int[] iter = new int[n];
        while (flow < flowLimit) {
            bfs(s, t, level, que);
            if (level[t] < 0) break;
            java.util.Arrays.fill(iter, 0);
            while (flow < flowLimit) {
                long d = dfs(t, s, flowLimit - flow, iter, level);
                if (d == 0) break;
                flow += d;
            }
        }
        return flow;
    }
 
    private void bfs(int s, int t, int[] level, int[] que) {
        java.util.Arrays.fill(level, -1);
        int hd = 0, tl = 0;
        que[tl++] = s;
        level[s] = 0;
        while (hd < tl) {
            int u = que[hd++];
            for (InternalCapEdge e : g[u]) {
                int v = e.to;
                if (e.cap == 0 || level[v] >= 0) continue;
                level[v] = level[u] + 1;
                if (v == t) return;
                que[tl++] = v;
            }
        }
    }
 
    private long dfs(int cur, int s, long flowLimit, int[] iter, int[] level) {
        if (cur == s) return flowLimit;
        long res = 0;
        int curLevel = level[cur];
        for (int itMax = g[cur].size(); iter[cur] < itMax; iter[cur]++) {
            int i = iter[cur];
            InternalCapEdge e = g[cur].get(i);
            InternalCapEdge re = getInternalEdgeReversed(e);
            if (curLevel <= level[e.to] || re.cap == 0) continue;
            long d = dfs(e.to, s, Math.min(flowLimit - res, re.cap), iter, level);
            if (d <= 0) continue;
            e.cap += d;
            re.cap -= d;
            res += d;
            if (res == flowLimit) break;
        }
        return res;
    }
 
    public boolean[] minCut(int s) {
        rangeCheck(s, 0, n);
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int ptr = 0;
        stack[ptr++] = s;
        visited[s] = true;
        while (ptr > 0) {
            int u = stack[--ptr];
            for (InternalCapEdge e : g[u]) {
                int v = e.to;
                if (e.cap > 0 && !visited[v]) {
                    visited[v] = true;
                    stack[ptr++] = v;
                }
            }
        }
        return visited;
    }
 
    private void rangeCheck(int i, int minInclusive, int maxExclusive) {
        if (i < 0 || i >= maxExclusive) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds for length %d", i, maxExclusive)
            );
        }
    }
 
    private void nonNegativeCheck(long cap, String attribute) {
        if (cap < 0) {
            throw new IllegalArgumentException(
                String.format("%s %d is negative.", attribute, cap)
            );
        }
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
