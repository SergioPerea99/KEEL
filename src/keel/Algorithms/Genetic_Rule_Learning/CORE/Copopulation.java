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

/**
 * <p>
 * @author Written by Juli�n Luengo Mart�n 13/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import java.util.*;

/**
 * <p>
 * This class represents the co-population of cochromosomes in the cooperative-competitive scheme of the CORE algorithm
 * </p>
 */
public class Copopulation {
	
	ArrayList<Cochromosome> chrom;
	
	/**
	 * <p>
	 * Default constructor. Allocates memory for the inner array.
	 * </p>
	 */
	public Copopulation(){
		chrom = new ArrayList<Cochromosome>();
	}
	
	/**
	 * <p>
	 * Adds a cochromsome to this population (cannot be latter deleted!)
	 * </p>
	 * @param ruleSet the new rule set to be aded
	 */
	public void addRule(Cochromosome ruleSet){
		chrom.add(ruleSet);
	}

}

