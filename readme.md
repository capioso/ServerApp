ssh -i googleCloud capioso@34.130.54.17

javac -d target/classes src/main/java/networksTwo/*.java

jar cfm MyChatServer.jar MANIFEST.MF -C target/classes .
