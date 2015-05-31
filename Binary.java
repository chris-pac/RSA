import java.util.Arrays;

/**
 * Utility class used for representing and creating a bit sequence.
 * 
 * @author cpp270
 *
 */
public class Binary {
  private int[] bitArray;
  public static final int BYTESIZE = 8;

  /**
   * Creates a binary object that represents the integer <code>n</code>
   * 
   * @param n the number to be converted to bit value
   */
  public Binary(int n) {
    this.bitArray = Binary.toBitArray(n);
  }
  
  /**
   * Creates a binary object that represents the integer <code>n</code> 
   * of maximum length <code>Size</code>  
   * 
   * @param n the number to be converted to bit value
   */
  public Binary(int n, int Size) {
    this.bitArray = Binary.toBitArray(n, Size);
  }
  /**
   * Creates a copy of the binary <code>b</code>
   * @param b a binary object
   */
  public Binary(Binary b) {
    this.bitArray = b.toIntArray();
  }

  /**
   * Creates a binary object equal to the bit array
   * @param bitArray bit array
   */
  public Binary(int[] bitArray) {
    this.bitArray = Arrays.copyOf(bitArray, bitArray.length);
  }

  /**
   * Creates a bit representation of the string <code>s</code> and length
   * <code>Size</code>
   * 
   * @param s the string to convert to binary representation
   * @param Size maximum length of the bit sequence
   */
  public Binary(String s, int Size) {
    this.bitArray = Binary.toBitArray(s, Size);
  }
  
  /**
   * Computes the hash value of this binary object.
   * 
   * @return the hash value of this object
   * @see #hash(int[])
   */
  public Binary hash() {
    return new Binary(Binary.hash(bitArray));
  }
  
  /**
   * Partitions the bit array <code>v</code> into one {@link #BYTESIZE} long chunks and
   * computes their bitwise exclusive OR
   * 
   * @param v value to be hashed
   * @return hash value of <code>v</code> as a bit array of one {@link #BYTESIZE} long
   */
  public static int[] hash(int[] v) {
    // return value initialized by JVM to 0s
    int[] h = new int[BYTESIZE];
    
    int j;
    for(int i = 0; i < v.length; i++) {
      //j = i % BYTESIZE;
      j = RSAMath.mod(i, BYTESIZE);
      h[j] = v[i] ^ h[j];
    }
    
    return h;
  }
  
  /**
   * Converts this Binary to an int value.
   * @return integer representation of this binary
   */
  public int toInt() {
    return Binary.toInt(this.bitArray);
  }

  /**
   * Returns the length of the bit sequence
   * @return bit sequence length
   */
  public int length() {
    return this.bitArray.length;
  }

  /**
   * This method returns the string representation of this binary object.
   * The format of the string is a sequence of 1's and 0's. The length varies based on the
   * size of this binary.
   * 
   * 
   * @return a string representation of this binary object which can be an empty string
   */
  @Override 
  public String toString() {
    StringBuilder b = new StringBuilder();
    Iterator it = createReverseIterator();
    
    while(it.hasNext()) {
      b.append(it.next());
    }
    
    return b.toString();
  }
  
  /**
   * Converts this Binary to a new int array.
   * 
   * @return a newly allocated int array whose length is the length of this binary 
   * and whose contents are initialized to contain the int sequence represented by this binary.
   */
  public int[] toIntArray() {
    return Arrays.copyOf(bitArray, bitArray.length);
  }

  /**
   * convert integer to bit array
   * 
   * @param n value to be converted
   * @param Size the maximum length
   * @return bit array
   */
  public static int[] toBitArray(int n, int Size) {
    if (n > ((1 << Size) - 1) && Size != Integer.SIZE) {
      throw new IllegalArgumentException("integer too big");
    }
    
    if (Size < 0 || n < 0) {
      throw new IllegalArgumentException("none negative arguments required");
    }    
    
    int[] bitArray = new int[Size];
    
    for (int i = 0; i < Size; i++) {
      bitArray[i] = n & 0x1;      
      n >>>= 1;
    }
    
    return bitArray;
  }
  
  /**
   * Added for completeness
   * 
   * @param n value to be converted
   * @return bit array representing an integer 
   */
  public static int[] toBitArray(int n) {
    return Binary.toBitArray(n, Integer.SIZE);
  }
  
