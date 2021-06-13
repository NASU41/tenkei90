import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{
        final ContestScanner sc = new ContestScanner();
        final ContestPrinter pr = new ContestPrinter();

        final ModIntFactory fact = new ModIntFactory(998244353);

        int R = sc.nextInt(), G = sc.nextInt(), B = sc.nextInt();

        int K = sc.nextInt();

        int b = K-sc.nextInt(), r = K-sc.nextInt(), g = K-sc.nextInt();

        long[] red = new long[R+1], green = new long[G+1], blue = new long[B+1];
        for(int i=r; i<=R; i++) red[i] = fact.combination(R, i).value();
        for(int j=g; j<=G; j++) green[j] = fact.combination(G,j).value();
        for(int k=b; k<=B; k++) blue[k] = fact.combination(B,k).value();
        
        long[] ans = Convolution.convolution(red, Convolution.convolution(blue,green,998244353), 998244353);

        pr.println(ans[K]);
        
        pr.close();
    }
}
class Convolution {
    /**
     * Find a primitive root.
     *
     * @param m A prime number.
     * @return Primitive root.
     */
    private static int primitiveRoot(int m) {
        if (m == 2) return 1;
        if (m == 167772161) return 3;
        if (m == 469762049) return 3;
        if (m == 754974721) return 11;
        if (m == 998244353) return 3;

        int[] divs = new int[20];
        divs[0] = 2;
        int cnt = 1;
        int x = (m - 1) / 2;
        while (x % 2 == 0) x /= 2;
        for (int i = 3; (long) (i) * i <= x; i += 2) {
            if (x % i == 0) {
                divs[cnt++] = i;
                while (x % i == 0) {
                    x /= i;
                }
            }
        }
        if (x > 1) {
            divs[cnt++] = x;
        }
        for (int g = 2; ; g++) {
            boolean ok = true;
            for (int i = 0; i < cnt; i++) {
                if (pow(g, (m - 1) / divs[i], m) == 1) {
                    ok = false;
                    break;
                }
            }
            if (ok) return g;
        }
    }

    /**
     * Power.
     *
     * @param x Parameter x.
     * @param n Parameter n.
     * @param m Mod.
     * @return n-th power of x mod m.
     */
    private static long pow(long x, long n, int m) {
        if (m == 1) return 0;
        long r = 1;
        long y = x % m;
        while (n > 0) {
            if ((n & 1) != 0) r = (r * y) % m;
            y = (y * y) % m;
            n >>= 1;
        }
        return r;
    }

    /**
     * Ceil of power 2.
     *
     * @param n Value.
     * @return Ceil of power 2.
     */
    private static int ceilPow2(int n) {
        int x = 0;
        while ((1L << x) < n) x++;
        return x;
    }

    /**
     * Garner's algorithm.
     *
     * @param c    Mod convolution results.
     * @param mods Mods.
     * @return Result.
     */
    private static long garner(long[] c, int[] mods) {
        int n = c.length + 1;
        long[] cnst = new long[n];
        long[] coef = new long[n];
        java.util.Arrays.fill(coef, 1);
        for (int i = 0; i < n - 1; i++) {
            int m1 = mods[i];
            long v = (c[i] - cnst[i] + m1) % m1;
            v = v * pow(coef[i], m1 - 2, m1) % m1;

            for (int j = i + 1; j < n; j++) {
                long m2 = mods[j];
                cnst[j] = (cnst[j] + coef[j] * v) % m2;
                coef[j] = (coef[j] * m1) % m2;
            }
        }
        return cnst[n - 1];
    }

    /**
     * Pre-calculation for NTT.
     *
     * @param mod NTT Prime.
     * @param g   Primitive root of mod.
     * @return Pre-calculation table.
     */
    private static long[] sumE(int mod, int g) {
        long[] sum_e = new long[30];
        long[] es = new long[30];
        long[] ies = new long[30];
        int cnt2 = Integer.numberOfTrailingZeros(mod - 1);
        long e = pow(g, (mod - 1) >> cnt2, mod);
        long ie = pow(e, mod - 2, mod);
        for (int i = cnt2; i >= 2; i--) {
            es[i - 2] = e;
            ies[i - 2] = ie;
            e = e * e % mod;
            ie = ie * ie % mod;
        }
        long now = 1;
        for (int i = 0; i < cnt2 - 2; i++) {
            sum_e[i] = es[i] * now % mod;
            now = now * ies[i] % mod;
        }
        return sum_e;
    }

    /**
     * Pre-calculation for inverse NTT.
     *
     * @param mod Mod.
     * @param g   Primitive root of mod.
     * @return Pre-calculation table.
     */
    private static long[] sumIE(int mod, int g) {
        long[] sum_ie = new long[30];
        long[] es = new long[30];
        long[] ies = new long[30];

        int cnt2 = Integer.numberOfTrailingZeros(mod - 1);
        long e = pow(g, (mod - 1) >> cnt2, mod);
        long ie = pow(e, mod - 2, mod);
        for (int i = cnt2; i >= 2; i--) {
            es[i - 2] = e;
            ies[i - 2] = ie;
            e = e * e % mod;
            ie = ie * ie % mod;
        }
        long now = 1;
        for (int i = 0; i < cnt2 - 2; i++) {
            sum_ie[i] = ies[i] * now % mod;
            now = now * es[i] % mod;
        }
        return sum_ie;
    }

