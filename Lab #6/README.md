# Multicasting in JAVA

Broadcasting is inefficient as packets are sent to all nodes in the network, irrespective of whether they are interested in receiving the communication or not. This may be a waste of resources.

Multicasting solves this problem and sends packets to only those consumers who are interested. **Multicasting is based on a group membership concept**, where a multicast address represents each group.

In IPv4, any address between 224.0.0.0 to 239.255.255.255 can be used as a multicast address. Only those nodes that subscribe to a group receive packets communicated to the group.

In Java, MulticastSocket is used to receive packets sent to a multicast IP.

For this laboratory work I had to show the work of a multicast packet. For this I created a class that will be on our client side and send & receive the messages send globally.

## Client Application in JAVA

Firstly, we need to create a new DatagramSocket to use it for sending the packets using the host (230.0.0.0) and port (1721). We will also need a byte array to convert the String (message) that we receive as an argument into bytes.

```java
public class EchoClient {
static final String host = "230.0.0.0";
static final int port = 1721;
private static DatagramSocket socket;
private static MulticastReceiver multicastReceiver;


public EchoClient() throws SocketException {
    socket = new DatagramSocket();
}

private boolean multicast(String msg) throws IOException {
    byte[] buff = msg.getBytes();
    DatagramPacket packet = new DatagramPacket(buff, buff.length, InetAddress.getByName(host), port);

    socket.send(packet);
    return true;
}

public boolean isSocketClosed(){
    return socket.isClosed();
}
```

Our application will have some basic commands that will have basic functionality. We will implement 3 internal commands: *exit*, *multicast* and *disableMulticast*. *Exit* will close the application, *multicast* will enable the listener and *disableMulticast* will close the listener on all other clients. Now, we will make this class return a result response when we send a command to it. For this we will add a method. It will multicast the message if its not an internal command (like help, exit or others):

```java
public String command(String command) throws IOException {
    if (command.equals("exit")){
        socket.close();
        return "Closed connection!";
    } else if (command.equals("multicast")){
        if (multicastReceiver == null || !multicastReceiver.isAlive()) {
            multicastReceiver = new MulticastReceiver();
            multicastReceiver.start();
        } else
            return "You are already in a multicast group!";
    } else if (command.equals("help")){
        return "help - display available commands\nexit - close the socket\n" +
                "multicast - join the multicast\ndisableMulticast - stop the multicast receiver on all clients";
    } else {
        multicast(command);
    }

    return command;
}
```

By default the clients will not be listening to the multicast messages. To enable that they will have to write *multicast* command into the shell. Having all this set-up we can test our multicasting.

### Testing Application

To test the application I will be running 2 clients at the same time. We can observe that once a client enables multicast listening, he will get all the messages that are being multicast from the time he joined the group. Below, client 2 enables multicast before client1 types *"test message 1"* which results in him being able to see this message.

Client 1:

```
> help
help - display available commands
exit - close the socket
multicast - join the multicast
disableMulticast - stop the multicast receiver on all clients
> test message 1
> multicast
MulticastReceiver joined the group at /230.0.0.0
> test message 2
Multicast received: test message 2
> now I can read all of the multicast messages
Multicast received: now I can read all of the multicast messages
Multicast received: I will leave now!
> exit
Closed connection!
```

Client 2:

```
> example text :)
> multicast
MulticastReceiver joined the group at /230.0.0.0
Multicast received: test message 1
Multicast received: test message 2
Multicast received: now I can read all of the multicast messages
> I will leave now!
Multicast received: I will leave now!
> exit
Closed connection!
```