  /**
   * Convert bit array to integer
   * 
   * @param bitArray
   * @return 32 bit integer
   */
  public static int toInt(int[] bitArray) {
    if (bitArray.length > Integer.SIZE) {
      throw new IllegalArgumentException("value too big for integer type");
    }
    
    int n = 0;
    for (int i = 0; i < bitArray.length; i++) {
      n = n ^ (bitArray[i] << i);
    }
        
    return n;    
  }

  /**
   * convert string to bit array
   * 
   * @param s value to be converted
   * @param Size the maximum length
   * @return bit array
   */
  public static int[] toBitArray(String s, int Size) {
    if (s.length() * BYTESIZE > Size) {
      throw new IllegalArgumentException("string too big");
    }
        
    if (Size < 0 || s == null) {
      throw new IllegalArgumentException("none negative or null arguments required");
    }    
    
    int[] bitArray = new int[Size];
    
    // the byte arrays is in reverse order i.e. e c i l A
    byte[] strByte = s.getBytes();    

    // we want to put the last letter in the first byte
    // so we need to start the strByte in decreasing order but the output array in increasing order
    int x = 0;
    for (int j = strByte.length - 1; j >= 0; j--, x++) {
      byte b = strByte[j];
      
      for (int i = x * BYTESIZE; i < (x+1) * BYTESIZE; i++) {
        bitArray[i] = b & 0x1;      
        b >>>= 1;
      }      
    }
    
    return bitArray;
  }
  
  /**
   * Computes the least significant bit of an integer
   * 
   * @param n an integer value
   * @return 0 or 1
   */
  public static int getLSB(int n) {
    return n & 0x1;
  }

  /**
   * Concatenates multiple Binary bit sequences
   * 
   * @param binaries
   * @return a new Binary object
   */
  public static Binary Concatenate(Binary... binaries) {
    int len = 0;
    for (Binary b : binaries) {
      len += b.length();
    }
    
    Binary bConc = new Binary(0, len);
    InputIterator itConc = bConc.createIterator();
    
    for (int i = binaries.length - 1; i >= 0; i--) {
      Iterator itB = binaries[i].createIterator();
      
      while(itB.hasNext()) {
        itConc.setNext(itB.next());
      }      
    }    
    
    return bConc;    
  }
  /**
   * Creates a reverse iterator for this binary value
   * 
   * @return a new reverse iterator
   */
  public InputIterator createReverseIterator() {
    return new ReverseBinaryIterator(bitArray);
  }

  /**
   * Creates a forward iterator
   * 
   * @return a new forward iterator
   */
  public InputIterator createIterator() {
    return new BinaryIterator(bitArray);
  }

  /**
   * A reverse iterator
   * @author cpp270
   *
   */
  private class ReverseBinaryIterator implements Iterator, InputIterator {
    private int[] bitArray;
    int position = 0;
    
    private ReverseBinaryIterator(int[] bitArray) {
      this.bitArray = bitArray;
      this.position = bitArray.length;
    }
    
    @Override
    public boolean hasNext() {
      return position > 0;
    }

    @Override
    public int next() {
      position--;
      return bitArray[position];
    }

    @Override
    public void setNext(int n) {
      position--;
      bitArray[position] = n;
    }

    @Override
    public void setFirst(int n) {
      bitArray[0] = n;      
    }

    @Override
    public void setLast(int n) {
      bitArray[bitArray.length-1] = n;      
    }
  }

  /**
   * A forward iterator
   * 
   * @author cpp270
   *
   */
  private class BinaryIterator implements Iterator, InputIterator {
    private int[] bitArray;
    int position = 0;
    
    private BinaryIterator(int[] bitArray) {
      this.bitArray = bitArray;
      this.position = 0;
    }
    
    @Override
    public boolean hasNext() {
      return position < bitArray.length;
    }

    @Override
    public int next() {
      return bitArray[position++];
    }

    @Override
    public void setNext(int n) {
      bitArray[position++] = n;
    }

    @Override
    public void setFirst(int n) {
      bitArray[0] = n;
    }

    @Override
    public void setLast(int n) {
      bitArray[bitArray.length-1] = n;      
    }  
  }
}
