package splar.samples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ItemFeature {
	
	private String feature;
	private String clave;
	private String estado;
	private String seleccionado;
	private final BooleanProperty ineditable = new SimpleBooleanProperty();
	
    public boolean isDisable() {
        return ineditable.get();
    }

    public void setDisable(boolean value) {
    	
        ineditable.set(value);
    }

    public BooleanProperty disabledProperty() {
        return ineditable;
    }
	
	public ItemFeature(String f, String c){
		feature=f;
		clave=c;
		estado= "indefinido";
		ineditable.set(false);
		
	}
	public String toString(){
		return feature;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	

	public String getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(String seleccionado) {
		this.seleccionado = seleccionado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	
}
