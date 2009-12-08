/*
 * Copyright 2004 Sun Microsystems, Inc. All  Rights Reserved.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * -Redistributions of source code must retain the above copyright  
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright 
 *  notice, this list of conditions and the following disclaimer in 
 *  the documentation and/or other materials provided with the 
 *  distribution.
 *  
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that Software is not designed, licensed or 
 * intended for use in the design, construction, operation or 
 * maintenance of any nuclear facility.
 */
	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.imageio.*;
import java.awt.image.*;

import java.io.*;
	
public class Server implements Compare {

  String codeBookFileName;
  int subImageWidth;
  int subImageHeight;
  CodeBook myCodeBook;

  public Server() {}

  public Server(String filename, int subW, int subH) {
    codeBookFileName = filename;
    subImageWidth = subW;
    subImageHeight = subH;
    myCodeBook = new CodeBook(codeBookFileName, subImageWidth, subImageHeight);
  }

  public int compareBox(byte[] imageAsBytes, double power) {
    //CodeBook myCodeBook = new CodeBook(codeBookFileName, subImageWidth, subImageHeight);
    System.out.println("Compare box...");

    BufferedImage subimage;
    try {
      subimage = ImageIO.read(new ByteArrayInputStream (imageAsBytes));
      int i = myCodeBook.compareBox(subimage, power);
      System.out.println("Compare successful: " + i);
      return i;
    } catch (IOException e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
      return -1;
    }

  }

  public static void main(String args[]) {
	
	try {
      String filename = (args.length < 1) ? "Baboon.bmp" : args[0];
	    Server obj = new Server( filename , (args.length < 2) ? 8 : Integer.parseInt(args[1]) , (args.length < 3) ? 8 : Integer.parseInt(args[2]) );
	    Compare stub = (Compare) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
      System.out.println("Registering as \"Compare" + (filename.split("\\."))[0]"\"...");
	    registry.bind("Compare" + (filename.split("\\."))[0], stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
}

