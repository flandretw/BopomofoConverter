# Bopomofo Converter

> 🇹🇼 **中文使用者**：點此查看 [GitHub 臺灣正體中文說明](https://github.com/flandretw/BopomofoConverter/blob/main/README-zh_TW.md)  
> (Traditional Chinese documentation is available on [GitHub](https://github.com/flandretw/BopomofoConverter/blob/main/README-zh_TW.md).)

"I can read all the alien gibberish you typed."

## What is this?

**Bopomofo Converter** is a client-side Fabric mod designed to save you from those awkward moments when you forgot to switch your Chinese input method.

Do you often forget to switch to your Chinese input method on a server, sending out cryptic codes like `ji394su3`?  
And then the chat gets flooded with `?`, forcing you to switch your keyboard and awkwardly re-type "I love you"?

This mod leaves your original message intact for everyone to laugh at, while displaying hover text with the converted Bopomofo whenever you move your cursor over the gibberish.

Best of all, this is a purely client-side mod. The server requires zero installation. You install it yourself, and you're good to go.

### Requirements & Compatibility

| Component | Requirement |
| :--- | :--- |
| **Side** | **Client-side only** (Do not install on servers) |
| **Mod Loader** | **Fabric Loader** (Requires **Fabric API**) |
| **Minecraft & Java** | • **1.20.1** (Java 17)<br>• **1.21.11** (Java 21)<br>• **26.1 / 26.2** (Java 25) |
| **Configuration GUI** | [Mod Menu](https://modrinth.com/mod/modmenu) (optional) or in-game command `/bopomofo` (or `/bopomofo-converter`) |

![In-game chat demonstration showing the mouse cursor hovering over the first part of the gibberish text "zo t;62k7vup vu0", revealing a translated Bopomofo hover tooltip "ㄈㄟ ㄔㄤˊㄉㄜ˙ㄒㄧㄣ ㄒㄧㄢ" meaning "very fresh"](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/very_fresh.webp)

![In-game chat demonstration showing the mouse cursor hovering over the second part of the gibberish text "zo t;62k7ao3jo4" across the comma, revealing a translated Bopomofo hover tooltip "ㄈㄟ ㄔㄤˊㄉㄜ˙ㄇㄟˇ ㄨㄟˋ" meaning "very delicious"](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/very_delicious.webp)

## Features

* **Strict Bopomofo Building-Block Algorithm**  
  Won't randomly translate normal English words like `hello` into `ㄘㄍㄠㄠㄟ`. Built-in syllable validation ensures only text matching the "Initial + Medial + Final + Tone" layout gets translated, leaving standard English conversations untouched.

  ![Normal English word hello does not trigger translation](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/hello.webp)

* **Chat & Title Boundary Separation**  
  On servers that glue player titles directly to chat messages (like Hypixel's `[VIP] Flandre_tw:`), the mod cleanly separates the title from the message and only translates the gibberish portion.

  ![Hypixel player rank title accurately separated from chat message](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/hypixel.webp)

* **CapsLock & Full-Width Handling**  
  Accidentally typed "想啊！很想啊！" (`VU;387！CP3VU;387！`) with CapsLock on, or full-width "想" (`ＶＵ；３`)? The mod silently normalizes characters into half-width lowercase to resolve "想" (`ㄒㄧㄤˇ`), while keeping full-width punctuation marks intact.

  ![CapsLock uppercase text automatically normalized and translated](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/miss.webp)

* **Punctuation & Multi-Word Segmentation**  
  Words adjacent to full-width punctuation won't shatter syllables. Mixed sentences containing Chinese, punctuation, and multiple Bopomofo phrases—such as "你是一個，一個一個一個" (`su3g4u6ek7，u6ek7u6ek7u6ek7`)—are parsed independently with punctuation preserved.

  ![Punctuation and multiple Bopomofo phrases independently parsed](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/you_are_a_a_a_a.webp)

* **Rapid-Typing Tone Correction**  
  When typing at terminal velocity, your fingers often race each other and slap down a tone key before the vowel (e.g. typing "有" mistakenly as `u3.` instead of `u.3`, or "紅" as `cj6/`). Built-in syllable recovery identifies key roles on standard DaQian keyboards and smoothly slides early tone keys back to the syllable's tail. It effortlessly rescues zero-initial syllables and frantic spaceless typing, armed with regex negative lookaheads to guarantee adjacent normal words are never messed with.

  ![Early tone key press automatically reordered and translated](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/only_black_tea_right.webp)

## Setup & Installation

1. Ensure you have installed **Fabric Loader** and **Fabric API** for your Minecraft version.
2. Download the mod `.jar` file matching your Minecraft version and place it into your `.minecraft/mods` folder.
3. Launch Minecraft, open the chat, and enjoy deciphering everyone's Martian gibberish!
4. Customize appearance anytime:  
   Open settings via [Mod Menu](https://modrinth.com/mod/modmenu), or type `/bopomofo` (or `/bopomofo-converter`) in chat. Supports custom colors, bold/italic/underline style previews, and a reset button.

![In-game settings configuration screen demonstrating custom color, bold, italic, and underline options](https://raw.githubusercontent.com/flandretw/BopomofoConverter/main/docs/images/menu.webp)

## AI Disclosure & Transparency

In compliance with Modrinth's Generative AI Policy:

This project is architected, maintained, and rigorously tested by a human developer, with generative AI utilized as a pair-programming assistant during code drafting and cross-version refactoring. All translation logic, syllable validation, and regex behaviors have been manually reviewed and verified through automated unit tests.

## FAQ

**Q: Can players without this mod see the translations?**  
A: Nope. This is a purely client-side mod. Translations happen locally on your machine. Those without it will remain confused.

**Q: Does it translate my own messages?**  
A: Yes, it processes all messages in the chat HUD, including your own.

---

**Links & License**  
* Source Code: [GitHub Repository](https://github.com/flandretw/BopomofoConverter)
* Issue Tracker: [GitHub Issues](https://github.com/flandretw/BopomofoConverter/issues)
* License: [MIT License](https://github.com/flandretw/BopomofoConverter/blob/main/LICENSE)
