import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Utility class responsible for generating prime numbers of size {@link #SIZE}
 * @author cpp270
 *
 */
public class Prime {
  // use a logger instead of System.out.println
  private static final Logger logger = 
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  
  // size of the prime in bits
  private final static int SIZE = 7;

  // number of times to run the Miller-Rabin algorithm to test for primality
  private final static int NUMTESTS = 20;

  // the prime number represented as binary format
  private Binary bitPrime;

  /**
   * Creates a new prime number with the probability of 1 - 1 / 2^{@link #NUMTESTS}
   * 
   */
  public Prime() {
    Binary b = generateRandomOddBinary(SIZE);
    
    boolean perhapsPrime = TestIfPrime(b, NUMTESTS);
    
    if (perhapsPrime) {
      // We got a prime on the first try so we need to pick a number thats not a prime and    
      // run the test till it returns false. This is done in order to generate the required trace printout.
      // Should be on the first try.
      
      Binary bNotPrime = new Binary(48, 7);
      while(TestIfPrime(bNotPrime, NUMTESTS));
    } else {
      // we did not find a prime on the first try so we need to keep looking for a prime
      while(!perhapsPrime) {
        b = generateRandomOddBinary(SIZE);
        
        perhapsPrime = TestIfPrime(b, NUMTESTS);        
      }
    }
    
    // at this point we should have a prime with the probability of 1 - 1 / 2^NUMTESTS. In our case ~ 0.9999990 
    bitPrime = new Binary(b);
  }
  
  /**
   * Returns a 32 bit integer value of the prime.
   * 
   * @return the prime number as integer
   */
  public int getAsInt() {
    return bitPrime.toInt();
  }

  /**
   * Generates a random odd integer of the specified size.
   * 
   * @param Size the size of the desired random odd number
   * @return the binary representation of the random number
   */
  private Binary generateRandomOddBinary(int Size) {
    int[] bitArray = new int[Size];
    Random r = new Random();

    // used only so that we can PRINT correct values
    int randomNumber;
    int leastSignificantBit;
    
    // set the first and last bit to 1
    bitArray[0] = 1;
    bitArray[Size-1] = 1;
    
    logger.info("Line #104");
    logger.info(String.format("%s %30s", "Random Number", "Least Significant Bit"));

    for (int i = 1; i < Size - 1; i++) {
      randomNumber = r.nextInt();
      leastSignificantBit = Binary.getLSB(randomNumber);
      
      bitArray[i] = leastSignificantBit;
      
      logger.info(String.format("%13d %30d", randomNumber, leastSignificantBit));
    }
    
    return new Binary(bitArray);
  }
  
  /**
   * The function runs the Miller-Rabin algorithm specified number of times with a new random number
   * "a"
   * 
   * 
   * @param b the number to test for primality
   * @param numberOfTestRuns the number of tests to run
   * @return true if the number <code>b</code> is perhaps a prime or false otherwise
   */
  private boolean TestIfPrime(Binary b, int numberOfTestRuns) {
    Level lvl = logger.getLevel();
    NonNegRandom r = new NonNegRandom();
    
    // our random number a which has to be  0 < a < n
    int a = 0;
    
    int n = b.toInt();
    boolean perhapsPrime = true;
    
    // turn logging off so we dont print every test
    logger.setLevel(Level.OFF);
    
    while (numberOfTestRuns > 0 && perhapsPrime) {
      // get a random number and "cut it down to size"
      a = RSAMath.mod(r.nextNonNegative(), n);
      
      // ignore a that is zero
      if (a != 0) {
        perhapsPrime = RSAMath.PrimalityTesting(a, b);                
        numberOfTestRuns--;
      }
    }

    // restore the logging level
    logger.setLevel(lvl);
    
    if (!perhapsPrime) {
      // print not a prime
      logger.info("Line #119");
    } else {
      // print perhaps prime
      logger.info("Line #123");
    }
    // re-run the last test, which can be prime or not prime, with printing enabled
    RSAMath.PrimalityTesting(a, b);      
        
    return perhapsPrime;
  }

  /**
   * Indicates whether some object is equal to this Prime object.
   * 
   * @param o the reference object with which to compare 
   * @return true if this object is the same as the argument; false otherwise 
   */
  @Override 
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }      
    if (!(o instanceof Prime)) {
      return false;
    }
    Prime p = (Prime) o;
    return p.bitPrime.toInt() == bitPrime.toInt();
  }
  
  /**
   * Computes and returns the hash code value for this prime object.
   * 
   * @return a hash code value for this prime object
   */
  @Override 
  public int hashCode() {
    int result = 17;
    result = 31 * result + bitPrime.toInt();
    return result;
  }  
  
  /**
   * This method returns friendly string representation of the prime object.
   * The exact representation are unspecified, subject to change, and should not be used to
   * infer the prime's value.
   * 
   * A typical string is composed of the integer value followed by its bit representation.
   * 
   */
  @Override 
  public String toString() {
    return String.format("%d %s", getAsInt(), bitPrime.toString());
  }

  /**
   * Helper class to generate non negative random numbers
   * 
   * @author cpp270
   *
   */
  public class NonNegRandom extends Random {
    private static final long serialVersionUID = 1L;

    // get the first 31 bits which will only give us a positive integers
    public int nextNonNegative() {      
      return next(31);
    }
  }
}
