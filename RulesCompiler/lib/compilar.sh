java -jar jflex-full-1.7.0.jar ../grammar.lex 
mv ../Lexer.java ../src/ 
java -jar java-cup-11b.jar -parser Parser -symbols Sym ../grammar.cup 
mv *.java ../src/ 
echo "all done"