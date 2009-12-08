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

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.imageio.*;
import java.awt.image.*;

import java.io.*;

public class Client {

    private Client() {}

    public static void main(String[] args) {

      String host = (args.length < 1) ? null : args[0];

        VectorQuantization theImage = new VectorQuantization("lena512.bmp");
        BufferedImage orgImage = theImage.getImage();
        BufferedImage subimage = orgImage.getSubimage(0, 0, 8, 8);

      
      try {
          Registry registry = LocateRegistry.getRegistry(host);
          Compare stub = (Compare) registry.lookup("Compare");

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(subimage, "png", baos);
          baos.flush();
          byte[] imageAsBytes = baos.toByteArray();
          baos.close();



          int response = stub.compareBox(imageAsBytes, 2.0);
          System.out.println("response: " + response);
      } catch (Exception e) {
          System.err.println("Client exception: " + e.toString());
          e.printStackTrace();
      }
    }
}
