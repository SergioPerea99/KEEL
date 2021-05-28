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
	
  /** Total number of Nodes in the tree */
	int NumberOfNodes;  
	
	/** Number of Leafs in the tree */
	int NumberOfLeafs;  
        
        /** Numero de etiquetas por variable*/
        int NumberOfLabs;
	
	
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
                NumberOfLabs = 5;
                
    		/*check if there are continous attributes*/
    		if(Attributes.hasRealAttributes() || Attributes.hasIntegerAttributes())
    		{
                    System.out.println("HE LLEGADO HASTA AQUI 1");
                    
                    //En caso de tener atributos continuos, aplicar la parte del fuzzificador
                    

                    //1. Crear las etiquetas de las variables, de forma x-distribuida con la función de pertenencia triangular.
                    BaseD baseD = new BaseD(NumberOfLabs, modelDataset); //TODO: HACER QUE ESE 3 (3 ETIQUETAS) SE INDIQUE DESDE EL FICHERO DE CONFIGURACIÓN
                    baseD.Semantica(modelDataset); //Crea las N etiquetas por los M atributos de forma x-distribuida.
                    //System.out.println(baseD.toStringBD(modelDataset));
                    
                    
                    //2. Recorrer las instancias de datos del DATASET e ir rellenando el grado de pertenencia de dicho valor para cada una de las etiquetas respecto a su variable.
                    gradosDePertenencia(baseD);
                    //System.out.println(baseD.toStringGradoPert());
                    System.out.println("HE LLEGADO HASTA AQUI 6");
                    
                    
                    //3. Cálculo de la ENTROPÍA.
                    double entropia_general = calcularEntropia(baseD); //Las entropías de variable-etiquetas están almacenadas en baseD (Clase contenedora de datos útiles).
                    
                    //4. Cálculo de la GANANCIA.
                    calcularGanancia(entropia_general, baseD);
                    System.out.println(baseD.toString_ganancia_var());
                    
    		}
    		
                else
                {
                    
                    // Executes the algorithm.
                    generateTree();

                    // Prints the results generates by the algorithm.
                    printTrain();
                    printTest();
                    printResult();
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
				" == \"" + modelDataset.getAttribute( node.getDecompositionAttribute() ).
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
						aux = classAtt.value( values[0] );
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
		
		for ( int i = 0; i < num; i++ )
		{
			Itemset current = (Itemset) data.elementAt( i );
			String symbol = modelDataset.getAttribute( attribute ).value( (int)current.getValue( attribute ) );
			int index = values.indexOf( symbol );
			
			if ( index < 0 )
				values.addElement( symbol );
		}

		int []array = new int[values.size()];
		
		for ( int i = 0; i < array.length; i++ )
		{
			String symbol = (String)values.elementAt( i );
			array[i] = modelDataset.getAttribute( attribute ).valueIndex( symbol );
		}
		
		values = null;
		
		return array;
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

	/** Function to returns a subset of data, which is the complement of the second argument.
	 * 
	 * @param data			The itemsets that where to extract the subset.
	 * @param oldset		The complement set.
	 * 
	 * @return				All the itemsets that are contained in data but are not in oldset.
	 */
	public Vector getComplement( Vector data, Vector oldset )
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
		node.addChildren( new Node() );
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
		
		for ( int i = 0; i < modelDataset.numItemsets(); i++ )
			itemsets.addElement( modelDataset.itemset( i ) );
		
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

    private void gradosDePertenencia(BaseD baseD) {
        //Para cada instancia (tupla de datos del dataset)...
        double valor_variable;
        for (int i = 0; i < modelDataset.IS.getNumInstances(); i++) {
            String tupla = modelDataset.IS.getInstances()[i].toString();
            String[] partes_tupla = tupla.split(",");
            
            //Atributos que no sean clase
            for (int j = 0; j < partes_tupla.length-1; j++){ //Todas las partes menos la de la clase
                //Tiene que haber tantas como variables (no clase)...
                valor_variable = Double.parseDouble(partes_tupla[j]);
                baseD.calcularGradosPertenencia(i, j, valor_variable);//Pasar el índice de la variable y su valor.
            }
            
            //VALOR del atributo clase
            String valor_instancia_clase = partes_tupla[partes_tupla.length-1]; //TODO: Esto en caso de que siempre sea discreta
            baseD.addValorClase(i,valor_instancia_clase);
            
            
        }
    
    }

    private double calcularEntropia(BaseD baseD) {
        //1. Recorrer los valores que hay en las instancias de la variable CLASE. Así saber cuántos hay de cada uno.
        Vector valores_clase = contador_valores_clase();
        
        //2. Cálculo de la ENTROPÍA GENERAL.
        double entropia_general = calcularEntropia_general(valores_clase);
        
        
        //3. Por cada VARIABLE, para sus correspondientes ETIQUETAS --> Calcular su Entropía.
        baseD.calcularSumatoriaGradosPertenencia(); //Calcular sumatoria de grados de pertenencia de cada variable-etiqueta 
        
        
        
        //4. CALCULAR LA SUMATORIA DE GRADOS DE PERTENENCIA DE CADA VARIABLE-ETIQUETA PARA DIVIDIR EN CUANTO SALE LA SUM(G.P.) t.q. SU VALOR DE VARIABLE CLASE SEA EL MISMO
            //p.e. Si dataset IRIS tiene 3 posibles valores de clase = {iris-versicolor, iris-setosa, iris-virginica} --> Habrá que sacar 3 sumatorias de grados de pertenencia por cada variable-etiqueta.
        baseD.sumatoria_GP_valorClase();
        
        System.out.println("HE LLEGADO HASTA AQUI 7");
        
        //5. Calcular las entropías de cada una de las etiquetas POR VARIABLE que existe.
        baseD.calcularEntropias_var_etq();
        
        return entropia_general; //Devuelve la ENTROPÍA GENERAL PARA TODAS LAS VARIABLES

    }
    
    
    private void calcularGanancia(double entropia_general, BaseD baseD) {
        baseD.calcularGanancias_var(entropia_general);
    }
    
    
    private Vector contador_valores_clase(){
        Vector valores_clase = new Vector();
        for (int i = 0; i < modelDataset.numClasses(); i++){
            String valor_atributo = modelDataset.getClassAttribute().value(i);
            valores_clase.addElement(new Pair(valor_atributo,0));
        }
        
        for (int i = 0; i < modelDataset.IS.getNumInstances(); i++) { //Por cada instancia...
            String tupla = modelDataset.IS.getInstances()[i].toString();
            String[] partes_tupla = tupla.split(",");
            
            for (int j = 0; j < valores_clase.size(); j++){
                Pair<String,Integer> par = (Pair<String, Integer>)valores_clase.get(j);
                if (partes_tupla[partes_tupla.length-1].equals(par.getKey()) ) //Comprobar si son el mismo valor de clase...
                    valores_clase.set(j, new Pair(par.getKey(), par.getValue()+1));
                
            }
            
        }
        
        /*System.out.println("");
        System.out.println("Archivo "+modelDataset.getName()+" con "+modelDataset.numClasses()+" : ");
        for (int j = 0; j < valores_clase.size(); j++){
            Pair<String,Integer> par = (Pair<String, Integer>)valores_clase.get(j);
            System.out.println("{"+par.getKey()+", "+par.getValue()+" de "+modelDataset.IS.getNumInstances()+"}");
        }*/
        
        return valores_clase;
    }   
    
    
    private double calcularEntropia_general(Vector valores_clase){
        double entropia_general = 0.0;
        for (int i = 0; i < modelDataset.numClasses(); i++){
            Pair<String,Integer> par = (Pair<String, Integer>)valores_clase.get(i);
            entropia_general -= ((double)par.getValue()/(double)modelDataset.IS.getNumInstances()) * log2((double)par.getValue()/(double)modelDataset.IS.getNumInstances());
        }
        //System.out.println("ENTROPIA GENERAL = "+entropia_general);
        return entropia_general;
    }

    private static double log2(double x){
        return (double) (Math.log(x) / Math.log(2));
    }

    
    
    
}//id3fuzzy