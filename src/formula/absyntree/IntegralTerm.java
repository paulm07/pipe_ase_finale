package formula.absyntree;

import formula.parser.Visitor;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class IntegralTerm extends SingleValuedExp<Number> {

  private final String changeVariable;
  private final AExp upperLimit;
  private final AExp lowerLimit;
  private final Term expression;

  public IntegralTerm(final int p, final Term t, final Exp lower, final Exp upper, final String v) {
    if (lower instanceof AExp && upper instanceof AExp) {
      lowerLimit = (AExp) lower;
      upperLimit = (AExp) upper;
    }
    else {
      throw new IllegalArgumentException("Improper expression for either lower limit or upper limit. These must be arithmatic expression");
    }

    pos = p;
    expression = t;
    changeVariable = v;
  }

  @Override
  public void accept(Visitor v) {
    v.visit(this);
  }

  public String getChangeVariable() {
    return changeVariable;
  }

  public AExp getUpperLimit() {
    return upperLimit;
  }

  public AExp getLowerLimit() {
    return lowerLimit;
  }

  public Term getExpression() {
    return expression;
  }

  public static void main(String[] args) {
    PolynomialFunction pf = new PolynomialFunction(new double[]{1.0, 1.0});
    SimpsonIntegrator integrator = new SimpsonIntegrator();
    double value = integrator.integrate(64, pf, 1.0, 3.0);
    System.out.println("integrate(64, pf, 1.0, 3.0) = "+value);

    value = integrator.integrate(64, pf, 0.0, 3.0);
    System.out.println("integrate(64, pf, 0.0, 3.0) = "+value);

    value = integrator.integrate(64, pf, 3.0, 5.0);
    System.out.println("integrate(64, pf, 3.0, 5.0) = "+value);
  }
}
