# Q034 There are few types of elements

## 解法
「尺取り法」を実装します. ある区間を見た時に, 部分列に含まれる要素がK種類以下なら区間を右に伸ばしてみます. 逆にK種類より多いなら左から縮めます. この方法によって, 解答するのに必要な区間は全て調べることができます.

## 実装
区間に含まれる要素の種類を求めるために, [`ACLforJava.Multiset`](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/Multiset)を利用しています. このライブラリは`java.util.Map`を利用していますが, 常に「集合に含まれる要素のみが`keySet()`で取得できる(一度追加した後に取り除かれた値は`keySet()`で出てこない)」ように実装を工夫しています. これによって, 単純に`keySet()`を呼ぶことにより区間に含まれる要素だけを列挙することができます.