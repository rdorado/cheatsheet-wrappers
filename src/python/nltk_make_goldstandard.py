import nltk
import inspect
import os
import re

from nltk.corpus import treebank
from nltk.corpus import brown
from nltk.corpus import cess_cat
from nltk.corpus import dependency_treebank
from nltk.corpus import conll2007
from nltk.corpus import conll2000
from nltk.corpus import conll2002
from nltk.corpus import universal_treebanks
from nltk.tokenize.treebank import TreebankWordDetokenizer
from sacremoses import MosesDetokenizer

# treebank
#nltk.download('treebank')
#nltk.download('universal_tagset')
#nltk.corpus.brown.sents()


class Tree:
	
	def __init__(self):
		self.children = []
		self.tag = None
		self.text = None
		
	def mkchild(self):
		tree = Tree()
		self.children.append(tree)
		return tree
	
	def fixPennTags(self):
		if "-" in self.tag:
			indx = self.tag.index("-")
			self.tag = self.tag[:indx]
		for child in self.children:
			child.fixPennTags()
			
	def print(self):
		print("("+self.tag+" ", end = '')
		if self.text != None: print(self.text, end = '')
		for child in self.children:
			child.print()
		print(")", end = '')
		
	def to_str(self):
		resp = "("+self.tag+" "
		if self.text != None: resp+=self.text
		for child in self.children:
			resp+= child.to_str()
		return resp+")"



def tranform(nltktree, restree):
	restree.tag = nltktree._label
	for child in nltktree:
		if isinstance(child, nltk.tree.Tree):
			tranform(child, restree.mkchild())
		elif isinstance(child, str):
			restree.text = child


def makeGoldstandard(nltkcorpus, outputRaw, outputTagged, outputParsed):
	#print( os.path.dirname(outputRaw) )
	detok = MosesDetokenizer(lang='en')
	#detok = TreebankWordDetokenizer()
	
	if not os.path.exists(os.path.dirname(outputRaw)):
		os.makedirs(os.path.dirname(outputRaw))		
		
	if not os.path.exists(os.path.dirname(outputTagged)):
		os.makedirs(os.path.dirname(outputTagged))		

	if not os.path.exists(os.path.dirname(outputParsed)):
		os.makedirs(os.path.dirname(outputParsed))	
	
	with open(outputRaw, 'w') as rawfile, open(outputTagged, 'w') as taggedFile:
		rawfile.write("<?xml version='1.0' encoding='UTF-8'?>\n")
		rawfile.write("<document>\n")
		nsents = 0
		nwords = 0	
		
		for sentid in nltkcorpus.fileids():
		
			tagged_sent = nltkcorpus.tagged_sents()
			words = []
			tagged = []
			nwrtmp = 0
			for word in tagged_sent:
				if word[1] != "-NONE-":
					words.append(word[0])
					tagged.append(word[0]+"_"+word[1])
					nwrtmp+=1	
			
			if re.match('^[a-zA-Z0-9 ,.]+$'," ".join(words)): 				
				nsents+=1
				nwords+=nwrtmp
				rawfile.write(" <paragraph>\n")
				#rawfile.write("  <sentence>"+(mt.detokenize(words)) +"</sentence>\n")
				rawfile.write("  <sentence>"+(detok.detokenize(words)) +"</sentence>\n")
				rawfile.write(" </paragraph>\n")
				taggedFile.write(" ".join(tagged)+"\n")
		rawfile.write("</document>\n")
	print("Sentences: "+str(nsents))
	print("Words: "+str(nwords))
		
'''
def makeGoldstandard(nltkcorpus, outputRaw, outputTagged, outputParsed):
	#print( os.path.dirname(outputRaw) )
	detok = MosesDetokenizer(lang='en')
	#detok = TreebankWordDetokenizer()ou
	
	if not os.path.exists(os.path.dirname(outputRaw)):
		os.makedirs(os.path.dirname(outputRaw))		
		
	if not os.path.exists(os.path.dirname(outputTagged)):
		os.makedirs(os.path.dirname(outputTagged))		
	
	if not os.path.exists(os.path.dirname(outputParsed)):
		os.makedirs(os.path.dirname(outputParsed))	
		
	with open(outputRaw, 'w') as rawfile, open(outputTagged, 'w') as taggedFile, open(outputParsed, 'w') as parsedFile:
		rawfile.write("<?xml version='1.0' encoding='UTF-8'?>\n")
		rawfile.write("<document>\n")
		nsents = 0
		nwords = 0	
		for sentid in nltkcorpus.fileids():
			words = []
			tagged = []
			nwrtmp = 0
			
			sent = nltkcorpus.tagged_sents(sentid)
			if len(sent) > 0:
				sent = sent[0] 
				for word in sent:
					if word[1] != "-NONE-":
						words.append(str(word[0]))
						tagged.append(str(word[0])+"_"+str(word[1]))
						nwrtmp+=1	
					
			
			if re.match('^[a-zA-Z0-9 ,.]+$'," ".join(words)): 				
				nsents+=1
				nwords+=nwrtmp
				rawfile.write(" <paragraph>\n")
				#rawfile.write("  <sentence>"+(mt.detokenize(words)) +"</sentence>\n")
				rawfile.write("  <sentence>"+(detok.detokenize(words)) +"</sentence>\n")
				rawfile.write(" </paragraph>\n")
				taggedFile.write(" ".join(tagged)+"\n")
				tree = Tree()
				tranform(nltkcorpus.parsed_sents(sentid)[0], tree)
				tree.fixPennTags()
				parsedFile.write(tree.to_str()+"\n")
				#	parsedFile.write(re.sub(" +"," ",str()).replace("\n","").replace(") (",")(")+"\n")

		rawfile.write("</document>\n")
	print("Sentences: "+str(nsents))
	print("Words: "+str(nwords))
'''
	
makeGoldstandard(treebank, "data/treebank/sentences/sentences.xml", "data/treebank/tagged/tagged.txt", "data/treebank/parsed/parsed.txt")
