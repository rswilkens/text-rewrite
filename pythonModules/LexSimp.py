# from LexSimp_M2 import Simp_MorphoM2
from LexSimp_tense import Simp_MorphoTense
from LexSimpL4 import Simp_LexL4

file = "/mnt/data/eclipse_workspace/SimpDiscur_V2/corpus/LesTroisSots_1.conll.output.conll"
t = Simp_MorphoTense()
out = t.process(file)
l = Simp_LexL4()
out = l.process(out)

print(out)
with open(out) as input:
    sent = ""
    for ln in input:
        if "\t" in ln:
            sent += ln.split("\t")[1] + " "
        else:
            print(sent)
            sent = ""