package PerformanceImprovedImplementation.Structures;


import BaseTemplateElements.AlgorithmState;
import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;

import java.util.*;

public class DependenciesOperationalSet extends ListSet<FunctionalDependency> implements AlgorithmState {
    private final boolean shouldMemorizeDerivableDependencies;
    private class AssociatedDependencies extends LinkedList<DependencyOwningSpecificAttribute>{
    }
    // 'String' below represents specific attribute
    private Map<Attribute, AssociatedDependencies> dictionaryOfDependenciesOwningSpecificAttribute;
    private ListSet<FunctionalDependency> derivableDependencies;

    private void createAssociatedDependenciesWithAttribute(Attribute associatedAttribute){
        dictionaryOfDependenciesOwningSpecificAttribute.put(associatedAttribute,new AssociatedDependencies());
    }

    private void addDependencyToItsAssociatedGroup(Attribute specificAttribute,DependencyOwningSpecificAttribute dependencyOperationalForm){
        AssociatedDependencies associatedGroup=dictionaryOfDependenciesOwningSpecificAttribute.get(specificAttribute);
        associatedGroup.add(dependencyOperationalForm);
    }

    private void deriveDictionaryOfDependenciesOwningSpecificAttribute(){
        dictionaryOfDependenciesOwningSpecificAttribute=new HashMap<>();

        for (FunctionalDependency dependency : this) {
            DependencyOwningSpecificAttribute dependencyOperationalForm=new DependencyOwningSpecificAttribute(dependency);
            for (Attribute leftAttribute : dependency.getLeftAttributes()) {
                if (!dictionaryOfDependenciesOwningSpecificAttribute.containsKey(leftAttribute))
                    createAssociatedDependenciesWithAttribute(leftAttribute);
                addDependencyToItsAssociatedGroup(leftAttribute,dependencyOperationalForm);
            }
        }
    }

    private void updateClosureElementsWithNewDerivableDependency(FunctionalDependency derivableDependency, Attributes newClosureElements, Attributes currentClosure){
        derivableDependency.getRightAttributes().forEach(attribute-> {
            if (!currentClosure.contains(attribute))
                newClosureElements.add(attribute);
        });
        currentClosure.addAll(derivableDependency.getRightAttributes());
    }

    private void findAndUpdateClosureElementsWithNewDerivableDependencies(Attribute currentAttribute, Attributes newClosureElements, Attributes currentClosure){
        if (!dictionaryOfDependenciesOwningSpecificAttribute.containsKey(currentAttribute))
            return;
        for (DependencyOwningSpecificAttribute dependencyOwningCurrentAttribute : dictionaryOfDependenciesOwningSpecificAttribute.get(currentAttribute)) {
            dependencyOwningCurrentAttribute.signalizeItsAttributeWasAssociatedWithClosure();
            if (dependencyOwningCurrentAttribute.isDerivableFromCurrentClosure()){
                FunctionalDependency derivableDependency=dependencyOwningCurrentAttribute.getAssociatedDependency();
                updateClosureElementsWithNewDerivableDependency(derivableDependency,newClosureElements,currentClosure);
                if (shouldMemorizeDerivableDependencies)
                    derivableDependencies.add(derivableDependency);
            }
        }
    }

    private void buildGivenClosure(Attributes closure){
        if (shouldMemorizeDerivableDependencies)
            derivableDependencies=new ListSet<>();

        NewClosureAttributes newClosureElements=new NewClosureAttributes(closure);

        while (!newClosureElements.isEmpty()){
            Attribute currentAttribute= newClosureElements.getAndThenRemoveAnyAttribute();
            findAndUpdateClosureElementsWithNewDerivableDependencies(currentAttribute,newClosureElements,closure);
        }
    }

    public DependenciesOperationalSet(){
        super();
        shouldMemorizeDerivableDependencies=false;
    }

    public DependenciesOperationalSet(Collection<FunctionalDependency> dependencies){
        super(dependencies);
        shouldMemorizeDerivableDependencies = false;
    }
    public DependenciesOperationalSet(Collection<FunctionalDependency> dependencies, boolean shouldMemorizeDerivableDependencies){
        super(dependencies);
        this.shouldMemorizeDerivableDependencies = shouldMemorizeDerivableDependencies;
    }

    public Attributes getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(Attributes baseOfClosureBuilding){
        deriveDictionaryOfDependenciesOwningSpecificAttribute();
        Attributes closure=new AttributesHashSet(baseOfClosureBuilding);
        buildGivenClosure(closure);
        return closure;
    }

    public boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency){
        Attributes closureAttributes=getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(possibleDependency.getLeftAttributes());
        return closureAttributes.containsAll(possibleDependency.getRightAttributes());
    }

    public FunctionalDependency removeExtraneousAttributesFromLeftSideOfDependency(FunctionalDependency initialDependency){
        FunctionalDependency copyOfDependency=initialDependency.getCopy();
        for (Attribute leftAttribute : initialDependency.getLeftAttributes()) {
            copyOfDependency.getLeftAttributes().remove(leftAttribute);
            boolean wasLeftAttributeExtraneous=checkIfThereIsTransitiveDependency(copyOfDependency);
            if (!wasLeftAttributeExtraneous)
                copyOfDependency.getLeftAttributes().add(leftAttribute);
        }
        return copyOfDependency;
    }

    private class RemoverOfRightAttributes{
        private final FunctionalDependency currentDependencyForRightSideReducing;
        private RemoverOfRightAttributes(FunctionalDependency currentDependencyForRightSideReducing){
            this.currentDependencyForRightSideReducing=currentDependencyForRightSideReducing;
        }
        private void setRightSideOfDependencyToSingleAttribute(FunctionalDependency dependency,Attribute attributeToSet){
            dependency.getRightAttributes().clear();
            dependency.getRightAttributes().add(attributeToSet);
        }
        private boolean executeRemovingAndAnswerIfDependencyStillExist(){
            FunctionalDependency dependencyToBeKept=currentDependencyForRightSideReducing.getCopy();
            List<Attribute> attributesToCheckIfCanBeRemoved=new LinkedList<>(currentDependencyForRightSideReducing.getRightAttributes());

            for (Attribute attributeToRemove : attributesToCheckIfCanBeRemoved) {
                currentDependencyForRightSideReducing.getRightAttributes().remove(attributeToRemove);
                setRightSideOfDependencyToSingleAttribute(dependencyToBeKept,attributeToRemove);
                boolean isDependencyKept=checkIfThereIsTransitiveDependency(dependencyToBeKept);
                if (!isDependencyKept)
                    currentDependencyForRightSideReducing.getRightAttributes().add(attributeToRemove);
            }
            return currentDependencyForRightSideReducing.getRightAttributes().isEmpty();
        }
    }

    public void transformItselfIntoMinimalCover(){
        minimizeCoverOfGivenDependencies(this);
    }

    public void minimizeCoverOfGivenDependencies(Set<FunctionalDependency> dependencies){
        Iterator<FunctionalDependency> givenDependenciesIterator=dependencies.iterator();
        while (givenDependenciesIterator.hasNext()){
            FunctionalDependency currentDependencyForRightSideReducing=givenDependenciesIterator.next();
            RemoverOfRightAttributes rightSideReducer=new RemoverOfRightAttributes(currentDependencyForRightSideReducing);
            if (rightSideReducer.executeRemovingAndAnswerIfDependencyStillExist())
                givenDependenciesIterator.remove();
        }
    }

    public ListSet<FunctionalDependency> getDerivableDependencies() {
        return derivableDependencies;
    }
}
