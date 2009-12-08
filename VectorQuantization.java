import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class VectorQuantization extends Component {
          
    BufferedImage img;

    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public BufferedImage getImage() {
      return img;
    }
    public int getImageType() {
      return img.getType();
    }
    public VectorQuantization(String fileName) {
       try {
           img = ImageIO.read(new File(fileName));
       } catch (IOException e) {
       }

    }

    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(100,100);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
       }
    }

    public static void main(String[] args) {
      
      String orgImageFileName = "lena512.bmp";
      String codebookImageFileName = "Baboon.bmp";
      String OutputFilename = "";
      int subImageWidth, subImageHeight, orgImageWidth, orgImageHeight;
      
      try {
        orgImageFileName = args[0];
        codebookImageFileName = args[1];
        subImageWidth = Integer.parseInt(args[2]);
        subImageHeight = Integer.parseInt(args[3]);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
        System.out.println("Usage java VectorQuantization <Input filename> <Codebook filename> <subImageWidth> <subImageHeight>");
        System.out.println("Using defaults: Input = lena512.bmp, Codebook = Baboon.bmp, subImageWidth = 8, subImageHeight = 8");
        orgImageFileName = "lena512.bmp";
        codebookImageFileName = "Baboon.bmp";
        subImageWidth = subImageHeight = 8;
      }

      String[] temp = orgImageFileName.split("\\.");
      String[] temp1 = codebookImageFileName.split("\\.");
      OutputFilename = temp[0]+" in terms of "+temp1[0]+".bmp";

      JFrame orgFrame = new JFrame("Original Image "+orgImageFileName);
      orgFrame.addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
      VectorQuantization orgImage = new VectorQuantization(orgImageFileName);
      BufferedImage myOrgImage = orgImage.getImage();
      orgImageWidth = myOrgImage.getWidth();
      orgImageHeight = myOrgImage.getHeight();
      orgFrame.add(orgImage);
      orgFrame.pack();
      orgFrame.setVisible(true);
      
      JFrame codeBookFrame = new JFrame("CodeBook "+codebookImageFileName);      
      codeBookFrame.addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
      VectorQuantization codeBookImage = new VectorQuantization(codebookImageFileName);
      codeBookFrame.add(codeBookImage);
      codeBookFrame.pack();
      codeBookFrame.setVisible(true);
      
      System.out.println("Encoding image, ...");
      CodeBook myCodeBook = new CodeBook(codebookImageFileName, subImageWidth, subImageHeight);
      int[] encoding = myCodeBook.encodeImage(orgImage.getImage(), subImageWidth, subImageHeight);
      System.out.println("Done encoding");       

      System.out.println("decoding, ...");
      BufferedImage decodedBufferedImage = myCodeBook.decodeImage(encoding, orgImageWidth, orgImageHeight, subImageWidth, subImageHeight);
      System.out.println("Done decoding");
      try {
        ImageIO.write(decodedBufferedImage, "BMP", new File(OutputFilename));
      } catch (IOException e) {
        System.out.println("exception writing to decoded file");
      }
      JFrame DecodedFrame = new JFrame(orgImageFileName + " in terms of "+codebookImageFileName);      
      DecodedFrame.addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
      VectorQuantization decodedImage = new VectorQuantization(OutputFilename);
      DecodedFrame.add(decodedImage);
      DecodedFrame.pack();
      DecodedFrame.setVisible(true);


      //Graphics myGraphics = f.getGraphics();
      // myIA1.paint(myGraphics);
    }
}


