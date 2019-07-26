from flair.data import Sentence
from flair.models import SequenceTagger

from cheatsheet.core import POSTagger, SentenceSegmenter

class FlairWrapper(POSTagger):

	def __init__(self):
		self.tagger = SequenceTagger.load('pos')

	def tag(self, sentece):
		sent = Sentence(sentece)
		result = self.tagger.predict(sent)
		toklist = []
		for token in sent.tokens:
			toklist.append(token.text+"_"+token.get_tag('pos').value)
		return " ".join(toklist)


