/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Decision_Trees.ID3fuzzy;

import java.util.Vector;
import javafx.util.Pair;

/**
 *
 * @author Lenovo
 */
public class BaseD {
    
    public int n_variables;
    public int n_etiquetas;
    public int n_valores_clase;
    public int n_instancias;

    public TipoIntervalo[] extremos;
    public TipoIntervalo[][] intervalos;
    
    
    //Los conjuntos difusos ("triangulos" en este caso) que marcan la función de pertenencia de cada etiqueta de una variable.
    public Difuso[][] BaseDatos;
    
    //Vector que: las posiciones indican las INSTANCIAS, su contenido es una matriz que indica (posiciones -> [variable][etiqueta] , contenido -> grado de pertenencia)
    public Vector<double[][]> gradosPertenencia;
    
    //Vector que: las posiciones son las INSTANCIAS y su contenido es el VALOR de la variable CLASE para esa instancia.
    public Vector<String> valores_clase;
    
    //Vector que recoge las sumas de todos los grados de pertenencia de cada etiqueta respecto a su variable. (siendo cada posición del vector una variable)
    public Vector<Vector<Double>> sum_grados_pertenencia_var_etq; 

    //Vector que recoge por los N posibles valores que puede tomar la variable CLASE. Un PAR(clave = valor_clase, valor = una matriz t.q. posiciones [variable][etiqueta] y valor = sumatoria de grados.)
    public Vector<Pair<String,double[][]>> sum_GP_valorClase_var_etq;
    
    
    public BaseD(int MaxEtiquetas, Dataset dataset) {
        int i, j;
        
        //Se inicializan...
        n_variables = dataset.numAttributes(); //El número de variables (sin contar variable CLASE --> CAMBIAR)
        n_etiquetas = MaxEtiquetas; //Nº etiquetas
        n_valores_clase = dataset.numClasses(); //Nº posibles valores de la CLASE
        n_instancias = dataset.IS.getNumInstances(); //Nº de tuplas (instancias) de datos.
        
        BaseDatos = new Difuso[n_variables][MaxEtiquetas]; 
        valores_clase = new Vector();
        
        gradosPertenencia = new Vector();
        for (i = 0; i < n_instancias; i++){ //Para cada instancia...
            gradosPertenencia.addElement(new double[n_variables][n_etiquetas]);
            for (int var = 0; var < n_variables; var++){ //Para cada variable...
              gradosPertenencia.get(i)[var] = new double[n_etiquetas]; 
              for (int etq = 0; etq < n_etiquetas; etq++) //Para cada etiqueta...
                  gradosPertenencia.get(i)[var][etq] = 0.0; 
              
            }
        }
        
        
        sum_grados_pertenencia_var_etq = new Vector();
        for (i = 0; i < n_variables-1; i++) //Todas las variables menos la variable CLASE
            sum_grados_pertenencia_var_etq.addElement(new Vector(MaxEtiquetas));
           
        
        sum_GP_valorClase_var_etq = new Vector();
        for (i = 0; i < n_valores_clase; i++){ //Para cada posible valor que puede tomar la CLASE...
            sum_GP_valorClase_var_etq.add(i, new Pair(dataset.getClassAttribute().value(i),new double[n_variables-1][n_etiquetas]));
            for (int var = 0; var < n_variables-1; var++) { //Para cada variable... (que no sea la CLASE)
                sum_GP_valorClase_var_etq.get(i).getValue()[var] = new double[n_etiquetas];
                for (int etq = 0; etq < n_etiquetas; etq++) //Para cada etiqueta...
                    sum_GP_valorClase_var_etq.get(i).getValue()[var][etq] = 0.0;
            }
        }
        
        
        Attribute a1;
        for (i = 0; i < n_variables; i++) {
            BaseDatos[i] = new Difuso[MaxEtiquetas];
            for (j = 0; j < MaxEtiquetas; j++)
                BaseDatos[i][j] = new Difuso(); //Para cada etiqueta de cada variable se crea un Difuso por defecto (Es decir, sin nada). 
        }
        
        System.out.println("HE LLEGADO HASTA AQUI 2");
        

        extremos = new TipoIntervalo[n_variables];
        for (i = 0; i < n_variables; i++) {
            a1 = (Attribute)dataset.attributes.get(i);
            if (a1.isContinuous())
                extremos[i] = new TipoIntervalo((double)a1.getMinRange(), (double)a1.getMaxRange());
        }
        
        System.out.println("HE LLEGADO HASTA AQUI 3");
    }


