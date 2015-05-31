/**
 * Output Iterator used to iterate through integer values.
 * 
 * @author cpp270
 *
 */
public interface Iterator {
  /**
   * 
   * @return true if there is a next item or false otherwise
   */
  boolean hasNext();
  /**
   * 
   * @return the next integer number in a sequence
   */
  int next();
}

