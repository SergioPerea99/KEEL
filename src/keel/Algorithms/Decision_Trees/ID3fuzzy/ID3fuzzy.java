/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
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
* @author Written by Cristobal Romero (Universidad de Córdoba) 27/02/2007
* @author Modified by Cristobal Romero (Universidad de Córdoba) 19/04/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.ID3fuzzy;
import java.io.*;
import java.util.*;
import javafx.util.Pair;

import keel.Dataset.Attributes;



/** 
   A Java implementation of the ID3fuzzy algorithm. This is a binary version where 
   nodes are split by atributtes and values, instead of the generic ID3fuzzy, which
   only considers attributes for splitting.
   
   @author Cristóbal Romero Morales (UCO)
   @version 1.0 (30-03-06)
*/
public class ID3fuzzy extends Algorithm 
{
	/** Root of the decomposition tree. */
	Node root = new Node();  
	
        /** Total number of Nodes in the tree.  */
	int NumberOfNodes;  
	
	/** Number of Leafs in the tree. */
	int NumberOfLeafs;  
        
        /** Numero de etiquetas por variable. */
        int NumberOfLabs;
        
        /**Proporción de una clase en el nodo actual. Por defecto 80%. */
        double PROPORCION_CLASE_NODO; 
	
        /**Porcentaje mínimo del TOTAL de ejemplos a considerar para finalizar la expansión de nodos. */
	double PORCENTAJE_MIN_EJEMPLOS;
        
        /**Base Difusa*/
        BaseD baseD;
        
        
	/** Constructor.
	 * 
	 * @param paramFile			The parameters file.
	 * 
	 */
	public ID3fuzzy( String paramFile ){
            try {

                // starts the time 
                long startTime = System.currentTimeMillis();

      		// Sets the options of the execution.
		StreamTokenizer tokenizer = new StreamTokenizer( new BufferedReader( new FileReader( paramFile ) ) );
    		initTokenizer( tokenizer) ;
    		setOptions( tokenizer );
    		
    		// Initializes the dataset.
    		modelDataset = new Dataset( modelFileName, true  );
    		
                
                
                trainDataset = new Dataset( trainFileName, false  );    		    	
                testDataset = new Dataset( testFileName, false  );

                NumberOfNodes = 0;
                NumberOfLeafs = 0;
                
                
    		/*check if there are continous attributes*/
    		if(Attributes.hasRealAttributes() || Attributes.hasIntegerAttributes())
    		{
                    baseD = new BaseD(NumberOfLabs, modelDataset); //TODO: SACAR ESTO FUERA DE LA CREACIÓN DEL ARBOL !!!!
                    baseD.Semantica(modelDataset);
                    
                    generateFuzzyTree();
                    printTrainFuzzy();
                    printTestFuzzy();
                    printResultFuzzy();
                    
    		}
    		
                else
                {
                    generateTree();
                    printTrain();
                    printTest();
                    printResult();
                    
                    /*
                    // Executes the algorithm.
                    generateTree();

                    // Prints the results generates by the algorithm.
                    printTrain();
                    printTest();
                    printResult();*/
		}
	    } 
            catch ( Exception e ) 
            {
                System.err.println( e.getMessage() );
                System.exit(-1);
	    }
	}
	
 	
	/** Function to read the options from the execution file and assign the values to the parameters.
	 * 
	 * @param options 		The StreamTokenizer that reads the parameters file.
	 * 
	 * @throws Exception	If the format of the file is not correct.
	 */ 
    protected void setOptions( StreamTokenizer options ) throws Exception
	{
  		options.nextToken();
		
  		// Checks that the file starts with the token algorithm.
		if ( options.sval.equalsIgnoreCase( "algorithm" ) )
		{
			options.nextToken();
			options.nextToken();

			//if (!options.sval.equalsIgnoreCase( "ID3fuzzy" ) )
			//	throw new Exception( "The name of the algorithm is not correct." );

			options.nextToken();
			options.nextToken();
			options.nextToken();
			options.nextToken();
			
			// Reads the names of the input files.
			if ( options.sval.equalsIgnoreCase( "inputData" ) )
			{
				options.nextToken();
				options.nextToken();
				modelFileName = options.sval;
					
				if ( options.nextToken() != StreamTokenizer.TT_EOL )
				{
					trainFileName = options.sval;
					options.nextToken();
					testFileName = options.sval;					
					if( options.nextToken() != StreamTokenizer.TT_EOL )
					{
					  trainFileName = modelFileName;	
					  options.nextToken();
					}										
				}																
				
			}
			else
				throw new Exception( "The file must start with the word inputData." );
				
			
			
			while ( true )
			{
				if( options.nextToken() == StreamTokenizer.TT_EOF )
					throw new Exception( "No output file provided." );
			
				if ( options.sval == null )
					continue;
				else if ( options.sval.equalsIgnoreCase( "outputData" ) )
					break;
			}

			/* Reads the names of the output files*/
			options.nextToken();
                        options.nextToken();
                        trainOutputFileName = options.sval;
                        options.nextToken();

                        testOutputFileName = options.sval;
                        options.nextToken();

                        resultFileName = options.sval;
                        
                        if (!getNextToken(options)) {
                            return;
                        }

                        while (options.ttype != StreamTokenizer.TT_EOF) {
                            
                            /* Reads the NumberOfLabs parameter */
                            if (options.sval.equalsIgnoreCase("Labs")) {
                                options.nextToken();
                                options.nextToken();

                                if (Integer.parseInt(options.sval) > 0) {
                                    NumberOfLabs = Integer.parseInt(options.sval);
                                }
                            }

                            /* Reads the PROPORCION_CLASE_NODO parameter */
                            if (options.sval.equalsIgnoreCase("PorcentajeDeClaseNodo")) {
                                
                                options.nextToken();
                                options.nextToken();
                                
                                float cf = Float.parseFloat(options.sval);

                                if (cf <= 1 || cf >= 0) {
                                    PROPORCION_CLASE_NODO = Float.parseFloat(options.sval);
                                }
                            }

                            /* Reads PORCENTAJE_MIN_EJEMPLOS parameter */
                            if (options.sval.equalsIgnoreCase("PorcentajeMINejemplos")) {
                                
                                options.nextToken();
                                options.nextToken();
                                
                                float cf = Float.parseFloat(options.sval);

                                if (cf <= 1 || cf >= 0) {
                                    PORCENTAJE_MIN_EJEMPLOS = Float.parseFloat(options.sval);
                                }
                            }

                            getNextToken(options);
                        }
				
		}
		else
			throw new Exception( "The file must start with the word algorithm followed of the name of the algorithm." );
	}



