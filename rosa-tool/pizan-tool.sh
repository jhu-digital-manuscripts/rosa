#! /bin/sh

exec java -Drosa.collection.path=/mnt/pizancollection -Drosa.website.gwt.resources.path=target/pizan/gwt/client -Drosa.website.data.path=target/project.build.finalName/data -Drosa.website.luceneindex.path=target/project.build.finalName/searchindex -Drosa.website.fsi.share=pizan -Drosa.website.fsi.url=http://fsiserver.library.jhu.edu -jar target/rosa-tool-exec-jar-with-dependencies.jar "$@"


