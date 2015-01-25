JFLAGS = -d ../dist -g -nowarn
JARFLAGS = cmf
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

JFILES = `find . -name '*.java' -print`

all: jar

jar: classes rc manifest
	cd dist; jar $(JARFLAGS) Manifest MapEditor.jar `find . -name '*.class' -print` resources

classes:
	cd src; $(JC) $(JFLAGS) $(JFILES)

rc:
	cp -r doc dist; cp -r src/resources dist

manifest:
	cp src/Manifest dist/Manifest

.PHONY: clean
clean:
	cd dist; find . -name '*.class' - delete

