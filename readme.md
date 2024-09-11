# Connecting to server
ssh -i googleCloud capioso@34.130.54.17

# Uploading jar to home from server
scp -i googleCloud target/Server-1.0.jar capioso@34.130.54.17:~

# List process if port is occuped
ps aux

# Stop job
sudo kill -9 pid