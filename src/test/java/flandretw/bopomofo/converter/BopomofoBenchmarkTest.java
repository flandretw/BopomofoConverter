package flandretw.bopomofo.converter;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Tag("benchmark")
public class BopomofoBenchmarkTest {

    private static final String SERVER_ANNOUNCEMENT =
            "§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n" +
            "§f                            §f§lReward Summary\n\n" +
            "   §7You earned\n" +
            "§f     • §61,218 SkyWars Coins\n" +
            "§f     • §3132 Hypixel Experience\n" +
            "§f     • §2517 Guild Experience\n\n" +
            "§7你獲得了 §d6 點經驗值\n" +
            "§7你收割了 §b8 個戰魂\n\n" +
            "§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";

    private static final String PURE_ENGLISH =
            "Hello everyone, welcome to the server! Please check out the discord server and vote for daily rewards.";

    private static final String PURE_CHINESE =
            "今天伺服器天氣真好，大家要不要一起去打終界龍？記得帶好裝備與金蘋果！";

    private static final String MIXED_CANDIDATES =
            "貓咪大戰爭，好好玩（al au 2845045/ ，cl3cl3j06）今天晚上九點伺服器打王活動，記得準時參加！";

    private static final String INVERTED_TONE =
            "53u.3cj6/t86dk3u3a87";

    private static final String STANDARD_BOPOMOFO =
            "ji394su3";

    private record BenchmarkEntry(String name, String text) {}

    @Test
    public void runBenchmark() {
        List<BenchmarkEntry> entries = new ArrayList<>();
        entries.add(new BenchmarkEntry("1. Server Announcement (Reward Summary)", SERVER_ANNOUNCEMENT));
        entries.add(new BenchmarkEntry("2. Pure English Chat", PURE_ENGLISH));
        entries.add(new BenchmarkEntry("3. Pure Chinese Chat", PURE_CHINESE));
        entries.add(new BenchmarkEntry("4. Mixed Candidates (Bopomofo + Chinese)", MIXED_CANDIDATES));
        entries.add(new BenchmarkEntry("5. Inverted Tone String", INVERTED_TONE));
        entries.add(new BenchmarkEntry("6. Standard Bopomofo String", STANDARD_BOPOMOFO));

        // Interleaved warm-up across all cases to avoid single-case JIT monomorphism/bias
        for (int round = 0; round < 10_000; round++) {
            for (BenchmarkEntry entry : entries) {
                BopomofoConverter.convert(entry.text);
            }
        }

        System.out.println("================== BOPOMOFO BENCHMARK (INTERLEAVED) ==================");

        // Multi-round interleaved timing
        int rounds = 50;
        int opsPerRound = 1_000;
        long[] totalNanos = new long[entries.size()];

        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < entries.size(); i++) {
                String text = entries.get(i).text;
                long start = System.nanoTime();
                for (int op = 0; op < opsPerRound; op++) {
                    BopomofoConverter.convert(text);
                }
                totalNanos[i] += (System.nanoTime() - start);
            }
        }

        for (int i = 0; i < entries.size(); i++) {
            BenchmarkEntry entry = entries.get(i);
            double avgNanos = (double) totalNanos[i] / (rounds * opsPerRound);
            long allocatedBytes = measureAllocatedBytes(entry.text);
            System.out.printf("%-42s | %8.2f ns/call | %6d bytes/call%n", entry.name, avgNanos, allocatedBytes);
        }

        System.out.println("======================================================================");
    }

    private long measureAllocatedBytes(String text) {
        try {
            java.lang.management.ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            if (mxBean instanceof com.sun.management.ThreadMXBean sunMxBean) {
                long tid = Thread.currentThread().getId();
                long startBytes = sunMxBean.getThreadAllocatedBytes(tid);
                int allocIterations = 10_000;
                for (int i = 0; i < allocIterations; i++) {
                    BopomofoConverter.convert(text);
                }
                long endBytes = sunMxBean.getThreadAllocatedBytes(tid);
                return (endBytes - startBytes) / allocIterations;
            }
        } catch (Throwable ignored) {}
        return 0;
    }
}
