import glob
import re

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
    

filesFullText = glob.glob("/mnt/data/eclipse_workspace/SimpDiscur_V3/testGSD/*.conll.output.txt")
for f in filesFullText:
    outputfile_path = "/mnt/data/eclipse_workspace/SimpDiscur_V3/output/" + f.split("/")[-1]
    with open(f) as input_file, open(outputfile_path, "w") as outputfile:
        
        for ln in input_file:
            new_ln = join(ln)
            print(ln.strip())
            print("\t",new_ln)
            outputfile.write(new_ln+"\n")
