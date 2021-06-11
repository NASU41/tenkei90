# 063 Monochromatic Subgrid

## 解法
Hが高々8という(異常な)制約から, 部分グリッドとして用いる行の組合せを全探索することができることが分かります. 使う行の組合せを固定すると, 各列がどの数として使えるか(あるいは使えないか)が分かるので, 最も多いものを利用するとよいです.

## 実装
[`Java.lang.Integer`](https://docs.oracle.com/javase/jp/8/docs/api/java/lang/Integer.html)には, ビット演算に便利な関数が多数収録されています. ビットが立っている位置を返す関数`lowestOneBit`などはDPでお世話になることが多いでしょう.