	/** Run the algorithm.
	 *
	 */ 
	public void generateTree()
	{
		root.setData( getItemsets() );
		decomposeNode( root );
	}

 	/** Function to write the decision tree in the form of rules.
 	 * 
 	 * @param node		The current node.
 	 * @param tab		The indentation of the current rule.
 	 * 
 	 * @return			The tree in form of rules.
 	 */
	public String writeTree( Node node, String tab ) 
	{
		int outputattr = modelDataset.getClassIndex();
		String cadena = "";
		Attribute classAtt = modelDataset.getClassAttribute();
		String attName = classAtt.name();

        try
        {
        	// Print a leaf node. 
        	if ( node.getChildren() == null ) 
        	{
        		String value = getCommonClass( node.getData(), outputattr );
        		
       			cadena = tab + attName + " = \"" + value + "\"\n";
       			
       			/* new */
       			NumberOfLeafs++;
       			
       			return cadena;
       			
        	}
		
        	// Print a rule. 
        	cadena += tab + "if( " + modelDataset.getAttribute( node.getDecompositionAttribute() ).name() + 
				" == \"" + modelDataset.getAttribute( node.getDecompositionAttribute()).
				value(node.getDecompositionValue()) + "\" ) then \n";
        	cadena += tab + "{\n";
        	cadena += writeTree( node.getChildren()[0], tab + "\t" );
			cadena += tab + "}\n";
        	cadena += tab +  "else\n";
        	cadena += tab + "{\n";
        	cadena +=writeTree( node.getChildren()[1], tab + "\t" );
			cadena += tab + "}\n";
			    
			    /* new */
     			NumberOfNodes++;

        	return cadena;
        }
        catch( Exception e )
		{
        	System.out.println( "Error writing tree" );
		}		

        return cadena;
	}
        
        //FUZZY
        public String writeTreeFuzzy( Node node, String tab ) 
	{
		int outputattr = modelDataset.getClassIndex();
		String cadena = "";
		Attribute classAtt = modelDataset.getClassAttribute();
		String attName = classAtt.name();

        try
        {
        	// Print a leaf node. 
        	if ( node.getChildren() == null ) 
        	{
        		String value = getCommonClass( node.getData(), outputattr );
        		
       			cadena = tab + attName + " = \"" + value + "\"\n";
       			
       			/* new */
       			NumberOfLeafs++;
       			
       			return cadena;
       			
        	}
		
        	// Print a rule. 
        	cadena += tab + "if( " + modelDataset.getAttribute( node.getDecompositionAttribute() ).name() + 
				" ==  ["+ node.getDecompositionValueFuzzy(0).getKey() +", "+node.getDecompositionValueFuzzy(0).getValue() +"]  ) then \n";
        	cadena += tab + "{\n";
        	if (node.getChildren()[0] != null){
                    cadena += writeTreeFuzzy( node.getChildren()[0], tab + "\t" );
                    cadena += tab + "}\n";
                    NumberOfNodes++;
                }
                for (int i = 1; i < node.numDescompositionValueFuzzy(); i++){
                    if (node.getChildren()[i] != null){
                        cadena += tab +  "else if ("+ modelDataset.getAttribute( node.getDecompositionAttribute() ).name() +
                                        " ==  ["+ node.getDecompositionValueFuzzy(i).getKey() +", "+node.getDecompositionValueFuzzy(i).getValue() +"]  ) then \n";
                        cadena += tab + "{\n";
                    
                        cadena +=writeTreeFuzzy( node.getChildren()[i], tab + "\t" );
                        cadena += tab + "}\n";
                    }
                    /* new */
     		    NumberOfNodes++;
                }	    
		
                NumberOfNodes++;

        	return cadena;
        }
        catch( Exception e ){
            System.out.println( "Error writing tree" );
        }		

        return cadena;
	}
	

