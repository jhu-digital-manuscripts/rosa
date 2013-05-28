#! /bin/sh

exec java -Drosa.collection.path=/mnt/rosecollection -Drosa.website.gwt.resources.path=target/rose/gwt/client -Drosa.website.data.path=target/project.build.finalName/data -Drosa.website.luceneindex.path=target/project.build.finalName/searchindex -Drosa.website.fsi.share=rose -Drosa.website.fsi.url=http://fsiserver.library.jhu.edu -jar target/rosa-tool-exec-jar-with-dependencies.jar "$@"


