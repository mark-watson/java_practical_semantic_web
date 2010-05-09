all: jar_to_local_mvn jarwithdata_to_local_mvn

jar:
	rm -f -r out/production/Java_practical_semantic_web/data
	cd out/production/Java_practical_semantic_web; ls; pwd
	cd out/production/Java_practical_semantic_web; jar cvf ../../../knowledgebooks-0.2.0.jar com org

jarwithdata:
	mkdir -p out/production/Java_practical_semantic_web/data
	cp data/propername.ser out/production/Java_practical_semantic_web/data
	cp data/tags.xml out/production/Java_practical_semantic_web/data
	cd out/production/Java_practical_semantic_web; ls; pwd
	cd out/production/Java_practical_semantic_web; jar cvf ../../../knowledgebooks-with-data-0.2.0.jar com org data

jar_to_local_mvn: jar
	mvn install:install-file -Dfile=knowledgebooks-0.2.0.jar -DgroupId=self  -DartifactId=knowledgebooks -Dversion=0.2.0 -Dpackaging=jar -DgeneratePom=true

jarwithdata_to_local_mvn: jarwithdata
	mvn install:install-file -Dfile=knowledgebooks-with-data-0.2.0.jar -DgroupId=self  -DartifactId=knowledgebooks-with-data -Dversion=0.2.0 -Dpackaging=jar -DgeneratePom=true

clean:
	rm -r -f out
	rm -f knowledgebooks.jar
