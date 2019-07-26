from nltk.tokenize import sent_tokenize
import nltk

from cheatsheet.core import POSTagger, SentenceSegmenter

class NLTKWrapper(POSTagger, SentenceSegmenter):

	def __init__(self):
		pass

	def tag(self, sentece):
		return " ".join(map(lambda x:x[0]+"_"+x[1], nltk.pos_tag(nltk.word_tokenize(sentece))))
		#return sentece

	def segment(self, sentece):
		return nltk.tokenize.sent_tokenize(sentece)

def nltk_segment(sentence):
	return sent_tokenize(sentence)

