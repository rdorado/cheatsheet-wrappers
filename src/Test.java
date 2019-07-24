import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

class Test {
	
	public void testTraining() {
		POSModel model = null;

		try {
			
		    InputStreamFactory inputStreamFactory = new InputStreamFactory() {
		        @Override
		        public InputStream createInputStream() throws FileNotFoundException{
		            //return TrainerFactory.class.getResourceAsStream("sentences.txt");
		        	File f = new File("all.train");
		        	return new FileInputStream(f);
		        }
		    };
		    
			ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
			ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
			POSTaggerFactory taggerFactory = new POSTaggerFactory();
			model = POSTaggerME.train("en", sampleStream, TrainingParameters.defaultParams(), taggerFactory);
			
			OutputStream modelOut = new BufferedOutputStream(new FileOutputStream("en-pos-maxent-gutenberg.model"));
			model.serialize(modelOut);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testTagger() {
		try {
		    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");
			
			InputStream inputStreamPOSTagger = new BufferedInputStream(new FileInputStream("sentences.model"));
		    POSModel posModel = new POSModel(inputStreamPOSTagger);
		    POSTaggerME posTagger = new POSTaggerME(posModel);
		    String tags[] = posTagger.tag(tokens);
		    
		    for (int i =0;i<tokens.length;i++) {
		    	System.out.print(tokens[i]+"_"+tags[i]+" ");
		    }
		    System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*@org.junit.jupiter.api.Test
	void test() throws Exception{
		String paragraph = "This is a statement. This is another statement."
			      + "Now is an abstract word for time, "
			      + "that is always flying. And my email address is google@gmail.com.";
		
	    InputStream is = new BufferedInputStream(new FileInputStream("models/en-sent.bin"));
	    SentenceModel model = new SentenceModel(is);	
	    
	    SentenceDetectorME sdetector = new SentenceDetectorME(model);
	    
	    String sentences[] = sdetector.sentDetect(paragraph);
	    
	    /*assertThat(sentences).contains(
	      "This is a statement.",
	      "This is another statement.",
	      "Now is an abstract word for time, that is always flying.",
	      "And my email address is google@gmail.com.");/
	}
	
	//@org.junit.jupiter.api.Test
	public void givenPOSModel_whenPOSTagging_thenPOSAreDetected() 
	  throws Exception {
	  
	    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");
	 
	    InputStream inputStreamPOSTagger = new BufferedInputStream(new FileInputStream("models/en-pos-maxent.bin"));
	    /*InputStream inputStreamPOSTagger = getClass()
	      .getResourceAsStream("/models/en-pos-maxent.bin");/
	    POSModel posModel = new POSModel(inputStreamPOSTagger);
	    POSTaggerME posTagger = new POSTaggerME(posModel);
	    String tags[] = posTagger.tag(tokens);
	  
	    for (String tag : tags) {
	    	System.out.println(tag);
	    }
	    //assertThat(tags, 
	    //assertThat(tags).contains("NNP", "VBZ", "DT", "NN", "VBN", "NNP", ".");
	}
	
	
	//@org.junit.jupiter.api.Test
	public void givenChunkerModel_whenChunk_thenChunksAreDetected() 
	  throws Exception {
	 
	    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	    String[] tokens = tokenizer.tokenize("He reckons the current account deficit will narrow to only 8 billion.");
	 
	    InputStream inputStreamPOSTagger = new BufferedInputStream(new FileInputStream("models/en-pos-maxent.bin"));
	    //InputStream inputStreamPOSTagger = getClass().getResourceAsStream("/models/en-pos-maxent.bin");
	    POSModel posModel = new POSModel(inputStreamPOSTagger);
	    POSTaggerME posTagger = new POSTaggerME(posModel);
	    String tags[] = posTagger.tag(tokens);
	 
	    InputStream inputStreamChunker = new BufferedInputStream(new FileInputStream("models/en-chunker.bin"));
	    //InputStream inputStreamChunker = getClass().getResourceAsStream("/models/en-chunker.bin");
	    ChunkerModel chunkerModel = new ChunkerModel(inputStreamChunker);
	    ChunkerME chunker = new ChunkerME(chunkerModel);
	    String[] chunks = chunker.chunk(tokens, tags);
	    
	    for(String chunk : chunks) {
	    	System.out.println(chunk);
	    }
	    /*assertThat(chunks).contains(
	      "B-NP", "B-VP", "B-NP", "I-NP", 
	      "I-NP", "I-NP", "B-VP", "I-VP", 
	      "B-PP", "B-NP", "I-NP", "I-NP", "O");/
	}
	
	public void traverseParse(Parse parse, String str) {
		for(Parse p : parse.getChildren()) {
			String tmp = p.getCoveredText();
			tmp+=", "+p.getHeadIndex();
			
			tmp+=", "+p.getLabel();
//			tmp+=", "+p.getText();
			tmp+=", "+p.getType();
			//tmp+=", "+p.ge
			
			System.out.println(str+"["+tmp+"]");
			traverseParse(p, str+"  ");
		}
	}
	

	@org.junit.jupiter.api.Test
	public void parsing() throws Exception {
	 
		InputStream modelIn = new BufferedInputStream(new FileInputStream("models/en-parser-chunking.bin"));
		ParserModel model = new ParserModel(modelIn);
		
		Parser parser = ParserFactory.create(model);
		//String sentence = "The quick brown fox jumps over the lazy dog .";
		String sentence = "The cellphone was broken in two days .";
		//String sentence = "The cellphone broke in two days .";
		
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
		for (Parse parse : topParses) {
			parse.show();
			//traverseParse(parse, "");
			
			//System.out.println(parse.);
			//parse.show();
			//parse.getChildCount()
			//System.out.println(parse.getChildCount());
			//System.out.println(parse.getLabel());
			//parse.showCodeTree();
			//parse.getChildren()
		}
	    /*SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	    String[] tokens = tokenizer.tokenize("He reckons the current account deficit will narrow to only 8 billion.");
	 
	    InputStream inputStreamPOSTagger = new BufferedInputStream(new FileInputStream("models/en-pos-maxent.bin"));
	    //InputStream inputStreamPOSTagger = getClass().getResourceAsStream("/models/en-pos-maxent.bin");
	    POSModel posModel = new POSModel(inputStreamPOSTagger);
	    POSTaggerME posTagger = new POSTaggerME(posModel);
	    String tags[] = posTagger.tag(tokens);
	 
	    InputStream inputStreamChunker = new BufferedInputStream(new FileInputStream("models/en-chunker.bin"));
	    //InputStream inputStreamChunker = getClass().getResourceAsStream("/models/en-chunker.bin");
	    ChunkerModel chunkerModel = new ChunkerModel(inputStreamChunker);
	    ChunkerME chunker = new ChunkerME(chunkerModel);
	    String[] chunks = chunker.chunk(tokens, tags);
	    
	    for(String chunk : chunks) {
	    	System.out.println(chunk);
	    }
	    /*assertThat(chunks).contains(
	      "B-NP", "B-VP", "B-NP", "I-NP", 
	      "I-NP", "I-NP", "B-VP", "I-VP", 
	      "B-PP", "B-NP", "I-NP", "I-NP", "O");/
	}*/
	
	

}
