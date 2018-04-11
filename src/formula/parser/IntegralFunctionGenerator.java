package formula.parser;

import formula.absyntree.*;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class IntegralFunctionGenerator implements Visitor {
  private final Term mExpression;
  private final String mChangeVariable;

  private Map<String, UnivariateFunction> mElementsToFunctions = new HashMap<>();

  public IntegralFunctionGenerator(final Term pExpression, final String pChangeVariable) {
    mExpression = pExpression;
    mChangeVariable = pChangeVariable;
  }

  @Override
  public void visit(AndFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(BraceTerm elem) {
    //Empty By Design
  }

  @Override
  public void visit(BraceTerms elem) {
    //Empty By Design
  }

  @Override
  public void visit(ComplexFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(ConstantTerm elem) {
    if (elem.c instanceof NumConstant) {
      org.apache.commons.math3.analysis.function.Constant constant =
          new org.apache.commons.math3.analysis.function.Constant(((NumConstant)elem.c).value.doubleValue());
      mElementsToFunctions.put(elem.identityToString(), constant);
    }

  }

  @Override
  public void visit(Diff elem) {
    //Empty By Design
  }

  @Override
  public void visit(Div elem) {
//    TODO:: implement
  }

  @Override
  public void visit(EqRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(EquivFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(Exists elem) {
    //Empty By Design
  }

  @Override
  public void visit(ExpTerm elem) {
    elem.e.accept(this);
    mElementsToFunctions.put(elem.identityToString(), mElementsToFunctions.get(elem.e.identityToString()));
  }

  @Override
  public void visit(False elem) {
    //Empty By Design
  }

  @Override
  public void visit(ForAll elem) {
    //Empty By Design
  }

  @Override
  public void visit(GeqRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(GtRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(Identifier elem) {
    //Empty By Design
  }

  @Override
  public void visit(IdVariable elem) {
    //Empty By Design
  }

  @Override
  public void visit(ImpFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(In elem) {
    //Empty By Design
  }

  @Override
  public void visit(Index elem) {
    elem.n.accept(this);
    elem.int_val = Integer.parseInt(elem.n.n);
  }

  @Override
  public void visit(IndexVariable elem) {
    elem.i.accept(this);
    elem.idx.accept(this);

    elem.key = elem.i.key;
    elem.index = elem.idx.int_val;
  }

  @Override
  public void visit(InRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(LeqRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(LtRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(Minus elem) {
//    TODO: IMplement
  }

  @Override
  public void visit(Mod elem) {
//    TODO: implement
  }

  @Override
  public void visit(Mul elem) {
//    TODO:: Implement
  }

  @Override
  public void visit(NegExp elem) {
//    ToDO: Implement
  }

  @Override
  public void visit(NeqRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(Nexists elem) {
    //Empty By Design
  }

  @Override
  public void visit(Nin elem) {
    //Empty By Design
  }

  @Override
  public void visit(NinRel elem) {
    //Empty By Design
  }

  @Override
  public void visit(NotFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(NumConstant elem) {
    //Empty By Design
  }

  @Override
  public void visit(StrConstant elem) {
    //Empty By Design
  }

  @Override
  public void visit(Num elem) {
    //Empty By Design
  }

  @Override
  public void visit(OrFormula elem) {
    //Empty By Design
  }

  @Override
  public void visit(Plus elem) {
//    TODO: Implement
  }

  @Override
  public void visit(TermRest elem) {
    //Empty By Design
  }

  @Override
  public void visit(Terms elem) {
    //Empty By Design
  }

  @Override
  public void visit(True elem) {
    //Empty By Design
  }

  @Override
  public void visit(Union elem) {
    //Empty By Design
  }

  @Override
  public void visit(UserVariable elem) {
    //Empty By Design
  }

  @Override
  public void visit(VariableTerm elem) {
    //Empty By Design
    mElementsToFunctions.put(elem.identityToString(), new PolynomialFunction(new double[]{0, 1}));
  }

  @Override
  public void visit(AExp elem) {
    //Empty By Design
  }

  @Override
  public void visit(RExp elem) {
    //Empty By Design
  }

  @Override
  public void visit(SExp elem) {
    //Empty By Design
  }

  @Override
  public void visit(AtomicTerm elem) {
    elem.t.accept(this);
    UnivariateFunction function = mElementsToFunctions.get(elem.t.identityToString());
    mElementsToFunctions.put(elem.identityToString(), function);
  }

  @Override
  public void visit(AtFormula elem) {
    elem.af.accept(this);
    UnivariateFunction function = mElementsToFunctions.get(elem.af.identityToString());
    mElementsToFunctions.put(elem.identityToString(), function);
  }

  @Override
  public void visit(CpFormula elem) {
    //Empty by design
  }

  @Override
  public void visit(CpxFormula elem) {
    //Empty by design
  }

  @Override
  public void visit(Sentence elem) {
    //Empty by design
  }

  @Override
  public void visit(Empty elem) {
    //Empty by design
  }

  @Override
  public void visit(EmptyTerm elem) {
    //Empty by design
  }

  @Override
  public void visit(Setdef elem) {
    //Empty by design
  }

  @Override
  public void visit(FunctionExp pTerm) {
    String functionName = pTerm.getName();
//    List<Term> arguments = pTerm.getArgumentTerms();
    UnivariateFunction function = null;
    if (functionName.equalsIgnoreCase("exp")) {
      function = new org.apache.commons.math3.analysis.function.Exp();
    }
    else if (functionName.equalsIgnoreCase("sin")) {
      function = new Sin();
    }
    else if (functionName.equalsIgnoreCase("cos")) {
      function = new Cos();
    }
    else if (functionName.equalsIgnoreCase("log")) {
      function = new Log();
    }

    mElementsToFunctions.put(pTerm.identityToString(), function);
  }

  public UnivariateFunction createFunction() {
    mElementsToFunctions.clear();
    mExpression.accept(this);
    return mElementsToFunctions.get(mExpression.identityToString());
  }
}
