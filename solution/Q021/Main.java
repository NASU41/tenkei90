import java.util.*;


public class Main {

    public static void main(String[] args) throws Exception{
        final ContestScanner sc = new ContestScanner();
        final ContestPrinter pr = new ContestPrinter();

        int N = sc.nextInt(), M = sc.nextInt();
        SCC scc = new SCC(N);
        for(int m=0; m<M; m++){
            scc.addEdge(sc.nextInt()-1, sc.nextInt()-1);
        }

        int[][] block = scc.build();

        long ans = 0;
        for(int[] b: block) ans += 1L * b.length * (b.length-1) / 2;

        pr.println(ans);

        pr.close();
    }
}

class SCC {

    static class Edge {
        int from, to;
        public Edge(int from, int to) {
            this.from = from; this.to = to;
        }
    }

    final int n;
    int m;
    final java.util.ArrayList<Edge> unorderedEdges;
    final int[] start;
    final int[] ids;
    boolean hasBuilt = false;

    public SCC(int n) {
        this.n = n;
        this.unorderedEdges = new java.util.ArrayList<>();
        this.start = new int[n + 1];
        this.ids = new int[n];
    }

    public void addEdge(int from, int to) {
        rangeCheck(from);
        rangeCheck(to);
        unorderedEdges.add(new Edge(from, to));
        start[from + 1]++;
        this.m++;
    }

    public int id(int i) {
        if (!hasBuilt) {
            throw new UnsupportedOperationException(
                "Graph hasn't been built."
            );
        }
        rangeCheck(i);
        return ids[i];
    }
    
    public int[][] build() {
        for (int i = 1; i <= n; i++) {
            start[i] += start[i - 1];
        }
        Edge[] orderedEdges = new Edge[m];
        int[] count = new int[n + 1];
        System.arraycopy(start, 0, count, 0, n + 1);
        for (Edge e : unorderedEdges) {
            orderedEdges[count[e.from]++] = e;
        }
        int nowOrd = 0;
        int groupNum = 0;
        int k = 0;
        // parent
        int[] par = new int[n];
        int[] vis = new int[n];
        int[] low = new int[n];
        int[] ord = new int[n];
        java.util.Arrays.fill(ord, -1);
        // u = lower32(stack[i]) : visiting vertex
        // j = upper32(stack[i]) : jth child
        long[] stack = new long[n];
        // size of stack
        int ptr = 0;
        // non-recursional DFS
        for (int i = 0; i < n; i++) {
            if (ord[i] >= 0) continue;
            par[i] = -1;
            // vertex i, 0th child.
            stack[ptr++] = 0l << 32 | i;
            // stack is not empty
            while (ptr > 0) {
                // last element
                long p = stack[--ptr];
                // vertex
                int u = (int) (p & 0xffff_ffffl);
                // jth child
                int j = (int) (p >>> 32);
                if (j == 0) { // first visit
                    low[u] = ord[u] = nowOrd++;
                    vis[k++] = u;
                }
                if (start[u] + j < count[u]) { // there are more children
                    // jth child
                    int to = orderedEdges[start[u] + j].to;
                    // incr children counter
                    stack[ptr++] += 1l << 32;
                    if (ord[to] == -1) { // new vertex
                        stack[ptr++] = 0l << 32 | to;
                        par[to] = u;
                    } else { // backward edge
                        low[u] = Math.min(low[u], ord[to]);
                    }
                } else { // no more children (leaving)
                    while (j --> 0) {
                        int to = orderedEdges[start[u] + j].to;
                        // update lowlink
                        if (par[to] == u) low[u] = Math.min(low[u], low[to]);
                    }
                    if (low[u] == ord[u]) { // root of a component
                        while (true) { // gathering verticies
                            int v = vis[--k];
                            ord[v] = n;
                            ids[v] = groupNum;
                            if (v == u) break;
                        }
                        groupNum++; // incr the number of components
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            ids[i] = groupNum - 1 - ids[i];
        }
        
        int[] counts = new int[groupNum];
        for (int x : ids) counts[x]++;
        int[][] groups = new int[groupNum][];
        for (int i = 0; i < groupNum; i++) {
            groups[i] = new int[counts[i]];
        }
        for (int i = 0; i < n; i++) {
            int cmp = ids[i];
            groups[cmp][--counts[cmp]] = i;
        }
        hasBuilt = true;
        return groups;
    }

    private void rangeCheck(int i) {
        if (i < 0 || i >= n) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds for length %d", i, n)
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