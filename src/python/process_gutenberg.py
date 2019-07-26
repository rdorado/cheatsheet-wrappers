#from cheatsheet.core import transformPragraphXMLtoSentenceXML
from cheatsheet.core import Processor, POSTaggerOutputType
from cheatsheet.nltk_wrapper import NLTKWrapper, nltk_segment
from cheatsheet.spacy_wrapper import SpacyWrapper, spacy_segment
from cheatsheet.flair_wrapper import FlairWrapper
 
def main():
    
	#transformPragraphXMLtoSentenceXML("output/gutenberg/paragraphs","output/gutenberg/sentences/nltk", nltk_segment)
	#transformPragraphXMLtoSentenceXML("output/gutenberg/paragraphs","output/gutenberg/sentences/spacy", spacy_segment)
	#Processor.tagSentences("output/gutenberg/sentences/filtered", "output/gutenberg/postagged/nltk", NLTKWrapper(), POSTaggerOutputType.Text)
	#Processor.tagSentences("output/gutenberg/sentences/filtered", "output/gutenberg/postagged/spacy", SpacyWrapper(), POSTaggerOutputType.Text)
	#Processor.tagSentences("output/gutenberg/sentences/filtered", "output/gutenberg/postagged/flair", FlairWrapper(), POSTaggerOutputType.Text)
	Processor.evaluate("output/gutenberg/postagged/nltk", "output/gutenberg/postagged/opennlp")

if __name__== "__main__":
  main()
