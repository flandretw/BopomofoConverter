package flandretw.bopomofo.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BopomofoConverter {
    private static final Map<Character, Character> KEY_MAP = new HashMap<>();
    private static final String I_PART = "[1qaz2wsxedcrfv5tgbyhn]";
    private static final String M_PART = "[ujm]";
    private static final String F_PART = "[8ik,9ol\\.0p;\\/\\-]";
    private static final String T_PART = "[3467\\s]";
    private static final String C_PART = "(?:" + I_PART + M_PART + "?" + F_PART + "?|" + M_PART + F_PART + "?|" + F_PART + ")";
    static final Pattern STRICT_PATTERN;

    static {
        // Initials
        KEY_MAP.put('1', 'ㄅ');
        KEY_MAP.put('q', 'ㄆ');
        KEY_MAP.put('a', 'ㄇ');
        KEY_MAP.put('z', 'ㄈ');
        KEY_MAP.put('2', 'ㄉ');
        KEY_MAP.put('w', 'ㄊ');
        KEY_MAP.put('s', 'ㄋ');
        KEY_MAP.put('x', 'ㄌ');
        KEY_MAP.put('e', 'ㄍ');
        KEY_MAP.put('d', 'ㄎ');
        KEY_MAP.put('c', 'ㄏ');
        KEY_MAP.put('r', 'ㄐ');
        KEY_MAP.put('f', 'ㄑ');
        KEY_MAP.put('v', 'ㄒ');
        KEY_MAP.put('5', 'ㄓ');
        KEY_MAP.put('t', 'ㄔ');
        KEY_MAP.put('g', 'ㄕ');
        KEY_MAP.put('b', 'ㄖ');
        KEY_MAP.put('y', 'ㄗ');
        KEY_MAP.put('h', 'ㄘ');
        KEY_MAP.put('n', 'ㄙ');

        // Medials
        KEY_MAP.put('u', 'ㄧ');
        KEY_MAP.put('j', 'ㄨ');
        KEY_MAP.put('m', 'ㄩ');

        // Finals
        KEY_MAP.put('8', 'ㄚ');
        KEY_MAP.put('i', 'ㄛ');
        KEY_MAP.put('k', 'ㄜ');
        KEY_MAP.put(',', 'ㄝ');
        KEY_MAP.put('9', 'ㄞ');
        KEY_MAP.put('o', 'ㄟ');
        KEY_MAP.put('l', 'ㄠ');
        KEY_MAP.put('.', 'ㄡ');
        KEY_MAP.put('0', 'ㄢ');
        KEY_MAP.put('p', 'ㄣ');
        KEY_MAP.put(';', 'ㄤ');
        KEY_MAP.put('/', 'ㄥ');
        KEY_MAP.put('-', 'ㄦ');

        // Tones
        KEY_MAP.put('6', 'ˊ');
        KEY_MAP.put('3', 'ˇ');
        KEY_MAP.put('4', 'ˋ');
        KEY_MAP.put('7', '˙');
        KEY_MAP.put(' ', ' ');

        // The whole string must be composed of syllables that have a tone,
        // with the exception that the very last syllable can lack a tone.
        STRICT_PATTERN = Pattern.compile("^(?:" + C_PART + T_PART + ")*(?:" + C_PART + T_PART + "?)$");
    }

    private static final Pattern DELIMITER_PATTERN = Pattern.compile("[^a-zA-Z0-9,\\.\\/;\\-\\s]+");

    public static class BopomofoResult {
        public final boolean changed;
        public final List<Segment> segments;

        public BopomofoResult(boolean changed, List<Segment> segments) {
            this.changed = changed;
            this.segments = segments;
        }
    }

    public static class Segment {
        public final String original;
        public final String translated; // null if no translation applied

        public Segment(String original, String translated) {
            this.original = original;
            this.translated = translated;
        }
    }

    public static BopomofoResult convert(String originalText) {
        if (originalText == null || originalText.trim().isEmpty()) {
            return new BopomofoResult(false, null);
        }

        String normalized = normalizeFullWidth(originalText);

        // If the entire text is a bopomofo sentence without delimiters
        if (!DELIMITER_PATTERN.matcher(normalized).find()) {
            String directTrans = tryTranslate(normalized);
            if (directTrans != null) {
                List<Segment> list = new ArrayList<>();
                list.add(new Segment(originalText, directTrans));
                return new BopomofoResult(true, list);
            }
        }

        List<Segment> segments = new ArrayList<>();
        java.util.regex.Matcher m = DELIMITER_PATTERN.matcher(normalized);
        int lastIndex = 0;
        boolean changed = false;

        while (m.find()) {
            int start = m.start();
            int end = m.end();

            if (start > lastIndex) {
                String candOrig = originalText.substring(lastIndex, start);
                String candNorm = normalized.substring(lastIndex, start);
                if (processCandidateChunk(candOrig, candNorm, segments)) {
                    changed = true;
                }
            }

            String delimOrig = originalText.substring(start, end);
            segments.add(new Segment(delimOrig, null));

            lastIndex = end;
        }

        if (lastIndex < normalized.length()) {
            String candOrig = originalText.substring(lastIndex);
            String candNorm = normalized.substring(lastIndex);
            if (processCandidateChunk(candOrig, candNorm, segments)) {
                changed = true;
            }
        }

        return new BopomofoResult(changed, changed ? segments : null);
    }

    private static boolean processCandidateChunk(String origChunk, String normChunk, List<Segment> segments) {
        if (normChunk == null || normChunk.isEmpty()) {
            return false;
        }

        int leadSpaces = 0;
        while (leadSpaces < normChunk.length() && normChunk.charAt(leadSpaces) == ' ') {
            leadSpaces++;
        }
        int trailSpaces = 0;
        while (trailSpaces < normChunk.length() - leadSpaces && normChunk.charAt(normChunk.length() - 1 - trailSpaces) == ' ') {
            trailSpaces++;
        }

        String trimmedNorm = normChunk.substring(leadSpaces, normChunk.length() - trailSpaces);
        String trimmedOrig = origChunk.substring(leadSpaces, origChunk.length() - trailSpaces);

        if (!trimmedNorm.isEmpty()) {
            String fullTrans = tryTranslate(trimmedNorm);
            if (fullTrans != null) {
                if (leadSpaces > 0) {
                    segments.add(new Segment(origChunk.substring(0, leadSpaces), null));
                }
                segments.add(new Segment(trimmedOrig, fullTrans));
                if (trailSpaces > 0) {
                    segments.add(new Segment(origChunk.substring(origChunk.length() - trailSpaces), null));
                }
                return true;
            }
        }

        String[] words = normChunk.split("(?<= )|(?= )");
        boolean changed = false;
        int curr = 0;

        for (String word : words) {
            int len = word.length();
            String origWord = origChunk.substring(curr, curr + len);
            curr += len;

            if (word.trim().isEmpty()) {
                segments.add(new Segment(origWord, null));
                continue;
            }

            String trans = tryTranslate(word);
            if (trans != null) {
                segments.add(new Segment(origWord, trans));
                changed = true;
                continue;
            }

            // Check if trailing punctuation (e.g. '.', ',', ';', '-') can be detached
            if (word.length() > 2) {
                char lastChar = word.charAt(word.length() - 1);
                if (lastChar == '.' || lastChar == ',' || lastChar == ';' || lastChar == '-') {
                    String sub = word.substring(0, word.length() - 1);
                    String subTrans = tryTranslate(sub);
                    if (subTrans != null) {
                        segments.add(new Segment(origWord.substring(0, origWord.length() - 1), subTrans));
                        segments.add(new Segment(origWord.substring(origWord.length() - 1), null));
                        changed = true;
                        continue;
                    }
                }
            }

            segments.add(new Segment(origWord, null));
        }

        return changed;
    }

    static String tryTranslate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String lowered = text.toLowerCase();
        String fixed = fixInvertedTone(lowered);
        boolean hasIndicator = fixed.matches(".*[0-9,\\.\\/;\\-].*");
        if (STRICT_PATTERN.matcher(fixed).matches() && (hasIndicator || fixed.length() > 1)) {
            if (allSyllablesValid(fixed)) {
                return translateFully(fixed);
            }
        }
        return null;
    }

    static boolean allSyllablesValid(String text) {
        Pattern p = Pattern.compile(C_PART + T_PART + "?");
        java.util.regex.Matcher m = p.matcher(text);
        int lastEnd = 0;
        while (m.find()) {
            if (m.start() != lastEnd) return false;
            if (!isSyllableValid(m.group())) return false;
            lastEnd = m.end();
        }
        return lastEnd == text.length();
    }

    private static boolean isSyllableValid(String s) {
        if (s == null || s.isEmpty()) return false;
        int len = s.length();
        int pos = 0;
        char initial = 0, medial = 0, finalChar = 0;

        String initials = "1qaz2wsxedcrfv5tgbyhn";
        String medials = "ujm";
        String finals = "8ik,9ol.0p;/-";
        String tones = "3467 ";

        if (pos < len && initials.indexOf(s.charAt(pos)) != -1) { initial = s.charAt(pos); pos++; }
        if (pos < len && medials.indexOf(s.charAt(pos)) != -1) { medial = s.charAt(pos); pos++; }
        if (pos < len && finals.indexOf(s.charAt(pos)) != -1) { finalChar = s.charAt(pos); pos++; }
        while (pos < len && tones.indexOf(s.charAt(pos)) != -1) { pos++; }
        if (pos < len) return false;

        // J, Q, X must be followed by I or YU
        if (initial == 'r' || initial == 'f' || initial == 'v') {
            if (medial != 'u' && medial != 'm') return false;
        }
        // ZH, CH, SH, R, Z, C, S cannot be followed by I or YU
        if ("5tgbyhn".indexOf(initial) != -1) {
            if (medial == 'u' || medial == 'm') return false;
        }
        // B, P, M, F, D, T, G, K, H cannot be followed by YU
        if ("1qaz2wedc".indexOf(initial) != -1) {
            if (medial == 'm') return false;
        }
        // Medial YU can only be followed by specific finals: ㄝ, ㄢ, ㄣ, ㄥ
        if (medial == 'm' && finalChar != 0 && ",0p/".indexOf(finalChar) == -1) return false;

        // Non-sibilant initials (i.e. not ㄓㄔㄕㄖㄗㄘㄙ) cannot stand alone without medial or final
        if (initial != 0 && medial == 0 && finalChar == 0 && "5tgbyhn".indexOf(initial) == -1) return false;

        return true;
    }

    static String normalizeFullWidth(String text) {
        if (text == null) return null;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\u3000') {
                chars[i] = ' ';
            } else if (c >= '\uFF10' && c <= '\uFF19') { // Fullwidth 0-9
                chars[i] = (char) (c - 0xFEE0);
            } else if (c >= '\uFF21' && c <= '\uFF3A') { // Fullwidth A-Z
                chars[i] = (char) (c - 0xFEE0);
            } else if (c >= '\uFF41' && c <= '\uFF5A') { // Fullwidth a-z
                chars[i] = (char) (c - 0xFEE0);
            }
            // Keep all other fullwidth punctuation (e.g. \uFF0C fullwidth comma, \uFF08/\uFF09 parens) unchanged
        }
        return new String(chars);
    }


    static String fixInvertedTone(String text) {
        if (text == null || text.length() < 2) return text;

        String initials = "1qaz2wsxedcrfv5tgbyhn";
        String medials = "ujm";
        String finals = "8ik,9ol\\.0p;\\/\\-";
        String tones = "3467";
        String notTone = "(?![" + tones + "])";
        String notFinalOrTone = "(?![" + finals + tones + "])";

        // 1a. Initial + Tone + Medial + Final (e.g. 23ul -> 2ul3)
        text = Pattern.compile("([" + initials + "])([" + tones + "])([" + medials + "])([" + finals + "])" + notTone)
                .matcher(text).replaceAll("$1$3$4$2");

        // 1b. Initial + Tone + Medial without Final (e.g. s3u -> su3)
        // Must not be followed by a final (like u.3 where .3 belongs to u)
        text = Pattern.compile("([" + initials + "])([" + tones + "])([" + medials + "])" + notFinalOrTone)
                .matcher(text).replaceAll("$1$3$2");

        // 2. Initial + Tone + Final (e.g. 148 -> 184, g4/ -> g/4)
        text = Pattern.compile("([" + initials + "])([" + tones + "])([" + finals + "])" + notTone)
                .matcher(text).replaceAll("$1$3$2");

        // 3. Initial + Medial + Tone + Final (e.g. 2u3l -> 2ul3)
        text = Pattern.compile("([" + initials + "])([" + medials + "])([" + tones + "])([" + finals + "])" + notTone)
                .matcher(text).replaceAll("$1$2$4$3");

        // 4. Medial + Tone + Final (e.g. u30 -> u03, j3i -> ji3)
        text = Pattern.compile("([" + medials + "])([" + tones + "])([" + finals + "])" + notTone)
                .matcher(text).replaceAll("$1$3$2");

        // 5. Zero-initial syllables with early tone (at start, after space, non-initial, or after preceding tone; e.g. 6u -> u6, 49 -> 94, 5k4g46uek7 -> 5k4g4u6ek7)
        // Note: We must ensure that a preceding syllable's final consonant/vowel is NOT treated as zero-initial!
        // So the preceding character before the early tone must be either start of string, whitespace, or an actual tone.
        String prev = "";
        Pattern p5 = Pattern.compile("(^|[\\s" + tones + "])([" + tones + "])((?:[" + medials + "][" + finals + "])|(?:[" + medials + "]" + notFinalOrTone + ")|[" + finals + "])" + notTone);
        while (!text.equals(prev)) {
            prev = text;
            text = p5.matcher(text).replaceAll("$1$3$2");
        }

        return text;
    }

    private static String translateFully(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(KEY_MAP.getOrDefault(c, c));
        }
        return sb.toString();
    }
}
