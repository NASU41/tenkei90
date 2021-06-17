import java.util.*;

class ModMatrix{
    private static long safe_mod(long x, long m){
        long r = x % m;
        if(r < 0) r += m;
        return r;
    }    
    private static long pow_mod(long x, long n, long m){
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
    private static long inv(long x, long m){
        return pow_mod(x, m-2, m);
    }


    private int H, W;
    private long[][] mat;
    private long mod;

    public ModMatrix(int H, int W, long[][] mat, long mod){
        this.H = H;
        this.W = W;
        this.mod = mod;
        this.mat = new long[H][W];
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            this.mat[h][w] = safe_mod(mat[h][w], mod);
        }
    }

    public static ModMatrix identity(int size, long mod){
        long[][] r = new long[size][size];
        for(int i=0; i<size; i++)for(int j=0; j<size; j++) r[i][j] = 1;
        return new ModMatrix(size,size, r, mod);
    }

    public int getHeight(){return H;}
    public int getWidth(){return W;}
    public long getMod(){return mod;}
    public long get(int h, int w){return mat[h][w];}
    public long[] getRow(int h){
        long[] r = new long[W];
        for(int w=0; w<W; w++) r[w] = mat[h][w];
        return r;
    }
    public long[] getCol(int w){
        long[] r = new long[H];
        for(int h=0; h<H; h++) r[h] = mat[h][w];
        return r;
    }
    public void set(int h, int w, long v){
        mat[h][w] = safe_mod(v, mod);
    }

    public ModMatrix add(ModMatrix another){
        assert(this.getHeight()==another.getHeight());
        assert(this.getWidth()==another.getWidth());
        
        long[][] r = new long[H][W];
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            r[h][w] = safe_mod(this.get(h,w) + another.get(h,w), mod);
        }
        return new ModMatrix(H,W, r, mod);
    }
    public ModMatrix addAsg(ModMatrix another){
        assert(this.getHeight()==another.getHeight());
        assert(this.getWidth()==another.getWidth());
        
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            mat[h][w] = safe_mod(this.get(h,w) + another.get(h,w), mod);
        }
        return this;
    }

    public ModMatrix neg(){
        long[][] r = new long[H][W];
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            r[h][w] = safe_mod(-this.get(h,w), mod);
        }
        return new ModMatrix(H,W, r, mod);
    }

    public ModMatrix sub(ModMatrix another){
        assert(this.getHeight()==another.getHeight());
        assert(this.getWidth()==another.getWidth());
        
        long[][] r = new long[H][W];
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            r[h][w] = safe_mod(this.get(h,w) - another.get(h,w), mod);
        }
        return new ModMatrix(H,W, r, mod);
    }
    public ModMatrix subAsg(ModMatrix another){
        assert(this.getHeight()==another.getHeight());
        assert(this.getWidth()==another.getWidth());
        
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            mat[h][w] = safe_mod(this.get(h,w) - another.get(h,w), mod);
        }
        return this;
    }

    public ModMatrix mul(long a){
        long[][] r = new long[H][W];
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            r[h][w] = safe_mod(a*this.get(h,w), mod);
        }
        return new ModMatrix(H,W, r, mod);
    }
    public ModMatrix mulAsg(long a){
        for(int h=0; h<H; h++)for(int w=0; w<W; w++){
            mat[h][w] = safe_mod(a*this.get(h,w), mod);
        }
        return this;
    }

    public ModMatrix mul(ModMatrix another){
        assert(this.getWidth() == another.getHeight());
        long[][] r = new long[this.getHeight()][another.getWidth()];
        for(int i=0; i<this.getHeight(); i++)for(int j=0; j<another.getWidth(); j++){
            for(int k=0; k<W; k++){
                r[i][j] = safe_mod(r[i][j] + this.get(i,k)*another.get(k,j), mod);
            }  
        }
        return new ModMatrix(this.getHeight(), another.getWidth(), r, mod);
    }
    public ModMatrix pow(long n){
        assert(H==W);
        if(n==0) return identity(this.getHeight(), mod);
        if(n==1) return this.clone();
        if(n%2==0) return this.mul(this).pow(n/2);
        return this.mul(this.pow(n-1));
    }

    public String toString(){
        return Arrays.deepToString(mat);
    }
    public ModMatrix clone(){
        return new ModMatrix(H,W, mat, mod);
    }

    private void rowSwap(int i, int j){
        if(i==j) return;
        for(int w=0; w<W; w++){
            long t = mat[i][w];
            mat[i][w] = mat[j][w];
            mat[j][w] = t;
        }
    }
    private void rowMul(int i, long v){
        if(v==1L) return;
        for(int w=0; w<W; w++) mat[i][w] = safe_mod(mat[i][w]*v, mod);
    }
    private void rowAdd(int i, int j, long a){
        if(a==0) return;
        for(int w=0; w<W; w++){
            long add = safe_mod(mat[j][w] * a, mod);
            mat[i][w] = safe_mod(mat[i][w] + add, mod);
        }
    }
    public int rowReduction(){
        int rank = 0;
        for(int w=0; w<W; w++){
            int pivot = -1;
            for(int h=rank; h<H; h++){
                if(mat[h][w] != 0){
                    pivot = h;
                    this.rowSwap(pivot, rank);
                    break;
                }
            }
            if(pivot == -1) continue;

            this.rowMul(rank, inv(this.get(rank, w), mod));

            for(int h=0; h<H; h++){
                if(h != rank && mat[h][w] != 0){
                    this.rowAdd(h, rank, -mat[h][w]);
                }               
            }
            rank++;
        }
        return rank;
    }

}

