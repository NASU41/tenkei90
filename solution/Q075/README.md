# 075 Magic For Balls

## 解法
整数`N`が書かれたボールに魔法を使い続けた最後の状態では全てのボールに素数が書かれていて, その積は`N`に一致するはずです. すなわち, これは`N`の素因数分解です. 素因数分解はO(√N)の計算時間で実行可能であることが知られています.
さて, `N`の素因数分解が与えられたとして, その状態に至るまでに必要な魔法の回数を考えます. 直感的には1回魔法を使うたびにボールの数は高々2倍になるので, ceil(log[2](N)) 回が答えのような気がします. 証明する方法はいくつかありますが, ここでは後ろから考える(参考:典型062)手法を使ってみましょう. 「魔法」の逆操作は「今あるボールを好きな数だけペアにする. ペアにした2つのボールを消し, 2数の積が書かれたボールを追加する」というものになりますが, ペアを作れるだけ作るのが最適です. よって1回の逆操作を行うごとにボールの数が半減します.