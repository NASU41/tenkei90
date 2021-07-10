# 084 There are two types of characters

## 解法
`o`と`x`両方が含まれる」という条件はやや複雑なので, 「`o`のみを含む」「`x`のみを含む」区間を数え上げることにします.

間に`x`を含まずに`o`がk個連続している部分があるとき, その部分列は全て「`o`のみを含む」文字列であり, その総数は k(k-1) と表されます. この計算を全ての「`o`が連続した区間」について行い, `x`についても同様に計算すれば答えが導かれます.

では, 元の文字列において同じ文字が連続した区間をどのように列挙すればよいでしょうか. これは文字列を前から順に見ていって, 「直前と違う文字が現れたらそこが切れ目」というように判定するとO(N)で計算できます. このような情報の変換を「ランレングス圧縮」と呼びます.