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

import java.net.*;						//Provides the classes for implementing networking applications.
import java.util.Properties;			//Provides for system input and output through data streams, serialization and the file system.
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args)  {
        int id;
        int c = 1;
        int value=1;
        int portofserver;
        int portasserver;
        Scanner scan = new Scanner(System.in);
        String serverName = "localhost";

        int peer;
        int count = 0;
        String msgid;
        String sharedDir;
        ArrayList<Thread> thread = new ArrayList<Thread>();
        ArrayList<ClientThread> peers = new ArrayList<ClientThread>();			//To store all client threads

        //Printing the available services
        System.out.println("*********************************************************************************");
        System.out.println("Please enter 1 for *Star Topology* : ");
        System.out.println("Please enter 2 for #Mesh Topology# : ");
        System.out.println("*********************************************************************************");
        int val = scan.nextInt();
        scan.nextLine();

        /*********************************************************************************************/
        if(value == 1)
        {
            try {
                System.out.println("*********************************************************************************");
                System.out.println("Please enter the peer_id: ");
                System.out.println("*********************************************************************************");
                int peer_id = scan.nextInt();			//Input value for peer_id
                scan.nextLine();
                System.out.println("*********************************************************************************");
                System.out.println("Enter the path of the shared directory (peer_id you entered in the previous step): ");			//Local Directory of the respective Peer
                System.out.println("*********************************************************************************");
                sharedDir = scan.nextLine();			//String value for shared directory

                Properties prop = new Properties();			//Properties class to read the configuration file
                //Properties class represents a persistent set of properties
                //Properties can be saved to a stream or loaded from a stream

                String fileName = "config.properties";			//Configuration file for star topology
                InputStream is = new FileInputStream(fileName);			//InputStream reads from file source and provides information byte by byte to our JAVA application
                //FileInputStream reads the contents of the file as a stream of bytes
                prop.load(is);			//Reference variable of Properties class

                portofserver = Integer.parseInt(prop.getProperty("peer" + peer_id + ".serverport"));			//Reading port number when peer acts as a server from configuration file
                ServerDownload sd = new ServerDownload(portofserver,sharedDir);			//Accepting port# (as server) and shared directory path as parameters
                sd.start();			//ServerDownload thread begins execution; the JVM calls the run method of this thread

                portasserver = Integer.parseInt(prop.getProperty("peer" + peer_id + ".port"));			//Reading port number when peer acts as a client from configuration file
                ServerThread cs = new ServerThread(portasserver,sharedDir,peer_id);			//Accepting port# (as client) and shared directory path as parameters
                cs.start();			//ServerThread thread begins execution; the JVM calls the run method of this thread



                System.out.println("*********************************************************************************");
                System.out.println("Please enter the filename(.txt) to be downloaded: ");
                System.out.println("*********************************************************************************");
                String f_name = scan.nextLine();			//Variable f_name of String class represents name of the file
                //long startTime = System.currentTimeMillis();
                ++count;
                msgid = peer_id+"."+count;
                String[] neighbours = prop.getProperty("port"+ peer_id +".next").split(",");
                for(int i = 0; i < neighbours.length; i++)
                {
                    int connectingport=Integer.parseInt(prop.getProperty("peer"+ neighbours[i]+ ".port"));
                    int neighbouringpeer=Integer.parseInt(neighbours[i]);
                    System.out.println("Sending request to peer_id: "+ neighbours[i]);			//A request sent to the peer that has the requested file
                    ClientThread cp = new ClientThread(connectingport,neighbouringpeer,f_name,msgid,peer_id);
                    Thread t = new Thread(cp);			//Creating a client thread for every neighbouring peer
                    t.start();			//ClientThread thread begins execution; the JVM calls the run method of this thread
                    thread.add(t);			//Adding threads to an ArrayList
                    peers.add(cp);			//Adding peers to an ArrayList
                }
                for(int i = 0;i < thread.size(); i++)
                {
                    try {
                        ((Thread) thread.get(i)).join();		//Wait until all the client threads are done executing
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                int[] peerswithfiles;			//part on how to send data from the ConnectingPeer

                System.out.println("Peers containing the requested file(s) are: ");
                for(int i=0;i<peers.size();i++)
                {
                    //System.out.println("Result of thread"+i);
                    peerswithfiles=((ClientThread)peers.get(i)).getarray();			//using the stored list of client threads to read all peers containing file
                    for(int j=0;j<peerswithfiles.length;j++)
                    {	if(peerswithfiles[j]==0)
                        break;
                        System.out.println(peerswithfiles[j]);
                    }
                }
                //long endTime   = System.currentTimeMillis();
                //long totalTime = endTime - startTime;
                //System.out.println(totalTime);
                System.out.println("*********************************************************************************");
                System.out.println("Enter the peer# from where to download the file: ");
                int peerfromdownload=scan.nextInt();
                int porttodownload=Integer.parseInt(prop.getProperty("peer" + peerfromdownload + ".serverport"));
                ClientasServer(peerfromdownload,porttodownload,f_name,sharedDir);
                System.out.println("3. File Transfer Confirmation: "+ f_name + " has been successfully downloaded from peer# " + peerfromdownload + " to peer# " + peer_id);
                System.out.println("*******************************OUTPUT*********************************************");
            }
            catch(IOException io)
            {
                io.printStackTrace();
            }

        }
        /*********************************************************************************************/

        /*********************************************************************************************/
        else if(value==2)
        {
            try {

                System.out.println("*********************************************************************************");
                System.out.println("Please enter the peer_id: ");
                System.out.println("*********************************************************************************");
                int peer_id=scan.nextInt();			//Input value for peer_id
                scan.nextLine();
                System.out.println("*********************************************************************************");
                System.out.println("Enter the path of the shared directory (peer_id you entered in the previous step): ");			//Local Directory of the respective Peer//Local Directory of the respective Peer
                System.out.println("*********************************************************************************");
                sharedDir=scan.nextLine();			//String value for shared directory


                Properties prop = new Properties();						//Properties class to read the configuration file
                //Properties class represents a persistent set of properties
                //Properties can be saved to a stream or loaded from a stream
                String fileName = "conff.properties";			//Configuration file for star topology
                InputStream is = new FileInputStream(fileName);			//InputStream reads from file source and provides information byte by byte to our JAVA application
                //FileInputStream reads the contents of the file as a stream of bytes
                prop.load(is);			//Reference variable of Properties class

                portofserver = Integer.parseInt(prop.getProperty("peer" + peer_id + ".serverport"));			//Reading port number when peer acts as a server from configuration file
                ServerDownload sd = new ServerDownload(portofserver,sharedDir);			//Accepting port# (as server) and shared directory path as parameters
                sd.start();			//ServerDownload thread begins execution; the JVM calls the run method of this thread

                portasserver = Integer.parseInt(prop.getProperty("peer"+peer_id+".port"));			//Reading port number when peer acts as a client from configuration file
                ServerThread cs = new ServerThread(portasserver,sharedDir,peer_id);			//Accepting port# (as client) and shared directory path as parameters
                cs.start();			//ServerThread thread begins execution; the JVM calls the run method of this thread




                System.out.println("*********************************************************************************");
                System.out.println("Please enter the filename(.txt) to be downloaded: ");
                System.out.println("*********************************************************************************");
                String f_name=scan.nextLine();
                //long startTime = System.currentTimeMillis();
                ++count;
                msgid=peer_id+"."+count;
                String[] neighbours=prop.getProperty("port" + peer_id + ".next").split(","); 	//Creating a client thread for every neighbouring peer
                for(int i=0;i<neighbours.length;i++)
                {
                    int connectingport=Integer.parseInt(prop.getProperty("peer"+neighbours[i]+".port"));
                    int neighbouringpeer=Integer.parseInt(neighbours[i]);
                    System.out.println("Sending request to peer_id: " + neighbours[i]);			//A request sent to the peer that has the requested file
                    ClientThread cp=new ClientThread(connectingport,neighbouringpeer,f_name,msgid,peer_id);
                    Thread t=new Thread(cp);			//Creating a client thread for every neighbouring peer
                    t.start();			//ClientThread thread begins execution; the JVM calls the run method of this thread
                    thread.add(t);			//Adding threads to an ArrayList
                    peers.add(cp);			//Adding peers to an ArrayList
                }
                for(int i=0;i<thread.size();i++)
                {
                    try {
                        ((Thread) thread.get(i)).join();		//Wait until all the client threads are done executing
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                int[] peerswithfiles;			//part on how to send data from the ConnectingPeer

                System.out.println("Peers containing the requested file(s) are: ");
                for(int i=0;i<peers.size();i++)
                {
                    //System.out.println("Result of thread"+i);
                    peerswithfiles=((ClientThread)peers.get(i)).getarray();			//using the stored list of client threads to read all peers containing file
                    for(int j=0;j<peerswithfiles.length;j++)
                    {	if(peerswithfiles[j]==0)
                        break;
                        System.out.println(peerswithfiles[j]);
                    }
                }
                System.out.println("*********************************************************************************");
                System.out.println("Enter the peer# from where to download the file: ");
                int peerfromdownload=scan.nextInt();
                int porttodownload=Integer.parseInt(prop.getProperty("peer" + peerfromdownload + ".serverport"));
                ClientasServer(peerfromdownload,porttodownload,f_name,sharedDir);
                System.out.println("3. File Transfer Confirmation: " + f_name + " has been successfully downloaded from peer# " + peerfromdownload + " to peer# " + peer_id);
                System.out.println("*******************************OUTPUT*********************************************");
            }
            catch(IOException io)
            {
                io.printStackTrace();
            }
        }
        /*********************************************************************************************/
    }
    /*********************************************************************************************/

    /*********************************************************************************************/
    public static void ClientasServer(int clientasserverpeerid,int clientasserverportno,String filename,String sharedDir)
    {																												//method to establish connection with Serverdownload thread to download the file
        try
        {
            Socket clientasserversocket = new Socket("localhost",clientasserverportno);
            ObjectOutputStream ooos = new ObjectOutputStream(clientasserversocket.getOutputStream());
            ooos.flush();
            ObjectInputStream oois = new ObjectInputStream(clientasserversocket.getInputStream());
            ooos.writeObject(filename);
            int readbytes = (Integer) oois.readObject();
            System.out.println("*******************************OUTPUT*********************************************");
            System.out.println("1. Number of bytes transferred: " + readbytes + " bytes");
            byte[] b=new byte[readbytes];
            oois.readFully(b);
            OutputStream fileos=new FileOutputStream(sharedDir+"//"+filename);
            BufferedOutputStream bos=new BufferedOutputStream(fileos);
            bos.write(b,0,(int) readbytes);
            System.out.println("2. Output Message: " + filename + " file has be downloaded to your directory " + sharedDir);
            bos.flush();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /*********************************************************************************************/

}