    // Rounds the generated value for the semantics
    public double Asigna(double val, double tope) {
        if ((val > -1E-4) && (val < 1E-4)) {
            return (0);
        }
        if ((val > tope - 1E-4) && (val < tope + 1E-4)) {
            return (tope);
        }

        return (val);
    }


    /**
     * Método encargado de crear las etiquetas de forma x-distribuida.
     */
    public void Semantica(Dataset dataset) {
        int var, etq;
        double marca, valor;
        double[] punto = new double[3];
        double[] punto_medio = new double[2];
        
        /* we generate the fuzzy partitions of the variables */
        Attribute a1;
        for (var = 0; var < n_variables; var++) {
            
            a1 = (Attribute)dataset.attributes.get(var);
            
            if (a1.isContinuous()){
                marca = (extremos[var].max - extremos[var].min) /
                        ((double) n_etiquetas - 1);
                for (etq = 0; etq < n_etiquetas; etq++) {
                    valor = extremos[var].min + marca * (etq - 1);
                    BaseDatos[var][etq].x0 = Asigna(valor, extremos[var].max);
                    valor = extremos[var].min + marca * etq;
                    BaseDatos[var][etq].x1 = Asigna(valor, extremos[var].max);
                    BaseDatos[var][etq].x2 = BaseDatos[var][etq].x1;
                    valor = extremos[var].min + marca * (etq + 1);
                    BaseDatos[var][etq].x3 = Asigna(valor, extremos[var].max);
                    BaseDatos[var][etq].y = 1;
                    BaseDatos[var][etq].Nombre = "V" + (var + 1);
                    BaseDatos[var][etq].Etiqueta = "E" + (etq + 1);
                }
            }
        }
        System.out.println("HE LLEGADO HASTA AQUI 5");
    }
    
    
    
    public void calcularGradosPertenencia(int instancia, int var, double valor_dato){
        boolean salir = false;
        
        for(int i = 0; i < gradosPertenencia.get(instancia)[var].length && !salir; i++){ //Recorre las etiquetas de la variable "var"
            gradosPertenencia.get(instancia)[var][i] = BaseDatos[var][i].Fuzzifica(valor_dato);
            if (gradosPertenencia.get(instancia)[var][i] > 0.0 && i < gradosPertenencia.get(instancia)[var].length-1){
                gradosPertenencia.get(instancia)[var][i+1] = 1 - gradosPertenencia.get(instancia)[var][i]; //Sabiendo que son x-distribuidas, la continua a ella es 1-grado_pertenencia
                salir = true; //Los demás se quedan con su valor por defecto, el es 0.0 que es el que les corresponderá.
            }
        }
    }
    
    
    public void addValorClase(int pos_instancia, String valor){
        valores_clase.add(pos_instancia, valor);
    }
    
    
    public void calcularSumatoriaGradosPertenencia(){
        double sum;
        Vector<Double> v_sumas;
        for (int var = 0; var < n_variables-1; var++) { //Por cada variable... (que no sea variable CLASE)
            v_sumas = new Vector(n_etiquetas);
            for (int etq = 0; etq < n_etiquetas; etq++){ //Por cada etiqueta...
                sum = 0.0;
                for (int ins = 0; ins < gradosPertenencia.size(); ins++){ //Por cada instancia...
                    sum += gradosPertenencia.get(ins)[var][etq];
                }
                v_sumas.add(etq, sum); //Añadir la sumatoria de grados de pertenencia de la etiqueta correspondiente.
            }
            sum_grados_pertenencia_var_etq.add(var, v_sumas); //Sumar el vector de sumatorias de grados de etiquetas en la posición de la variable correspondiente.
        }
        
        
        //Mostrar que se calcula bien...
        /*for (int i = 0; i < sum_grados_pertenencia_var_etq.size(); i++) {
            for (int etq = 0; etq < sum_grados_pertenencia_var_etq.get(i).size(); etq++) {
                System.out.println("Variable "+i+" Etiqueta "+etq+" --> Sumatoria de grados = "+sum_grados_pertenencia_var_etq.get(i).get(etq));
            }
        }*/
        
    }
    
    
    public void sumatoria_GP_valorClase() {
        //4. CALCULAR LA SUMATORIA DE GRADOS DE PERTENENCIA DE CADA VARIABLE-ETIQUETA PARA DIVIDIR EN CUANTO SALE LA SUM(G.P.) t.q. SU VALOR DE VARIABLE CLASE SEA EL MISMO
        //p.e. Si dataset IRIS tiene 3 posibles valores de clase = {iris-versicolor, iris-setosa, iris-virginica} --> Habrá que sacar 3 sumatorias de grados de pertenencia por cada variable-etiqueta.
        
        for (int ins = 0; ins < n_instancias; ins++) { //Para cada instancia...
            for (int pos_valor_clase = 0; pos_valor_clase < n_valores_clase; pos_valor_clase++) { //Para cada posible valor de clase...
                
                if ( sum_GP_valorClase_var_etq.get(pos_valor_clase).getKey().equals( valores_clase.get(ins) ) ){ //Si la instancia tiene el valor de clase correspondiente al vector de sum_GP_valorClase_var_etq...
                    
                    //Como esa instancia tiene valor de clase X --> Hay que hacer sumatoria a cada una de las variable-etiquetas para ese valor de clase X.
                    for (int var = 0; var < sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue().length; var++)
                        for (int etq = 0; etq < sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue()[var].length; etq++)
                            sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue()[var][etq] += gradosPertenencia.get(ins)[var][etq]; //SUMAR LOS GRADOS DE PERTENENCIA DE LA INSTANCIA CORRESPONDIENTE.
                    
                }
            }
        }
    }
    
