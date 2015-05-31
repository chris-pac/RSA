/**
 * A convenience class used for returning the results from Extended Euclidean algorithm.
 *  
 * @author cpp270
 *
 */
public interface ExtendedEuclidResults {
  /**
   * Returns gcd(a, b). If the result is 1 then a and b are relatively prime
   * 
   * @return the greatest common divisor
   */
  public int getGCD();
  /**
   * Normalized Bezout's s coefficients
   * a*s + b*t = gcd(a,b)
   * 
   * @return the multiplicative inverse of a modulo b
   */
  public int getPositiveMultInverseOfAModB();
  /**
   * Normalized Bezout's t coefficients
   * a*s + b*t = gcd(a,b)
   * 
   * @return the multiplicative inverse of b modulo a
   */
  public int getPositiveMultInverseOfBModA();

}
