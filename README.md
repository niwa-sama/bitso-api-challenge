# Bitso Challenge
This is the application for Sonar Training about Bitso API integration challenge

### How the project is organized
The project is organized as one maven project with two modules, as following
``` [sh]
challenge
  |-> challenge-core
  \-> challenge-endpoint
```
The parent project is **challenge**.

The module **challenge-core** contains the classes for REST Service client, WebSocket client and ActiveMQ embedded instance.

The **challenge-endpoint** module contains the beans and the controllers for JavaFX aplication.

### Requirements
- Maven >= 3.3.9
- Java >= 1.8

### How to build
In order to build the executable jar of the application, execute **package** phase of maven running the following command inside **challenge** directory

``` [sh]
mvn clean package
```

After execute the previous command, maven create the **target** directory, inside that directory, **javafx-maven-plugin** create a new directory to put the JavaFX jar file there, with all jars files needed in the **lib** directory. The following is the structure of target directory created by **javafx-maven-plugin**

```
target
  |-> jfx
    |-> app
      |-> challenge-endpoint-jfx.jar
      \-> lib
        \-> [JARS FILES]
```

You can copy **challenge-endpoint-jfx.jar** file and **lib** directory (with its content) to a new directory.

### How to run
To run the JavaFX aplication, execute the following command in the location of **challenge-endpoint-jfx.jar** file

```
java -jar challenge-endpoint-jfx.jar
```

### Challenge Checklist

1. Schedule the polling of trades over REST
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** newTrade(String)
  * **Explanation:** There isn't a schedule thread to update the trade list using REST Service. The trade list is updated when a new trade is received in the WebSocket, then a request is sent to REST Service to update the trade list.

2. Request a book snapshot over REST
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** webSocketConnection() - Lines 129-162
  * **Explanation:** The request for book snapshot is executed after the WebSocket connection is successfully created.

3. Listen for diff-orders over WebSocket
  * **File name:** com.sonar.traiding.challenge.core.bitso.websocket.BitsoWebSocketClient
  * **Method name:** onWebSocketMessage(Session, String)
  * **Explanation:** The method is called when any message is sent to the WebSocket connection: trades, order-diff and order (not used), then, the message type is validated and a message is enqueued in the corresponding queue, if apply.

4. Replay diff-orders after book snapshot
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** newDiffOrder(String)
  * **Explanation:** The order book is updated when a diff-order is received in the WebSocket. As the documentation in Bitso WebSocket specify, when the sequence number in the message received is more than the previous plus one, a request for a new snapshot is sent.

5. Use config option X to request X most recent trades
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** onActionCbRecentOperSize(ActionEvent)
  * **Explanation:** The trade list is updated in real time when you select a size in the Size ComboBox of the trade list (up-right corner)

6. Use config option X to limit number of ASKs displayed in UI
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** onActionCbBestBidsSize(ActionEvent)
  * **Explanation:** The best bids/asks lists are updated in real time when you select a size in the Size ComboBox of the bids/asks lists (up-middle of window)

7. The loop that causes the trading algorithm to reevaluate
  * **File name:** com.sonar.traiding.challenge.endpoint.controller.Controller
  * **Method name:** newTrade(String)
  * **Explanation:** There isn't a loop to validate the last trades. They are validated when they are received from WebSocket.

### Comments
I am aware that there are code lines in wrong places (in com.sonar.traiding.challenge.endpoint.controller.Controller for example), but i didn't have much time to organize the source code as well as i want. My apologies about that.

If you have any question or comment, please send a message to isc.felipe.o@gmail.com
