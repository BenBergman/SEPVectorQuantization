public class ConnectionToServer implements Runnable {
  byte[] imageAsBytes;
  double pow;
  Compare stub;
  CodeBook client;
  int index;

  public ConnectionToServer(){}

  public ConnectionToServer(byte[] imageAsBytes, double pow, Compare stub, CodeBook client, int x, int y, int orgImageWidth, int subImageWidth, int subImageHeight) {
    this.imageAsBytes = imageAsBytes;
    this.pow = pow;
    this.stub = stub;
    this.client = client;
    index = (y/subImageHeight)*(orgImageWidth/subImageWidth) + (x/subImageWidth);
  }

  public static void main (String[] args) {
    // testing method

  }

  public void run() {
    int bestMatch = 0;
    try {
      bestMatch = stub.compareBox(imageAsBytes, pow);
    } catch (Exception e) {
      System.err.println("Thread error: " + e.toString());
      System.exit(1);
    }
    client.setEncoded(index, bestMatch);
  }
}
