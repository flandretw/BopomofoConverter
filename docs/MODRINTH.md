# 注音轉換器 - Bopomofo Converter

「你打的那串火星文，我全看懂了。」

## 這是什麼酷東西？

**注音轉換器**，專門用來拯救「忘記切換輸入法」各種社死瞬間的 Fabric 用戶端模組。

你是不是常常在伺服器打字時忘記切換中文輸入法，送出一串像 `ji394su3` 這樣神祕代碼？  
然後聊天室立刻被 `?` 淹沒，你還得切換輸入法再尷尬地重打一次「我愛你」？

這款模組會默默保留原文讓大家笑，但只要把滑鼠游標移到亂碼上，就會浮現出**轉換後的注音懸浮文字**。

而且**這是一個純用戶端模組**，伺服器不需要安裝任何東西，你一個人裝就能自己看得懂。

### 需求與相容性

| 項目 | 需求說明 |
| :--- | :--- |
| **安裝端** | **僅限用戶端**（請勿安裝在伺服器端） |
| **模組載入器** | **Fabric Loader**（必須安裝 **Fabric API**） |
| **Minecraft 與 Java** | • **1.20.1** (Java 17)<br>• **1.21.11** (Java 21)<br>• **26.1 / 26.2** (Java 25) |
| **外觀設定介面** | 可透過 [Mod Menu](https://modrinth.com/mod/modmenu)（選用）或在遊戲內輸入指令 `/bopomofo`（或 `/bopomofo-converter`）開啟 |

![遊戲內聊天室截圖：滑鼠游標懸浮在前半段英數亂碼「zo t;62k7vup vu0」上方，精準浮現正確注音翻譯「ㄈㄟ ㄔㄤˊㄉㄜ˙ㄒㄧㄣ ㄒㄧㄢ」懸浮方框](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/very_fresh.webp)

![遊戲內聊天室截圖：滑鼠游標越過逗號懸浮在後半段英數亂碼「zo t;62k7ao3jo4」上方，精準浮現正確注音翻譯「ㄈㄟ ㄔㄤˊㄉㄜ˙ㄇㄟˇ ㄨㄟˋ」懸浮方框](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/very_delicious.webp)

## 魔法特性

* **嚴格的注音積木演算法**  
  不會隨便把正常的英文單字 `hello` 翻成 `ㄘㄍㄠㄠㄟ`。內建大千鍵盤音節檢驗，只有符合「聲母 + 介音 + 韻母 + 聲調」結構的字串才會觸發翻譯，正常的英文對話依然安然無恙。

  ![正常英文單字 hello 不會觸發注音翻譯](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/hello.webp)

* **聊天與稱號邊界分離**  
  遇到會把玩家稱號跟訊息黏在一起的伺服器（例如 Hypixel 的 `[VIP] Flandre_tw:`），模組會嘗試把稱號與對話切開，游標指到亂碼片段時才會顯示翻譯。

  ![Hypixel 稱號與聊天訊息精準分離翻譯示範](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/hypixel.webp)

* **大寫鎖定與全形字元處理**  
  不小心開啟 CapsLock 打出「想啊！很想啊！」（`VU;387！CP3VU;387！`），或是卡鍵切到了全形打出「想」（`ＶＵ；３`），模組會在後台轉換成半形小寫解析出「想」（`ㄒㄧㄤˇ`），同時保留中文全形標點符號不被誤判。

  ![大寫鎖定輸入自動轉換與翻譯示範](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/miss.webp)

* **標點符號與多詞不破碎**  
  字詞緊貼全形逗號、句號等標點時不會導致音節破碎。即使整句混雜中文、標點與多組注音，例如「你是一個，一個一個一個」（`su3g4u6ek7，u6ek7u6ek7u6ek7`），各個注音片段也能各自獨立辨識與翻譯，標點原樣保留。

  ![標點符號與多組注音詞句獨立翻譯示範](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/you_are_a_a_a_a.webp)

* **手速過快與聲調提前自動校正**  
  打字過快時兩手手指同時落下，容易把聲調鍵按在韻母前面，例如打「只有紅茶可以嗎」（`53u.3cj/6t86dk3u3a87`）時誤按成 `53u3.cj/6t86dk3u3a87`（先按了 `3` 再按 `.`）。  
  音節修復機制會自動進行順序調整：
  - **結構自動重排**：大千式注音鍵盤各鍵角色分明。偵測到聲調提早落下時（例如「紅」`cj6/` -> `cj/6`、「茶」`t68` -> `t86`、「可」`d3k` -> `dk3`），會在解析前將聲調移回字尾。
  - **零聲母字支援**：沒有聲母的字也能識別，例如「有」（`u3.` -> `u.3`），即使整句連打未空格也能智慧識別並修正。
  - **安全防護**：使用負向先行斷言確保該字後面沒有獨立聲調時才觸發移動，避免誤傷相鄰單字。

  ![聲調提前輸入自動校正為正確注音示範](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/only_black_tea_right.webp)

## 安裝與使用指南

1. 確保已安裝對應 Minecraft 版本的 **Fabric Loader** 與 **Fabric API**。
2. 下載符合您 Minecraft 版本的模組 `.jar` 檔案，並放入 `.minecraft/mods` 資料夾。
3. 啟動遊戲，打開聊天室，享受看懂大家火星文的樂趣！
4. 隨時調整外觀樣式：  
   可透過 [Mod Menu](https://modrinth.com/mod/modmenu) 模組選單，或在遊戲聊天室輸入指令 `/bopomofo`（或 `/bopomofo-converter`）開啟設定畫面。

![遊戲內外觀設定介面截圖：可自訂懸浮文字顏色、粗體、斜體與底線樣式](https://raw.githubusercontent.com/flandretw/BopomofoConverter/master/docs/images/menu.webp)

## AI 使用揭露與透明度宣告

為符合 Modrinth 的生成式 AI 規範：

本專案整體架構、維護及測試均由真人開發者主導，並在程式碼草擬與跨版本重構過程中將生成式 AI 作為結對程式設計輔助工具。所有的注音轉換邏輯、音節檢驗規則以及正則表達式行為均經過人工審閱，並具備完整的自動化單元測試進行驗證。

## 常見問題

**Q: 其他沒裝這個模組的玩家，看得到翻譯嗎？**  
A: 看不懂的依然看不懂。這是純**用戶端模組**，翻譯只在你的本機執行，別人沒裝就繼續滿頭問號。

**Q: 自己打的火星文自己也能翻譯嗎？**  
A: 可以，聊天室裡出現的所有訊息（包括自己發送的）都能正常辨識與翻譯。

---

**相關連結與授權**  
* 原始碼：[GitHub 專案庫](https://github.com/flandretw/BopomofoConverter)
* 問題回報：[GitHub Issues](https://github.com/flandretw/BopomofoConverter/issues)
* 授權條款：[MIT License](https://github.com/flandretw/BopomofoConverter/blob/master/LICENSE)
