jar:
	cd out/production/Java_practical_semantic_web; ls; pwd
	cd out/production/Java_practical_semantic_web; jar cvf ../../../knowledgebooks.jar com org

clean:
	rm -r -f out
	rm -f knowledgebooks.jar
