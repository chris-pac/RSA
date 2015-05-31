/**
 * An immutable container class that contains public exponent e and modulus n information.
 * 
 * @author cpp270
 *
 */
public class RSAPublicKey {
  private final int publicExponent;
  private final int modulus;
  
  /**
   * Creates a new public key object.
   * 
   * @param modulus the public modulus n
   * @param publicExponent the public exponent e
   */
  public RSAPublicKey(int modulus, int publicExponent){
    this.modulus = modulus;
    this.publicExponent = publicExponent;
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
   * Returns the public exponent e.
   *
   * @return the public exponent.
   */
  public int getPublicExponent() {
    return publicExponent;
  }
}
