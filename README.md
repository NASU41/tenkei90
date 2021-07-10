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
001 002 003 004 005 006 007 008 009 010
011 012 013 014 015 016 (*) 018 019 020
021 022 023 024 025 026 027 028 029 030
031 032 033 034 (*) 036 037 038 039 040
041 042 043 044 045 046 047 048 049 050
051 052 053 054 055 056 057 058 ___ 060
061 062 063 064 065 066 067 068 069 070
071 072 073 074 075 076 077 078 079 080
___ 082 ___ 084 085 ___ ___ ___ ___ ___
```
`(*)`は「コードのみ公開, 解説markdownはまだ」の状況を表しています.

## 使用ライブラリについて
本リポジトリで公開するコードには, [ACLforJava](https://github.com/NASU41/AtCoderLibraryForJava)の機能が用いられることが予想されます. ライブラリの機能についてはこちらもご覧ください.

それ以外については, 頻出のものを以下にまとめています.
- 有向/無向グラフ (003, 013, 039, 049, 054)