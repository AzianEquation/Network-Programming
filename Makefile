# Makefile for JAVA network programs

default: http_client.class

# Http_client
http_client.class: http_client.java
	javac http_client.java

clean:
	rm -rf *.class \
	rm http_client_output
