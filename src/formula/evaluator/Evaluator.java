package formula.evaluator;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */

@FunctionalInterface
public interface Evaluator<T> {
  T evaluate(final T pFirst, final T pSecond);
}
