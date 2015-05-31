import java.util.logging.Logger;

/**
 * This class generates public/private key pair.
 * 
 * @author cpp270
 *
 */
public class RSAKeyGen {
  // use a logger instead of System.out.println
  private static final Logger logger = 
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private Prime p;
  private Prime q;
  
  private int publicKeyE;
  private int privateKeyD;
  private int modulus;
  
  /**
   * Creates a new public/private key pair
   */
  public RSAKeyGen() {
    // generate two different primes p and q
    generatePandQPrimes();
    
    // generate the public and private key: e and d; and n
    while(!genPublicKeyEPrivateKeyD(p, q)) {
      // all of the e values tried did not work so generate new p and q
      generatePandQPrimes();
    }
    
    logger.info("Line #156");
    logger.info(String.format("p = %5d %s", p.getAsInt(), new Binary(p.getAsInt())));
    
    logger.info(String.format("q = %5d %s", q.getAsInt(), new Binary(q.getAsInt())));

    logger.info(String.format("n = %5d %s", modulus, new Binary(modulus)));

    logger.info(String.format("e = %5d %s", publicKeyE, new Binary(publicKeyE)));

    logger.info(String.format("d = %5d %s", privateKeyD, new Binary(privateKeyD)));
  }
  
  /**
   * 
   * @return RSA public key
   */
  public RSAPublicKey getPublicKey() {
    return new RSAPublicKey(modulus, publicKeyE);
  }
  
  /**
   * 
   * @return RSA private key
   */
  public RSAPrivateKey getPrivateKey() {
    return new RSAPrivateKey(modulus, privateKeyD);
  }
  
  /*
   * Generates the primes p and q. Also ensures that p and q are distinct for each other.
   */
  private void generatePandQPrimes() {
    // really easy
    p = new Prime();
    q = new Prime();
    
    // need to check if they are not equal and if they are get a new prime till we have different p and q
    while (p.equals(q)) {
      q = new Prime();
    }
  }
  
  /*
   * Computes the number of positive integers smaller than n that are relatively prime with n
   * Where phi(n) = (p - 1)(q - 1)
   * 
   * @param p a prime number
   * @param q a prime number
   * 
   * @return the cardinality of the set Z*n, where n = p*q
   */
  private int Phi(Prime p, Prime q) {    
    int phiN = (p.getAsInt() - 1) * (q.getAsInt() - 1);
    
    return phiN;    
  }
  
  /*
   * Given two primes p and q this function computes public key e, private key d, and modulus n.
   * 
   * The modulus is computed by multiplying p and q, n = p*q
   * 
   * Public key e is any number between 3 < e < phi(n), where phi(n) = (p - 1)(q - 1), and
   * is also coprime with phi(n). They are coprime if gcd( phi(n), e ) = 1
   * 
   * Private key d is the modular multiplicative inverse of e mod phi(n).
   * 
   * Relative primality and multiplicative inverse is computed using Extended Euclidean algorithm.
   * 
   * @param p prime number
   * @param q prime number
   * @return true if the key pair was generated successfully or false otherwise
   */
  private boolean genPublicKeyEPrivateKeyD(Prime p, Prime q) {
    int n = p.getAsInt() * q.getAsInt();
    
    int phiN = Phi(p, q);

    // we start with number 3 and go up; e will be incremented first, so we set e to two
    int e = 2;
    
    boolean isRelativelyPrime = false;
    ExtendedEuclidResults r = null;
    
    // e has to be relatively prime with phi(n) and between 3 < e < phi(n)
    while (e < phiN && !isRelativelyPrime) {
      e++;
      
      logger.info("Line #142");
      r = RSAMath.ExtendedEuclid(phiN, e);
            
      isRelativelyPrime = r.getGCD() == 1;
      logger.info(String.format("e = %d is %srelatively prime with phi(%d) = %d. gcd(%d, %d) = %d", 
          e, isRelativelyPrime ? "" : "not ", n, phiN, phiN, e, r.getGCD()));
    }
    
    if (isRelativelyPrime) {
      modulus = n;
      publicKeyE = e;
      privateKeyD = r.getPositiveMultInverseOfBModA();
      
      logger.info("Line #152");
      logger.info(String.format("d = %d is the multiplicative inverse of %d [=e] modulo phi(%d) [=%d]", 
          privateKeyD, publicKeyE, modulus, phiN));      
    }    
    
    return isRelativelyPrime;
  }
}
