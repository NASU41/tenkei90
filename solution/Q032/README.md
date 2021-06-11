# Q032 AtCoder Ekiden

## 解法
選手N人の走順を全探索します. N人の走順は N! 通りありますが, 制約は N<=10 なので十分間に合います(10! = 3628800).

## 実装
数列の並べ替えを全列挙するライブラリはC++では標準ライブラリとして実装されていますが, Javaには存在しません. そこで, ACL移植班によって実装されたものを[こちら](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/Permutation)で公開しています. ぜひご利用ください.