import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.Date;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;

public class CodeBook {
          
  BufferedImage pallete;
  int palleteWidth, palleteHeight, cwWidth, cwHeight, imageType;
  int numCodeWords;
  BufferedImage[] codewords;

  String filename;

  private static ArrayList<String> hosts = new ArrayList<String>();
  private static Thread[] servers;
  private static int[] encoding;
  
  public CodeBook(){}

  public CodeBook(String fileName, int cwWidth, int cwHeight) {
    this.filename = fileName;
    this.cwWidth = cwWidth;
    this.cwHeight = cwHeight;
    int index = 0;
    try {
      pallete = ImageIO.read(new File(fileName));
      imageType = pallete.getType();
      palleteWidth = pallete.getWidth();
      palleteHeight = pallete.getHeight();
      numCodeWords = palleteWidth*palleteHeight/(cwWidth*cwHeight);
      codewords = new BufferedImage[numCodeWords];
      for(int y = 0; y < palleteHeight; y+= cwHeight) {
        for(int x = 0; x < palleteWidth; x+= cwWidth) {
          //System.out.println("x, y, cwWidth, cwHeight = "+x+", "+y+", "+cwWidth+", "+cwHeight+", ");
          codewords[index++] = pallete.getSubimage(x, y, cwWidth, cwHeight);
        }
      }
      System.out.println("Number of Codewords created = "+index);
    } 
    catch (NullPointerException e) {
      System.out.println("NullPointerException Exception: "+e.getMessage());
      System.exit(0);
    }
    catch (IOException e) {
      System.out.println("IOException Exception: "+e.getMessage());
      System.exit(0);
    }
    catch (RasterFormatException e) {
      System.out.println("RasterFormatException Exception: "+e.getMessage());
      System.exit(0);
    }
    finally {
      //numCodeWords = index;
    }
  }

  public int[] encodeImage(BufferedImage orgImage, int subImageWidth, int subImageHeight) {
    double minDistance = Double.MAX_VALUE, start, duration = 0.0;
    //int[] encoding;
    int bestMatch = 0, index = 0, count = 0, numSubImages, imageHeight, imageWidth;
    BufferedImage subimage;
    
    imageWidth = orgImage.getWidth();
    imageHeight = orgImage.getHeight();
    numSubImages = imageWidth*imageHeight/(subImageWidth*subImageHeight);
    encoding = new int[numSubImages];
    
    









    System.out.println("Loading hosts.txt...");

    readHosts("hosts.txt");
    int serverCount = hosts.size();
    servers = new Thread[serverCount];
    Registry[] registries = new Registry[serverCount];
    Compare[] stubs = new Compare[serverCount];


    System.out.println("Connecting to servers...");

    for (int i = 0; i < serverCount; i ++) {
      try {
        registries[i] = LocateRegistry.getRegistry((String)hosts.get(i));
        stubs[i] = (Compare) registries[i].lookup("Compare" + (filename.split("\\."))[0]);
      } catch (Exception e) {
        System.err.println("Client exception while locating server: " + e.toString());
        System.exit(1);
      }
    }


    System.out.println("Initializing threads...");

    for (int i = 0; i < serverCount; i++) 
      servers[i] = new Thread();






    System.out.println("Comparing image");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] imageAsBytes = null;

