# Q030 K Factors

## 解法
```java
for(int i=0; i<N; i++){
  for(int j=0; i*j<N; j++) {
    //O(1)の処理
  }
} 
```
上の二重ループは一見するとO(N^2)の時間がかかりそうですが, 実際にはO(NlogN)です. 次の例も見てみましょう.
```java
//素数テーブルを構築するなどして, isPrime(_)はO(1)で計算できるものとする

for(int i=0; i<N; i++) if(isPrime(i)){
  for(int j=0; i*j<N; j++) {
    //O(1)の処理
  }
} 
```
この二重ループはなんとO(NloglogN)と, きわめて線形オーダーに近い計算量で操作が可能です.
このように, 二重ループであっても思ったより計算量が控えめで済むケースは案外多いです. O(N^2)が通らない制約では, ちょっとループの構造を工夫するなどしてオーダーが落とせないか考えてみましょう.

この問題に似た有名アルゴリズムとして「エラトステネスの篩」などがあります.