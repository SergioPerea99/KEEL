/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Decision_Trees.ID3fuzzy;

import java.util.Vector;

/**
 *
 * @author Lenovo
 */
public class BaseD {
    public Difuso[][] BaseDatos;
    public int n_variables;
    public int n_etiquetas;

    public TipoIntervalo[] extremos;
    public TipoIntervalo[][] intervalos;
    
    //Vector que: las posiciones indican las INSTANCIAS, su contenido es una matriz que indica (posiciones -> [variable][etiqueta] , contenido -> grado de pertenencia)
    public Vector<double[][]> gradosPertenencia;
    
    //Vector que: las posiciones son las INSTANCIAS y su contenido es el VALOR de la variable CLASE para esa instancia.
    public Vector<String> valores_clase;
    
    //Vector que recoge las sumas de todos los grados de pertenencia de cada etiqueta respecto a su variable. (siendo cada posición del vector una variable)
    public Vector<Vector<Double>> sum_grados_pertenencia_var_etq; 
    
    
    public BaseD(int MaxEtiquetas, Dataset dataset) {
        int i, j;
        
        //Se inicializan...
        n_variables = dataset.numAttributes(); //El número de variables (sin contar variable CLASE --> CAMBIAR)
        n_etiquetas = MaxEtiquetas;
        BaseDatos = new Difuso[n_variables][MaxEtiquetas]; //Los "triangulos" que marcan la función de pertenencia de cada etiqueta de una variable.
        valores_clase = new Vector();
        
        sum_grados_pertenencia_var_etq = new Vector();
        for (i = 0; i < n_variables-1; i++) //Todas las variables menos la variable CLASE
            sum_grados_pertenencia_var_etq.addElement(new Vector(MaxEtiquetas));
        
        gradosPertenencia = new Vector();
        for (i = 0; i < dataset.IS.getNumInstances(); i++){
            gradosPertenencia.addElement(new double[n_variables][MaxEtiquetas]);
            for (j = 0; j < n_variables; j++){
              gradosPertenencia.get(i)[j] = new double[MaxEtiquetas]; 
              for (int k = 0; k < MaxEtiquetas; k++){
                  gradosPertenencia.get(i)[j][k] = 0.0; //Inicializado a 0, por si no se recorren todos a la hora de calcular los grados...
              }
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
                //System.out.println("VARIABLE "+var+" --> "+valor_dato+" ::::: {etiqueta"+i+" = "+gradosPertenencia.get(instancia)[var][i]+"etiqueta"+(i+1)+" = "+gradosPertenencia.get(instancia)[var][i+1]+"}");
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
        for (int i = 0; i < sum_grados_pertenencia_var_etq.size(); i++) {
            for (int etq = 0; etq < sum_grados_pertenencia_var_etq.get(i).size(); etq++) {
                System.out.println("Variable "+i+" Etiqueta "+etq+" --> Sumatoria de grados = "+sum_grados_pertenencia_var_etq.get(i).get(etq));
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
}
