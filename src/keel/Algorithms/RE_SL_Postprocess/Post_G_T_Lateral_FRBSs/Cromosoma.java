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

package keel.Algorithms.RE_SL_Postprocess.Post_G_T_Lateral_FRBSs;
/**
 * the class which contains the characteristics of the chromosome
 * @author Diana Arquillos
 */
public class Cromosoma {
	private double [] Gene;	
	private double Perf;
	private int HaEntrado;
	
	public double gene(int pos){
		return Gene[pos];
	}
	
	public double perf(){
		
		return Perf;
	}
	public double [] Gene(){
		
		return Gene;
	}
	public void set_perf(double value){
		Perf=value;
		
	}
	public int entrado(){
		
		return HaEntrado;
	}
	public void set_entrado(int value){
		HaEntrado=value;
		
	}
	public Cromosoma(int Genes) {
        Gene = new double [Genes];
    }
	public void set_gene(int pos , double value){
		Gene[pos]=value;
	}
	
}

