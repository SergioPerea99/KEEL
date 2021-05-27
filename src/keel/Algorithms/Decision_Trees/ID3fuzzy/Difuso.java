/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Decision_Trees.ID3fuzzy;

/**
 *
 * @author Sergio Perea
 */
public class Difuso {
    /* This class allows trapezium or triangular-shaped fuzzy set */
    public double x0, x1 ,x2 ,x3, y; 
    public String Nombre, Etiqueta;
    
    
    //MÉTODOS NECESARIOS PARA LA FUZZIFICACIÓN DEL ID3.
        
    public double Fuzzifica(double X) {
        /* If X are not in the rank D, the degree is 0 */
        if ((X < x0) || (X > x3)) {
            return (0);
        }
        if (X < x1) {
            return ((X - x0) * (y / (x1 - x0)));
        }
        if (X > x2) {
            return ((x3 - X) * (y / (x3 - x2)));
        }

        return (y);
    }
    
}
