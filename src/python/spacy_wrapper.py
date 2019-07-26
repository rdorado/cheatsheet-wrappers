from spacy.lang.en import English
import spacy
from cheatsheet.core import POSTagger, SentenceSegmenter

class SpacyWrapper(POSTagger, SentenceSegmenter):

	def __init__(self):
		self.segmenter = English()
		self.segmenter.add_pipe(self.segmenter.create_pipe("sentencizer"))

		#self.tagger = spacy.load('en')
		self.tagger = spacy.load('en_core_web_sm')

	def tag(self, sentece):
		return " ".join(map(lambda x: x.text+"_"+x.tag_, self.tagger(sentece)))

	def segment(self, sentece):
		pass

def spacy_segment(paragraph):
	nlp = English()
	
	#sentencizer = 
	nlp.add_pipe(nlp.create_pipe("sentencizer"))
	doc = nlp(paragraph)
	#print(" -->"+str(doc)+"<--")
	try:
		return list(doc.sents)	
	except:	
		return []

