/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    S. Garc�a (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Luengo (julianlm@decsai.ugr.es)


   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

import javolution.xml.XmlElement;
import javolution.xml.XmlFormat;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class InputNeuron implements INeuron {
	
	/**
	 * <p>
	 * Input Neuron of a neural net
	 * </p>
	 */
	
    /////////////////////////////////////////////////////////////////
    // ------------------------------------- Marshal/unmarshal format
    /////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Marshal/Unmarshal neuron index
	 * </p>
	 */
	protected static final javolution.xml.XmlFormat<InputNeuron> XML = 
		new XmlFormat<InputNeuron>(InputNeuron.class) 
		{
			public void format(InputNeuron source, XmlElement xml) 
			{
				// Marshal index
				xml.setAttribute("index", source.index);
			}

			public InputNeuron parse(XmlElement xml) 
			{
				// Resulting object
				InputNeuron result = (InputNeuron) xml.object();
				// Unmarshal index
				result.index = xml.getAttribute("index", 1);
				// Return result
				return result;
			}

			public String defaultName() 
			{
				return "input-neuron";
			}
		};
    
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////
	
	/** Generated by Eclipse */
	
	private static final long serialVersionUID = -6041931197564234348L;
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Attributes
	/////////////////////////////////////////////////////////////////

	/** Index of the neuron */
    protected int index;
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * Empty Constructor
     */
    
    public InputNeuron() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Getting and setting attributes
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns the index associated to this neuron
	 * </p>
	 * @return int Index of the neuron
	 */
    public int getIndex() {
        return index;
    }
    
	/**
	 * <p>
	 * Sets the index associated to this neuron
	 * </p>
	 * @param index New index of the neuron
	 */
    public void setIndex(int index) {
        this.index = index;
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Implementing INeuron interface
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Checks if this link is equal to another
	 * </p>
	 * @param other Other link to compare
	 * @return true if both neurons are equal
	 */
    public boolean equals(INeuron other){
        if(this.hashCode()!=other.hashCode())
            return false;
        else
            return true;
    }
    
    /**
     * <p>
	 * Returns an integer number that identifies the neuron
	 * </p>
	 * @return int Hashcode
	 */
    public int hashCode(){
        HashCodeBuilder hcb = new HashCodeBuilder(31, 37);
        hcb.append(index);
        return hcb.toHashCode();
    }	
    
    /**
     * <p>
	 * Operates this neuron. An input neuron has to do no operation
	 * </p>
	 * @param inputs Double array to be used for the inputs observations
	 * @return double Output of the neuron for the array specified
	 */
   public double operate(double [] inputs){
       return inputs[index];
   }
    
	/**
	 * <p>
	 * Operates this neuron. An input neuron has to do no operation
	 * </p>
	 * @param dataSet DataSet to be used for the inputs observations
	 * @return double [] Array outputs of the neuron for the dataSet specified
	 */
    public double [] operate(DoubleTransposedDataSet dataSet){
        return dataSet.getObservationsOf(index);
    }
    
	/**
	 * <p>
	 * Operates this neuron. An input neuron has to do no operation
	 * </p>
	 * @param inputs Input matrix to be used to feed the input neurons
	 * @return double [] Array outputs of the neuron for the matrix specified
	 */
    public double [] operate(double[][] inputs){
        return inputs[index];
    }
    
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Returns a string representation of the InputNeuron
	 * </p>
	 * @return String Representation of the InputNeuron
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("x" + (index+1));
		return sb.toString();
	}
}

