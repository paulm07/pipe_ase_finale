package formula.evaluator;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class ComparisonProvider<T extends Comparable<T>> {

  public boolean isEqual(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) == 0;
  }

  public boolean isNotEqual(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) != 0;
  }

  public boolean isGreater(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) > 0;
  }

  public boolean isGreaterEqual(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) >= 0;
  }

  public boolean isLess(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) < 0;
  }

  public boolean isLessEqual(final T pFirst, final T pSecond) {
    return pFirst.compareTo(pSecond) <= 0;
  }
}
