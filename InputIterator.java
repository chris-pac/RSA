/**
 * Input iterator used to iterate through a sequence of integers and modify each value in
 * when desired.
 * 
 * @author cpp270
 *
 */
public interface InputIterator extends Iterator {
  /**
   * Sets the next value in a sequence to <code>n</code> and advances to the next item.
   * 
   * @param n the next integer value in a sequence
   */
  void setNext(int n);
  
  /**
   * Sets the first value (e.g index zero) to <code>n</code>
   * 
   * @param n the value to set at first position in the sequence
   */
  void setFirst(int n);
  
  /**
   * Sets the last value (e.g length - 1) to <code>n</code>
   * @param n the value to set at last position in the sequence
   */
  void setLast(int n);
}
