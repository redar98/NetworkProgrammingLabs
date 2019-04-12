# Sockets API

> A **client/server application** is a piece of software that runs on a **client** computer and makes requests to a remote **server**.

It is a distributed application structure that partitions tasks or workloads between the providers of a resource or service, called servers, and service requesters, called clients.[1] Often clients and servers communicate over a computer network on separate hardware, but both client and server may reside in the same system. A server host runs one or more server programs which share their resources with clients. A client does not share any of its resources, but requests a server's content or service function. Clients therefore initiate communication sessions with servers which await incoming requests.

### Server application in JAVA

We will be using Socker and ServerSocket from *java.net* library to create a server application that will listen for any incoming connection requests from clients. To start we need to create new ServerSocket for our application.

```java
public static void main(String[] args){
    ServerSocket serverSocket = new ServerSocket(port);
    System.err.println("Started server on port " + port);
}
```

Then to process every client request we will be creating new Threads per client.

```java
public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    System.err.println("Started server on port " + port);

    while(true){
        Socket clientSocket = serverSocket.accept();

        EchoThread clientThread = new EchoThread(clientSocket);
        clientThread.start();
    }
}
```

This thread (EchoThread) is a custom class that extends main Thread class. Client requests will be processed inside these new threads:

```java
public class EchoThread extends Thread {
    private Socket socket;

    EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){
            System.err.println(this + ": Accepted connection on " +
                    socket.getRemoteSocketAddress());

            for(String receivedLine = ""; !receivedLine.equals("exit"); ){
                receivedLine = inputStream.readUTF();
                System.out.println(this + "@Client: " + receivedLine);

                interpretCommand(outputStream, receivedLine);
            }

            System.err.println(this + ": Closing connection for " +
                    socket.getRemoteSocketAddress());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

We are can confirm successfull connection to the client once we gather the DataInputStream and DataOutputStream for this socket. After that we start to listen for the inputStream of our client by calling *inputStream.readUTF()*. Our server automatically echo'es back each command from the client. Additionally there are some commands like: help, exit, connections, factorial, time, save and messages. Some of these commands have some relationships. For example calling save command makes your string to be added in server messages list and calling messages command returns all these saved messages from the server. This is done using concurrency...

```java
private void interpretCommand(DataOutputStream outputStream, String command) throws IOException {
    command = command.trim();
    String feedback = command + "\n";
    String[] commandPart = command.toLowerCase().split(" ");

    switch (commandPart[0]){
        case "help":
            feedback += "help - display commands\nexit - close the connection\n" + 
                    "connections - display active connections\n" +
                    "factorial <int> - calculate factorial of a number\n" +
                    "time - display the server time\n" +
                    "save <String> - saves a message in the server\n" +
                    "messages - gets all messages saved in the server";
            break;
        case "connections":
            feedback += "Total active connections to the server: " +
                    EchoServer.getInstance().getConnections();
            break;
        case "factorial":
            if (commandPart.length < 2) {
                feedback += "Usage: factorial <integer number>";
                break;
            }
            try {
                final int number = Integer.parseInt(commandPart[1]);
                feedback += "Factorial of " + number + " is " + factorialOf(number);
            } catch (Exception e){
                feedback += "Invalid arguments!";
            }
            break;
        case "time":
            feedback += "Current server time: " + Calendar.getInstance().getTime();
            break;
        case "save":
            if (commandPart.length < 2){
                feedback += "Usage: save <String message>";
                break;
            }
            EchoServer.getInstance().addMessage(command.substring(command.indexOf(' ') + 1));
            feedback += "Your message is saved in server!";
            break;
        case "messages":
            feedback += EchoServer.getInstance().getMessages();
            break;
    }
    outputStream.writeUTF(feedback.equals(command + "\n")? feedback: feedback + "\n");
}
```

Above is the interpretator for the commands. Now, our server is ready for making connections with multiple clients at once and manage some shared data between them.

### Client Application in JAVA

To make the client we will also be using Socket class from the *java.net* library. For this we need to indicate the host address (ip) and port. We will be using 'localhost' as our host and 1721 as our port. We will retrieve DataInputStream and DataOutputStream as well. They will be used to send and receive messages from the Server.

```java
public static void main(String[] args) throws IOException {
    Socket socket = new Socket(host, port);
    System.err.println("Connected to " + host + " on port " + port);

    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    while(true){
        String lineInput = bufferedReader.readLine();

        outputStream.writeUTF(lineInput);

        System.out.println(inputStream.readUTF());

        if (lineInput.equals("exit")) break;
    }

    System.err.println("Closing connection to " + host);
}
```

Here we will be writing and sending commands to the server using *outputStream.writeUTF()* method and receive the Server response using the *inputStream.readUTF()* method. In case if client types exit then the connection to the server will be closed.

### Testing Client-Server application

To properly test the application we first need to run the server and then the clients we want to connect with.

Client:

```
Connected to localhost on port 1721
> test
test

> help
help
help - display commands
exit - close the connection
connections - display active connections
factorial <int> - calculate factorial of a number
time - display the server time
save <String> - saves a message in the server
messages - gets all messages saved in the server

> save this message is saved in server!
save this message is saved in server!
Your message is saved in server!

> messages
messages
this message is saved in server!
```

Server:

```
Started server on port 1721
EST-11: Accepted connection on /127.0.0.1:56654
EST-11@Client: test
EST-11@Client: help
EST-11@Client: save this message is saved in server!
EST-11@Client: messages
```