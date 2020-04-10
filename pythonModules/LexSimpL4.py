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