# ChatAppConcurrency
A Client - Server Application for the implementation of Concurrency with Java

##Instructions
In order to run both programs, it is need to download the zip containing both the Server and Client jar. The Server jar has as name Server, however the Client jar is named ConcurrencyFinalProject. 
<i><strong>Important NOTE:</strong> There is a requirement of having at least version 1.8 of Java Runtime in order to run the program.</i>
Server:
* In order for the server to run, just open the server jar in a console (running the command java -jar Server.jar) and with that you should see the server waiting for client connections.

Client:
* In order for the Client to run, the server must be up and running. Doing so, open Client jar in a console (running the command java -jar ConcurrencyFinalProject.jar), which will lead to a welcome message prompting the user for the server’s local ip address. If the address is found, the login GUI should pop up and then entering a username should lead to the Chat Room GUI. If the client wants to disconnect, simply close the window (either the Chat Room or the Login).
