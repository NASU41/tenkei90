import java.util.*;

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

class Point extends Pair<Long,Long>{
    public Point(long x, long y){
        super(x,y);
    }
    public long getX(){return super.getFirst();}
    public long getY(){return super.getSecond();}    
}
class Vector extends Pair<Long,Long>{
    public Vector(long x, long y){
        super(x,y);
    }

    public static Vector between(Point from, Point to){
        return new Vector(to.getX()-from.getX(), to.getY()-from.getY());
    }
    public long getX(){return super.getFirst();}
    public long getY(){return super.getSecond();}  
    public Vector plus(Vector v){
        return new Vector(this.getX()+v.getX(), this.getY()+v.getY());
    }
    public Vector neg(){
        return new Vector(-this.getX(), -this.getY());
    }    
    public Vector minus(Vector v){
        return new Vector(this.getX()-v.getX(), this.getY()-v.getY());
    }
    public Vector mult(long a){
        return new Vector(this.getX()*a, this.getY()*a);
    }
    public long dot(Vector v){
        return this.getX()*v.getX() + this.getY()*v.getY();
    }
    public long cross(Vector v){
        return this.getX()*v.getY() - this.getY()*v.getX();
    }
}

class Polygon{
    ArrayList<Point> vertex;
    int N;

    public Polygon(List<Point> vertex){
        this.N = vertex.size();
        this.vertex = new ArrayList<>();
        for(Point p:vertex) this.vertex.add(p);
    }
    public Polygon(Point[] vertex){
        this.N = vertex.length;
        this.vertex = new ArrayList<>();
        for(Point p:vertex) this.vertex.add(p);
    }

    static public Polygon convexHull(Point[] point){
        int N = point.length;
        Arrays.sort(point);
        if(point.length < 3) return new Polygon(point);

        List<Point> upper = new ArrayList<>(), lower = new ArrayList<>();
        upper.add(point[0]); upper.add(point[1]);
        for(int n=2; n<N; n++){
            while(upper.size() > 1 &&
                  Vector.between(upper.get(upper.size()-2), upper.get(upper.size()-1))
                  .cross(Vector.between(upper.get(upper.size()-2), point[n])) > 0 ){
                upper.remove(upper.size()-1);
            }
            upper.add(point[n]);
        }
        lower.add(point[0]); lower.add(point[1]);
        for(int n=2; n<N; n++){
            while(lower.size() > 1 &&
                  Vector.between(lower.get(lower.size()-2), lower.get(lower.size()-1))
                  .cross(Vector.between(lower.get(lower.size()-2), point[n])) < 0 ){
                lower.remove(lower.size()-1);
            }
            lower.add(point[n]);
        }
        
        List<Point> vertex = new ArrayList<>();
        for(int i=0; i<upper.size(); i++) vertex.add(upper.get(i));
        for(int j=lower.size()-2; j>0; j--) vertex.add(lower.get(j));
        return new Polygon(vertex);
    }
    static public Polygon convexHull(List<Point> point){
        return Polygon.convexHull(point.toArray(new Point[point.size()]));
    }

    public long areaX2(){
        long a = 0;
        for(int n=0; n<N; n++){
            Point cur = vertex.get(n), next = vertex.get((n+1)%N);
            a += cur.getX()*next.getY() - cur.getY()*next.getX();
        } 
        return Math.abs(a);
    }

    public ArrayList<Point> getVertexList(){return vertex;}

    public String toString(){
        return vertex.toString();
    }
}
 
public class Main {
    public static void main(String[] args){
        ContestScanner sc = new ContestScanner();
        ContestPrinter pr = new ContestPrinter();
 
        int N = sc.nextInt();
        List<Point> pile = new ArrayList<>();
        for(int n=0; n<N; n++){
            pile.add(new Point(sc.nextInt(), sc.nextInt()));
        }

        Polygon farm = Polygon.convexHull(pile);
        List<Point> vertex = farm.getVertexList();

        int V = vertex.size();
        long onEdge = 0;
        for(int v=0; v<V; v++){
            Point cur = vertex.get(v), next = vertex.get((v+1)%V);
            onEdge += MathLib.gcd(cur.getX()-next.getX(), cur.getY()-next.getY());
        }

        long areaX2 = farm.areaX2();
        
        long internal = (areaX2 - onEdge + 2) / 2;

        pr.println(internal + onEdge - N);

        pr.close();
    }
}

