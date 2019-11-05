default: chat_server.class chat_client.class ClientConnection.class server.class

# chat_server
chat_server.class: chat_server.java
	javac chat_server.java
# chat client rule
chat_client.class: chat_client.java
	javac chat_client.java
# ClientConnection rule
ClientConnection.class: ClientConnection.java
	javac ClientConnection.java
# server rule
server.class: server.java
	javac server.java
# make clean rule
clean:
	rm -rf *.class \
