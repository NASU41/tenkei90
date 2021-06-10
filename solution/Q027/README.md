# Q027 Sign Up Requests

## 解法
「現在のユーザー一覧」を保持しておくと, 各クエリに対しては以下の操作を行えばよいです.
- ユーザー一覧にその名前が入っているかを調べる.
- 入っていなければそのユーザーを追加し, 日付を出力する.

## 実装
Javaにおいては[`java.util.Set`](https://docs.oracle.com/javase/jp/8/docs/api/java/util/Set.html)という標準ライブラリが集合操作を実装しています. 具体的には, [`HashSet`](https://docs.oracle.com/javase/jp/8/docs/api/java/util/HashSet.html)や[TreeSet](https://docs.oracle.com/javase/jp/8/docs/api/java/util/TreeSet.html)を利用することが多いでしょう. 特に必要がない限りは`HashSet`でOKです(計算量が軽いので).