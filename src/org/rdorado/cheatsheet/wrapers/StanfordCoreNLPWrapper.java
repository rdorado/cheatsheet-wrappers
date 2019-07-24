package org.rdorado.cheatsheet.wrapers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.rdorado.cheatsheet.parser.ParseTree;
import org.rdorado.cheatsheet.parser.Parser;
import org.rdorado.cheatsheet.segmenter.SenteceSegmenter;
import org.rdorado.cheatsheet.tagger.POSTagger;
import org.rdorado.cheatsheet.tagger.TaggedSentence;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

//import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class StanfordCoreNLPWrapper implements SenteceSegmenter, POSTagger, Parser{

	StanfordCoreNLP pipeline;
	
	public StanfordCoreNLPWrapper() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, parse"); //, pos, lemma, ner, parse, dcoref
		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	public String[] segment(String paragraph) {
		Annotation document = new Annotation(paragraph);
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		String[] result = new String[sentences.size()];
		for(int i=0;i<sentences.size();i++) {
			  CoreMap sentence = sentences.get(i);
			  result[i] = sentence.toString();
		}
		
		return result;
	}

	@Override
	public TaggedSentence tag(String sentence) {
		TaggedSentence result = new TaggedSentence(); 
		Annotation document = new Annotation(sentence);
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap taggedsentence: sentences) {
			for (CoreLabel token: taggedsentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
			    String pos = token.get(PartOfSpeechAnnotation.class);
			    result.addTaggedToken(word, pos);
			}
		}
		/*MaxentTagger maxentTagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

	    String tag = maxentTagger.tagString("He half closed his eyes and searched the horizon.");
	    String[] eachTag = tag.split("\\s+");

	    for(int i = 0; i< eachTag.length; i++) {
	      System.out.print(eachTag[i].split("_")[0] +"_"+ eachTag[i].split("_")[1]+" ");
	    }*/
		return result;
	}
	
	
	@Override
	public ParseTree parse(String sentence) {
		ParseTree result = new ParseTree();
		Annotation document = new Annotation(sentence);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap taggedsentence: sentences) {
			Tree constituencyParse = taggedsentence.get(TreeAnnotation.class);
			result.setText(constituencyParse.toString());
		}
		return result;
	}
	
	public static void main(String[] args){

		/*MaxentTagger maxentTagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

    String tag = maxentTagger.tagString("He half closed his eyes and searched the horizon.");
    String[] eachTag = tag.split("\\s+");

    for(int i = 0; i< eachTag.length; i++) {
      System.out.print(eachTag[i].split("_")[0] +"_"+ eachTag[i].split("_")[1]+" ");
    }*/
		
		StanfordCoreNLPWrapper coreNLPWrapper = new StanfordCoreNLPWrapper();
		//coreNLPWrapper.tag("The dog ate chocolate.");
		coreNLPWrapper.tag("He half closed his eyes and searched the horizon.");
	}






}