public class Main {
    private static boolean solve(ModMatrix A, int rank, int[] S){
        S = S.clone();
        final int H = A.getHeight(), W = A.getWidth();
        for(int h=0; h<rank; h++){
            for(int w=0; w<W; w++){
                if(S[w] != 0){
                    for(int i=w; i<W; i++) S[i] ^= A.get(h,i);
                    break;
                }else if(A.get(h,w) != 0) break;
            }
        }
        
        for(int w=0; w<W; w++) if(S[w]!=0) return false;
        return true;
    }
    public static void main(String[] args) throws Exception{
        final ContestScanner sc = new ContestScanner();
        final ContestPrinter pr = new ContestPrinter();
        final int MOD = 998244353;
        final ModIntFactory fact = new ModIntFactory(MOD);
 
        final int N = sc.nextInt(), M = sc.nextInt();
        long[][] A = new long[N][M];
        for(int n=0; n<N; n++){
            int T = sc.nextInt();
            for(int t=0; t<T; t++) A[n][sc.nextInt()-1] = 1;
        }

        int[] S = sc.nextIntArray(M);
        
        ModMatrix matrix = new ModMatrix(N, M, A, 2);
        int rank = matrix.rowReduction();

        if(solve(matrix, rank, S)){
            pr.println(fact.create(2).pow(N-rank));
        }else{
            pr.println(0);
        }
        pr.close();
    }
}

class ModIntFactory {
    private final ModArithmetic ma;
    private final int mod;

    private final boolean usesMontgomery;
    private final ModArithmetic.ModArithmeticMontgomery maMontgomery;

    private ArrayList<Integer> factorial;

    public ModIntFactory(int mod) {
        this.ma = ModArithmetic.of(mod);
        this.usesMontgomery = ma instanceof ModArithmetic.ModArithmeticMontgomery;
        this.maMontgomery = usesMontgomery ? (ModArithmetic.ModArithmeticMontgomery) ma : null;
        this.mod = mod;

        this.factorial = new ArrayList<>();
    }

    public ModInt create(long value) {
        if ((value %= mod) < 0) value += mod;
        if (usesMontgomery) {
            return new ModInt(maMontgomery.generate(value));
        } else {
            return new ModInt((int) value);
        }
    }

    private void prepareFactorial(int max){
        factorial.ensureCapacity(max+1);
        if(factorial.size()==0) factorial.add(1);
        for(int i=factorial.size(); i<=max; i++){
            factorial.add(ma.mul(factorial.get(i-1), i));
        }
    }

    public ModInt factorial(int i){
        prepareFactorial(i);
        return create(factorial.get(i));
    }

