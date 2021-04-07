from verbecc import Conjugator

class Simp_MorphoTense():
    def __init__(self):
        self.cg = Conjugator(lang='fr')

    def process(self,filePath):
#         filePath = "/mnt/data/eclipse_workspace/SimpDiscur_V2/corpus/cendrillon_1.conll.output.conll"
        with open(filePath) as input, open(filePath + "_aux.simpLexTense", "w") as ouput:
            lns = input.readlines() 
            for i, ln in enumerate(lns):
                if "\t" in ln:
                    cols = ln.split("\t")
                    if cols[-2] == "true" and "Tense=Past" in cols[5] and "VerbForm=Fin" in cols[5]:
        #                 print(i,ln.strip())
                        
                        lemma = cols[2]
                        person = "1"
                        number= "Sing"
                        for x in cols[5].split("|"):
                            if "=" in x:
                                x,y = x.split("=")
                                if x == "Number":
                                    number = y
                                elif x == "Person":
                                    person = int(y)
        #                 print(lemma,person,number)
                        if number=="Plur":
                            person += 3
                        try:
                            conjugation = self.cg.conjugate(lemma)
                            c = conjugation['moods']['indicatif']['passé-simple'][person-1]
                            if cols[1] in c:
                                conjugation = self.cg.conjugate(lemma)
                                aux, verb = conjugation['moods']['indicatif']['passé-composé'][person-1].replace("ils","").replace("elles","").replace("j'","").replace("je","").replace("tu","").replace("il","").replace("elle","").replace("nous","").replace("vous","").strip().split(" ")
                                
#                                 print(lemma,person,number,cols[1],aux, verb)
                                cols[1] = aux
                                cols[2] = ""
                                cols[3] = "AUX"
                                ln = "\t".join(cols)
                                ouput.write(ln)
                                
                                cols[1] = verb
                                cols[2] = lemma
                                cols[3] = "VERB"
                                ln = "\t".join(cols)
                                ouput.write(ln)
                        except:
        #                     print("bad lemma:",lemma)
                            ln = "\t".join(cols)
                            ouput.write(ln)
                        
        #                 isSimplePast = False
        #                 for aux in range(i-1,i-4,-1):
        #                     if aux < 0: 
        #                         break
        #                     if "\t" not in lns[aux]:
        #                         break
        #                     colsAux = lns[aux].split("\t")
        #                     if "AUX" in colsAux[3]:
        #                         isSimplePast = True
        #                     if isSimplePast:
                    else:      
                        ln = "\t".join(cols)
                        ouput.write(ln)
                else:
                    ouput.write(ln)
        return filePath + "_aux.simpLexTense"
    