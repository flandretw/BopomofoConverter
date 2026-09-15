<p align="center">
  <img src="src/main/resources/assets/bopomofo-translator/icon.png" alt="Bopomofo Translator Icon" width="128">
</p>

# Bopomofo Translator - 注音亂碼翻譯蒟蒻

[English](README.md) | [臺灣正體中文](README-zh_TW.md)

"I can read all the alien gibberish you typed!!"

---

## What is this cool stuff?

From the Minecraft universe comes a savior — the **Bopomofo Translator**! A super-smart mod dedicated to saving those brain-fart moments when you "forgot to switch your input method"!

Do you often forget to switch your Chinese input method while typing on a server, ending up sending mysterious codes like `ji394su3`?
And then everyone below spams `?`, forcing you to switch your keyboard and awkwardly re-type "I love you"?

Worry no more! This **Fabric Client-Side Exclusive** mod is born for this exact scenario!
It will retain your original text, leaving the social death scene intact for everyone to laugh at, while **gently floating the correctly translated Bopomofo text as a Hover Text** whenever you hover your mouse over the gibberish!

Best of all, **this is a purely client-side mod**. The server requires zero installation or configuration. You install it yourself, and boom—you now have the superpower to decode the entire server's gibberish!

> **Disclaimer**  
> This project was forged using **Gemini "Vibe Coding"**, fueled by AI magic and excessive amounts of digital fries. **Proceed with caution**! If the UI starts dancing or the code looks like a magical incantation, don't worry—it's just the vibe.

---

## Crazy Magical Features

* **Strict Bopomofo Building-Block Algorithm**  
  We won't foolishly translate normal English words like `hello` into `ㄘㄍㄠㄠㄟ`! The program has an extremely rigorous DaChen keyboard syllable validator built-in. Only the Martian fragments that **perfectly** match the "Initial + Medial + Final + Tone" structure will trigger the translation! Normal English conversations will remain completely unharmed.

* **Seamless & Precise Segmentation - Hypixel-Proof**  
  Even on servers like Hypixel that messily glue custom titles together with chat messages `[VIP] Flandre_tw:`, the mod perfectly separates the title from the dialogue! Your cursor will only unleash its magic when pointing exactly at the gibberish.

* **Sneak Shift Typo Correction**  
  Sneaking around in Minecraft holding Shift while typing, and accidentally producing `JI#CL#`, crashing the translation? We thought of that too! A built-in Shift decoder runs in the background. Whether numbers turned into special symbols or cases got messed up, they are automatically restored to their rightful places!

* **Full-Width Character Dimensional Strike**  
  Even if your keyboard goes haywire and you type a massive full-width `ｊｉ３`, it silently crushes it into half-width in the background and precisely spoon-feeds you the `ㄨㄛˇ`! Chinese full-width punctuation marks are smartly preserved and never mistakenly converted into Bopomofo keys.

* **Punctuation & Multi-Word Segmentation - Anti-Shattering**  
  Previously, typing Bopomofo adjacent to full-width punctuation marks or English punctuation could shatter translation tokens or corrupt syllables. Our boundary delimiter engine intelligently segments non-Bopomofo symbols so that even complex sentences mixed with Chinese, full-width punctuation, and multiple Bopomofo phrases, such as `非常的新鮮，非常的美味，zo t;6 2k7 vup vu0 ，zo t;6 2k7 ao3 jo4`, will have each Bopomofo phrase accurately identified and translated without fragmentation!

* **Rapid-Typing Early and Inverted Tone Correction**  
  Typing too fast and accidentally hitting the tone key before the vowel? For example, typing `ji394su3` as `ji394s3u`, pressing `3` before `u`; or typing `5k4g4u6ek7` as `5k4g46uek7`, pressing `6` before `u`, causing the entire translation to fail?
  We've got you covered! The mod includes an intelligent syllable reordering engine `fixInvertedTone`:
  - **Structure-Aware Reordering**: On standard DaChen keyboard layouts, each key's role is strictly defined across initials, medials, finals, and tones. When the parser detects an inverted sequence such as `s3u` -> `su3`, `148` -> `184`, `2u3l` -> `2ul3`, it automatically shifts the premature tone back to the end of the syllable.
  - **Zero-Initial Support**: Handles vowel-only syllables, such as `6u` -> `u6` for "一" or `49` -> `94` for "愛", even in continuous typing without spaces like `5k4g46uek7` -> `5k4g4u6ek7`.
  - **Collision-Safe via Negative Lookahead**: Uses `(?![3467])` lookaheads to ensure the tone is only shifted when the following vowel doesn't already have its own tone, completely preventing false positives across adjacent words.

---

## Setup & Installation

1. Ensure you have downloaded the mod that matches your Minecraft version, and have installed the **Fabric/Quilt Loader**.
2. Toss the compiled `.jar` file into your `mods` folder.
3. Enter the game, open the chat, and enjoy your new life as the server's supreme Martian Translator!

---

## Development & Testing

This project uses **Stonecutter** for cross-version 1.20.1, 1.21.11, and 26.2 development and build management, avoiding the need to manually switch branches. Use the following Gradle commands:

* **Build all supported versions:**
  ```bash
  .\gradlew clean buildAndCollect
  ```
  Compiled `.jar` files will be output to `build/libs/{mod.version}/` in the project root.

* **Run a specific version's test client:**
  Test environments for each version are completely isolated, each with dedicated `run` directories. You can launch them directly:
  - For 1.20.1: `.\gradlew :1.20.1:runClient`
  - For 1.21.11: `.\gradlew :1.21.11:runClient`
  - For 26.2: `.\gradlew :26.2.x:runClient`

* **Switch active project in IDE:**
  ```bash
  .\gradlew "Set active project to 1.21.11"
  .\gradlew "Set active project to 26.2.x"
  ```

---

## FAQ

**Q: Can other players who don't have this mod see the translations?**  
A: If they couldn't read it before, they still can't! This is a **purely client-side mod**, so all the translation magic happens locally on your computer. You install it, you enjoy it. Those who don't will continue spacing out with `???`.

**Q: Will it translate my own gibberish too?**  
A: Yes! It catches all `ChatHud` messages in the chat room. Whether it's sent by others or yours, nothing escapes its gaze.

---

**License & Copyright**  
Copyright © 2026 flandretw | This project is licensed under the [MIT License](LICENSE).