	/** Function to evaluate the class which the itemset must have according to the classification of the tree.
	 * 
	 * @param itemset		The itemset to evaluate.
	 * @param node			The node that is evaluated at this time.
	 * 
	 * @return				The index of the class index predicted.
	 */
	public int evaluateItemset( Itemset itemset, Node node ) 
	{		
		int outputattr = modelDataset.getClassIndex();
		boolean correct = false;
		String aux = null;
		Attribute classAtt = modelDataset.getClassAttribute();
		
		try 
		{
      	        	                
			// if the node is a final leaf
			if ( node.getChildren() == null ) 
			{
				int []values = getAllValues( node.getData(), outputattr );
			
				if ( values.length == 1 ) 
				{				
					if( values[0] == itemset.getClassValue() )
					{
						aux = classAtt.value( values[0] ); //
						aux = aux + " " + aux + "\n";	
				 
						return values[0];
					}
					else
					{
						aux = classAtt.value( (int)itemset.getClassValue() );
						aux = aux + " " + classAtt.value(values[0]) + "\n";	 			 
				 
						return values[0];
					}
				}

				aux = classAtt.value( (int)itemset.getClassValue() );
				aux = aux + " null\n";
			
				return (int)itemset.getClassValue();
			}
		}
		catch ( Exception e)
		{
			return Integer.parseInt( aux.toString() );
      	}

		// Evaluate the children of the node.
		if( itemset.getValue( node.getDecompositionAttribute() ) == node.getDecompositionValue() )
			return( evaluateItemset( itemset, node.getChildren()[0] ) );
		else
			return( evaluateItemset( itemset, node.getChildren()[1] ) );
	}
        
        //FUZZY
        public int evaluateItemsetFuzzy(Itemset itemset, Node node) {
            int outputattr = modelDataset.getClassIndex();
            boolean correct = false;
            String aux = null;
            Attribute classAtt = modelDataset.getClassAttribute();
            
            try 
		{
      	        	                
			// if the node is a final leaf
			if ( node.getChildren() == null ) 
			{
				int []values = getAllValues( node.getData(), outputattr );
			
				if ( values.length == 1 ) 
				{				
					if( values[0] == itemset.getClassValue() )
					{
						aux = classAtt.value( values[0] ); //
						aux = aux + " " + aux + "\n";	
				 
						return values[0];
					}
					else
					{
						aux = classAtt.value( (int)itemset.getClassValue() );
						aux = aux + " " + classAtt.value(values[0]) + "\n";	 			 
				 
						return values[0];
					}
				}

				aux = classAtt.value( (int)itemset.getClassValue() );
				aux = aux + " null\n";
			
				return (int)itemset.getClassValue();
			}
		}
            catch ( Exception e)
            {
                    return Integer.parseInt( aux.toString() );
            }
            
            
            /*try {

                // Si el nodo es un nodo HOJA -> Hay que sacar que valor de clase te dice que debe de ser.
                if (node.getChildren() == null) {
                    
                    int value = indexValueClassPredominant(node.getData(), outputattr);

                    if (value == itemset.getClassValue()) //Comprobamos si ese valor se corresponde con el valor de la clase del itemset pasado por parametro.
                    {
                        aux = classAtt.value(value);
                        aux = aux + " " + aux + "\n";	//Esto sirve para mostrar luego la correspondencia de que tienen el mismo valor de clase.
                    } else if (value != -1) {
                        aux = classAtt.value((int) itemset.getClassValue());
                        aux = aux + " " + classAtt.value(value) + "\n"; //Esto sirve para mostrar luego que NO tienen el mismo valor de clase.
                        return value;
                    } else {  //En caso de que no se haya retornado ningun indice de valor de clase predominante entre el conjunto de datos de itemsets de este nodo hoja. 
                        aux = classAtt.value((int) itemset.getClassValue()); //Recoge el valor de la clase
                        aux = aux + " null\n";
                    }
                    return (int)itemset.getClassValue();
                }
            } catch (Exception e) {
                return Integer.parseInt(aux.toString());
            }*/

            // En caso de no ser nodo hoja, llega hast aquí -> Encontrar aquel nodo por el que debe seguir profundizando según el Valor por el que descompone...
            for (int i = 0; i < node.numChildren() - 1; i++) {
                //Si el valor en el itemset para ese atributo concreto está entre el intervalo de la etiqueta (del hijo correspondiente)
                if (itemset.getValue(node.getDecompositionAttribute()) > node.getDecompositionValueFuzzy(i).getKey() && itemset.getValue(node.getDecompositionAttribute()) < node.getDecompositionValueFuzzy(i).getValue()) {
                    return (evaluateItemsetFuzzy(itemset, node.getChildren()[i]));
                }

            }
            return (evaluateItemsetFuzzy(itemset, node.getChildren()[node.numChildren() - 1]));
        }
        
        

	/** Function to return all the values of the specified attribute in the data set.
	 * 
	 * @param data 			All the itemsets.
	 * @param attribute		Number of attributes.
	 * 
	 * @return				All the values that can have the attributes.
	 */
	public int []getAllValues( Vector data, int attribute ) 
	{
		Vector values = new Vector();
		int num = data.size();
		
		for ( int i = 0; i < num; i++ ) //Recorremos todos los itemsets del vector
		{
			Itemset current = (Itemset) data.elementAt( i ); //Se obtiene el itemset correspondiente.
			String symbol = modelDataset.getAttribute( attribute ).value( (int)current.getValue( attribute ) ); //Se coge el valor de la CLASE para el itemset recogido.
			int index = values.indexOf( symbol ); //Recoge si ese valor de CLASE EXISTE YA EN EL VECTOR O NO.
			
			if ( index < 0 )
				values.addElement( symbol ); //En caso de no existir se añade al vector.
		}

		int []array = new int[values.size()]; //Se crea un vector con el tamaño de los diferentes VALORES DE CLASE encontrados.
		
		for ( int i = 0; i < array.length; i++ ) //Recorremos el vector...
		{
			String symbol = (String)values.elementAt( i ); //Recoge el valor de CLASE correspondiente
			array[i] = modelDataset.getAttribute( attribute ).valueIndex( symbol );
		}
		
		values = null;
		
		return array;
	}
        