    public ModInt permutation(int n, int r){
        if(n < 0 || r < 0 || n < r) return create(0);
        prepareFactorial(n);
        return create(ma.div(factorial.get(n), factorial.get(r)));
    }
    public ModInt combination(int n, int r){
        if(n < 0 || r < 0 || n < r) return create(0);
        prepareFactorial(n);
        return create(ma.div(factorial.get(n), ma.mul(factorial.get(r),factorial.get(n-r))));
    }

    public int getMod() {
        return mod;
    }

    public class ModInt {
        private int value;
        private ModInt(int value) {
            this.value = value;
        }
        public int mod() {
            return mod;
        }
        public int value() {
            if (ma instanceof ModArithmetic.ModArithmeticMontgomery) {
                return ((ModArithmetic.ModArithmeticMontgomery) ma).reduce(value);
            }
            return value;
        }
        public ModInt add(ModInt mi) {
            return new ModInt(ma.add(value, mi.value));
        }
        public ModInt add(ModInt mi1, ModInt mi2) {
            return new ModInt(ma.add(value, mi1.value)).addAsg(mi2);
        }
        public ModInt add(ModInt mi1, ModInt mi2, ModInt mi3) {
            return new ModInt(ma.add(value, mi1.value)).addAsg(mi2).addAsg(mi3);
        }
        public ModInt add(ModInt mi1, ModInt mi2, ModInt mi3, ModInt mi4) {
            return new ModInt(ma.add(value, mi1.value)).addAsg(mi2).addAsg(mi3).addAsg(mi4);
        }
        public ModInt add(ModInt mi1, ModInt... mis) {
            ModInt mi = add(mi1);
            for (ModInt m : mis) mi.addAsg(m);
            return mi;
        }
        public ModInt add(long mi) {
            return new ModInt(ma.add(value, ma.remainder(mi)));
        }
        public ModInt sub(ModInt mi) {
            return new ModInt(ma.sub(value, mi.value));
        }
        public ModInt sub(long mi) {
            return new ModInt(ma.sub(value, ma.remainder(mi)));
        }
        public ModInt mul(ModInt mi) {
            return new ModInt(ma.mul(value, mi.value));
        }
        public ModInt mul(ModInt mi1, ModInt mi2) {
            return new ModInt(ma.mul(value, mi1.value)).mulAsg(mi2);
        }
        public ModInt mul(ModInt mi1, ModInt mi2, ModInt mi3) {
            return new ModInt(ma.mul(value, mi1.value)).mulAsg(mi2).mulAsg(mi3);
        }
        public ModInt mul(ModInt mi1, ModInt mi2, ModInt mi3, ModInt mi4) {
            return new ModInt(ma.mul(value, mi1.value)).mulAsg(mi2).mulAsg(mi3).mulAsg(mi4);
        }
        public ModInt mul(ModInt mi1, ModInt... mis) {
            ModInt mi = mul(mi1);
            for (ModInt m : mis) mi.mulAsg(m);
            return mi;
        }
        public ModInt mul(long mi) {
            return new ModInt(ma.mul(value, ma.remainder(mi)));
        }
        public ModInt div(ModInt mi) {
            return new ModInt(ma.div(value, mi.value));
        }
        public ModInt div(long mi) {
            return new ModInt(ma.div(value, ma.remainder(mi)));
        }
        public ModInt inv() {
            return new ModInt(ma.inv(value));
        }
        public ModInt pow(long b) {
            return new ModInt(ma.pow(value, b));
        }
        public ModInt addAsg(ModInt mi) {
            this.value = ma.add(value, mi.value);
            return this;
        }
        public ModInt addAsg(ModInt mi1, ModInt mi2) {
            return addAsg(mi1).addAsg(mi2);
        }
        public ModInt addAsg(ModInt mi1, ModInt mi2, ModInt mi3) {
            return addAsg(mi1).addAsg(mi2).addAsg(mi3);
        }
        public ModInt addAsg(ModInt mi1, ModInt mi2, ModInt mi3, ModInt mi4) {
            return addAsg(mi1).addAsg(mi2).addAsg(mi3).addAsg(mi4);
        }
        public ModInt addAsg(ModInt... mis) {
            for (ModInt m : mis) addAsg(m);
            return this;
        }
        public ModInt addAsg(long mi) {
            this.value = ma.add(value, ma.remainder(mi));
            return this;
        }
        public ModInt subAsg(ModInt mi) {
            this.value = ma.sub(value, mi.value);
            return this;
        }
        public ModInt subAsg(long mi) {
            this.value = ma.sub(value, ma.remainder(mi));
            return this;
        }
        public ModInt mulAsg(ModInt mi) {
            this.value = ma.mul(value, mi.value);
            return this;
        }
        public ModInt mulAsg(ModInt mi1, ModInt mi2) {
            return mulAsg(mi1).mulAsg(mi2);
        }
        public ModInt mulAsg(ModInt mi1, ModInt mi2, ModInt mi3) {
            return mulAsg(mi1).mulAsg(mi2).mulAsg(mi3);
        }
        public ModInt mulAsg(ModInt mi1, ModInt mi2, ModInt mi3, ModInt mi4) {
            return mulAsg(mi1).mulAsg(mi2).mulAsg(mi3).mulAsg(mi4);
        }
        public ModInt mulAsg(ModInt... mis) {
            for (ModInt m : mis) mulAsg(m);
            return this;
        }
        public ModInt mulAsg(long mi) {
            this.value = ma.mul(value, ma.remainder(mi));
            return this;
        }
        public ModInt divAsg(ModInt mi) {
            this.value = ma.div(value, mi.value);
            return this;
        }
        public ModInt divAsg(long mi) {
            this.value = ma.div(value, ma.remainder(mi));
            return this;
        }
        @Override
        public String toString() {
            return String.valueOf(value());
        }
        @Override
        public boolean equals(Object o) {
            if (o instanceof ModInt) {
                ModInt mi = (ModInt) o;
                return mod() == mi.mod() && value() == mi.value();
            }
            return false;
        }
        @Override
        public int hashCode() {
            return (1 * 37 + mod()) * 37 + value();
        }
    }

