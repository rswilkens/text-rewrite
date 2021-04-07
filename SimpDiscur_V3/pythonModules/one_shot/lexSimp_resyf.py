import ReSyf
import operator

class Simp_Resyf():
	def __init__(self, ReSyfPath="/mnt/data/eclipse_workspace/SimpDiscur_V2/resources/ReSyf-0.2/"):
		self.lexicalRes =  ReSyf.load(ReSyfPath)

	def selectSyn(self, wordCount, wordsRank):
		if len(wordCount)==0 and len(wordsRank)==0:
			return [None, None, None]
		mostFreq = max(wordCount.items(), key=operator.itemgetter(1))
		lowerRank = max(wordsRank.items(), key=operator.itemgetter(1))
		lowerRankMostFreq = ""
		# print(wordCount)
		# print(wordsRank)
		# print(mostFreq[1] ,lowerRank[1], int(mostFreq[1]) == int(lowerRank[1]), mostFreq, lowerRankMostFreq)
		if mostFreq[0] == lowerRank[0]:
			lowerRankMostFreq = mostFreq[0]
		return lowerRankMostFreq, lowerRank[0], mostFreq[0]
		# print(mostFreq[1] ,lowerRank[1], int(mostFreq[1]) == int(lowerRank[1]), mostFreq, lowerRankMostFreq)

	def simp(self, word, pos):
		result = ReSyf.search(self.lexicalRes, word, pos)
		#
		# result[0].lemma.feat
		# result[0].lemma.senses
		#
		wordCount = {}
		wordsRank = {}
		for i, r in enumerate(result):
			for sense in r.sense:
				#id = result[0].sense[0].id
				id = sense.id
				syno = ReSyf.get_more_simple_synonyms(self.lexicalRes, word, pos, id)
				#
				for s in list(syno.keys()):
					senseName = syno[s]["usage"]
					synos = syno[s]["synonyms"]
					# print("RESULT:",i, "senseID:", id, senseName, len(synos))
					for inst in synos:
						# print("\t",inst.get_word(), inst.get_rank(), inst.get_type(), inst.get_annotation())
						if inst.get_word() in wordCount:
							wordCount[inst.get_word()] += 1
						else:
							wordCount[inst.get_word()] = 1
						if inst.get_word() in wordsRank:
							if wordsRank[inst.get_word()] > int(inst.get_rank()):
								wordsRank[inst.get_word()] = int(inst.get_rank())
						else:
							wordsRank[inst.get_word()] = int(inst.get_rank())
		return wordCount, wordsRank

# # # print(i.get_word(), i.get_rank(), i.get_score_sense(), i.get_score_lemma(), i.get_type(), i.get_annotation())
# # word = "bal"
# # pos = "NC"
# # s = Simp_Resyf()
# # print(s.simp(word, pos))
# 
# # word = "beaucoup"
# # pos = "ADV"
# # s = Simp_Resyf()
# # print(s.simp(word, pos))
# 
# word = "bal"
# pos = "NC"
# s = Simp_Resyf()
# print(s.simp(word, pos))

