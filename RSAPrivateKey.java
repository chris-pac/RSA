/**
 * An immutable container class that contains private exponent d and modulus n information.
 * 
 * @author cpp270
 *
 */
public class RSAPrivateKey {
  private final int privateExponent;
  private final int modulus;
  
  /**
   * Creates a new private key object.
   * 
   * @param modulus the public modulus n
   * @param privateExponent the private exponent d
   */
  public RSAPrivateKey(int modulus, int privateExponent){
    this.modulus = modulus;
    this.privateExponent = privateExponent;
  }

  /**
   * Returns the modulus n.
   *
   * @return the modulus.
   */
  public int getModulus() {
    return modulus;
  }
  
  /**
   * Returns the private exponent d.
   *
   * @return the private exponent.
   */
  public int getPrivateExponent() {
    return privateExponent;
  }

}
