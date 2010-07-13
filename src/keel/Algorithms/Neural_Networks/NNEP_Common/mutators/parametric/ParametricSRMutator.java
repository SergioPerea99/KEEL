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

package keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric;



import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividual;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @param <I> Type of individuals to mutate
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class ParametricSRMutator<I extends NeuralNetIndividual> extends ParametricMutator<I>
{
	
	/**
	 * <p>
	 * Parametric mutator for neural nets, mutate the weights of the neural nets
	 * mutated. This implementation uses the "1/5 Success Rule of Rechenberg" 
	 * method to update alpha values.
	 * 
	 * IMPORTANT NOTE: Parametric mutator works directly with  he individuals instead
	 *                 of returning a mutated copy of them. This is for performance 
	 *                 reasons. If you want to use another mutator you have to consider 
	 *                 that individuals will be changed when you use parametric mutatio
	 * </p>                
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////
	
	/** Generated by Eclipse */

	private static final long serialVersionUID = -4723976893815126429L;
		
	/////////////////////////////////////////////////////////////////
	// --------------------- Private variables for the alpha control
	/////////////////////////////////////////////////////////////////
	
	/** Number of successful mutations  */
	
	private int successfulMutations = 0;
	
	/** Number of mutations  */
	
	private int totalMutations = 0;
	
	/** Number of generations from last alpha update  */
	
	private int nOfGenerations = 0;
	
	/** Ratio of successful mutations  */
	
	protected double successRatio;
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
	
    /**
     * <p>
     * Empty Constructor
     * </p>
     */
    
    public ParametricSRMutator() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Getting and setting properties
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the success ratio of last generation
	 * </p>
	 * @return     double success ratio
	 */
    
    public double getSuccessRatio(){
    	return successRatio;
    }

	/////////////////////////////////////////////////////////////////
	// ------------------------ Overwriting ParametricMutator methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Init the values of alpha parameters used in the mutations
	 * </p>
	 *
	 */
    
    public void alphaInit() {
        alphaInput = initialAlphaInput;
        alphaOutput = initialAlphaOutput;
        successfulMutations = 0;
        totalMutations = 0;
        nOfGenerations = 0;
    }
    
	/**
	 * <p>
	 * Updates the values of alpha parameters used in the mutations
	 * </p>
	 *
	 * @param bestFitness Best fitness of this generation
	 */
    
    protected void alphaUpdate(double bestFitness) {

	    // ------------------------------
	    // 1/5 Success Rule of Rechenberg
	    // ------------------------------
    	
    	if(nOfGenerations==5){
    		successRatio = (double) successfulMutations / totalMutations;
	
    		if(successRatio < 0.2){
    			alphaInput*=0.9;
    			alphaOutput*=0.9;
    		}
    		else if(successRatio > 0.2){
    			alphaInput*=1.1;
    			alphaOutput*=1.1;
    		}
    		successfulMutations = 0;
    		totalMutations = 0;
    		nOfGenerations = 0;
    	}
    	nOfGenerations++;
    }
    
	/**
	 * <p>
	 * Updates alpha control parameters at the end of each
	 * neuron mutation, if neccesary
	 * </p>
	 *
	 * @param newFitness Result fitness of the mutation
	 * @param fitness Previous fitness befor making the mutation
	 */
    
    protected void alphaControlParametersUpdate(double newFitness, double fitness){		
        // Control ratio of succesful mutations
        if(newFitness > fitness)
			successfulMutations++;
		totalMutations++;
    }
}

