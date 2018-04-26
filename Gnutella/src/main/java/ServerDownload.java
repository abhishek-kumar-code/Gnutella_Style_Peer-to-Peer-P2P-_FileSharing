/*
 * Copyright © 2018 by Abhishek Kumar
 *
   All rights reserved. No part of this code may be reproduced, distributed, or transmitted
   in any form or by any means, without the prior written permission of the programmer.
 *
 *
   CS 5352 Advanced Operating Systems and Design
   Project Title: Gnutella style peer-to-peer (P2P) file sharing system in JAVA
 *
 * */

import java.io.*;
import java.net.*;

public class ServerDownload extends Thread{

    int portno;
    String FileDirectory;
    ServerSocket serverSocket;
    Socket socket;
    ServerDownload(int portno,String FileDirectory)
    {
        this.portno=portno;
        this.FileDirectory=FileDirectory;
    }
    public void run()
    {
        try{
            serverSocket=new ServerSocket(portno);
            System.out.println("Thread is in wait mode...");
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
        try{
            socket=serverSocket.accept();

        }catch(IOException io)
        {
            io.printStackTrace();
        }
        new Downloading(socket,portno,FileDirectory).start();
    }
}

class Downloading extends Thread
{

    int portno;
    String sharedDirectory;
    Socket socket;
    String filename;
    Downloading(Socket socket,int portno,String FileDir)
    {
        this.socket=socket;
        this.portno=portno;
        this.sharedDirectory=FileDir;
    }

    public void run()
    {
        try{

            InputStream is=socket.getInputStream();		    //Connecting Client acting as a server to the file requesting Client
            ObjectInputStream ois=new ObjectInputStream(is);
            OutputStream os=socket.getOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(os);
            filename=(String)ois.readObject();					//Fileaname to be downloaded
            String FileLocation;
            while(true)
            {
                File myFile = new File(sharedDirectory+"//"+filename);
                long length = myFile.length();
                byte [] mybytearray = new byte[(int)length];		//Sending file length of the file to be downloaded to the client
                oos.writeObject((int)myFile.length());
                oos.flush();
                FileInputStream fileInSt=new FileInputStream(myFile);
                BufferedInputStream objBufInStream = new BufferedInputStream(fileInSt);
                //transferring the contents of the file as stream of bytes
                objBufInStream.read(mybytearray,0,(int)myFile.length());
                System.out.println("transferring file of " + mybytearray.length + " bytes");
                oos.write(mybytearray,0,mybytearray.length);
                oos.flush();
            }
        }catch(Exception e)
        {

            e.printStackTrace();

        }
    }

}