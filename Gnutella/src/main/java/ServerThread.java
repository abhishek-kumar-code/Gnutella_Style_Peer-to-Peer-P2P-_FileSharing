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
import java.util.ArrayList;
import java.util.Properties;

public class ServerThread extends Thread {
    String FileDirectory;
    int port_no;
    ServerSocket serverSocket=null;
    Socket socket=null;
    int peer_id;
    static ArrayList<String> peermsg;
    ServerThread(int port,String SharedDir,int peer_id)
    {
        port_no=port;
        FileDirectory=SharedDir;
        this.peer_id=peer_id;
        peermsg=new ArrayList<String>();
    }

    public void run()
    {

        try{

            serverSocket=new ServerSocket(port_no);


        }catch(IOException ie)
        {
            ie.printStackTrace();
        }

        while(true)//Accept() to create server socket for every request
        {
            try{

                socket=serverSocket.accept();
                System.out.println("*********************************************************************************");
                System.out.println("Connected to client at " + socket.getRemoteSocketAddress() + " with peer# " + peer_id);
                new ClientDownload(socket,FileDirectory,peer_id,peermsg).start();

            }catch(IOException io)
            {
                io.printStackTrace();
            }
        }
    }

}


class ClientDownload extends Thread
{
    protected Socket socket;
    String FileDirectory;
    int port;
    String filename;
    int peer_id;
    //Peer p=new Peer();
    ArrayList<String> peermsg;
    ArrayList<Thread> thread=new ArrayList<Thread>();
    ArrayList<ClientThread> peerswithfiles=new ArrayList<ClientThread>();
    int[] globalarrayofpeers=new int[20];
    int[] a=new int[20];
    int countofpeers=0;
    int messageId;
    int set=0;
    PeerMessageId p=new PeerMessageId();
    ClientDownload(Socket socket,String FileDirectory,int peer_id,ArrayList<String> peermsg)
    {
        this.socket=socket;
        this.FileDirectory=FileDirectory;
        this.peer_id=peer_id;
        this.peermsg=peermsg;
    }

    public void run()
    {
        try{
            System.out.println("Server thread for peer# " + peer_id);

            InputStream is=socket.getInputStream();
            ObjectInputStream ois=new ObjectInputStream(is);
            OutputStream os=socket.getOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(os);
            boolean peerduplicate = true;

            p=(PeerMessageId)ois.readObject();					//reading the serialized PeerMessageID class

            System.out.println("A request is received from Peer# " + p.frompeer_id);
            //System.out.println("size of arraylist "+peermsg.size());

            peerduplicate=this.peermsg.contains(p.message_id);
            if(peerduplicate==false){
                this.peermsg.add(p.message_id);

            }
            else{
                System.out.println("duplicate");
            }

            filename=p.filename;
            System.out.println("We have searched the file " + filename + " as requested.");
            //System.out.println(p.peeridsearched);
            //System.out.println("message id value "+p.message_id);
            //System.out.println("bool value "+peerduplicate);

            if(!peerduplicate)
            {

                //	System.out.println("Entered inside the loop");
                File newfind;
                File directoryObj = new File(FileDirectory);
                String[] filesList = directoryObj.list();

                for (int j = 0; j < filesList.length; j++)
                {
                    newfind = new File(filesList[j]);
                    if(newfind.getName().equals(filename))
                    {
                        globalarrayofpeers[countofpeers++]=peer_id;
                        break;
                    }
                }
                System.out.println("SUCCESSFUL: Search in local directory over");
                Properties prop = new Properties();
                String fileName = "config.properties";
                is = new FileInputStream(fileName);
                prop.load(is);

                String ab=prop.getProperty("port"+peer_id+".next");
                if(ab!=null)
                {
                    //System.out.println("entered inside the loop");
                    String[] neighbours=ab.split(",");

                    for(int i=0;i<neighbours.length;i++)
                    {
                        if(p.frompeer_id==Integer.parseInt(neighbours[i]))	//creat client thread for all neighbouring peers
                        {
                            continue;
                        }
                        int connectingport=Integer.parseInt(prop.getProperty("peer"+neighbours[i]+".port"));
                        int neighbouringpeer=Integer.parseInt(neighbours[i]);

                        System.out.println("sending to"+neighbouringpeer);
                        ClientThread cp=new ClientThread(connectingport,neighbouringpeer,filename,p.message_id,peer_id);
                        Thread t=new Thread(cp);
                        t.start();
                        thread.add(t);
                        peerswithfiles.add(cp);

                    }
                }
                //oos.writeObject(p);
                for(int i=0;i<thread.size();i++)
                {
                    ((Thread) thread.get(i)).join();
                }
                for(int i=0;i<peerswithfiles.size();i++)
                {
                    a=((ClientThread)peerswithfiles.get(i)).getarray();
                    for(int j=0;j<a.length;j++)
                    {	if(a[j]==0)
                        break;
                        globalarrayofpeers[countofpeers++]=a[j];
                    }
                }
            }

            oos.writeObject(globalarrayofpeers);


        }catch(Exception e)
        {

            e.printStackTrace();

        }
    }


}


