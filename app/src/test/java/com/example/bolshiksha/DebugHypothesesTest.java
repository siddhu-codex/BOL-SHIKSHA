package com.example.bolshiksha;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DebugHypothesesTest {

    @Test
    public void hindiWordBoundaryDoesNotMatchDevanagari() {
        String hi = "नमस्ते";
        Pattern p = Pattern.compile("(?i)\\b" + Pattern.quote(hi) + "\\b");
        Matcher isolated = p.matcher(hi);
        Matcher sentence = p.matcher("नमस्ते बच्चों");
        boolean isolatedFind = isolated.find();
        boolean sentenceFind = sentence.find();
        boolean contains = "नमस्ते बच्चों".contains(hi);
        AgentDebugLog.log("A", "DebugHypothesesTest.java:regex",
                "java \\\\b on hindi",
                "{\"isolatedFind\":" + isolatedFind
                        + ",\"sentenceFind\":" + sentenceFind
                        + ",\"contains\":" + contains + "}");
        // Evidence assertion: this documents the suspected bug (\\b misses Devanagari).
        assertFalse("isolated \\b should fail for Devanagari", isolatedFind);
        assertFalse("sentence \\b should fail for Devanagari", sentenceFind);
        assertTrue(contains);
    }

    @Test
    public void dailyWordsCategoryTruncatesOnFirstSpace() {
        String full = "रोजमर्रा के शब्द (Daily Words)";
        String truncated = full.split(" ")[0];
        boolean matchesGenerator = "रोजमर्रा के शब्द".equals(truncated)
                || "Daily Words".equals(truncated);
        AgentDebugLog.log("B", "DebugHypothesesTest.java:category",
                "split first token",
                "{\"fullLen\":" + full.length()
                        + ",\"truncatedLen\":" + truncated.length()
                        + ",\"matchesGeneratorCase\":" + matchesGenerator + "}");
        assertFalse(matchesGenerator);
    }
}
