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

//
//  ReferenciaMNV.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 25-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MNV;

public class ReferenciaMNV implements Comparable {
	
  public int entero;
  public double real;
  public double dist;

  public ReferenciaMNV () {}

  public ReferenciaMNV (int a, double b, double c) {
    entero = a;
    real = b;
    dist = c;
  }

  public int compareTo (Object o1) {
    if (this.real > ((ReferenciaMNV)o1).real)
      return 1;
    else if (this.real < ((ReferenciaMNV)o1).real)
      return -1;
    else if (this.dist > ((ReferenciaMNV)o1).dist)
    	return 1;
    else if (this.dist < ((ReferenciaMNV)o1).dist)
    	return -1;
    else return 0;
  }

  public String toString () {
    return new String ("{"+entero+", "+real+", "+dist+"}");
  }
}
