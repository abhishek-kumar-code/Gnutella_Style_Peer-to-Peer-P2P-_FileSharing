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


import java.io.Serializable;

public class PeerMessageId implements Serializable {

    String filename;
    String message_id;
    int frompeer_id;

}