class MathLib{
    private static long safe_mod(long x, long m){
        x %= m;
        if(x<0) x += m;
        return x;
    }

    private static long[] inv_gcd(long a, long b){
        a = safe_mod(a, b);
        if(a==0) return new long[]{b,0};

        long s=b, t=a;
        long m0=0, m1=1;
        while(t>0){
            long u = s/t;
            s -= t*u;
            m0 -= m1*u;
            long tmp = s; s = t; t = tmp;
            tmp = m0; m0 = m1; m1 = tmp;
        }
        if(m0<0) m0 += b/s;
        return new long[]{s,m0};
    }

    public static long gcd(long... a){
        if(a.length == 0) return 0;
        long r = java.lang.Math.abs(a[0]);
        for(int i=1; i<a.length; i++){
            if(a[i]!=0) {
                if(r==0) r = java.lang.Math.abs(a[i]);
                else r = inv_gcd(r, java.lang.Math.abs(a[i]))[0];
            }
        }
        return r;
    }

    public static long lcm(long... a){
        if(a.length == 0) return 0;
        long r = java.lang.Math.abs(a[0]);
        for(int i=1; i<a.length; i++){
            r = r / gcd(r,java.lang.Math.abs(a[i])) * java.lang.Math.abs(a[i]);
        }
        return r;
    }

    public static long pow_mod(long x, long n, int m){
        assert n >= 0;
        assert m >= 1;
        if(m == 1)return 0L;
        x = safe_mod(x, m);
        long ans = 1L;
        while(n > 0){
            if((n&1) == 1) ans = (ans * x) % m;
            x = (x*x) % m;
            n >>>= 1;
        }
        return ans;
    }

    public static long[] crt(long[] r, long[] m){
        assert(r.length == m.length);
        int n = r.length;

        long r0=0, m0=1;
        for(int i=0; i<n; i++){
            assert(1 <= m[i]);
            long r1 = safe_mod(r[i], m[i]), m1 = m[i];
            if(m0 < m1){
                long tmp = r0; r0 = r1; r1 = tmp;
                tmp = m0; m0 = m1; m1 = tmp;
            }
            if(m0%m1 == 0){
                if(r0%m1 != r1) return new long[]{0,0};
                continue;
            }

            long[] ig = inv_gcd(m0, m1);
            long g = ig[0], im = ig[1];

            long u1 = m1/g;
            if((r1-r0)%g != 0) return new long[]{0,0};

            long x = (r1-r0) / g % u1 * im % u1;

            r0 += x * m0;
            m0 *= u1;
            if(r0<0) r0 += m0;
            //System.err.printf("%d %d\n", r0, m0);
        } 
        return new long[]{r0, m0};
    }
    public static long floor_sum(long n, long m, long a, long b){
        long ans = 0;
        if(a >= m){
            ans += (n-1) * n * (a/m) / 2;
            a %= m;
        }
        if(b >= m){
            ans += n * (b/m);
            b %= m;
        }

        long y_max = (a*n+b) / m;
        long x_max = y_max * m - b;
        if(y_max == 0) return ans;
        ans += (n - (x_max+a-1)/a) * y_max;
        ans += floor_sum(y_max, a, m, (a-x_max%a)%a);
        return ans;
    }

    public static java.util.ArrayList<Long> divisors(long n){
        java.util.ArrayList<Long> divisors = new ArrayList<>();
        java.util.ArrayList<Long> large = new ArrayList<>();

        for(long i=1; i*i<=n; i++) if(n%i==0){
            divisors.add(i);
            if(i*i<n) large.add(n/i);
        }
        for(int p=large.size()-1; p>=0; p--){
            divisors.add(large.get(p));
        }
        return divisors;
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