    private static abstract class ModArithmetic {
        abstract int mod();
        abstract int remainder(long value);
        abstract int add(int a, int b);
        abstract int sub(int a, int b);
        abstract int mul(int a, int b);
        int div(int a, int b) {
            return mul(a, inv(b));
        }
        int inv(int a) {
            int b = mod();
            if (b == 1) return 0;
            long u = 1, v = 0;
            while (b >= 1) {
                int t = a / b;
                a -= t * b;
                int tmp1 = a; a = b; b = tmp1;
                u -= t * v;
                long tmp2 = u; u = v; v = tmp2;
            }
            if (a != 1) {
                throw new ArithmeticException("divide by zero");
            }
            return remainder(u);
        }
        int pow(int a, long b) {
            if (b < 0) throw new ArithmeticException("negative power");
            int r = 1;
            int x = a;
            while (b > 0) {
                if ((b & 1) == 1) r = mul(r, x);
                x = mul(x, x);
                b >>= 1;
            }
            return r;
        }
    
        static ModArithmetic of(int mod) {
            if (mod <= 0) {
                throw new IllegalArgumentException();
            } else if (mod == 1) {
                return new ModArithmetic1();
            } else if (mod == 2) {
                return new ModArithmetic2();
            } else if (mod == 998244353) {
                return new ModArithmetic998244353();
            } else if (mod == 1000000007) {
                return new ModArithmetic1000000007();
            } else if ((mod & 1) == 1) {
                return new ModArithmeticMontgomery(mod);
            } else {
                return new ModArithmeticBarrett(mod);
            }
        }

