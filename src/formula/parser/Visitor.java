package formula.parser;

import formula.absyntree.*;

public interface Visitor {
	default void visit(AndFormula elem) {
		//do nothing by design
	}
	
	default void visit(BraceTerm elem) {
		//do nothing by design
	}
	
	default void visit(BraceTerms elem) {
		//do nothing by design
	}
	
	default void visit(ComplexFormula elem) {
		//do nothing by design
	}
	
	default void visit(ConstantTerm elem) {
		//do nothing by design
	}
	
	default void visit(Diff elem) {
		//do nothing by design
	}
	
	default void visit(Div elem) {
		//do nothing by design
	}
	
	default void visit(EqRel elem) {
		//do nothing by design
	}
	
	default void visit(EquivFormula elem) {
		//do nothing by design
	}
	
	default void visit(Exists elem) {
		//do nothing by design
	}
	
	default void visit(ExpTerm elem) {
		//do nothing by design
	}
	
	default void visit(False elem) {
		//do nothing by design
	}
	
	default void visit(ForAll elem) {
		//do nothing by design
	}
	
	default void visit(GeqRel elem) {
		//do nothing by design
	}
	
	default void visit(GtRel elem) {
		//do nothing by design
	}
	
	default void visit(Identifier elem) {
		//do nothing by design
	}
	
	default void visit(IdVariable elem) {
		//do nothing by design
	}
	
	default void visit(ImpFormula elem) {
		//do nothing by design
	}
	
	default void visit(In elem) {
		//do nothing by design
	}
	
	default void visit(Index elem) {
		//do nothing by design
	}
	
	default void visit(IndexVariable elem) {
		//do nothing by design
	}
	
	default void visit(InRel elem) {
		//do nothing by design
	}
	
	default void visit(LeqRel elem) {
		//do nothing by design
	}
	
	default void visit(LtRel elem) {
		//do nothing by design
	}
	
	default void visit(Minus elem) {
		//do nothing by design
	}
	
	default void visit(Mod elem) {
		//do nothing by design
	}
	
	default void visit(Mul elem) {
		//do nothing by design
	}
	
	default void visit(NegExp elem) {
		//do nothing by design
	}
	
	default void visit(NeqRel elem) {
		//do nothing by design
	}
	
	default void visit(Nexists elem) {
		//do nothing by design
	}
	
	default void visit(Nin elem) {
		//do nothing by design
	}
	
	default void visit(NinRel elem) {
		//do nothing by design
	}
	
	default void visit(NotFormula elem) {
		//do nothing by design
	}
	
	default void visit(NumConstant elem) {
		//do nothing by design
	}
	
	default void visit(StrConstant elem) {
		//do nothing by design
	}
	
	default void visit(Num elem) {
		//do nothing by design
	}
	
	default void visit(OrFormula elem) {
		//do nothing by design
	}
	
	default void visit(Plus elem) {
		//do nothing by design
	}
	
	default void visit(final Power elem) {
		//Empty by design
	}
	default void visit(TermRest elem) {
		//do nothing by design
	}
	
	default void visit(Terms elem) {
		//do nothing by design
	}
	
	default void visit(True elem) {
		//do nothing by design
	}
	
	default void visit(Union elem) {
		//do nothing by design
	}
	
	default void visit(UserVariable elem) {
		//do nothing by design
	}
	
	default void visit(VariableTerm elem) {
		//do nothing by design
	}
	
	default void visit(AExp elem) {
		//do nothing by design
	}
	
	default void visit(RExp elem) {
		//do nothing by design
	}
	
	default void visit(SExp elem) {
		//do nothing by design
	}
	
	default void visit(AtomicTerm elem) {
		//do nothing by design
	}
	
	default void visit(AtFormula elem) {
		//do nothing by design
	}
	
	default void visit(CpFormula elem) {
		//do nothing by design
	}
	
	default void visit(CpxFormula elem) {
		//do nothing by design
	}
	
	default void visit(Sentence elem) {
		//do nothing by design
	}
	
	default void visit(Empty elem) {
		//do nothing by design
	}
	
	default void visit(EmptyTerm elem) {
		//do nothing by design
	}
	
	default void visit(Setdef elem) {
		//do nothing by design
	}
	
	default void visit (final FunctionExp pTerm) {
		//do nothing by design
	}
	
	default void visit(final IntegralTerm pTerm) {
		//do nothing by design
	}
	
}
