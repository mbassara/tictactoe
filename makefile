
default: build server_jar client_jar game_jar

build:
	ant

server_jar: build
	ant -f server.xml

client_jar: build
	ant -f client.xml

game_jar: build
	ant -f game.xml
	chmod +x jar/game.jar

clean:
	rm -rf bin/ jar/
