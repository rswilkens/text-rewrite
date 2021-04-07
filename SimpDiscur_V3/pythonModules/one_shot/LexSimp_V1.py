import glob
import re
# from LexSimp_M2 import Simp_MorphoM2
from LexSimp_tense import Simp_MorphoTense
from LexSimpL4 import Simp_LexL4_v2 as Simp_LexL4

workspace_path = "/mnt/data/eclipse_workspace/SimpDiscur_V3/testGSD/"
complex_word_features = "/mnt/data/eclipse_workspace/SimpDiscur_V3/pythonModules/complex_word_features_v2.csv"

def step1(workspace_path, complex_word_features):
    files = glob.glob(workspace_path + "*.conll.output.conll")
    for file in files:
        t = Simp_MorphoTense()
        out = t.process(file)
        l = Simp_LexL4(complex_word_features)
        out = l.process(out)
        print("files created", out)

def join(lstSents):
    finalSent = lstSents[0][0].split()
    for sent, position in lstSents[1:]:
        word = sent.split()[position]
        finalSent[position] = word
    return " ".join(finalSent)

def step2(workspace_path):
    for file in glob.glob(workspace_path + "*_aux.simpLexL4_all"):
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
    
def case_sensitive_replace(string, old, new):
    """ replace occurrences of old with new, within string
        replacements will match the case of the text it replaces
    """
    def repl(match):
        current = match.group()
        result = ''
        all_upper=True
        for i,c in enumerate(current):
            if i >= len(new):
                break
            if c.isupper():
                result += new[i].upper()
            else:
                result += new[i].lower()
                all_upper=False
        #append any remaining characters from new
        if all_upper:
            result += new[i+1:].upper()
        else:
            result += new[i+1:].lower()
        return result

    regex = re.compile(re.escape(old), re.I)
    return regex.sub(repl, string)


def join(ln):
    replace = {"de le":"du", "de les":"des", "à le":"au","à les":"aux", "à lequel":"auquel", "à lesquels":"auxquels", "à lesquelles":"auxquelles",
               "de lequel":"duquel", "de lesquels":"desquels", "de lesquelles":"desquelles", }
    
    ln = " " + ln.strip() + " "
    for r in replace:
        ln = case_sensitive_replace(ln," " + r + " ", " " + replace[r] + " ")
    
    ln = ln.replace("' ", "'")
    ln = ln.replace(" -", "-")
    ln = ln.replace(" ,",",").replace(" .",".")
    return ln.strip()
    
def step3(workspace_path):
    for f in glob.glob(workspace_path + "*.conll.output.txt"):
        outputfile_path = workspace_path + f.split("/")[-1]
        with open(f) as input_file, open(outputfile_path, "w") as outputfile:
            for ln in input_file:
                new_ln = join(ln)
                print(ln.strip())
                print("\t",new_ln)
                outputfile.write(new_ln+"\n")

def run(workspace_path, complex_word_features):
    step1(workspace_path, complex_word_features)
    step2(workspace_path)
    step3(workspace_path)

run(workspace_path, complex_word_features)