    public String toStringBD(Dataset dataset){
        String result = "";
        Attribute a1;
        for (int var = 0; var < n_variables; var++){
            a1 = (Attribute)dataset.attributes.get(var);
            if (a1.isContinuous()){
                for (int etq = 0; etq < n_etiquetas; etq++)
                    result += BaseDatos[var][etq].Nombre+"_"+BaseDatos[var][etq].Etiqueta+" = {"+BaseDatos[var][etq].x0+", "+BaseDatos[var][etq].x1+", "+ BaseDatos[var][etq].x2 +", "+ BaseDatos[var][etq].x3 +"}\n";
            }
        }
        return result;
    }
    
    
    public String toStringGradoPert(){
        String result = "";
        for (int ins = 0; ins < gradosPertenencia.size(); ins++){
            result += "INSTANCIA nº"+ins+":\n";
            for (int var = 0; var < n_variables; var++){
                result += "Variable "+var+" -> ";
                for (int etq = 0; etq < n_etiquetas; etq++)
                    result += gradosPertenencia.get(ins)[var][etq]+" ";
                result += "\n";
            }
            result += "\n";
        }
        return result;
    }

    public String toString_sumGP_valorClase() {
        String result = "";
        result += "------- SUMATORIA DE GRADOS DE PERTENENCIA SEGÚN EL VALOR DE CLASE POR CADA VARIABLE-ETIQUETA --------\n";
        for (int pos_valor_clase = 0; pos_valor_clase < n_valores_clase; pos_valor_clase++) {
            result += "Valor de clase = "+ sum_GP_valorClase_var_etq.get(pos_valor_clase).getKey()+" : \n";
            for (int var = 0; var < sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue().length; var++) {
                for (int etq = 0; etq < sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue()[var].length; etq++) {
                    result += "V"+var+"E"+etq+" = "+sum_GP_valorClase_var_etq.get(pos_valor_clase).getValue()[var][etq]+" ";
                }
                result += "\n";
            }
        }
        
        //Mostrar que se calcula bien...
        for (int i = 0; i < sum_grados_pertenencia_var_etq.size(); i++) {
            for (int etq = 0; etq < sum_grados_pertenencia_var_etq.get(i).size(); etq++) {
                System.out.println("Variable "+i+" Etiqueta "+etq+" --> Sumatoria de grados = "+sum_grados_pertenencia_var_etq.get(i).get(etq));
            }
        }
        
        return result;
    }

    
}
