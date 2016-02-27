JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	GetMime.java \
	Server.java \
	Client.java \
	Request.java \
	RequestParser.java \
	RequestHandler.java \
	Response.java \
	ResponseGenerator.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
