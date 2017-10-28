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

If you have any question or comment, please send a message to isc.felipe.o@gmail.com
