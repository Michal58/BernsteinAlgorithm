package CorrectnessBaseImplementation.Structures;

import BaseTemplateElements.FunctionalDependency;

import java.util.Collection;
import java.util.HashSet;

public class HashGroupOfFunctionalDependencies extends HashSet<FunctionalDependency> implements GroupOfFunctionalDependencies{
    public HashGroupOfFunctionalDependencies(Collection<FunctionalDependency> functionalDependencies){
        super(functionalDependencies);
    }
}