        private static final class ModArithmetic1 extends ModArithmetic {
            int mod() {return 1;}
            int remainder(long value) {return 0;}
            int add(int a, int b) {return 0;}
            int sub(int a, int b) {return 0;}
            int mul(int a, int b) {return 0;}
            int pow(int a, long b) {return 0;}
        }
        private static final class ModArithmetic2 extends ModArithmetic {
            int mod() {return 2;}
            int remainder(long value) {return (int) (value & 1);}
            int add(int a, int b) {return a ^ b;}
            int sub(int a, int b) {return a ^ b;}
            int mul(int a, int b) {return a & b;}
        }
        private static final class ModArithmetic998244353 extends ModArithmetic {
            private final int mod = 998244353;
            int mod() {
                return mod;
            }
            int remainder(long value) {
                return (int) ((value %= mod) < 0 ? value + mod : value);
            }
            int add(int a, int b) {
                int res = a + b;
                return res >= mod ? res - mod : res;
            }
            int sub(int a, int b) {
                int res = a - b;
                return res < 0 ? res + mod : res;
            }
            int mul(int a, int b) {
                return (int) (((long) a * b) % mod);
            }
        }
        private static final class ModArithmetic1000000007 extends ModArithmetic {
            private final int mod = 1000000007;
            int mod() {
                return mod;
            }
            int remainder(long value) {
                return (int) ((value %= mod) < 0 ? value + mod : value);
            }
            int add(int a, int b) {
                int res = a + b;
                return res >= mod ? res - mod : res;
            }
            int sub(int a, int b) {
                int res = a - b;
                return res < 0 ? res + mod : res;
            }
            int mul(int a, int b) {
                return (int) (((long) a * b) % mod);
            }
        }
        private static final class ModArithmeticMontgomery extends ModArithmeticDynamic {
            private final long negInv;
            private final long r2;
    
            private ModArithmeticMontgomery(int mod) {
                super(mod);
                long inv = 0;
                long s = 1, t = 0;
                for (int i = 0; i < 32; i++) {
                    if ((t & 1) == 0) {
                        t += mod;
                        inv += s;
                    }
                    t >>= 1;
                    s <<= 1;
                }
                long r = (1l << 32) % mod;
                this.negInv = inv;
                this.r2 = (r * r) % mod;
            }
            private int generate(long x) {
                return reduce(x * r2);
            }
            private int reduce(long x) {
                x = (x + ((x * negInv) & 0xffff_ffffl) * mod) >>> 32;
                return (int) (x < mod ? x : x - mod);
            }
            @Override
            int remainder(long value) {
                return generate((value %= mod) < 0 ? value + mod : value);
            }
            @Override
            int mul(int a, int b) {
                return reduce((long) a * b);
            }
            @Override
            int inv(int a) {
                return super.inv(reduce(a));
            }
            @Override
            int pow(int a, long b) {
                return generate(super.pow(a, b));
            }
        }
        private static final class ModArithmeticBarrett extends ModArithmeticDynamic {
            private static final long mask = 0xffff_ffffl;
            private final long mh;
            private final long ml;
            private ModArithmeticBarrett(int mod) {
                super(mod);
                /**
                 * m = floor(2^64/mod)
                 * 2^64 = p*mod + q, 2^32 = a*mod + b
                 * => (a*mod + b)^2 = p*mod + q
                 * => p = mod*a^2 + 2ab + floor(b^2/mod)
                 */
                long a = (1l << 32) / mod;
                long b = (1l << 32) % mod;
                long m = a * a * mod + 2 * a * b + (b * b) / mod;
                mh = m >>> 32;
                ml = m & mask;
            }
            private int reduce(long x) {
                long z = (x & mask) * ml;
                z = (x & mask) * mh + (x >>> 32) * ml + (z >>> 32);
                z = (x >>> 32) * mh + (z >>> 32);
                x -= z * mod;
                return (int) (x < mod ? x : x - mod);
            }
            @Override
            int remainder(long value) {
                return (int) ((value %= mod) < 0 ? value + mod : value);
            }
            @Override
            int mul(int a, int b) {
                return reduce((long) a * b);
            }
        }
        private static class ModArithmeticDynamic extends ModArithmetic {
            final int mod;
            ModArithmeticDynamic(int mod) {
                this.mod = mod;
            }
            int mod() {
                return mod;
            }
            int remainder(long value) {
                return (int) ((value %= mod) < 0 ? value + mod : value);
            }
            int add(int a, int b) {
                int sum = a + b;
                return sum >= mod ? sum - mod : sum;
            }
            int sub(int a, int b) {
                int sum = a - b;
                return sum < 0 ? sum + mod : sum;
            }
            int mul(int a, int b) {
                return (int) (((long) a * b) % mod);
            }
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
