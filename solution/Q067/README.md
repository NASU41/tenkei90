# 067 Base 8 to 9

## 解法
Javaには, [「文字列をn進表記の整数とみなしてlong型に変換する」](https://docs.oracle.com/javase/jp/8/docs/api/java/lang/Long.html#valueOf-java.lang.String-int-)[「long型整数をn進表記の文字列に変換する」](https://docs.oracle.com/javase/jp/8/docs/api/java/lang/Long.html#toString-long-int-)といった関数が標準ライブラリで実装されています. これらを用いることで, N進数の仕組みを深く理解しなくても正しく実装することが可能です.

ところで, 上記の方法で正しく動くのは36進法までです. 競技プログラミングにおいては例えば「10進法のNは123進法で何桁?」というような問題も十分考えられるため, 公式解説に書かれた考え方も理解しておきましょう.