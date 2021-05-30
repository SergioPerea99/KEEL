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
public class TipoIntervalo {
    /* Each interval type has this form */
    public double min;
    public double max;
    
    
    public TipoIntervalo(double _min, double _max){
        min = _min;
        max = _max;
    }
}
