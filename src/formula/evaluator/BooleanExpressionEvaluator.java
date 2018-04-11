package formula.evaluator;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class BooleanExpressionEvaluator {

  public static boolean and(final Boolean pFirst, final Boolean pSecond) {
    return pFirst && pSecond;
  }

  public static boolean or(final Boolean pFirst, final Boolean pSecond) {
    return pFirst || pSecond;
  }

  public static boolean not(final Boolean pFirst, final Boolean pSecond) {
    return !pFirst;
  }

  public static boolean implication(final Boolean pFirst, final Boolean pSecond) {
    return !pFirst || pSecond;
  }

  public static boolean equivalence(final Boolean pFirst, final Boolean pSecond) {
    return pFirst && pSecond || !pFirst && !pSecond;
  }
}
