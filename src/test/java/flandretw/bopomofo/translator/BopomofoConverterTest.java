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
    public void testNormalEnglishNotAffected() {
        BopomofoConverter.BopomofoResult res = BopomofoConverter.convert("hello world");
        assertFalse(res.changed);
    }
}
