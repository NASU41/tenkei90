# Q007 CP classes

## 実装
公式解説などのC++実装では`std::sort`, `std::lower_bound`といったライブラリ関数を呼び出しています.
これらの挙動を正確に模倣しようと思うなら, `java.util.Arrays::binarySearch`などがよいでしょう.

ところで, 私が本リポジトリにアップロードしているものはその方法をとっていません. 代わりに, [`java.util.TreeSet`](https://docs.oracle.com/javase/jp/8/docs/api/java/util/TreeSet.html)を用いた実装を行っています.

`TreeSet`は, ソート済の集合を表すクラスであり, [「ある値x以上の要素だけを抜き出した部分集合」みたいな操作を高速にできる機能](https://docs.oracle.com/javase/jp/8/docs/api/java/util/TreeSet.html#tailSet-E-)があります. 
これを利用することで, 同等の実装を容易に行うことができています.

## テクニック
15-16行目についての説明です.

https://github.com/NASU41/tenkei90/blob/bf8ba2283ccb56f7ca867249206494ea2a653734/solution/Q007/Main.java#L15-L16

「`B[q]`がどの塾のレートよりも低い(あるいは高い)」というようなケースが存在します. 
入力で与えられた配列$A$をそのまま`TreeSet`に変換して調査を行っていると, そのようなケースで返ってくるnullに個別で対応しなくてはならないのでなかなか面倒です.
そこで, 「回答に影響しないレベルに極端な値」を追加しておくことで, 常に対応する値を`TreeSet`から取り出せるようになり, 結果としてより単純な実装を得ることができます.
