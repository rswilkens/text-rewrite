import glob

def join(lstSents):
    finalSent = lstSents[0][0].split()
    for sent, position in lstSents[1:]:
        word = sent.split()[position]
        finalSent[position] = word
    return " ".join(finalSent)

# filesFullText = glob.glob("/mnt/data/eclipse_workspace/SimpDiscur_V3/corpusAlector/21_CE1_sci_orig_grotte.conll.output.conll_aux.simpLexTense_aux.simpLexL4_all")
filesFullText = glob.glob("/mnt/data/eclipse_workspace/SimpDiscur_V3/testGSD/*_aux.simpLexL4_all")


for file in filesFullText:
    print("processing", file)
    filesSimpLexText = glob.glob(file.replace("_aux.simpLexTense_aux.simpLexL4_all","_aux.simpLexTense_aux.simpLexL4_comp.simlex"))
    filesSimpLexOrig = glob.glob(file.replace("_aux.simpLexTense_aux.simpLexL4_all","_aux.simpLexTense_aux.simpLexL4_comp"))
    simpLex = {}
    if len(filesSimpLexText)==1 and len(filesSimpLexOrig)==1:
        for lnS, lnO in zip(open(filesSimpLexText[0]).readlines(), open(filesSimpLexOrig[0]).readlines()):
            sO, sS = lnS.strip().split("\t")
            s, position, word = lnO.split("\t")
            position = int(position)
            if sO not in simpLex:
                simpLex[s]=[]
            simpLex[s].append( (sS, position))
    else:
        print("no lexical simplification")
    out = file.replace(".conll_aux.simpLexTense_aux.simpLexL4_all",".txt")
    print(out)
    with open(out, "w") as outputfile:
        for sent in open(file).readlines():
            sent = sent.strip()
            if sent in simpLex:
                aux = simpLex[sent]
                if len(aux)==1:
                    sent = aux[0][0]
                else:
                    sent = join(aux)
    #         print("\t#"+sent+"#")
            outputfile.write(sent + "\n")

