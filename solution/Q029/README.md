# Q029 Long Bricks

## 実装
「区間の最大値をとる」「区間に対して一定の値を代入する」といった操作を行えるデータ構造として, 「遅延評価つきセグメント木」が有名です.
[ACLforJavaでは実装済](https://github.com/NASU41/AtCoderLibraryForJava/tree/master/LazySegTree)なので, ぜひご利用ください.

この`LazySegTree`を利用する際には, まず「各要素の値」「行いたい操作」を表すクラスを定義します. 「値」はモノイドである必要がありますが, モノイドをなすための関数は別途(ラムダ式などで)指定することもできます.
コンストラクタの引数が多いですが, 以下の順に並べてください.
- 格納したい要素の個数
- 「値」2つから「値」1つを得るための関数. 例えば本問では最大値を計算したいので, `Math.max(_, _)`が乗ります
- 「操作」と「値」を受け取り, 「値に操作を加えた結果」を返す関数. 
- 「操作」2つを受け取り, 合成関数を返す関数.
- 何もしないことを表す「操作」.