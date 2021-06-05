# Q006 Smallest Subsequence

## 解法
基本的な方針としては, 公式解と同様の貪欲法です. 
「k文字目に文字cを使えるか?」と判断する場面で, 公式は配列を用意していますが, こちらではSortedSet を利用して実装しています.

SortedSetは, 「○○より大きい最小の要素を返せ(ないならそれを知らせよ)」というようなクエリに対応できるデータ構造になっており, これを用いることで全体計算量が $O(N \log N)$になっています.

## 実装
'a'から'z'までに対応する26個のTreeSetを保持しています. c番目のTreeSetは, `'a'+c`にあたるアルファベットが現れた場所を保持しています.
