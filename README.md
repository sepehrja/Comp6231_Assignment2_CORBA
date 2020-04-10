# Distributed Event Management System (DEMS)
Comp6231 Assignment2(CORBA) - Winter 2020 - Concordia University

1. how to run the project:
From the `cmd` go to your java JDK  bin directory then run `start orbd -ORBInitialPort 1050`

2. Then from the run configurations of both client.java and server.java you should add `-ORBInitialPort 1050` and `-ORBInitialHost localhost` then you will be able to run them.

> If you have the error for CORBA NameServer, then you should also run: `tnameserv -ORBInitialPort 1050` int the terminal. 

> Also in some cases in some systems eclipse cannot find CORBA related packages, you should add the latest CORBA `jar` file to the external libraries
