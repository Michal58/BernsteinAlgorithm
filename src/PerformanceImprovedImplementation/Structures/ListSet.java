package PerformanceImprovedImplementation.Structures;

import BaseTemplateElements.AlgorithmState;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class ListSet<T> extends LinkedList<T> implements Set<T>, AlgorithmState {
    public ListSet(){
        super();
    }
    public ListSet(Collection<T> someCollection){
        super(someCollection);
    }
}
