import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a program that demonstrates the use of RSA public/private cryptosystem for 
 * creating digital certificates and authentication.
 * 
 * @author cpp270
 *
 */
public class DemoApp {
  private static final Logger logger = 
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  
  private void startDemo() {
    // generate public/private keys for Alice
    RSAKeyGen Alice = new RSAKeyGen();
    
    Level lvl = logger.getLevel(); logger.setLevel(Level.OFF); // turn off logging for Trent; its same as for Alice
    
    // generate public/private keys for Trent
    RSAKeyGen Trent = new RSAKeyGen();
    
    logger.setLevel(lvl); // re-enable logging
    
    // create digital certificate for Alice
    DigitalCertificate cert = new DigitalCertificate("Alice", Alice.getPublicKey());
    
    cert.signCertificate("Trent", Trent.getPrivateKey());

    /*
     ***  6 Alice authenticates herself to Bob
     */
    int messageU = sendCertificateToBobAndReceiveMessage(cert);
    
    Binary u = new Binary(messageU);    
    Binary hashOfU = u.hash();
    
    /*
     *  Alice signs the message u that Bob sent her by decrypting the hash of u [D(d, h(u))] with her private key
     */
    int v = RSAMath.FastExponentiation(hashOfU.toInt(), 
        Binary.toBitArray(Alice.getPrivateKey().getPrivateExponent()), Alice.getPrivateKey().getModulus());
    
    /*
     * Alice sends v = D(d, h(u)) to Bob
     */
    
    /*
     *  Bob gets Alice's public key out of her Certificate and encrypts v with it [E(e, v)]
     */
    RSAPublicKey alicePublicKey = cert.getSubjectPublicKey();
    
    // Bob encrypts v with Alice's public key using fast exponentiation
    int Eev = RSAMath.FastExponentiation(v, 
        Binary.toBitArray(alicePublicKey.getPublicExponent()), 
        alicePublicKey.getModulus());
    
    /*
     * At this point Bob would calculate his own hash of u (i.e. the hash of the message he initially sent to Alice)
     * He would then compare it to the value he got by encrypting Alice's message
     * 
     * He checks if h(u) == E(e, v)
     * 
     * Note: We do not regenerate h(u) since it would be redundant and I do not call another function
     * since all the values that need to be printed are here.
     */
    if (Eev != hashOfU.toInt()) {
      throw new IllegalStateException("Failed to authenticate Alice");
    }
    
    logger.info("Line #215");
    logger.info(String.format("u = %20d %s", messageU, u));
    logger.info(String.format("h(u) = %17d %s", hashOfU.toInt(), hashOfU));
    logger.info(String.format("v = D(d, h(u)) = %7d %s", v, new Binary(v)));
    logger.info(String.format("E(e ,v) = %14d %s", Eev, new Binary(Eev)));
    logger.info(String.format("e = %20d %s", Alice.getPublicKey().getPublicExponent(), 
        new Binary(Alice.getPublicKey().getPublicExponent())));
    logger.info(String.format("d = %20d %s", Alice.getPrivateKey().getPrivateExponent(), 
        new Binary(Alice.getPrivateKey().getPrivateExponent())));
    logger.info(String.format("n = %20d %s", Alice.getPrivateKey().getModulus(), 
        new Binary(Alice.getPrivateKey().getModulus())));
    
    logger.info("Line #219");
    // re-do the above FastExponentiation with logging ON
    logger.setLevel(Level.FINE);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.FINE);
    logger.addHandler(handler);
    
    // run it again just to produce the required trace
    Eev = RSAMath.FastExponentiation(v, 
        Binary.toBitArray(alicePublicKey.getPublicExponent()), 
        alicePublicKey.getModulus());    
  }
  
  /*
   * Alice authenticates herself to Bob by first sending the certificate.
   * 
   * Bob receives a certificate and wants to know if he's talking to Alice so he sends a message to her. 
   */
  /**
   * This function is part of 
   * 
   * @param cert the certificate
   * @return initial message used to authenticate the sender's certificate (i.e. identity)
   */
  private int sendCertificateToBobAndReceiveMessage(DigitalCertificate cert) {
    // Bob get n out of the certificate
    int n = cert.getSubjectPublicKey().getModulus();

    int k = -1;
    
    while (n != 0) {
      n >>>= 1;
      k++;
    }

    // Bob creates a random message u
    int u = randomMessageU(k);
    
    logger.info("Line #206");
    logger.info(String.format("k = %d, u = %d", k, u));

    logger.info("Line #208");
    logger.info(String.format("u = %s", new Binary(u)));    
    
    return u;
  }
  
  /**
   * Generates a random integer such that its most significant bit position is k - 1
   * and the remaining bits are random
   * 
   * @param k most significant bit position
   * @return random integer such that its most significant bit position is k - 1 and the
   * the remaining bits are random
   */
  private int randomMessageU(int k) {
    Binary u = new Binary(0, k);
    
    InputIterator it = u.createReverseIterator();
    Random r = new Random();
    
    while(it.hasNext()) {
      it.setNext(Binary.getLSB(r.nextInt()));
    }
    
    // set one at k position
    it.setLast(1);
    
    // return u as integer
    return u.toInt();
  }
    
	public static void main(String[] args) {
	  new DemoApp().startDemo();
	}

}
