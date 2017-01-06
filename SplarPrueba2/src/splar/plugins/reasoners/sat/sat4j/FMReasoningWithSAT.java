package splar.plugins.reasoners.sat.sat4j;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import splar.core.constraints.CNFClause;
import splar.core.constraints.CNFLiteral;
import splar.core.constraints.PropositionalFormula;
import splar.core.fm.FeatureModel;
import splar.samples.SATReasoningExample;


public class FMReasoningWithSAT extends FTReasoningWithSAT {

	
	public FMReasoningWithSAT(String solverName, FeatureModel featureModel, int timeout) {
		super(solverName, featureModel, timeout);
	}
	
	public List<PropositionalFormula> constraintInconsistentes= new ArrayList<PropositionalFormula>();
	
	protected void addSolverClauses(ISolver solver) throws Exception {
		super.addSolverClauses(solver);		
		// add extra constraints
		for( PropositionalFormula formula : featureModel.getConstraints() ) {
			for( CNFClause clause : formula.toCNFClauses() ) {
				IVecInt vectInt = new VecInt(clause.countLiterals());
				for( CNFLiteral literal : clause.getLiterals() ) {
					int signal = literal.isPositive() ? 1 : -1;
					int varID = getVariableIndex(literal.getVariable().getID());
					vectInt.push(signal * varID);
				}
				try{
					solver.addClause(vectInt);
				}
				catch (ContradictionException e){
					System.out.println(formula.getFormula());
					constraintInconsistentes.add(formula);
					
				}
//				System.out.println("EC: " + vectInt);
			}
		}
		SATReasoningExample.constInconsistentes= this.constraintInconsistentes;
	}		
}
