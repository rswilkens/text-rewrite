

class Simp_MorphoM2():
    def __init__(self):
        self.seletecWords = {}
        with open("/mnt/data/eclipse_workspace/SimpDiscur_V2/resources/Verbaction-1.0_seletecWords.txt") as seletecWordsFile:
            next(seletecWordsFile)
            for ln in seletecWordsFile:
                shouldReplace, verb, genre, number, noun, _, _ = ln.split("\t")
                if shouldReplace == "True":
                    self.seletecWords[verb] = noun # TODO we should verify the genre information
    
    def createSelectedWordsList(self):
        allFreqs = {}
        with open("/home/rswilkens/Downloads/tableFrequencesWikipediaFR2008-06-18.txt") as wikipediaFR2008: # from http://redac.univ-tlse2.fr/corpus/wikipedia.html
            for ln in wikipediaFR2008:
                freq, word = ln.split("\t")
                allFreqs[word] = int(freq)
        
        with open("/mnt/data/eclipse_workspace/SimpDiscur_V2/resources/Verbaction-1.0.txt") as verbaction, open("/mnt/data/eclipse_workspace/SimpDiscur_V2/resources/Verbaction-1.0_seletecWords.txt", "w") as output:
            output.write("shouldReplace\tverb\tgenre\tnumber\tnoun\tverbFreq\tnounFreq\n")
            for ln in verbaction:
                verb, genre, number, noun = ln.split("\t")
                verbFreq = 0
                nounFreq = 0
                if verb in allFreqs:
                    verbFreq = allFreqs[verb]
                if noun in allFreqs:
                    nounFreq = allFreqs[noun]
                shouldReplace = nounFreq>verbFreq
                if shouldReplace:
                    output.write(str(shouldReplace) + "\t" + verb + "\t" + genre + "\t" + number + "\t" + noun.strip() + "\t" + str(verbFreq) + "\t" + str(nounFreq) + "\n") 

    def process(self,filePath):
#         filePath = "/mnt/data/eclipse_workspace/SimpDiscur_V2/corpus/cendrillon_1.conll.output.conll"
        with open(filePath) as input, open(filePath + "_aux.simpLexM2", "w") as ouput:
            lns = input.readlines() 
            for i, ln in enumerate(lns):
                if "\t" in ln:
                    cols = ln.split("\t")
                    if "VERB" in cols[3] and "VerbForm=Inf" in cols[5]:
                        if cols[1] in self.seletecWords:
                            print("replacing:",cols[1],self.seletecWords[cols[1]])
                            cols[1] = self.seletecWords[cols[1]]
                            cols[2] = ""
                            cols[3] = "NOUNVERB"
                    ln = "\t".join(cols)
                ouput.write(ln)
        return filePath + "_aux.simpLexM2"
                