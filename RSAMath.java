import java.util.logging.Logger;

/**
 * Utility class that contains the general algorithms necessary to implement a basic RSA system.
 * 
 * This class contains only static methods that correspond the the required algorithms.
 * 
 * @author cpp270
 *
 */
public class RSAMath {
  // use a logger instead of System.out.println
  private static final Logger logger = 
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  /*
   * Static class; disable constructor
   */
  private RSAMath() {
  }
  
  /**
   * Computes the remainder of the division of a by n
   * 
   * a modulo n
   * 
   * @param a dividend
   * @param n divisor
   * @return remainder
   */
  public static int remainder(int a, int n) {
    if ( n <= 0) {
      throw new IllegalArgumentException("invalid divisor");
    }

    int q = a / n; // integer devision so result is automatically floored
    return a - n*q;
  }
  
  /**
   * Computes the remainder of the division of a by n
   * 
   * a modulo n
   * 
   * @param a dividend
   * @param n divisor
   * @return remainder
   */
  public static int mod(int a, int n) {
    return RSAMath.remainder(a, n);
  }
  
  /**
   * Computes a^x mod n
   * 
   * @param a the base
   * @param x the exponent converted to binary 
   * @param n the modulus
   * @return
   */
  public static int FastExponentiation(int a, int[] x, int n) {
    logger.fine(String.format(">> Fast Exponentiation: computing %d^%d mod %d", a, Binary.toInt(x), n));
    logger.fine(String.format("%21s %24s", "Squaring", "Multiplying"));
    logger.fine(String.format("%2s %5s %5s %21s", "i", "xi", "y", "y"));

    String out;
    
    int y = 1;
    
    for (int i = x.length-1; i >= 0; i--) {
      
      out = String.format("%2d %5d %5d^2 mod %d = ", i, x[i], y, n);
      
      // squaring
      y = RSAMath.mod( y * y, n);
      
      out = out + String.format("%d", y);
      
      if (x[i] == 1) {
        out = out + String.format("%7d x %d mod %d =", y, a, n);

        // multiplying
        y = RSAMath.mod( a * y, n);        
      }
      
      out = out + String.format(x[i] == 1 ? "%3d" : "%7d", y);
      logger.fine(out);
    }
    
    logger.fine(String.format("<< Fast Exponentiation: computed %d^%d mod %d = %d", a, Binary.toInt(x), n, y));

    return y;
  }
  
  /**
   * Computes greatest common divisor of integers a and b
   * Computes Bezout's coefficients s and t such that a*s + b*t = gcd(a,b)
   * t is the multiplicative inverse of b mod a
   * s is the multiplicative inverse of a mod b
   * 
   * @param a integer a
   * @param b integer b less than a
   * @return object containing results
   */
  public static ExtendedEuclidResults ExtendedEuclid(int a, int b) {
    if (b > a) {
      throw new IllegalArgumentException("b is greater than a");
    }
    
    int i = 0;
    int r;
    int r1 = a;
    int r2 = b;
    int q ;
    
    int s = 0;
    int s1 = 1;
    int t = 1;
    int t1 = 0;
    
    int tempS;
    int tempT;
    
    logger.info(String.format(">> Extended Euclidean algorithm for %d and %d", a, b));
    logger.info(String.format("%s %5s %5s %5s %5s %5s %5s", "i", "qi", "ri", "ri+1", "ri+2", "si", "ti"));
    
    while (r2 != 0) {
      i++;
      
      r = r1;
      r1 = r2;
      q = r / r1;
      r2 = r - r1*q;
            
      logger.info(String.format("%d %5d %5d %5d %5d %5d %5d", i, q, r, r1, r2, s, t));
      
      // save the values before recalculating
      tempS = s;
      tempT = t;
      s = s1 - q*s;
      t = t1 - q*t;
      
      // set the old t and s values
      s1 = tempS;
      t1 = tempT;
    }
    
    logger.info(String.format("<< Extended Euclidean algorithm for %d and %d", a, b));
    // need to return r1 = gcd and the multiplicative inverses
    return new RSAMath().new ExtendedEuclid(r1, s1, t1, a, b);
  }
  
  /**
   * Helper class for returning results from Extended Euclidean algorithm
   * @author cpp270
   *
   */
  private class ExtendedEuclid implements ExtendedEuclidResults{
    private int gcd;
    private int sBezoutCoefficient;
    private int tBezoutCoefficient;
    private int a, b;
    
    private ExtendedEuclid(int gcd, int s, int t, int a, int b) {
      this.gcd = gcd;
      this.sBezoutCoefficient = s;
      this.tBezoutCoefficient = t;
      this.a = a;
      this.b = b;
    }
    
    @Override
    public int getGCD(){
      return gcd;
    }

    @Override
    public int getPositiveMultInverseOfAModB() {
      if (sBezoutCoefficient < 0) {
        return b + sBezoutCoefficient;
      }
      return sBezoutCoefficient;
    }

    @Override
    public int getPositiveMultInverseOfBModA() {
      if (tBezoutCoefficient < 0) {
        return a + tBezoutCoefficient;
      }
      return tBezoutCoefficient;
    }
  }
  
  /**
   * Miller-Rabin primality test
   * 
   * a^(x-1) != 1 mod x then x is not a prime
   * 
   * @param a a random value such that 0 < a < x
   * @param x the number to be tested for primality
   * @return false if the number is not a prime or true if the number is maybe a prime
   */
  public static boolean PrimalityTesting(int a, Binary x) {
    int n = x.toInt();
    x = new Binary(n-1, x.length());
    
    if (a < 0 || a > n) {
      throw new IllegalArgumentException("a is not in 0 < a < x");
    }
    Iterator it = x.createReverseIterator();
    
    int y = 1;
    int z = y;
    
    /*
     *  these variables are only used to print in the format show in class and
     *  are irrelevant to the workings of the algorithm. If anything they make it harder to read.
     */
    int i = x.length();
    int y1;
    int xi;

    logger.info(String.format(">> Miller-Rabin for n = %d and a = %d", n, a));
    logger.info(String.format("%s %5s %5s %5s %5s", "i", "xi", "z", "y", "y"));

    while (it.hasNext()) {
       z = y;
       y = RSAMath.mod( y * y, n);
       
       y1 = y; // for printing only
       i--; // for printing only
       
       if (y == 1 && z != 1 && z != (n - 1)) {
         logger.info(String.format("<< Miller-Rabin says: %d is not a prime.", n));
         return false;
       }
       
       xi = it.next();
       if (xi == 1) {
         y = RSAMath.mod( y * a, n);
       }
       
       logger.info(String.format("%d %5d %5d %5d %5d", i, xi, z, y1, y));
    }
    
    if ( y != 1 ) {
      logger.info(String.format("<< Miller-Rabin says: %d is not a prime.", n));
      return false;
    }
        
    logger.info(String.format("<< Miller-Rabin says: %d is perhaps a prime.", n));
    return true;
  }
}
