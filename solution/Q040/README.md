# Q040 Get More Money

## 解法
いわゆる「燃やす埋める問題」に帰着させることができます.
家iに入ることを「燃やす」, 入らないことを「埋める」とすると,

- 家iを燃やすとW円失う.
- 家iを埋めるとA[i]円失う. (事前にA[i]円得ておく)
- 鍵jがある家iを埋めて家jを燃やすと爆発する

のように, フローを用いて解ける形に表現できます.

## 実装
AtCoderLibraryでは[最大フロー問題](https://github.com/atcoder/ac-library/blob/master/document_ja/maxflow.md)が既に実装されており, それに合わせて[Java用の移植も完了しています](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/MaxFlow). ぜひご利用ください.