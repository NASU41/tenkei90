# 競プロ典型90問 回答集
本リポジトリには, [競プロ典型90問](https://github.com/E869120/kyopro_educational_90)への解答コードをアップロードしていきます.

フォルダ[solution](https://github.com/NASU41/tenkei90/solution)には, 各問題に対応するフォルダ`Qxxx`が含まれています.

基本的に問題フォルダには以下のファイルが含まれています.
- 解答コード`Main.java`
- 提出データ`submission.txt`
- 解法ドキュメント`README.md`

なお, 一部の問題では複数の解法を紹介するためプログラムファイルの名前が `Main.java` となっていないケースがあります.
この場合, 手元で実行するためには名前を`Main.java`に変更する必要があるのでご注意ください.

現在作成済問題番号:
```
001 002 003 ___ ___ 006 007 008 009 010
011 012 013 014 015 016 ___ 018 ___ 020
021 022 ___ ___ 025 026 027 ___ 029 030
___ 032 033 034 ___ 036 037 038 ___ 040
041 042 ___ ___ ___ ___ ___ 048 049 ___
___ ___ ___ 054 ___ ___ ___ 058 ___ ___
061 ___ 063 ___ ___ ___ ___ ___ ___ ___
___ ___ ___ ___ ___ ___ ___ ___ ___ ___
___ ___ ___ ___ ___ ___ ___ ___ ___ ___
```

## 使用ライブラリについて
本リポジトリで公開するコードには, [ACLforJava](https://github.com/NASU41/AtCoderLibraryForJava)の機能が用いられることが予想されます. ライブラリの機能についてはこちらもご覧ください.

それ以外については, 頻出のものを以下にまとめています.
太字の問題の提出コードが最新の状態です.
- 有向/無向グラフ (003, **013**, 049, 054)