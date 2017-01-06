package splar.samples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import splar.core.constraints.PropositionalFormula;
import splar.core.fm.FeatureModel;
import splar.core.fm.XMLFeatureModel;
import splar.plugins.reasoners.sat.sat4j.FMReasoningWithSAT;



/**
 * SPLAR library - Feature Model Reasoning and Configuration API
 * SPLOT portal - Software Product Lines Online Tools (www.splot-research.org)
 * 
 * ***************************************************************************
 *  
 * @author Marcilio Mendonca
 * University of Waterloo, Canada
 * July, 2009
 *
 * This class illustrates how to use a SAT reasoner to reason on a feature model
 */
public class SATReasoningExample {
	public static List<PropositionalFormula> constInconsistentes;
	private ArrayList<String> caracteristicasMuertas;

	public static void main(String args[]) {
		//run("src/test/resources/models/simple_bike_fm.xml");
	}
	
	public int[] run(String path) {

		try {
			int[] retorno= new int[4];  
			// Feature model path
			String featureModelPath = path;
			
			// Create feature model object from an XML file (SXFM format - see www.splot-research.org for details)	
			// If an identifier is not provided for a feature use the feature name as id
			FeatureModel featureModel = new XMLFeatureModel(featureModelPath, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
			// load feature model from 			
			featureModel.loadModel();			

			// SAT reasoner construction parameters
			// - "MiniSAT" - name of the SAT4J solver used
			// - Timeout parameter
			int SATtimeout = 60000;  	// 1 minute is given to the SAT solver to check the consistency of the feature model  
			
			FMReasoningWithSAT reasoner = new FMReasoningWithSAT("MiniSAT", featureModel, SATtimeout);

			// Initialize the reasoner
			reasoner.init();
			
			// Use the reasoner			
						
			// Check if feature model is consistent, i.e., has at least one valid configuration
			//System.out.println("Feature model is " + (reasoner.isConsistent() ? "" : " NOT ") + "consistent!");
			if(reasoner.isConsistent()){
				
				retorno[0]=1;
			}
			else
				retorno[0]=0;
			
			
			
			// Compute dead, common, and valid domains, i.e., for each feature check whether the feature can be 
			// only true, only false, or (true or false)
			
			Map<String,String> stats = new HashMap<String,String>();
			Map<String,Boolean[]> domainTable = reasoner.allValidDomains(stats);			
			
			System.out.println("Domains ---------------------");
			int countCommon = 0, countDead = 0;
			caracteristicasMuertas= new ArrayList<String>();
			for( String featureId : domainTable.keySet() ) {				
				System.out.print( "- " + featureId + ": [");
				Boolean domain[] = domainTable.get(featureId);
				for( Boolean value : domain ) {
					System.out.print(value + " ");
				}
				System.out.print("]");				
				if ( domain.length == 1 && domain[0] == true ) {
					System.out.print(" (common)");
					countCommon++;
				}
				else if ( domain.length == 1 && domain[0] == false ) {
					
					caracteristicasMuertas.add(featureId.substring(featureId.indexOf("_r"), featureId.length()));
					System.out.print(" (dead)");
					countDead++;
				}
				System.out.println();
			}

			System.out.println("Stats ---------------------");
			System.out.println("- Total Common Features.: " + countCommon);
			retorno[1]=countCommon;
			System.out.println("- Total Dead Features...: " + countDead);
			retorno[2]=countDead;
			System.out.println("- Running Time..........: " + stats.get("processing-time"));
			System.out.println("- Number of SAT Checks..: " + stats.get("sat-checks"));
			
			retorno[3]=(int) reasoner.countValidConfigurations();
			//retorno[3]=1;
			System.out.println(retorno[3]);
			return retorno;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getCaracteristicasMuertas() {
		return caracteristicasMuertas;
	}
	
	public String configuracionesPosibles(String path,Map<String, ItemFeature> mapa){
		try{
			String featureModelPath = path;
			
			FeatureModel featureModel = new XMLFeatureModel(featureModelPath, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
					
			featureModel.loadModel();			
	
			
			int SATtimeout = 60000;  
for (Map.Entry<String, ItemFeature> entry : mapa.entrySet()) {
				
				try{
					String sitem =entry.getValue().getFeature();
					if(entry.getValue().getEstado()=="seleccionado"){
						PropositionalFormula pf= new PropositionalFormula("constraint"+featureModel.getConstraints().size(),entry.getKey() );
						
						featureModel.addConstraint(pf);
					}
					else if (entry.getValue().getEstado()=="noSeleccionado"){
						PropositionalFormula pf= new PropositionalFormula("constraint"+featureModel.getConstraints().size(),"~"+entry.getKey() );
						
						featureModel.addConstraint(pf);
					}
					
					
				
				}
				catch(Exception e){
					System.out.println("contradiction");
				}

			}
			FMReasoningWithSAT reasoner = new FMReasoningWithSAT("MiniSAT", featureModel, SATtimeout);
	
			
			reasoner.init();
			
			return ""+(int)reasoner.countValidConfigurations();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