        //FUZZY
        public int indexValueClassPredominant( Vector data, int attribute ) 
	{
		int index = -1;
                String symbol = getCommonClass(data, attribute); //Recoge el valor de CLASE correspondiente
                index = modelDataset.getAttribute( attribute ).valueIndex( symbol ); //Me da el índice que corresponde a ese valor de clase.

		return index;
	}
        
        

	/** Function to return the most common class of the itemsets in data.
	 * 
	 * @param data 			All the itemsets.
	 * @param attribute		Index of attribute.
	 * 
	 * @return				The most common class.
	 */
	public String getCommonClass( Vector data, int attribute ) 
	{
		Vector values = new Vector();
		int []counter = new int[20]; 
		int num = data.size();
		int bestIndex = 0;
		
		for ( int i = 0; i < num; i++ )
		{
			Itemset current = (Itemset)data.elementAt( i );
			String symbol = modelDataset.getAttribute( attribute ).value( (int)current.getValue( attribute ) );
			int index = values.indexOf( symbol );
			
			if ( index < 0 )
				values.addElement( symbol );
			else
				counter[index]++;
		}

		for ( int i = 1; i < counter.length; i++ )
			if ( counter[i] > counter[bestIndex] )
				bestIndex = i;
		
		return (String)values.elementAt( bestIndex );
	}

	/** Function to returns a subset of data.
	 * 
	 * @param data			The itemsets that where to extract the subset.
	 * @param attribute		The attribute to make the division.
	 * @param value			The value of the attribute.
	 * 
	 * @return				All the itemsets in data that has the value given for the attribute given.
	 */
	public Vector getSubset( Vector data, int attribute, int value )
	{
		Vector subset = new Vector();
		int num = data.size();
		
		for ( int i = 0; i < num; i++ )
		{
			Itemset current = (Itemset)data.elementAt( i );
			if ( current.getValue( attribute ) == value ) 
				subset.addElement( current );
		}
		
		return subset;
	}
        
        //FUZZY
        public void getSubsetFuzzy( Vector<Itemset> data, int attribute, Vector<Vector<Itemset>> subsets_etiquetas){
            
            for (int etq = 0; etq < baseD.n_etiquetas; etq++)
                subsets_etiquetas.add(etq, new Vector());
            int num = data.size();
            
            //Para la variable seleccionada...
            for (int inst = 0; inst < num; inst++) //Para cada instancia de datos...
                for (int etq = 0; etq < baseD.n_etiquetas; etq++) //Para cada etiqueta...
                    if (baseD.gradosPertenencia.get(inst)[attribute][etq] > 0.0) //Si su grado de pertenencia a esa etiqueta es > 0, entonces dicha instancia debe de pertenencer a dicho conjunto de instancias por etiqueta.
                        subsets_etiquetas.get(etq).add(data.get(inst));
            
            
	}

	/** Function to returns a subset of data, which is the complement of the second argument.
	 * 
	 * @param data			The itemsets that where to extract the subset.
	 * @param oldset		The complement set.
	 * 
	 * @return				All the itemsets that are contained in data but are not in oldset.
	 */
	public Vector getComplement( Vector data, Vector oldset ) //FUZZY (Reutilizable)
	{
		Vector subset = new Vector();
		int num = data.size();
		
		for ( int i = 0; i < num; i++ )
		{
			Itemset current = (Itemset)data.elementAt( i );
			int index = oldset.indexOf( current );
			
			if ( index < 0 )
				subset.addElement( current );
		}
		
		return subset;
	}
        
        

	/** Function to compute the entropy of the set of data points.
	 * 
	 * @param data 		The set of itemsets over is wanted to compute the entropy.
	 * 
	 * @return			The entropy of data.
	 */
	public double computeEntropy( Vector data )
	{

		int numdata = data.size();
		
		if ( numdata == 0 ) 
			return 0;

		int attribute = modelDataset.getClassIndex();
		int numvalues = modelDataset.getClassAttribute().numValues();
		double sum = 0;

		for ( int i = 0; i < numvalues; i++ )
		{
			int count = 0;
			
			for ( int j = 0; j < numdata; j++ )
			{
				Itemset current = (Itemset)data.elementAt( j );

				if ( current.getValue( attribute ) == i ) 
					count++;
			}
			double probability = 1. * count / numdata;
			
			if ( count > 0 )
				sum += -probability * Math.log( probability );
		}
		
		return sum;
	}

