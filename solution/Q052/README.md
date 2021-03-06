# 052 Dice Product

## 解法
まず説明のために, 「4面ダイス2個」の場合を考えます.
1個めのダイスが`[a,b,c,d]`, 2個めのダイスが`[p,q,r,s]`という目を持っているものとすると, 得点の総和は
`ap + aq + ar + as + bp + bq + br + bs + cp + cq + cr + cs + dp + dq + dr + ds`
のように表せます. この式をじっと睨むとうまく因数分解することができて, 
`(a+b+c+d)(p+q+r+s)`
となります. これで計算のステップ数が落とせました.

本来の問題(6面ダイス100個)でも同様の作業ができて, 
`(A[1][1]+...+A[1][6]) * (A[2][1]+...+A[2][6]) * ... * (A[N][1]+...+A[N][6])`
が答えとなります. 計算量は`O(N)`です.

## 解釈
上記の計算式を別の方向から解釈してみましょう. ダイスは全て均一なものと仮定し,  `i`個めのダイスから出る目を`X[i]`とすると, 得点の期待値は`E(X[1] * X[2] * ... * X[N])`と表せます. ここで, 各ダイスは完全に独立なので期待値と積の計算は交換可能で, 前述の式は `E(X[1]) * E(X[2]) * ... * E(X[N])` と等価です. この値は`O(N)`時間で計算可能です. 本来求めたい値は得点の総和なので, 出目の組合せ`6^N`[通り]を掛け合わせることで答えが得られます.