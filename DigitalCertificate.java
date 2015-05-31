import java.util.logging.Logger;

/**
 * The digital certificate class is used to sign and store subject's identity plus subject's public key.
 * 
 * The certificate also contains the resulting signature and the issuer's identity.
 * 
 * The certificate can be used to authenticate subject's identity.
 * 
 * @author cpp270
 *
 */
public class DigitalCertificate {
  // use a logger instead of System.out.println
  private static final Logger logger = 
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private String subject;
  private RSAPublicKey subjectPublicKey;
  private String issuer;
  
  private Binary r = null;
  private Binary signature;
  
  // format of r value; each size is number of bytes
  private static final int SUBJECT_BYTE_SIZE = 6;
  private static final int MODULUS_BYTE_SIZE = 4;
  private static final int EXPONENT_BYTE_SIZE = 4;

  /**
   * Creates an unsigned digital certificate for the specified subject.
   * 
   * @param subject the identity of the subject
   * @param subjectPublicKey the public key of the subject
   * @throws NullPointerException if <code>subject</code> or <code>subjectPublicKey</code> is null
   * @throws IllegalArgumentException if <code>subject</code> is empty
   */
  public DigitalCertificate(String subject, RSAPublicKey subjectPublicKey){
    if (subject == null) {
      throw new NullPointerException("the subject parameter must be non-null");
    }
    if (subjectPublicKey == null) {
      throw new NullPointerException("the subject's public key parameter must be non-null");
    }
    if (subject.isEmpty()) {
      throw new IllegalArgumentException("the subject parameter must not be empty");
    }
    
    this.subject = subject;
    this.subjectPublicKey = subjectPublicKey;
  }
  
  /**
   * Produces a signature for this certificate. In effect this signs this certificate with the 
   * <code>issuerPrivateKey</code>
   * 
   * The issuer private key is not retained.
   * 
   * @param issuer the identity who will sign this certificate, i.e. the signatory
   * @param issuerPrivateKey the private key used to sign this certificate
   * @throws NullPointerException if <code>issuer</code> or <code>issuerPrivateKey</code> is null
   * @throws IllegalArgumentException if <code>issuer</code> is empty
   */
  public void signCertificate(String issuer, RSAPrivateKey issuerPrivateKey) {
    if (issuer == null) {
      throw new NullPointerException("the issuer parameter must be non-null");
    }
    if (issuerPrivateKey == null) {
      throw new NullPointerException("the issuer private key parameter must be non-null");
    }
    if (issuer.isEmpty()) {
      throw new IllegalArgumentException("the issuer parameter must not be empty");
    }
    
    this.issuer = issuer;
    
    // convert the subject information into bits/binary format
    Binary bSubject = new Binary(subject, SUBJECT_BYTE_SIZE * Binary.BYTESIZE);
    
    Binary bModulus = new Binary(subjectPublicKey.getModulus(), MODULUS_BYTE_SIZE * Binary.BYTESIZE);
    
    Binary bExponent = new Binary(subjectPublicKey.getPublicExponent(), EXPONENT_BYTE_SIZE * Binary.BYTESIZE);
   
    // Tren creates a concatenation of Subject's Name and Public Key 
    r = Binary.Concatenate(bSubject, bModulus, bExponent);
    
    // Tren hashes r
    Binary hashOfR = r.hash();
    
    // Tren signs it by decrypting the hash with his private key; D(dT, h(Alice||eA))
    int s = RSAMath.FastExponentiation(hashOfR.toInt(), 
        Binary.toBitArray(issuerPrivateKey.getPrivateExponent()), issuerPrivateKey.getModulus());
    
    signature = new Binary(s);
    
    logger.info("Line #185");    
    logger.info(String.format("r    = %s", r.toString()));
    logger.info(String.format("h(r) = %s", hashOfR.toString()));
    logger.info(String.format("s    = %s", signature.toString()));

    logger.info("Line #187");    
    logger.info(String.format("h(r) = %d", hashOfR.toInt()));
    logger.info(String.format("s    = %d", s));
  }
  
  /**
   * Returns the certificate signature.
   * 
   * @return binary representation of the signature value
   */
  public Binary getSignatureValue() {
    return signature;
  }
  
  /**
   * Returns the identity of the subject.
   * 
   * @return subject identity
   */
  public String getSubject() {
    return subject;
  }
  
  /**
   * Returns the subject's public key
   * 
   * @return subject's public key
   */
  public RSAPublicKey getSubjectPublicKey() {
    return subjectPublicKey;
  }
  
  /**
   * Returns the identity of the certificate's signatory.
   * 
   * @return the certificate's issuer identity
   */
  public String getIssuer() {
    return issuer;
  }
}
