# Q022 Cubic Cake

## 解法
三辺の長さがA,B,Cであるとき, 切り出せる最も大きな立方体の一辺の長さは`gcd(A,B,C)`として計算されます. この値をXとすると, 縦に(幅の辺を切るように)切る回数は $ A/X - 1 $と表すことができます. 他の方向についても同様に考えることで, 答えが $ A/X + B/X + C/X - 3 $となることが分かります.

## 実装
C++には標準ライブラリとして`std::gcd` 関数が実装されていますが, Javaには(少なくとも私が知る限り)存在しません. そこで, [ACLforJava](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/Math)のほうで用意しておきました. ご自由にご利用ください.

```java
System.out.println(MathLib.gcd(4,6)); //2になる
System.out.println(MathLib.gcd(6, 15, 21)); //3になる. 可変長引数に対応しました
```