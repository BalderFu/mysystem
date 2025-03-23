package com.sensitive.biz;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

public class Test {
    public static void main(String[] args) throws IOException {
        String text = "我爱你中国";

        // 创建 IKAnalyzer 分词器（true = 智能分词模式，false = 细粒度分词）
        IKAnalyzer analyzer = new IKAnalyzer(true);

        // 解析文本
        TokenStream tokenStream = analyzer.tokenStream("", new StringReader(text));
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print(term.toString() + " | ");
        }
        tokenStream.close();


    }
}
