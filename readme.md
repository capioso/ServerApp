# Commands
mvn clean
mvn compile
mvn package

# Connecting to server
ssh -i googleCloud capioso@34.130.54.17

# Uploading jar to home from server
scp -i googleCloud target/Server-1.0.jar capioso@34.130.54.17:~

# List process if port is occupied
ps aux

# Stop job
sudo kill -9 pid

# port
10852

# Key store password
j@L9DZQ6y=3"

```shell
Generando par de claves RSA de 2.048 bits para certificado autofirmado (SHA384withRSA) con una validez de 365 días
        para: CN=Alexis Segales, OU=Jala University, O=Jala University, L=Oruro, ST=Bolivia, C=BO
```

# Generate certificate
keytool -genkeypair -alias server-cert -keyalg RSA -keystore serverkeystore.jks -keysize 2048 -validity 365

# Check keystore
keytool -list -keystore serverkeystore.jks

# Get cert file
keytool -export -alias server-cert -file server.cer -keystore serverkeystore.jks
 
Move the server.cer to client app and create the truststore