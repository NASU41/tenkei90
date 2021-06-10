import java.util.*;
import java.util.function.*;

public class Main {
    public static void main(String[] args) throws Exception{
        final ContestScanner sc = new ContestScanner();
        final ContestPrinter pr = new ContestPrinter();

        int W = sc.nextInt(), N = sc.nextInt();

        LazySegTree<Long,ChMax> tree = new LazySegTree<>(W, (x,y)->Math.max(x,y), 0L, (f, x)->f.applyAsLong(x), ChMax::composite, ChMax.identity());
        for(int n=0; n<N; n++){
            int l = sc.nextInt()-1;
            int r = sc.nextInt();
            long h = tree.prod(l, r);
            pr.println(h+1);
            tree.apply(l, r, new ChMax(h+1));
        }
    
        pr.close();
    }
}

class ChMax implements LongUnaryOperator{
    long m;
    public ChMax(long m){
        this. m = m;
    }
    public ChMax composite(ChMax another){
        return new ChMax(Math.max(this.m, another.m));
    }
    public static ChMax identity(){return new ChMax(Long.MIN_VALUE);}
    public long applyAsLong(long x){
        return Math.max(x, m);
    }
}

class LazySegTree<S, F> {
    final int MAX;

    final int N;
    final int Log;
    final java.util.function.BinaryOperator<S> Op;
    final S E;
    final java.util.function.BiFunction<F, S, S> Mapping;
    final java.util.function.BinaryOperator<F> Composition;
    final F Id;

    final S[] Dat;
    final F[] Laz;

    @SuppressWarnings("unchecked")
    public LazySegTree(int n, java.util.function.BinaryOperator<S> op, S e, java.util.function.BiFunction<F, S, S> mapping, java.util.function.BinaryOperator<F> composition, F id) {
        this.MAX = n;
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.Log = Integer.numberOfTrailingZeros(N);
        this.Op = op;
        this.E = e;
        this.Mapping = mapping;
        this.Composition = composition;
        this.Id = id;
        this.Dat = (S[]) new Object[N << 1];
        this.Laz = (F[]) new Object[N];
        java.util.Arrays.fill(Dat, E);
        java.util.Arrays.fill(Laz, Id);
    }

    public LazySegTree(S[] dat, java.util.function.BinaryOperator<S> op, S e, java.util.function.BiFunction<F, S, S> mapping, java.util.function.BinaryOperator<F> composition, F id) {
        this(dat.length, op, e, mapping, composition, id);
        build(dat);
    }

    private void build(S[] dat) {
        int l = dat.length;
        System.arraycopy(dat, 0, Dat, N, l);
        for (int i = N - 1; i > 0; i--) {
            Dat[i] = Op.apply(Dat[i << 1 | 0], Dat[i << 1 | 1]);
        }
    }

    private void push(int k) {
        if (Laz[k] == Id) return;
        int lk = k << 1 | 0, rk = k << 1 | 1;
        Dat[lk] = Mapping.apply(Laz[k], Dat[lk]);
        Dat[rk] = Mapping.apply(Laz[k], Dat[rk]);
        if (lk < N) Laz[lk] = Composition.apply(Laz[k], Laz[lk]);
        if (rk < N) Laz[rk] = Composition.apply(Laz[k], Laz[rk]);
        Laz[k] = Id;
    }

    private void pushTo(int k) {
        for (int i = Log; i > 0; i--) push(k >> i);
    }

    private void pushTo(int lk, int rk) {
        for (int i = Log; i > 0; i--) {
            if (((lk >> i) << i) != lk) push(lk >> i);
            if (((rk >> i) << i) != rk) push(rk >> i);
        }
    }

    private void updateFrom(int k) {
        k >>= 1;
        while (k > 0) {
            Dat[k] = Op.apply(Dat[k << 1 | 0], Dat[k << 1 | 1]);
            k >>= 1;
        }
    }

    private void updateFrom(int lk, int rk) {
        for (int i = 1; i <= Log; i++) {
            if (((lk >> i) << i) != lk) {
                int lki = lk >> i;
                Dat[lki] = Op.apply(Dat[lki << 1 | 0], Dat[lki << 1 | 1]);
            }
            if (((rk >> i) << i) != rk) {
                int rki = (rk - 1) >> i;
                Dat[rki] = Op.apply(Dat[rki << 1 | 0], Dat[rki << 1 | 1]);
            }
        }
    }

    public void set(int p, S x) {
        exclusiveRangeCheck(p);
        p += N;
        pushTo(p);
        Dat[p] = x;
        updateFrom(p);
    }

    public S get(int p) {
        exclusiveRangeCheck(p);
        p += N;
        pushTo(p);
        return Dat[p];
    }

    public S prod(int l, int r) {
        if (l > r) {
            throw new IllegalArgumentException(
                String.format("Invalid range: [%d, %d)", l, r)
            );
        }
        inclusiveRangeCheck(l);
        inclusiveRangeCheck(r);
        if (l == r) return E;
        l += N; r += N;
        pushTo(l, r);
        S sumLeft = E, sumRight = E;
        while (l < r) {
            if ((l & 1) == 1) sumLeft = Op.apply(sumLeft, Dat[l++]);
            if ((r & 1) == 1) sumRight = Op.apply(Dat[--r], sumRight);
            l >>= 1; r >>= 1;
        }
        return Op.apply(sumLeft, sumRight);
    }