    /**
     * Inverse NTT.
     *
     * @param a     Target array.
     * @param sumIE Pre-calculation table.
     * @param mod   NTT Prime.
     */
    private static void butterflyInv(long[] a, long[] sumIE, int mod) {
        int n = a.length;
        int h = ceilPow2(n);

        for (int ph = h; ph >= 1; ph--) {
            int w = 1 << (ph - 1), p = 1 << (h - ph);
            long inow = 1;
            for (int s = 0; s < w; s++) {
                int offset = s << (h - ph + 1);
                for (int i = 0; i < p; i++) {
                    long l = a[i + offset];
                    long r = a[i + offset + p];
                    a[i + offset] = (l + r) % mod;
                    a[i + offset + p] = (mod + l - r) * inow % mod;
                }
                int x = Integer.numberOfTrailingZeros(~s);
                inow = inow * sumIE[x] % mod;
            }
        }
    }

    /**
     * Inverse NTT.
     *
     * @param a    Target array.
     * @param sumE Pre-calculation table.
     * @param mod  NTT Prime.
     */
    private static void butterfly(long[] a, long[] sumE, int mod) {
        int n = a.length;
        int h = ceilPow2(n);

        for (int ph = 1; ph <= h; ph++) {
            int w = 1 << (ph - 1), p = 1 << (h - ph);
            long now = 1;
            for (int s = 0; s < w; s++) {
                int offset = s << (h - ph + 1);
                for (int i = 0; i < p; i++) {
                    long l = a[i + offset];
                    long r = a[i + offset + p] * now % mod;
                    a[i + offset] = (l + r) % mod;
                    a[i + offset + p] = (l - r + mod) % mod;
                }
                int x = Integer.numberOfTrailingZeros(~s);
                now = now * sumE[x] % mod;
            }
        }
    }

    /**
     * Convolution.
     *
     * @param a   Target array 1.
     * @param b   Target array 2.
     * @param mod NTT Prime.
     * @return Answer.
     */
    public static long[] convolution(long[] a, long[] b, int mod) {
        int n = a.length;
        int m = b.length;
        if (n == 0 || m == 0) return new long[0];

        int z = 1 << ceilPow2(n + m - 1);
        {
            long[] na = new long[z];
            long[] nb = new long[z];
            System.arraycopy(a, 0, na, 0, n);
            System.arraycopy(b, 0, nb, 0, m);
            a = na;
            b = nb;
        }

        int g = primitiveRoot(mod);
        long[] sume = sumE(mod, g);
        long[] sumie = sumIE(mod, g);

        butterfly(a, sume, mod);
        butterfly(b, sume, mod);
        for (int i = 0; i < z; i++) {
            a[i] = a[i] * b[i] % mod;
        }
        butterflyInv(a, sumie, mod);
        a = java.util.Arrays.copyOf(a, n + m - 1);

        long iz = pow(z, mod - 2, mod);
        for (int i = 0; i < n + m - 1; i++) a[i] = a[i] * iz % mod;
        return a;
    }

    /**
     * Convolution.
     *
     * @param a   Target array 1.
     * @param b   Target array 2.
     * @param mod Any mod.
     * @return Answer.
     */
    public static long[] convolutionLL(long[] a, long[] b, int mod) {
        int n = a.length;
        int m = b.length;
        if (n == 0 || m == 0) return new long[0];

        int mod1 = 754974721;
        int mod2 = 167772161;
        int mod3 = 469762049;

        long[] c1 = convolution(a, b, mod1);
        long[] c2 = convolution(a, b, mod2);
        long[] c3 = convolution(a, b, mod3);

        int retSize = c1.length;
        long[] ret = new long[retSize];
        int[] mods = {mod1, mod2, mod3, mod};
        for (int i = 0; i < retSize; ++i) {
            ret[i] = garner(new long[]{c1[i], c2[i], c3[i]}, mods);
        }
        return ret;
    }

    /**
     * Convolution by ModInt.
     *
     * @param a Target array 1.
     * @param b Target array 2.
     * @return Answer.
     */
    public static java.util.List<ModIntFactory.ModInt> convolution(
            java.util.List<ModIntFactory.ModInt> a,
            java.util.List<ModIntFactory.ModInt> b
    ) {
        int mod = a.get(0).mod();
        long[] va = a.stream().mapToLong(ModIntFactory.ModInt::value).toArray();
        long[] vb = b.stream().mapToLong(ModIntFactory.ModInt::value).toArray();
        long[] c = convolutionLL(va, vb, mod);

        ModIntFactory factory = new ModIntFactory(mod);
        return java.util.Arrays.stream(c).mapToObj(factory::create).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Naive convolution. (Complexity is O(N^2)!!)
     *
     * @param a   Target array 1.
     * @param b   Target array 2.
     * @param mod Mod.
     * @return Answer.
     */
    public static long[] convolutionNaive(long[] a, long[] b, int mod) {
        int n = a.length;
        int m = b.length;
        int k = n + m - 1;
        long[] ret = new long[k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                ret[i + j] += a[i] * b[j] % mod;
                ret[i + j] %= mod;
            }
        }
        return ret;
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
