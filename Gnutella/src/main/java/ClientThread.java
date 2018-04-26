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

import java.net.Socket;
import java.io.IOException;
import java.io.*;

public class ClientThread extends Thread {

    int portofconnection;
    int peertoconnect;
    String filetodownload;
    Socket socket=null;
    int[] arrayofpeerswithfile;
    PeerMessageId p=new PeerMessageId();
    String msgid;
    int frompeer_id;

    public ClientThread(int portofconnection,int peertoconnect,String filetodownload,String msgid,int frompeer_id)
    {
        this.portofconnection=portofconnection;
        this.peertoconnect=peertoconnect;
        this.filetodownload=filetodownload;
        this.msgid=msgid;
        this.frompeer_id=frompeer_id;
    }

    public void run()
    {
        try{
            //System.out.println("got the request ");
            socket=new Socket("localhost",portofconnection);
            OutputStream os=socket.getOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(os);
            InputStream is=socket.getInputStream();
            ObjectInputStream ois=new ObjectInputStream(is);
            p.filename=filetodownload;							//writing the data to be serialized and send to the server thread
            p.message_id=msgid;
            p.frompeer_id=frompeer_id;

            oos.writeObject(p);

            arrayofpeerswithfile=(int[])ois.readObject();
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
        catch(ClassNotFoundException cp)
        {
            cp.printStackTrace();
        }

    }

    public int[] getarray()
    {
        return arrayofpeerswithfile;
    }
}