    for(int y = 0; y < imageHeight; y+= subImageHeight) {
      for(int x = 0; x < imageWidth; x+= subImageWidth) {
        subimage = orgImage.getSubimage(x, y, subImageWidth, subImageHeight);
        start = new Date().getTime();
        //bestMatch = compareBox(subimage, 2.0 /*Double.MAX_VALUE*/);
        try {
          baos = new ByteArrayOutputStream();
          ImageIO.write(subimage, "png", baos);
          baos.flush();
          imageAsBytes = baos.toByteArray();
          baos.close();
        } catch (Exception e) {
          System.err.println("Client Exception: " + e.toString());
          e.printStackTrace();
        }





        int j = 0;
        for (j = 0; j < serverCount; j++) {
          if (!servers[j].isAlive()) {
            break;
          }
        }
        servers[j] = new Thread(new ConnectionToServer(imageAsBytes, 2.0, stubs[j], this, x, y, imageWidth, subImageWidth, subImageHeight));
        servers[j].start();
        while(!availableServer()) {
          try{
            //Thread.sleep(1000);
          } catch (Exception e) {
            System.err.println("Client error while sleeping: " + e.toString());
            System.err.println("Continuing execution...");
          }
        }


//        bestMatch = stub.compareBox(imageAsBytes, 2.0);


        duration += new Date().getTime() - start;
//        encoding[index++] = bestMatch;
        count++;
        if (count%128==0) {
          System.out.println("Encoded subimage "+count+" of "+numCodeWords);
          System.out.println("Average time to execute compareBox(): " + duration/128.0 + "ms");
          duration = 0.0;
        }
      }
    }











/* Old Method
    Compare stub = null;
    try {
      Registry registry = LocateRegistry.getRegistry(null);
      stub = (Compare) registry.lookup("Compare" + (filename.split("\\."))[0]);
    } catch(Exception e) {
      System.err.println("Client Exception while comparing: " + e.toString());
      System.exit(1);
    }
      

    for(int y = 0; y < imageHeight; y+= subImageHeight) {
      for(int x = 0; x < imageWidth; x+= subImageWidth) {
        subimage = orgImage.getSubimage(x, y, subImageWidth, subImageHeight);
        start = new Date().getTime();
        //bestMatch = compareBox(subimage, 2.0 );

        
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(subimage, "png", baos);
          baos.flush();
          byte[] imageAsBytes = baos.toByteArray();
          baos.close();

          bestMatch = stub.compareBox(imageAsBytes, 2.0);
        } catch (Exception e) {
          System.err.println("Client Exception: " + e.toString());
          e.printStackTrace();
        }



        duration += new Date().getTime() - start;
        encoding[index++] = bestMatch;
        count++;
        if (count%128==0) {
          System.out.println("Encoded subimage "+count+" of "+numCodeWords+": "+bestMatch);
          System.out.println("Average time to execute compareBox(): " + duration/128.0 + "ms");
          duration = 0.0;
        }
      }
    }
*/

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter("encoding.txt"));
      for(int i = 0; i < numCodeWords; i++) {
        out.write(Integer.toString(encoding[i])+"\n");
      }
      out.close();
    }
    catch (IOException e) {
      System.out.println("File IOException: "+e.getMessage());
    }

    return encoding;
  }
  
  public BufferedImage decodeImage(int[] encoding, int orgImageWidth, int orgImageHeight, int subImageWidth, int subImageHeight) {
    BufferedImage decodedImage, subimage;
    decodedImage = new BufferedImage(palleteWidth, palleteHeight, imageType);
    int index = 0, pixel, c = 0;
    for(int yOffset = 0; yOffset < orgImageHeight; yOffset += subImageWidth) {
      for(int xOffset = 0; xOffset < orgImageWidth; xOffset += subImageWidth) {
        subimage = codewords[encoding[c++]];
        for(int y = 0; y < cwHeight; y++) {
          for(int x = 0; x < cwWidth; x++) {
            pixel = subimage.getRGB(x, y);
            decodedImage.setRGB(xOffset + x, yOffset + y, pixel);
          }
        }
      }
    }
    return decodedImage;
  }
  
  public int compareBox(BufferedImage subimage, double power) {
    double minDistance = Double.MAX_VALUE;
    double sum = 0.0;
    int bestMatch = 0;
    for (int i = 0; i < numCodeWords; i++) {
      sum = 0.0;
      for (int y = 0; y < cwHeight; y++) {
        for (int x = 0; x < cwWidth; x++) {
          sum += Math.pow(Math.abs(codewords[i].getRGB(x, y) -  subimage.getRGB(x, y)), power);
        }
      }
      sum += Math.pow(sum, 1.0/power);
      if (sum < minDistance) {
        minDistance = sum;
        bestMatch = i;
      }
    }
    return bestMatch;
  }

  private static boolean availableServer() {
    for (int i = 0; i < hosts.size(); i++) 
      if (!servers[i].isAlive())
        return true;
    return false;
  }

  private void readHosts(String filename) {
    try{
      // Open the file that is the first 
      // command line parameter
      FileInputStream fstream = new FileInputStream(filename);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
        // Print the content on the console
        System.out.println (strLine);
        hosts.add(new String(strLine));
      }
      //Close the input stream
      in.close();
    }catch (Exception e){//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }
  }

  public void setEncoded(int i, int val) {
    encoding[i] = val;
  }
}

