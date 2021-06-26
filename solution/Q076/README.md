# 076 Cake Cut

## 解法
ピース1とピースNが繋がっていないとみなすなら, 単純に尺取り法を適用すると問題を解くことができます. 「現在持っている区間が短すぎるなら次のピースを区間に加える, 長すぎるなら一番左のピースを区間から除く」というようなアルゴリズムです. 尺取り法については典型034も参照してください.

本来の「ピース1とピースNが繋がっている」問題に戻りましょう. 「ピース1とNを使うケース」「使わないケース」で場合分けしても解くことは可能ですが, ここではそのような場合分けを用いずに解を導く方法を説明します.
配列 `{A_1, A_2, A_N, A_1, A_2, ..., A_N}` というように, 配列`A`を2回繰り返したものを配列`B`とします. 配列`B`に対して前述の尺取り法を適用すると, ピース1とNを使うケースを検出することができます. このように, 円環上の区間を扱う際に2周分のデータを用意して直線的区間とみなす手法は頻出ですので, 典型技術としておぼえておくとよいでしょう.