	/**	Function to check if the specified attribute and value are already used to decompose the data.
	 * 
	 * @param node			The node to check at this time.
	 * @param attribute		The attribute to check.
	 * @param value			The value to check.
	 * 
	 * @return				True if the attribute and the values are already used to decompose, 
	 * 							or false otherwise.
	 */
	public boolean alreadyUsedToDecompose( Node node, int attribute, int value )
	{
		if ( node.getChildren() != null )
			if ( node.getDecompositionAttribute() == attribute && node.getDecompositionValue() == value ) 
				return true;
		if ( node.getParent() == null )
			return false;
		
		return alreadyUsedToDecompose( node.getParent(), attribute, value );
	}
        
        
        //FUZZY
        public boolean alreadyUsedToDecomposeFuzzy( Node node, int attribute)
	{
		if ( node.getChildren() != null )
			if ( node.getDecompositionAttribute() == attribute) 
				return true;
		if ( node.getParent() == null )
			return false;
		
		return alreadyUsedToDecomposeFuzzy( node.getParent(), attribute);
	}
        
        

	/** Function to decompose the specified node.
	 * 
	 * @param node			The node to decompose.
	 */
	public void decomposeNode( Node node )
	{
		double bestEntropy;
		boolean selected = false;
		int selectedAttribute = 0;
		int selectedValue = 0;
		int numdata = node.getData().size();
		int numinputattributes = modelDataset.numAttributes() - 1;
		
		node.setEntropy( computeEntropy( node.getData() ) );
		
		double initialEntropy = bestEntropy = node.getEntropy();

		if ( node.getEntropy() == 0 )
			return;

		//  The best attribute and value are located which causes maximum decrease in entropy. 
		for ( int i = 0; i < numinputattributes; i++ ) 
		{
			if ( i == modelDataset.getClassIndex() )
				continue;
			
			int numvalues = modelDataset.getAttribute(i).numValues();
		
			for ( int j = 0; j < numvalues; j++ )
			{
				if ( alreadyUsedToDecompose( node, i, j ) ) 
					continue;
				
				Vector subset = getSubset( node.getData(), i, j );
				
				if ( subset.size() == 0 )
					continue;
				
				Vector complement = getComplement( node.getData(), subset );
				double e1 = computeEntropy( subset );
				double e2 = computeEntropy( complement );
				double entropy = ( e1 * subset.size() + e2 * complement.size() ) / numdata;
				
				if ( entropy < bestEntropy ) 
				{
					selected = true;
					bestEntropy = entropy;
					selectedAttribute = i;
					selectedValue = j;
				}
			}
		}

		if ( selected == false )
		{
			return;
		}
		// Now divide the dataset into two using the selected attribute and value.
		node.setDecompositionAttribute( selectedAttribute );
		node.setDecompositionValue( selectedValue );
		node.setChildren( new Node [2] );
		node.addChildren(0, new Node() );
		node.getChildren( 0 ).setParent( node );
		node.getChildren( 0 ).setData( getSubset( node.getData(), selectedAttribute, selectedValue ) );
		node.getChildren()[1] = new Node();
		node.getChildren( 1 ).setParent( node );

		// This loop copies all the data that are not in the first child node into the second child node.
		for ( int j = 0; j < numdata; j++ ) 
		{
			Itemset current = (Itemset)node.getData().elementAt( j );
			
			if ( node.getChildren( 0 ).getData().indexOf( current ) >= 0 )
				continue;
			
			node.getChildren( 1 ).getData().addElement( current );
		}

		decomposeNode( node.getChildren()[0] );
		decomposeNode( node.getChildren()[1] );

		// There is no more any need to keep the original vector.  Release this memory.
		node.setData( null );		
	}
    
	/** Funtion to get all the itemsets of the dataset. 
	 * 
	 * @return The itemsets.
	 */
	private Vector getItemsets()
	{
		Vector itemsets = new Vector( modelDataset.numItemsets());
		
		for ( int i = 0; i < modelDataset.numItemsets(); i++ ){
			itemsets.addElement( modelDataset.itemset( i ) );
                        //System.out.println(itemsets.get(i).toString());
                }
                        
		
                return itemsets;
	}
    
