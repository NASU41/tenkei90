# Q012 Red Painting

## 解法
以下, マスの座標などは全て 0-indexed として考えます(入出力の段階で1ずらす). そのほうがプログラミング言語の実装と相性がよいです.

前提として, 各マスに一意なIDを振ります. ここではマス`(r, c)`に整数 `r*W+c` を振ることで重複なく 0 から HW-1 までのIDをつけられます.

このIDを利用して, [UnionFind木](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/DSU)を構成します. 隣り合う2マスが赤くなったとき, UnionFind木のmerge処理を行います. 1マス塗ったときに結ばれる辺の数は高々4本なので, ほぼ定数時間で処理が完了します.
(本当はUnionFindの計算量にはO(α(N))という表記があるのですが, 実質的に定数と思っていいです)