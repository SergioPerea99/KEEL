/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Decision_Trees.ID3fuzzy;

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


    public BaseD(int MaxEtiquetas, Dataset dataset) {
        int i, j;
        
        //Se inicializan...
        n_variables = dataset.numAttributes(); //El número de variables (sin contar variable CLASE --> CAMBIAR)
        BaseDatos = new Difuso[n_variables][MaxEtiquetas]; //Los "triangulos" que marcan la función de pertenencia de cada etiqueta de una variable.
        
        Attribute a1;
        for (i = 0; i < n_variables; i++) {
            BaseDatos[i] = new Difuso[MaxEtiquetas];
            for (j = 0; j < MaxEtiquetas; j++) {
                BaseDatos[i][j] = new Difuso(); //Para cada etiqueta de cada variable se crea un Difuso por defecto (Es decir, sin nada). 
                
            }
        }
        System.out.println("HE LLEGADO HASTA AQUI 2");
        n_etiquetas = MaxEtiquetas;

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
    public void Semantica() {
        int var, etq;
        double marca, valor;
        double[] punto = new double[3];
        double[] punto_medio = new double[2];
        
        /* we generate the fuzzy partitions of the variables */
        for (var = 0; var < n_variables; var++) {
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
                System.out.println(BaseDatos[var][etq].Nombre+"_"+BaseDatos[var][etq].Etiqueta+" = {"+BaseDatos[var][etq].x0+", "+BaseDatos[var][etq].x1+", "+ BaseDatos[var][etq].x2 +", "+ BaseDatos[var][etq].x3 +"}");
            }
        }
        System.out.println("HE LLEGADO HASTA AQUI 5");
    }
}
