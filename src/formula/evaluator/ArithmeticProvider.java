package formula.evaluator;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class ArithmeticProvider {

  public static BigDecimal add(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.add(pSecond);
  }

  public static BigDecimal subtract(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.subtract(pSecond);
  }

  public static BigDecimal multiply(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.multiply(pSecond);
  }

  public static BigDecimal divide(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.divide(pSecond);
  }

  public static BigDecimal remainder(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.remainder(pSecond);
  }

  public static BigDecimal negate(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.negate();
  }

  public static BigDecimal power(final BigDecimal pFirst, final BigDecimal pSecond) {
    return pFirst.pow(pSecond.intValue());
  }
}
