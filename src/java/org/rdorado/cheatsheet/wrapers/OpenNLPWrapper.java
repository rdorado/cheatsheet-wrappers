package org.rdorado.cheatsheet.wrapers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.rdorado.cheatsheet.parser.ParseTree;
import org.rdorado.cheatsheet.parser.Parser;
import org.rdorado.cheatsheet.segmenter.SentenceSegmenter;
import org.rdorado.cheatsheet.tagger.POSTagger;
import org.rdorado.cheatsheet.tagger.TaggedSentence;
import org.rdorado.cheatsheet.tagger.TaggedToken;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPWrapper implements SentenceSegmenter, POSTagger, Parser{

	SentenceDetector detector;
	POSTaggerME tagger;
	Tokenizer tokenizer;
	opennlp.tools.parser.Parser parser;
			
	public OpenNLPWrapper(){		
		try {
			InputStream inputStream = new FileInputStream("models/opennlp/en-sent.bin");
			detector = new SentenceDetectorME(new SentenceModel(inputStream));
			
			inputStream = new FileInputStream("models/opennlp/en-pos-maxent.bin");
			//inputStream = new FileInputStream("models/opennlp/en-pos-maxent-gutenberg.model");
			tagger = new POSTaggerME(new POSModel(inputStream));
			
			inputStream = new FileInputStream("models/opennlp/en-token.bin");
			tokenizer = new TokenizerME(new TokenizerModel(inputStream));
			
			inputStream = new FileInputStream("models/opennlp/en-parser-chunking.bin");
			parser = ParserFactory.create(new ParserModel(inputStream));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 
	
	@Override
	public String[] segment(String paragraph) {
		if (detector != null) {
			return detector.sentDetect(paragraph);
		}
		return null;
	}
	
	@Override
	public TaggedSentence tag(String sentence) {
		 
		if (tokenizer != null && tagger != null) {
			TaggedSentence result = new TaggedSentence(); 
			String[] tokens  = tokenizer.tokenize(sentence);
			String[] tags= tagger.tag(tokens);
			for (int i=0;i<tokens.length;i++) {
				result.addTaggedToken(tokens[i], tags[i]);
			}
			return result;
		}
		return null;
	}
	

	@Override
	public ParseTree parse(String sentence) {
		if(parser != null) {
			ParseTree result = new ParseTree();
			
			//Parse topParses[] = ParserTool.parseLine(sentence, this.parser, 1);
			Parse topParses[] = ParserTool.parseLine(sentence, this.parser, this.tokenizer, 1);
			toParseTree(topParses[0], result);
			return result;
			
		}
		return null;
	}
	
	
	private void toParseTree(Parse parse, ParseTree result) {
		if (parse.getType().equals("TOP")) {
			toParseTree(parse.getChildren()[0], result);
			return;
		}
		result.setTag(parse.getType());
		for(Parse p : parse.getChildren()) {
			if (p.getType().equals("TK")) {
				result.setText(parse.getCoveredText());
			}
			else {
				toParseTree(p, result.createChild());
			}
			
		}
	}


/*	public static void main(String[] args) {
		OpenNLPWrapper wrapper = new OpenNLPWrapper();
		
		for(TaggedToken token : (wrapper.tag("The dog ate chocolate.")).getTaggedTokens()) {
			System.out.println(token);
		};
	}*/

}
