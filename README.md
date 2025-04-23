# java-server-with-clients
Multi-client server in Java using sockets
# ☕ Java Multi-Client Chat Server

A multithreaded TCP chat application built in Java. The server supports multiple clients connecting simultaneously, private messaging, user exclusion from messages, and banned phrases filtering — all configurable via a `.txt` file.

---

##  Project Structure

📁 java-server-with-clients/ ├── src/ │ ├── Server/ │ │ └── Server.java # Main server logic with thread-based client handling │ ├── Client/ │ │ └── Client.java # Simple client with real-time chat capabilities ├── server_config.txt # Server settings (port, name, banned phrases) ├── README.md # You're here!


---

##  Features

-  **Multithreaded server**: handles many clients in parallel  
-  **Private messages**: `/to <username> <message>`  
-  **Exclusion-based broadcast**: `/exclude <username> <message>`  
-  **Banned phrases**: messages containing banned words are blocked  
-  **Configurable setup** via `server_config.txt`  
-  **Client list announcement** on join  
-  Based on **Java Sockets API** for TCP communication

---

##  Technologies Used

- Java SE 8+
- Java I/O & Networking (`java.io`, `java.net`)
- Threads & Synchronization
- Properties & File Configuration

---

## ⚙ Configuration File: `server_config.txt`

Example:

port=1515 serverName=ChatZone bannedPhrases=spam,ads,curse


---

##  How to Run

### 1. Clone the repository

```bash
git clone https://github.com/Desstori15/java-server-with-clients.git
cd java-server-with-clients
2. Compile the server & client
javac src/Server/Server.java
javac src/Client/Client.java
3. Start the server
java src.Server.Server server_config.txt
4. Start one or more clients in new terminals
java src.Client.Client
🧠 Example Commands (Client)

/to John Hello! → Send private message to user John
/exclude Mike Hello all (except Mike)! → Broadcast excluding one user
/banned → View the list of banned phrases
🔐 Banned Phrase Filtering

If a user sends a message containing any banned phrase (defined in server_config.txt), the message is blocked and a warning is shown.

💡 What I Learned

Java networking with ServerSocket and Socket
Real-time input/output using BufferedReader and PrintWriter
Managing concurrent clients with threads
Centralized server configuration through .txt files
Designing simple communication protocols
🙋‍♂️ Author

Vladislav Dobriyan
@Desstori15
