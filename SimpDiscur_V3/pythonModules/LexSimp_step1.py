import glob

# from LexSimp_M2 import Simp_MorphoM2
from LexSimp_tense import Simp_MorphoTense
from LexSimpL4 import Simp_LexL4_v2 as Simp_LexL4

# file = "/mnt/data/eclipse_workspace/SimpDiscur_V2/corpus/LesTroisSots_1.conll.output.conll"
file = "/mnt/data/eclipse_workspace/SimpDiscur_V3/corpusAlector/07_CE1_litt_orig_mousquetaires.conll.output.conll"
file = "/mnt/data/eclipse_workspace/SimpDiscur_V3/corpusAlector/21_CE1_sci_orig_grotte.conll.output.conll"

files = glob.glob("/mnt/data/eclipse_workspace/SimpDiscur_V3/corpusAlector/*.conll.output.conll")

for file in files:
    t = Simp_MorphoTense()
    out = t.process(file)
    l = Simp_LexL4("/mnt/data/eclipse_workspace/SimpDiscur_V3/pythonModules/complex_word_features_v2.csv")
    out = l.process(out)
    print("files created", out)

# print(out)
# with open(out) as input:
#     sent = ""
#     for ln in input:
#         if "\t" in ln:
#             sent += ln.split("\t")[1] + " "
#         else:
#             print(sent)
#             sent = ""