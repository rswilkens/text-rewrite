from lexSimp_resyf import Simp_Resyf

# filePath = "/mnt/data/eclipse_workspace/SimpDiscur_V2/corpus/cendrillon_1.conll.output.conll"

class Simp_LexL4():
    def __init__(self):
        self.ls = Simp_Resyf()
    def process(self,filePath):
        with open(filePath) as input, open(filePath + "_aux.simpLexL4", "w") as ouput:
            lns = input.readlines() 
            for i, ln in enumerate(lns):
                if "\t" in ln:
                    cols = ln.split("\t")
                    pos = cols[3]
                    simp = [cols[1]]*3
                    if cols[-2] == "false":
                        pass
                    elif pos=="ADV" and (cols[1]=="ne" or cols[1]=="n'" or cols[1]=="pas"):
                        pass
                    elif "false" not in cols[-2] and (pos=="NOUN" or pos=="VERB" or pos=="ADJ" or pos=="ADV"):
                        lemma=cols[2].replace("\"","")
                        if pos=="NOUN":
                            pos = "NC"
#                         elif pos=="VERB":
#                             pos = "VER"
                        f, r = self.ls.simp(lemma, pos)
                        lowerRankMostFreq, lowerRank, mostFreq = self.ls.selectSyn(f,r)
                        if lowerRank != None:
                            if lowerRankMostFreq == '':
                                lowerRankMostFreq = cols[1]
                            simp = [lowerRankMostFreq, lowerRank, mostFreq]
                            cols[2] = lowerRankMostFreq
                            if lowerRankMostFreq != cols[1]:
                                cols[1] = lowerRankMostFreq + "["+ cols[1] + "]"
                            cols[3] += "toCheck"
                    ln = "\t".join(cols)
                ouput.write(ln)
        return filePath + "_aux.simpLexL4"
    
class Simp_LexL4_v2():
    def __init__(self, complex_words_path):
        self.complex_words = [ln.split(",")[0] + "\t" + ln.split(",")[1]  for ln in open(complex_words_path).readlines()]
        
#         self.ls = Simp_Resyf()
    def process(self,filePath):
        with open(filePath) as input: #, open(filePath + "_aux.simpLexL4", "w") as ouput:
            lns = input.readlines() 
            sent_words = []
            sent = []
            output_sents = []
            for i, ln in enumerate(lns):
                if "\t" in ln:
                    cols = ln.split("\t")
                    pos = cols[3]
                    simp = [cols[1]]*3
                    surface = cols[1].replace("\"","")
                    sent_words.append(surface)
                    if cols[-2] == "false":
                        pass
                    elif pos=="ADV" and (cols[1]=="ne" or cols[1]=="n'" or cols[1]=="pas"):
                        pass
                    elif "false" not in cols[-2] and (pos=="NOUN" or pos=="VERB" or pos=="ADJ" or pos=="ADV"):
                        lemma=cols[2].replace("\"","")
                        surface = cols[1].replace("\"","")
                        if pos=="NOUN":
                            pos = "NOM"
                        elif pos=="VERB":
                            pos = "VER"
                        if surface.lower() + "\t" + pos in self.complex_words:
#                             sent.append((sent_words,"comp",len(sent_words))) # complex
                            sent.append(len(sent_words)-1) # complex
#                         else:
#                             sent.append((sent_words,"simp",len(sent_words)))
                        
                        
                        
# #                         _____________________
#                         if pos=="NOUN" or pos=="NOM":
#                             pos = "NC"
# #                         elif pos=="VERB":
# #                             pos = "VER"
#                         f, r = self.ls.simp(lemma, pos)
#                         lowerRankMostFreq, lowerRank, mostFreq = self.ls.selectSyn(f,r)
#                         if lowerRank != None:
#                             if lowerRankMostFreq == '':
#                                 lowerRankMostFreq = cols[1]
#                             simp = [lowerRankMostFreq, lowerRank, mostFreq]
#                             cols[2] = lowerRankMostFreq
#                             if lowerRankMostFreq != cols[1]:
#                                 cols[1] = lowerRankMostFreq + "["+ cols[1] + "]"
#                             cols[3] += "toCheck"
#                     ln = "\t".join(cols)
                else:
                    output_sents.append((sent_words,sent))
                    sent = []
                    sent_words = []
#                 ouput.write(ln)
        with open(filePath + "_aux.simpLexL4_all", "w") as ouputfile_all, open(filePath + "_aux.simpLexL4_comp", "w") as ouputfile_comp:
            for words, comp in output_sents:
                print("##########################")
    #             print(aux)
                ouputfile_all.write(" ".join(words) + "\n")
                for a in comp:
                    ouputfile_comp.write(" ".join(words) + "\t" + str(a)  + "\t" + words[a] + "\n")
                    print(a, words[a], words)
            
        return filePath + "_aux.simpLexL4_all", filePath + "_aux.simpLexL4_comp" # filePath + "_aux.simpLexL4"
    