package formula.absyntree;

import formula.absyntree.ArithExp;
import formula.absyntree.Term;
import formula.parser.Visitor;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class Power extends ArithExp {
  public Power(int pPos, Term pT1, Term pT2) {
    super(pPos, pT1, pT2);
  }

  @Override
  public void accept(Visitor v) {
    v.visit(this);
  }
}