    public S allProd() {
        return Dat[1];
    }

    public void apply(int p, F f) {
        exclusiveRangeCheck(p);
        p += N;
        pushTo(p);
        Dat[p] = Mapping.apply(f, Dat[p]);
        updateFrom(p);
    }

    public void apply(int l, int r, F f) {
        if (l > r) {
            throw new IllegalArgumentException(
                String.format("Invalid range: [%d, %d)", l, r)
            );
        }
        inclusiveRangeCheck(l);
        inclusiveRangeCheck(r);
        if (l == r) return;
        l += N; r += N;
        pushTo(l, r);
        for (int l2 = l, r2 = r; l2 < r2;) {
            if ((l2 & 1) == 1) {
                Dat[l2] = Mapping.apply(f, Dat[l2]);
                if (l2 < N) Laz[l2] = Composition.apply(f, Laz[l2]);
                l2++;
            }
            if ((r2 & 1) == 1) {
                r2--;
                Dat[r2] = Mapping.apply(f, Dat[r2]);
                if (r2 < N) Laz[r2] = Composition.apply(f, Laz[r2]);
            }
            l2 >>= 1; r2 >>= 1;
        }
        updateFrom(l, r);
    }

    public int maxRight(int l, java.util.function.Predicate<S> g) {
        inclusiveRangeCheck(l);
        if (!g.test(E)) {
            throw new IllegalArgumentException("Identity element must satisfy the condition.");
        }
        if (l == MAX) return MAX;
        l += N;
        pushTo(l);
        S sum = E;
        do {
            l >>= Integer.numberOfTrailingZeros(l);
            if (!g.test(Op.apply(sum, Dat[l]))) {
                while (l < N) {
                    push(l);
                    l = l << 1;
                    if (g.test(Op.apply(sum, Dat[l]))) {
                        sum = Op.apply(sum, Dat[l]);
                        l++;
                    }
                }
                return l - N;
            }
            sum = Op.apply(sum, Dat[l]);
            l++;
        } while ((l & -l) != l);
        return MAX;
    }

    public int minLeft(int r, java.util.function.Predicate<S> g) {
        inclusiveRangeCheck(r);
        if (!g.test(E)) {
            throw new IllegalArgumentException("Identity element must satisfy the condition.");
        }
        if (r == 0) return 0;
        r += N;
        pushTo(r - 1);
        S sum = E;
        do {
            r--;
            while (r > 1 && (r & 1) == 1) r >>= 1;
            if (!g.test(Op.apply(Dat[r], sum))) {
                while (r < N) {
                    push(r);
                    r = r << 1 | 1;
                    if (g.test(Op.apply(Dat[r], sum))) {
                        sum = Op.apply(Dat[r], sum);
                        r--;
                    }
                }
                return r + 1 - N;
            }
            sum = Op.apply(Dat[r], sum);
        } while ((r & -r) != r);
        return 0;
    }

    private void exclusiveRangeCheck(int p) {
        if (p < 0 || p >= MAX) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d is not in [%d, %d).", p, 0, MAX)
            );
        }
    }

    private void inclusiveRangeCheck(int p) {
        if (p < 0 || p > MAX) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d is not in [%d, %d].", p, 0, MAX)
            );
        }
    }

    // **************** DEBUG **************** //

    private int indent = 6;

    public void setIndent(int newIndent) {
        this.indent = newIndent;
    }

    @Override
    public String toString() {
        return toSimpleString();
    }

    private S[] simulatePushAll() {
        S[] simDat = java.util.Arrays.copyOf(Dat, 2 * N);
        F[] simLaz = java.util.Arrays.copyOf(Laz, 2 * N);
        for (int k = 1; k < N; k++) {
            if (simLaz[k] == Id) continue;
            int lk = k << 1 | 0, rk = k << 1 | 1;
            simDat[lk] = Mapping.apply(simLaz[k], simDat[lk]);
            simDat[rk] = Mapping.apply(simLaz[k], simDat[rk]);
            if (lk < N) simLaz[lk] = Composition.apply(simLaz[k], simLaz[lk]);
            if (rk < N) simLaz[rk] = Composition.apply(simLaz[k], simLaz[rk]);
            simLaz[k] = Id;
        }
        return simDat;
    }

    public String toDetailedString() {
        return toDetailedString(1, 0, simulatePushAll());
    }

    private String toDetailedString(int k, int sp, S[] dat) {
        if (k >= N) return indent(sp) + dat[k];
        String s = "";
        s += toDetailedString(k << 1 | 1, sp + indent, dat);
        s += "\n";
        s += indent(sp) + dat[k];
        s += "\n";
        s += toDetailedString(k << 1 | 0, sp + indent, dat);
        return s;
    }

    private static String indent(int n) {
        StringBuilder sb = new StringBuilder();
        while (n --> 0) sb.append(' ');
        return sb.toString();
    }

    public String toSimpleString() {
        S[] dat = simulatePushAll();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < N; i++) {
            sb.append(dat[i + N]);
            if (i < N - 1) sb.append(',').append(' ');
        }
        sb.append(']');
        return sb.toString();
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
