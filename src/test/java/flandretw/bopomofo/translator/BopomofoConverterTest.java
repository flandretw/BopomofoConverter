package flandretw.bopomofo.translator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BopomofoConverterTest {

    @Test
    public void testNormalConversion() {
        BopomofoConverter.BopomofoResult result = BopomofoConverter.convert("ji394su3");
        assertTrue(result.changed);
        assertNotNull(result.segments);
        assertEquals(1, result.segments.size());
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", result.segments.get(0).translated);
    }

    @Test
    public void testInvertedToneAtEnd() {
        // Case: ji394su3 typed as ji394s3u
        BopomofoConverter.BopomofoResult result = BopomofoConverter.convert("ji394s3u");
        assertTrue(result.changed, "Should be converted despite inverted tone in s3u");
        assertNotNull(result.segments);
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", result.segments.get(0).translated);
    }

    @Test
    public void testInvertedToneInSentence() {
        BopomofoConverter.BopomofoResult result = BopomofoConverter.convert("hello ji394s3u world");
        assertTrue(result.changed);
        assertNotNull(result.segments);
        // segments: "hello", " ", "ji394s3u", " ", "world"
        boolean foundTranslated = false;
        for (BopomofoConverter.Segment seg : result.segments) {
            if ("ji394s3u".equals(seg.original)) {
                assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", seg.translated);
                foundTranslated = true;
            }
        }
        assertTrue(foundTranslated);
    }

    @Test
    public void testOtherInvertedTones() {
        // 148 -> 184 (ba4)
        BopomofoConverter.BopomofoResult res1 = BopomofoConverter.convert("148");
        assertTrue(res1.changed);
        assertEquals("ㄅㄚˋ", res1.segments.get(0).translated);

        // 2u3l -> 2ul3 (diao3)
        BopomofoConverter.BopomofoResult res2 = BopomofoConverter.convert("2u3l");
        assertTrue(res2.changed);
        assertEquals("ㄉㄧㄠˇ", res2.segments.get(0).translated);

        // u30 -> u03 (yan3)
        BopomofoConverter.BopomofoResult res3 = BopomofoConverter.convert("u30");
        assertTrue(res3.changed);
        assertEquals("ㄧㄢˇ", res3.segments.get(0).translated);
    }

    @Test
    public void testInvertedToneZeroInitial() {
        // Case: "this is one" (5k4g4u6ek7) typed as 5k4g46uek7
        BopomofoConverter.BopomofoResult res = BopomofoConverter.convert("5k4g46uek7");
        assertTrue(res.changed, "Should be converted despite zero-initial inverted tone 6u -> u6");
        assertNotNull(res.segments);
        assertEquals("ㄓㄜˋㄕˋㄧˊㄍㄜ˙", res.segments.get(0).translated);
    }

    @Test
    public void testFranceOil() {
        BopomofoConverter.BopomofoResult res = BopomofoConverter.convert("z83eji6u.6");
        assertTrue(res.changed, "z83eji6u.6 should convert to 法國油");
        assertNotNull(res.segments);
        assertEquals("ㄈㄚˇㄍㄨㄛˊㄧㄡˊ", res.segments.get(0).translated);
    }

    @Test
    public void testUserCase() {
        String input1 = "al au 2845045/ ，cl3cl3j06";
        BopomofoConverter.BopomofoResult res1 = BopomofoConverter.convert(input1);
        assertTrue(res1.changed);
        assertNotNull(res1.segments);
        assertEquals("al au 2845045/", res1.segments.get(0).original);
        assertEquals("ㄇㄠ ㄇㄧ ㄉㄚˋㄓㄢˋㄓㄥ", res1.segments.get(0).translated);
        assertEquals(" ", res1.segments.get(1).original);
        assertEquals("，", res1.segments.get(2).original);
        assertEquals("cl3cl3j06", res1.segments.get(3).original);
        assertEquals("ㄏㄠˇㄏㄠˇㄨㄢˊ", res1.segments.get(3).translated);

        String input2 = "貓咪大戰爭，好好玩（al au 2845045/ ，cl3cl3j06）";
        BopomofoConverter.BopomofoResult res2 = BopomofoConverter.convert(input2);
        assertTrue(res2.changed);
        assertNotNull(res2.segments);
        assertEquals("貓咪大戰爭，好好玩（", res2.segments.get(0).original);
        assertNull(res2.segments.get(0).translated);
        assertEquals("al au 2845045/", res2.segments.get(1).original);
        assertEquals("ㄇㄠ ㄇㄧ ㄉㄚˋㄓㄢˋㄓㄥ", res2.segments.get(1).translated);
        assertEquals("cl3cl3j06", res2.segments.get(4).original);
        assertEquals("ㄏㄠˇㄏㄠˇㄨㄢˊ", res2.segments.get(4).translated);
        assertEquals("）", res2.segments.get(5).original);
        assertNull(res2.segments.get(5).translated);
    }

    @Test
    public void testPunctuationAndMixedCases() {
        // Exclamation mark
        BopomofoConverter.BopomofoResult resExcl = BopomofoConverter.convert("ji394su3!");
        assertTrue(resExcl.changed);
        assertEquals(2, resExcl.segments.size());
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", resExcl.segments.get(0).translated);
        assertEquals("!", resExcl.segments.get(1).original);

        // Halfwidth parentheses
        BopomofoConverter.BopomofoResult resParen = BopomofoConverter.convert("(ji394su3)");
        assertTrue(resParen.changed);
        assertEquals(3, resParen.segments.size());
        assertEquals("(", resParen.segments.get(0).original);
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", resParen.segments.get(1).translated);
        assertEquals(")", resParen.segments.get(2).original);

        // Fullwidth period (Chinese punctuation)
        BopomofoConverter.BopomofoResult resDot = BopomofoConverter.convert("ji394su3。");
        assertTrue(resDot.changed);
        assertEquals(2, resDot.segments.size());
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", resDot.segments.get(0).translated);
        assertEquals("。", resDot.segments.get(1).original);

        // Tilde / wave
        BopomofoConverter.BopomofoResult resWave = BopomofoConverter.convert("ji394su3~");
        assertTrue(resWave.changed);
        assertEquals(2, resWave.segments.size());
        assertEquals("ㄨㄛˇㄞˋㄋㄧˇ", resWave.segments.get(0).translated);
        assertEquals("~", resWave.segments.get(1).original);

        // Dot in bopomofo is 'ㄡ' (e.g. su3. -> su.3 ㄋㄧㄡˇ via early tone fix)
        BopomofoConverter.BopomofoResult resOu = BopomofoConverter.convert("ji394su3.");
        assertTrue(resOu.changed);
        assertEquals(1, resOu.segments.size());
        assertEquals("ㄨㄛˇㄞˋㄋㄧㄡˇ", resOu.segments.get(0).translated);
    }

    @Test
    public void testNormalEnglishNotAffected() {
        BopomofoConverter.BopomofoResult res = BopomofoConverter.convert("hello world");
        assertFalse(res.changed);
    }
}
