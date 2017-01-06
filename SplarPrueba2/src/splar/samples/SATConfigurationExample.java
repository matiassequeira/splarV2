package splar.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.ContradictionException;



//import application.ItemFeature;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import splar.core.fm.FeatureGroup;
import splar.core.fm.FeatureTreeNode;
import splar.core.fm.configuration.ConfigurationEngine;
import splar.core.fm.configuration.ConfigurationEngineException;
import splar.core.fm.configuration.ConfigurationStep;
import splar.plugins.configuration.sat.sat4j.SATConfigurationEngine;



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
 * This class illustrates how to use the SAT-based configuration engine
 */
public class SATConfigurationExample {

	public static void main(String args[]) {
		//SATConfigurationExample.select("src/test/resources/models/simple_bike_fm.xml", "_frame_aluminium",1,null);
		//SATConfigurationExample.validarSelecciones("src/test/resources/models/simple_bike_fm.xml");
		//SATConfigurationExample.validarConfiguracionParcial("src/test/resources/models/simple_bike_fm.xml");
		//SATConfigurationExample.run("src/test/resources/models/simple_bike_fm.xml");
	}
	
	/**
	 * @param path
	 * @return
	 */
	public static void run(String path) {

		try {
			
			// Feature model path
			String featureModelURL = path ;
			
			// Creates the SAT configuration engine
			ConfigurationEngine satConfEngine = new SATConfigurationEngine(featureModelURL);
			
			// Initializes the engine
			satConfEngine.reset();
			
			/***********************************************************************************************
			 *  CONFIGURATION STEP: selects feature '_pedal_pedalb'
			 ************************************************************************************************/						
			ConfigurationStep step = satConfEngine.configure("_pedal_pedala", 1);
			
			System.out.println("> Feature '" + satConfEngine.getModel().getNodeByID("_pedal_pedala").getName() + "' has been selected");
			
			// Check which other features were impacted by propagation
			for( FeatureTreeNode propagatedNode: step.getPropagations() ) {
				System.out.println("--> Propagated: " + propagatedNode.getName() + " = " + propagatedNode.getValue());
			}
					
			/***********************************************************************************************
			 *  CONFLICT DETECTION: what if feature '_frame_aluminium' that was DESELECTED automatically 
			 *                      in the previous step is now toggled (SELECTED)?
			 ************************************************************************************************/						
			List<FeatureTreeNode> conflicts = satConfEngine.detectConflicts("_frame_aluminium");
			
			System.out.println("> Conflicts if feature '" + satConfEngine.getModel().getNodeByID("_frame_aluminium").getName() + "' is toggled");
			if ( conflicts.size() == 0 ) {
				System.out.println("No conflicts toggling feature " + satConfEngine.getModel().getNodeByID("_frame_aluminium").getName() );
			}
			// List all MANUAL previous decisions impacted by the toggling
			else {
				for( FeatureTreeNode conflictingFeature: satConfEngine.detectConflicts("_frame_aluminium") ) {
					System.out.println("---> Conflict: " + conflictingFeature.getName() );
				}				
			}
			
			/***********************************************************************************************
			 *  TOGGLE: go ahead and toggles feature '_frame_aluminium' to SELECTED state
			 ************************************************************************************************/						
			System.out.println("> Toggling feature '" + satConfEngine.getModel().getNodeByID("_frame_aluminium").getName() + "'");			

			//ConfigurationStep step = satConfEngine.configure("_pedal_pedalb", 1);
			//satConfEngine.toggleDecision("_frame_aluminium");
			
			// Check current state of the features in the feature model
			System.out.println("> Current State of the Feature Model ---------------------------");
			
			for( FeatureTreeNode featureNode: satConfEngine.getModel().getNodes()) {
				if ( !(featureNode instanceof FeatureGroup) ) {
					System.out.println("--> " + featureNode.getName() + " = " + (featureNode.getValue()==1 ? "true" : (featureNode.getValue()==0?"false":"undefined")) );
				}
			}			
			
			/***********************************************************************************************
			 *  UNDO STEP: Retract decision made in the previous step (toggle)
			 ************************************************************************************************/				
			//satConfEngine.undoLastStep();

			System.out.println("> Undoing previous step");			
			// Check current state of the features in the feature model
			System.out.println("> Current State of the Feature Model ---------------------------");
			
			for( FeatureTreeNode featureNode: satConfEngine.getModel().getNodes()) {
				if ( !(featureNode instanceof FeatureGroup) ) {
					System.out.println("--> " + featureNode.getName() + " = " + (featureNode.getValue()==1 ? "true" : (featureNode.getValue()==0?"false":"undefined")) );
				}
			}
			
			
			/***********************************************************************************************
			 *  AUTO-COMPLETION: automatically completes the configuration by attempting to select all "undefined" features
			 *  use 'false' argument to deselect all 'undefined' features
			 ************************************************************************************************/				
			satConfEngine.autoComplete(true);
			System.out.println("> Auto-completing configuration");
			
			// Check current state of the features in the feature model
			System.out.println("> Current State of the Feature Model ---------------------------");
			
			for( FeatureTreeNode featureNode: satConfEngine.getModel().getNodes()) {
				if ( !(featureNode instanceof FeatureGroup) ) {
					System.out.println("--> " + featureNode.getName() + " = " + (featureNode.getValue()==1 ? "true" : (featureNode.getValue()==0?"false":"undefined")) );
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void select(String path,ItemFeature item,Map<String, ItemFeature> mapa ) {

		try {
			
			// Feature model path
			String featureModelURL = path ;
			
			// Creates the SAT configuration engine
			ConfigurationEngine satConfEngine = new SATConfigurationEngine(featureModelURL);
			
			// Initializes the engine
			satConfEngine.reset();
			
			int valor= item.getEstado() == "seleccionado"? 1:0;
			satConfEngine.configure(item.getClave(),valor);
			//satConfEngine.toggleDecision("_frame_aluminium");
			
			// Check current state of the features in the feature model
			System.out.println("> Current State of the Feature Model ---------------------------");
			
			for( FeatureTreeNode featureNode: satConfEngine.getModel().getNodes()) {
				if ( !(featureNode instanceof FeatureGroup) ) {
					String id= featureNode.getID().substring(featureNode.getID().indexOf("_r"), featureNode.getID().length());
					System.out.println("--> " + id + " = " + (featureNode.getValue()==1 ? "true" : (featureNode.getValue()==0?"false":"undefined")) );
					try{
					mapa.get(id).setEstado(featureNode.getValue()==1 ? "seleccionado" : (featureNode.getValue()==0?"noSeleccionado":mapa.get(id).getEstado()));;
					//mapa.get(id).setDisable(featureNode.getValue()==1 ? true : (featureNode.getValue()==0?true:false));
					}catch(Exception e){
						System.out.println("excepcion SATConfigurationExample");
					}
					
				}
			}			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<ItemFeature> validarSelecciones(String path, Map<String, ItemFeature> mapa){
			try {
				
				// Feature model path
				String featureModelURL = path ;
				
				// Creates the SAT configuration engine
				ConfigurationEngine satConfEngine = new SATConfigurationEngine(featureModelURL);
				
				// Initializes the engine
				satConfEngine.reset();
				for (Map.Entry<String, ItemFeature> entry : mapa.entrySet()) {
				
					try{
						String sitem =entry.getValue().getFeature();
						if(entry.getValue().getEstado()=="seleccionado"){
							satConfEngine.configure(entry.getKey(),1);
						}
						else if (entry.getValue().getEstado()=="noSeleccionado"){
							satConfEngine.configure(entry.getKey(),0);
						}
						
						
					
					}
					catch(Exception e){
						System.out.println("contradiction");
					}

				}
				//satConfEngine.toggleDecision("_frame_aluminium");
				
				// Check current state of the features in the feature model
				System.out.println("> Current State of the Feature Model ---------------------------");
				ArrayList<ItemFeature> listaItemsIncorrectos = new ArrayList();
				for( FeatureTreeNode featureNode: satConfEngine.getModel().getNodes()) {
					if ( !(featureNode instanceof FeatureGroup) ) {
						System.out.println("--> " + featureNode.getID()+ " = " + (featureNode.getValue()==1 ? "true" : (featureNode.getValue()==0?"false":"undefined")) );
						String valorFeature = (featureNode.getValue()==1 ? "seleccionado" : (featureNode.getValue()==0?"noSeleccionado":"indefinido"));
						ItemFeature item = mapa.get(featureNode.getID());
						
						if(item!= null && valorFeature!= item.getEstado()){
							item.setSeleccionado(valorFeature);
							listaItemsIncorrectos.add(item);
						}
						
						
					}
				}			
				
				
				
			return listaItemsIncorrectos;	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	
	public static ArrayList<ItemFeature> validarConfiguracionParcial(String path, Map<String, ItemFeature> mapa){
		try {
			
			// Feature model path
			String featureModelURL = path ;
			
			// Creates the SAT configuration engine
			ConfigurationEngine satConfEngine = new SATConfigurationEngine(featureModelURL);
			
			// Initializes the engine
			satConfEngine.reset();

			ArrayList<ItemFeature> listaItemsIncorrectos = new ArrayList();
			for (Map.Entry<String, ItemFeature> entry : mapa.entrySet()) {
				try{
					String sitem =entry.getValue().getFeature();
					if(entry.getValue().getEstado()=="seleccionado"){
						satConfEngine.configure(entry.getKey(),1);
					}
					else if (entry.getValue().getEstado()=="noSeleccionado"){
						satConfEngine.configure(entry.getKey(),0);
					}
					
					
				
				}
				catch(Exception e){
					System.out.println("contradiction");
					listaItemsIncorrectos.add(entry.getValue());
				}

			}
						
			
			
			
		return listaItemsIncorrectos;	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
	
	

	

