import nltk
import inspect
import os
import re

from nltk.corpus import treebank
from nltk.corpus import brown
from nltk.tokenize.treebank import TreebankWordDetokenizer
from sacremoses import MosesDetokenizer
# treebank
#nltk.download('treebank')
#nltk.download('universal_tagset')
#nltk.corpus.brown.sents()


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
			sent = nltkcorpus.tagged_sents(sentid)[0]
			for word in sent:
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
				parsedFile.write(re.sub(" +"," ",str(treebank.parsed_sents(sentid)[0])).replace("\n","").replace(") (",")(")+"\n")

		rawfile.write("</document>\n")
	print("Sentences: "+str(nsents))
	print("Words: "+str(nwords))

	
makeGoldstandard(treebank, "data/treebank/sentences/sentences.xml", "data/treebank/tagged/tagged.txt", "data/treebank/parsed/parsed.txt")
