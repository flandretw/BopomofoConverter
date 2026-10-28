package flandretw.bopomofo.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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

    private static final Pattern DELIMITER_PATTERN = Pattern.compile("(?:§[0-9a-fk-orA-FK-OR]|[^a-zA-Z0-9,\\.\\/;\\-\\s§]|§(?![0-9a-fk-orA-FK-OR]))+");
    private static final Pattern SYLLABLE_PATTERN = Pattern.compile(C_PART + T_PART + "?");

    private static final String INVERTED_INITIALS = "1qaz2wsxedcrfv5tgbyhn";
    private static final String INVERTED_MEDIALS = "ujm";
    private static final String INVERTED_FINALS = "8ik,9ol\\.0p;\\/\\-";
    private static final String INVERTED_TONES = "3467";
    private static final String NOT_TONE = "(?![" + INVERTED_TONES + "])";
    private static final String NOT_FINAL_OR_TONE = "(?![" + INVERTED_FINALS + INVERTED_TONES + "])";

    private static final Pattern INVERTED_P1A = Pattern.compile("([" + INVERTED_INITIALS + "])([" + INVERTED_TONES + "])([" + INVERTED_MEDIALS + "])([" + INVERTED_FINALS + "])" + NOT_TONE);
    private static final Pattern INVERTED_P1B = Pattern.compile("([" + INVERTED_INITIALS + "])([" + INVERTED_TONES + "])([" + INVERTED_MEDIALS + "])" + NOT_FINAL_OR_TONE);
    private static final Pattern INVERTED_P2 = Pattern.compile("([" + INVERTED_INITIALS + "])([" + INVERTED_TONES + "])([" + INVERTED_FINALS + "])" + NOT_TONE);
    private static final Pattern INVERTED_P3 = Pattern.compile("([" + INVERTED_INITIALS + "])([" + INVERTED_MEDIALS + "])([" + INVERTED_TONES + "])([" + INVERTED_FINALS + "])" + NOT_TONE);
    private static final Pattern INVERTED_P4 = Pattern.compile("([" + INVERTED_MEDIALS + "])([" + INVERTED_TONES + "])([" + INVERTED_FINALS + "])" + NOT_TONE);
    private static final Pattern INVERTED_P5 = Pattern.compile("(^|[\\s" + INVERTED_TONES + "])([" + INVERTED_TONES + "])((?:[" + INVERTED_MEDIALS + "][" + INVERTED_FINALS + "])|(?:[" + INVERTED_MEDIALS + "]" + NOT_FINAL_OR_TONE + ")|[" + INVERTED_FINALS + "])" + NOT_TONE);

    public static final BopomofoResult EMPTY_RESULT = new BopomofoResult(false, null);

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
            return EMPTY_RESULT;
        }

        // Fast-path: A valid bopomofo translation requires at least 2 potential bopomofo characters
        if (!hasCandidateChars(originalText)) {
            return EMPTY_RESULT;
        }

        String normalized = normalizeFullWidth(originalText);

        // If the entire text has no delimiters (only letters, numbers, spaces)
        if (!DELIMITER_PATTERN.matcher(normalized).find()) {
            String directTrans = tryTranslate(normalized);
            if (directTrans != null) {
                List<Segment> list = new ArrayList<>(1);
                list.add(new Segment(originalText, directTrans));
                return new BopomofoResult(true, list);
            }
            if (!hasTranslation(normalized, 0, normalized.length())) {
                return EMPTY_RESULT;
            }
            List<Segment> segments = new ArrayList<>();
            processCandidateChunk(originalText, normalized, segments);
            return new BopomofoResult(true, segments);
        }

        // Fast pre-check: verify if any candidate chunk actually produces a translation
        // before allocating Segment objects and ArrayList.
        boolean changed = false;
        Matcher m = DELIMITER_PATTERN.matcher(normalized);
        int lastIndex = 0;

        while (m.find()) {
            int start = m.start();
            if (start > lastIndex) {
                if (hasTranslation(normalized, lastIndex, start)) {
                    changed = true;
                    break;
                }
            }
            lastIndex = m.end();
        }

        if (!changed && lastIndex < normalized.length()) {
            if (hasTranslation(normalized, lastIndex, normalized.length())) {
                changed = true;
            }
        }

        if (!changed) {
            return EMPTY_RESULT;
        }

        // Translation confirmed: build segments
        List<Segment> segments = new ArrayList<>();
        m.reset();
        lastIndex = 0;

        while (m.find()) {
            int start = m.start();
            int end = m.end();

            if (start > lastIndex) {
                String candOrig = originalText.substring(lastIndex, start);
                String candNorm = normalized.substring(lastIndex, start);
                processCandidateChunk(candOrig, candNorm, segments);
            }

            String delimOrig = originalText.substring(start, end);
            segments.add(new Segment(delimOrig, null));

            lastIndex = end;
        }

        if (lastIndex < normalized.length()) {
            String candOrig = originalText.substring(lastIndex);
            String candNorm = normalized.substring(lastIndex);
            processCandidateChunk(candOrig, candNorm, segments);
        }

        return new BopomofoResult(true, segments);
    }

    private static boolean hasTranslation(CharSequence norm, int chunkStart, int chunkEnd) {
        if (chunkStart >= chunkEnd) {
            return false;
        }

        int start = chunkStart;
        while (start < chunkEnd && norm.charAt(start) <= ' ') {
            start++;
        }
        int end = chunkEnd;
        while (end > start && norm.charAt(end - 1) <= ' ') {
            end--;
        }

        if (start >= end) {
            return false;
        }

        String trimmedNorm = norm.subSequence(start, end).toString();
        if (tryTranslate(trimmedNorm) != null) {
            return true;
        }

        int curr = start;
        while (curr < end) {
            if (norm.charAt(curr) <= ' ') {
                curr++;
                continue;
            }

            int wordStart = curr;
            while (curr < end && norm.charAt(curr) > ' ') {
                curr++;
            }

            String word = norm.subSequence(wordStart, curr).toString();
            if (tryTranslate(word) != null) {
                return true;
            }

            if (word.length() > 2) {
                char lastChar = word.charAt(word.length() - 1);
                if (lastChar == '.' || lastChar == ',' || lastChar == ';' || lastChar == '-') {
                    String sub = word.substring(0, word.length() - 1);
                    if (tryTranslate(sub) != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean processCandidateChunk(String origChunk, String normChunk, List<Segment> segments) {
        if (normChunk == null || normChunk.isEmpty()) {
            return false;
        }

        int leadSpaces = 0;
        int len = normChunk.length();
        while (leadSpaces < len && normChunk.charAt(leadSpaces) <= ' ') {
            leadSpaces++;
        }
        int trailSpaces = 0;
        while (trailSpaces < len - leadSpaces && normChunk.charAt(len - 1 - trailSpaces) <= ' ') {
            trailSpaces++;
        }

        String trimmedNorm = normChunk.substring(leadSpaces, len - trailSpaces);
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

        boolean changed = false;
        int curr = 0;

        while (curr < len) {
            if (normChunk.charAt(curr) <= ' ') {
                segments.add(new Segment(origChunk.substring(curr, curr + 1), null));
                curr++;
                continue;
            }

            int start = curr;
            while (curr < len && normChunk.charAt(curr) > ' ') {
                curr++;
            }

            String word = normChunk.substring(start, curr);
            String origWord = origChunk.substring(start, curr);

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
        if (text.length() < 2) return null;
        String lowered = text.toLowerCase();
        String fixed = fixInvertedTone(lowered);
        boolean hasIndicator = hasIndicatorChar(fixed);
        if (STRICT_PATTERN.matcher(fixed).matches()) {
            if (allSyllablesValid(fixed, hasIndicator)) {
                return translateFully(fixed);
            }
        }
        return null;
    }

    static boolean allSyllablesValid(String text, boolean hasIndicator) {
        Matcher m = SYLLABLE_PATTERN.matcher(text);
        int lastEnd = 0;
        int count = 0;
        while (m.find()) {
            if (m.start() != lastEnd) return false;
            if (!isSyllableValid(text, m.start(), m.end())) return false;
            lastEnd = m.end();
            count++;
        }
        if (lastEnd != text.length()) return false;
        if (!hasIndicator && count < 2) return false;
        return true;
    }

    private static boolean isSyllableValid(CharSequence s, int start, int end) {
        if (s == null || start >= end) return false;
        int pos = start;
        char initial = 0, medial = 0, finalChar = 0;

        String initials = "1qaz2wsxedcrfv5tgbyhn";
        String medials = "ujm";
        String finals = "8ik,9ol.0p;/-";
        String tones = "3467 ";

        if (pos < end && initials.indexOf(s.charAt(pos)) != -1) { initial = s.charAt(pos); pos++; }
        if (pos < end && medials.indexOf(s.charAt(pos)) != -1) { medial = s.charAt(pos); pos++; }
        if (pos < end && finals.indexOf(s.charAt(pos)) != -1) { finalChar = s.charAt(pos); pos++; }
        while (pos < end && tones.indexOf(s.charAt(pos)) != -1) { pos++; }
        if (pos < end) return false;

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

        // EI (o) without medial cannot follow T (w), K (d), CH (t), R (b), C (h), S (n)
        if (finalChar == 'o' && medial == 0 && "wdtbhn".indexOf(initial) != -1) return false;

        // O (i) without medial can only follow B (1), P (q), M (a), F (z), or zero-initial
        if (finalChar == 'i' && medial == 0 && initial != 0 && "1qaz".indexOf(initial) == -1) return false;

        return true;
    }

    private static boolean hasCandidateChars(String text) {
        int candidateCount = 0;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == ',' || c == '.' || c == '/' || c == ';' || c == '-'
                    || (c >= '\uFF10' && c <= '\uFF19') || (c >= '\uFF21' && c <= '\uFF3A') || (c >= '\uFF41' && c <= '\uFF5A')) {
                candidateCount++;
                if (candidateCount >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasIndicatorChar(String s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || c == ',' || c == '.' || c == '/' || c == ';' || c == '-') {
                return true;
            }
        }
        return false;
    }

    private static boolean hasInvertedToneChar(String s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '3' || c == '4' || c == '6' || c == '7') {
                return true;
            }
        }
        return false;
    }

    static String normalizeFullWidth(String text) {
        if (text == null) return null;
        boolean hasFullWidth = false;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == '\u3000' || (c >= '\uFF10' && c <= '\uFF19') || (c >= '\uFF21' && c <= '\uFF3A') || (c >= '\uFF41' && c <= '\uFF5A')) {
                hasFullWidth = true;
                break;
            }
        }
        if (!hasFullWidth) {
            return text;
        }

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
        if (!hasInvertedToneChar(text)) return text;

        // 1a. Initial + Tone + Medial + Final (e.g. 23ul -> 2ul3)
        Matcher m = INVERTED_P1A.matcher(text);
        if (m.find()) {
            text = m.replaceAll("$1$3$4$2");
        }

        // 1b. Initial + Tone + Medial without Final (e.g. s3u -> su3)
        m = INVERTED_P1B.matcher(text);
        if (m.find()) {
            text = m.replaceAll("$1$3$2");
        }

        // 2. Initial + Tone + Final (e.g. 148 -> 184, g4/ -> g/4)
        m = INVERTED_P2.matcher(text);
        if (m.find()) {
            text = m.replaceAll("$1$3$2");
        }

        // 3. Initial + Medial + Tone + Final (e.g. 2u3l -> 2ul3)
        m = INVERTED_P3.matcher(text);
        if (m.find()) {
            text = m.replaceAll("$1$2$4$3");
        }

        // 4. Medial + Tone + Final (e.g. u30 -> u03, j3i -> ji3)
        m = INVERTED_P4.matcher(text);
        if (m.find()) {
            text = m.replaceAll("$1$3$2");
        }

        // 5. Zero-initial syllables with early tone (at start, after space, non-initial, or after preceding tone; e.g. 6u -> u6, 49 -> 94, 5k4g46uek7 -> 5k4g4u6ek7)
        m = INVERTED_P5.matcher(text);
        if (m.find()) {
            String prev = "";
            while (!text.equals(prev)) {
                prev = text;
                text = INVERTED_P5.matcher(text).replaceAll("$1$3$2");
            }
        }

        return text;
    }

    private static String translateFully(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Character mapped = KEY_MAP.get(c);
            sb.append(mapped != null ? mapped : c);
        }
        return sb.toString();
    }
}
