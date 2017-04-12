#TESSDATA_PREFIX=/usr/local/share/  mvn compile exec:java -Dexec.args="sample.png"
mvn compile exec:java -Dexec.mainClass="twse.brs.CaptchaSolver" -Dexec.args="sample.png"