    /** Writes the tree and the results of the training and the test in the file.
     * 
     * @throws java.io.IOException If the file cannot be written.
     */
  	public void printResult() throws IOException 
  	{
	  	long totalTime = ( System.currentTimeMillis() - startTime ) / 1000;
	  	long seconds = totalTime % 60;
	  	long minutes = ( ( totalTime - seconds ) % 3600 ) / 60;
  		String tree = "";
		PrintWriter resultPrint;

   		tree += writeTree( root, "" );
   		   		       			
      tree += "\n@TotalNumberOfNodes " + NumberOfNodes; 
      tree += "\n@NumberOfLeafs " + NumberOfLeafs; 
   		
   		tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets(); 
  		tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;	  		  		  		  		
  		tree += "\n@PercentageOfCorrectlyClassifiedTraining " + (float)(correct*100.0)/(float)trainDataset.numItemsets() + "%" ;  		
  		tree += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets()-correct);
  		tree += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float)((trainDataset.numItemsets()-correct)*100.0)/(float)trainDataset.numItemsets() + "%" ;
  		
		  tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets(); 
	  	tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;		  		  	
	  	tree += "\n@PercentageOfCorrectlyClassifiedTest " + (float)(testCorrect*100.0)/(float)testDataset.numItemsets() + "%" ;
	  	tree += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets()-testCorrect);		  		  	
	  	tree += "\n@PercentageOfInCorrectlyClassifiedTest " + (float)((testDataset.numItemsets()-testCorrect)*100.0)/(float)testDataset.numItemsets() + "%" ;
	  		  	
	  	tree += "\n\n@ElapsedTime " + ( totalTime - minutes * 60 - seconds ) / 3600 + ":" + minutes / 60 + ":" + seconds;

		resultPrint = new PrintWriter( new FileWriter ( resultFileName ) );
		resultPrint.print( getHeader() + "\n@decisiontree\n\n" + tree );
		resultPrint.close();
  	}
        
        
        public void printResultFuzzy() throws IOException 
  	{
	  	long totalTime = ( System.currentTimeMillis() - startTime ) / 1000;
	  	long seconds = totalTime % 60;
	  	long minutes = ( ( totalTime - seconds ) % 3600 ) / 60;
  		String tree = "";
		PrintWriter resultPrint;

   		tree += writeTreeFuzzy(root, "" );
   		   		       			
      tree += "\n@TotalNumberOfNodes " + NumberOfNodes; 
      tree += "\n@NumberOfLeafs " + NumberOfLeafs; 
   		
   		tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets(); 
  		tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;	  		  		  		  		
  		tree += "\n@PercentageOfCorrectlyClassifiedTraining " + (float)(correct*100.0)/(float)trainDataset.numItemsets() + "%" ;  		
  		tree += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets()-correct);
  		tree += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float)((trainDataset.numItemsets()-correct)*100.0)/(float)trainDataset.numItemsets() + "%" ;
  		
		  tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets(); 
	  	tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;		  		  	
	  	tree += "\n@PercentageOfCorrectlyClassifiedTest " + (float)(testCorrect*100.0)/(float)testDataset.numItemsets() + "%" ;
	  	tree += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets()-testCorrect);		  		  	
	  	tree += "\n@PercentageOfInCorrectlyClassifiedTest " + (float)((testDataset.numItemsets()-testCorrect)*100.0)/(float)testDataset.numItemsets() + "%" ;
	  		  	
	  	tree += "\n\n@ElapsedTime " + ( totalTime - minutes * 60 - seconds ) / 3600 + ":" + minutes / 60 + ":" + seconds;

		resultPrint = new PrintWriter( new FileWriter ( resultFileName ) );
		resultPrint.print( getHeader() + "\n@decisiontree\n\n" + tree );
		resultPrint.close();
  	}
    
    /** Evaluates the training dataset and writes the results in the file.
     * 
     */
	public void printTrain()
	{
		String text = getHeader();
		for ( int i = 0; i < trainDataset.numItemsets(); i++ )
		{
			try
			{
				Itemset itemset = trainDataset.itemset( i );				
				int cl = evaluateItemset( itemset, root );
			
				if ( cl == (int) itemset.getValue( trainDataset.getClassIndex() ) ) 
					correct++;
			
				text += trainDataset.getClassAttribute().value( cl ) + " " + 
				trainDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + "\n";
			}
			catch ( Exception e )
			{
				System.err.println( e.getMessage() );
			}
		}
		
   		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( trainOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file: " + e.getMessage() );
   		}
	}
        
        //FUZZY
        public void printTrainFuzzy()
	{
		String text = getHeader();
		for ( int i = 0; i < trainDataset.numItemsets(); i++ )
		{
			try
			{
				Itemset itemset = trainDataset.itemset( i ); //Recoge el itemset (que toque) del dataset de entrenamiento			
				int cl = evaluateItemsetFuzzy(itemset, root ); //Evalua ese itemset sacando que valor de clase (numérico) debe de dar respecto al árbol.
			
				if ( cl == (int) itemset.getValue( trainDataset.getClassIndex() ) )  //Comprueba que ese valor de clase (numérico) es correcto respecto al que tenía el itemset.
					correct++;
			
				text += trainDataset.getClassAttribute().value( cl ) + " " + 
				trainDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + "\n"; //Almacena en un string ambos valores de clases obtenidos, para después mostrarlos por fila.
			}
			catch ( Exception e )
			{
				//System.err.println( e.getMessage() );
			}
		}
		
   		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( trainOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file: " + e.getMessage() );
   		}
	}
        
	
    /** Evaluates the test dataset and writes the results in the file.
     * 
     */
	public void printTest()
	{
		String text = getHeader();
		
		for ( int i = 0; i < testDataset.numItemsets(); i++)
		{
			try
			{
				int cl = (int) evaluateItemset( testDataset.itemset( i ), root );
				Itemset itemset = testDataset.itemset( i );
			
				if ( cl == (int) itemset.getValue( testDataset.getClassIndex() ) ) 
					testCorrect++;
			
				text += testDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + " " + 
					testDataset.getClassAttribute().value( cl )+ "\n";
			}
			catch ( Exception e )
			{
				System.err.println( e.getMessage());
			}
		}
		
		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( testOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file." );
   		}
	}
        
        
        //FUZZY
        public void printTestFuzzy()
	{
		String text = getHeader();
		
		for ( int i = 0; i < testDataset.numItemsets(); i++)
		{
			try
			{
				int cl = (int) evaluateItemsetFuzzy(testDataset.itemset( i ), root );
				Itemset itemset = testDataset.itemset( i );
			
				if ( cl == (int) itemset.getValue( testDataset.getClassIndex() ) ) 
					testCorrect++;
			
				text += testDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + " " + 
					testDataset.getClassAttribute().value( cl )+ "\n";
			}
			catch ( Exception e )
			{
				//System.err.println( e.getMessage());
			}
		}
		
		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( testOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file." );
   		}
	}
        
        
  
	/** Main function.
	 * 
	 * @param args 			The parameters file.
	 */
   	public static void main(String[] args) {
		if ( args.length != 1){
  			System.err.println("\nError: you have to specify the parameters file\n\tusage: java -jar ID3fuzzy.jar parameterfile.txt" );
  			 System.exit(-1);
  		}
      	else{
			ID3fuzzy id3fuzzy = new ID3fuzzy( args[0] );
		}
   	}
        
        
    
        
    /**-------- MÉTODOS PARA EL CÁLCULO DE LAS GANANCIAS DE LOS ATRIBUTOS --------*/

    private void gradosDePertenencia(Vector<Itemset> itemset) {
        
        double valor_variable;
        for (int i = 0; i < itemset.size(); i++) { //Para cada instancia (tupla de datos del dataset)...
            
            for (int var = 0; var < itemset.get(i).getDataset().numAttributes()-1; var++){ //Atributos que no sean clase
                valor_variable = itemset.get(i).getValue(var);
                baseD.calcularGradosPertenencia(i, var, valor_variable);//Pasar el índice de la variable y su valor.
            }
            
            
            //Recoger el valor de la CLASE
            Attribute att = itemset.get(i).getDataset().getAttribute(itemset.get(i).getDataset().numAttributes()-1); //Atributo CLASE
            String valor_instancia_clase =  att.value( (int)itemset.get(i).values[itemset.get(i).getDataset().numAttributes()-1] ); //TODO: Esto en caso de que siempre sea discreta
            baseD.addValorClase(i,valor_instancia_clase);
            
            
        }
    
    }

    private double calcularEntropia(Vector<Itemset> itemsets, Vector valores_clase) {
        
        //1. Recorrer los valores que hay en las instancias de la variable CLASE. Así saber cuántos hay de cada uno.
        contador_valores_clase(itemsets, valores_clase);
        
        //2. Cálculo de la ENTROPÍA GENERAL.
        double entropia_general = calcularEntropia_general(valores_clase, itemsets);
        
        //3. Por cada VARIABLE, para sus correspondientes ETIQUETAS --> Calcular su Entropía.
        baseD.calcularSumatoriaGradosPertenencia(); //Calcular sumatoria de grados de pertenencia de cada variable-etiqueta 
        
        
        //4. CALCULAR LA SUMATORIA DE GRADOS DE PERTENENCIA DE CADA VARIABLE-ETIQUETA PARA DIVIDIR EN CUANTO SALE LA SUM(G.P.) t.q. SU VALOR DE VARIABLE CLASE SEA EL MISMO
            //p.e. Si dataset IRIS tiene 3 posibles valores de clase = {iris-versicolor, iris-setosa, iris-virginica} --> Habrá que sacar 3 sumatorias de grados de pertenencia por cada variable-etiqueta.
        baseD.sumatoria_GP_valorClase(itemsets.size());
        
        //5. Calcular las entropías de cada una de las etiquetas POR VARIABLE que existe.
        baseD.calcularEntropias_var_etq();
        
        return entropia_general; //Devuelve la ENTROPÍA GENERAL PARA TODAS LAS VARIABLES

    }
    
    
    private Vector<Pair<Integer,Double>> calcularGanancia(double entropia_general) {
        baseD.calcularGanancias_var(entropia_general);
        
        Vector<Pair<Integer,Double>> v_ord_ganancias = new Vector();
        Vector<Double> v_ganancias = new Vector(baseD.ganancias_var); //Copiamos el vector de ganancias...
       
        Collections.sort(v_ganancias);
        boolean encontrado;
        for(int i = v_ganancias.size()-1; i >= 0; i--){
            encontrado = false;
            for (int j = 0; j < baseD.ganancias_var.size() && !encontrado; j++){
                if (baseD.ganancias_var.get(j) == v_ganancias.get(i)){
                    v_ord_ganancias.addElement(new Pair(j, baseD.ganancias_var.get(j))); //Meter en ORDEN de mayor a menor ganancia, el nºatributo y su ganancia 
                    encontrado = true;
                } 
            }
        }
        return v_ord_ganancias;
    }
    
    
    private void contador_valores_clase(Vector<Itemset> itemsets,Vector valores_clase){
        for (int i = 0; i < modelDataset.numClasses(); i++){
            String valor_atributo = modelDataset.getClassAttribute().value(i);
            valores_clase.addElement(new Pair(valor_atributo,0));
        }
        
        
        for (int i = 0; i < itemsets.size(); i++) { //Por cada instancia...
            Attribute att = itemsets.get(i).getDataset().getAttribute(itemsets.get(i).getDataset().numAttributes()-1); //Atributo CLASE
            String valor_instancia_clase =  att.value( (int)itemsets.get(i).values[itemsets.get(i).getDataset().numAttributes()-1] );
            
            for (int j = 0; j < valores_clase.size(); j++){ //Para cada posible valor de clase...
                Pair<String,Integer> par = (Pair<String, Integer>)valores_clase.get(j);
                if (par.getKey() == valor_instancia_clase ) //Comprobar si son el mismo valor de clase...
                    valores_clase.set(j, new Pair(par.getKey(), par.getValue()+1));
                
            }
            
        }
        
    }   
    
    
    private double calcularEntropia_general(Vector valores_clase, Vector<Itemset> itemsets){
        double entropia_general = 0.0;
        for (int i = 0; i < modelDataset.numClasses(); i++){
            Pair<String,Integer> par = (Pair<String, Integer>)valores_clase.get(i);
            entropia_general -= ((double)par.getValue()/(double)itemsets.size()) * log2((double)par.getValue()/(double)itemsets.size());
        }
        return entropia_general;
    }

    private static double log2(double x){
        return (double) (Math.log(x) / Math.log(2));
    }
    
    
    
    
    
    
    
    
    /**------------- GENERACIÓN DE ARBOL PARA VALORES CONTINUOS Y ENTEROS -----------*/
    
    
    
    /** Run the algorithm.
	 *
	 */ 
	public void generateFuzzyTree()
	{
		root.setData( getItemsets() ); //getItemsets = Vector donde cada posición es una tupla de datos que contiene de todo.
                decomposeFuzzyNode( root );
	}
    
    
    /** Function to decompose the specified node.
	 * 
	 * @param node			The node to decompose.
	 */
	public void decomposeFuzzyNode(Node node) {
            
            boolean selected = false;
            int selectedAttribute = 0;
            int numdata = node.getData().size();
            Vector<Vector<Itemset>> subsets_etiquetas_atributo = new Vector();
            
            
            //1. Reinicio de las EEDD de la baseD menos el calculo de las etiquetas x-distribuidas.
            baseD.reinicio(modelDataset, node.getData().size());
            
            //2. Recorrer las instancias de datos del DATASET e ir rellenando el grado de pertenencia de dicho valor para cada una de las etiquetas respecto a su variable.
            gradosDePertenencia(node.getData());
            
            //3. Cálculo de la ENTROPÍA.
            Vector cont_valores_clase = new Vector();
            double entropia_general = calcularEntropia(node.getData(), cont_valores_clase); //Las entropías de variable-etiquetas están almacenadas en baseD (Clase contenedora de datos útiles).
            
            
            //4. Cálculo de la GANANCIA.
            Vector<Pair<Integer,Double>> v_ganancias_ordenado = calcularGanancia(entropia_general);
            
            node.setEntropy( entropia_general );
            if ( node.getEntropy() == 0 )
                    return;
            
            //  Obtener el atributo que mejor GANANCIA tiene t.q. no haya sido YA seleccionado en sus rama hijo o padres... 
            for ( int i = 0; i < v_ganancias_ordenado.size() && !selected; i++ ) //Para cada atributo...
            {
                    if ( v_ganancias_ordenado.get(i).getKey() == modelDataset.getClassIndex() ) //Comprobar que no sea un atributo CLASE
                            continue;
                    
                    if ( alreadyUsedToDecomposeFuzzy( node, v_ganancias_ordenado.get(i).getKey()) ) //Comprobar que no se haya usado ya para descomoponer
                            continue;

                    getSubsetFuzzy(node.getData(), i, subsets_etiquetas_atributo); //Calcular los conjuntos de itemset para cada etiqueta de esta variable.
                    selectedAttribute = v_ganancias_ordenado.get(i).getKey(); //Ponemos el indice del atributo que indica la división.
                    selected = true;
                    
            }

            if ( selected == false ){ //En caso de no seleccionarse ninguno, se empieza a volver hacia atrás en el árbol.
                node.setChildren(null);
                return;
            }
            

            // Dividido el dataset en N conjuntos usando el indice del atributo, ahora se crean los N nodos correspondientes.
            node.setDecompositionAttribute(selectedAttribute); //Indica el indice del atributo que provoca la división.
            
            
            Vector<Pair<Double,Double>> v_valueFuzzy = new Vector();
            for (int etq = 0; etq < NumberOfLabs; etq++)
                v_valueFuzzy.addElement(new Pair(baseD.BaseDatos[selectedAttribute][etq].x0, baseD.BaseDatos[selectedAttribute][etq].x3)); //Almaceno cada uno de los Pares<MIN,MAX> de las etiquetas que provocan la división.
            node.setDecompositionValueFuzzy(v_valueFuzzy);
            
            node.setChildren(new Node[subsets_etiquetas_atributo.size()]);
            for (int etq = 0; etq < subsets_etiquetas_atributo.size(); etq++) {
                node.addChildren(etq, new Node());
                node.getChildren(etq).setParent(node);
                node.getChildren(etq).setData(subsets_etiquetas_atributo.get(etq)); //Subconjunto de datos del nodo hijo 1.
                
                if (node.getChildren(etq).getData().size() > (int)0.05*modelDataset.numItemsets()){//Se seguirá descomponiendo si el número de instancias > al 5% de instancias TOTALES desde el inicio de la creación del árbol
                    if (!proporcionClase(cont_valores_clase)){
                        decomposeFuzzyNode(node.getChildren(etq));
                    }else{
                        node.deleteChildren(etq);
                    }
                }else{
                    node.deleteChildren(etq);
                }
                
            }
            
            // There is no more any need to keep the original vector.  Release this memory.
            node.setData( null );	
        }

    private boolean proporcionClase(Vector cont_valores_clase) {
        
        for (int j = 0; j < cont_valores_clase.size(); j++){
            Pair<String,Integer> par = (Pair<String, Integer>)cont_valores_clase.get(j);
            if ( (double)((double) par.getValue()/(double)baseD.n_instanicas_inicial) >= PROPORCION_CLASE_NODO)
                return true;
        }
        return false;
        
    }
    
    
    
}//id3fuzzy