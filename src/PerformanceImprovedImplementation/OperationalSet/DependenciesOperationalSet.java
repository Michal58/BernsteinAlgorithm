package PerformanceImprovedImplementation.OperationalSet;


import BaseTemplateElements.Attribute;
import BaseTemplateElements.Attributes;
import BaseTemplateElements.FunctionalDependency;
import CommonElements.ListSet;
import CorrectnessBaseImplementation.Structures.AttributesHashSet;

import java.util.*;

public class DependenciesOperationalSet extends LinkedList<FunctionalDependency> implements CommonElements.DependenciesOperationalSet, Set<FunctionalDependency>, BaseTemplateElements.AlgorithmState {
    private final boolean shouldMemorizeDerivableDependencies;
    private class AssociatedDependencies extends LinkedList<OperationalContainerAsDependencyOwningSpecificAttribute>{
    }
    private Map<Attribute, AssociatedDependencies> dictionaryOfDependenciesOwningSpecificAttributes;
    private ListSet<FunctionalDependency> derivableDependencies;

    private void createNewAssociatedDependenciesGroupWithAttribute(Attribute associatedAttribute){
        dictionaryOfDependenciesOwningSpecificAttributes.put(associatedAttribute,new AssociatedDependencies());
    }

    private void addDependencyToItsAssociatedGroup(Attribute specificAttribute, OperationalContainerAsDependencyOwningSpecificAttribute dependencyOperationalForm){
        AssociatedDependencies associatedGroup= dictionaryOfDependenciesOwningSpecificAttributes.get(specificAttribute);
        associatedGroup.add(dependencyOperationalForm);
    }

    private void updateDictionaryWithGivenOperationalFromDependency(OperationalContainerAsDependencyOwningSpecificAttribute dependencyOperationalForm){
        for (Attribute leftAttribute : dependencyOperationalForm.getAssociatedDependency().getLeftAttributes()) {
            if (!dictionaryOfDependenciesOwningSpecificAttributes.containsKey(leftAttribute))
                createNewAssociatedDependenciesGroupWithAttribute(leftAttribute);
            addDependencyToItsAssociatedGroup(leftAttribute,dependencyOperationalForm);
        }
    }

    private void deriveDictionaryOfDependenciesOwningSpecificAttributes(){
        dictionaryOfDependenciesOwningSpecificAttributes =new HashMap<>();

        for (FunctionalDependency dependency : this) {
            OperationalContainerAsDependencyOwningSpecificAttribute dependencyOperationalForm=new OperationalContainerAsDependencyOwningSpecificAttribute(dependency);
            updateDictionaryWithGivenOperationalFromDependency(dependencyOperationalForm);
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
        if (!dictionaryOfDependenciesOwningSpecificAttributes.containsKey(currentAttribute))
            return;
        for (OperationalContainerAsDependencyOwningSpecificAttribute dependencyOwningCurrentAttribute : dictionaryOfDependenciesOwningSpecificAttributes.get(currentAttribute)) {
            dependencyOwningCurrentAttribute.signalizeItsLeftAttributeWasAssociatedWithClosure();
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
        deriveDictionaryOfDependenciesOwningSpecificAttributes();
        Attributes closure=new AttributesHashSet(baseOfClosureBuilding);
        buildGivenClosure(closure);
        return closure;
    }

    @Override
    public Attributes constructTransitiveClosure(Attributes baseAttributes) {
        return getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(baseAttributes);
    }

    @Override
    public boolean checkIfThereIsTransitiveDependency(FunctionalDependency possibleDependency){
        Attributes closureAttributes=getClosureOfAttributesAndPossiblyMemorizeAdditionalInformation(possibleDependency.getLeftAttributes());
        return closureAttributes.containsAll(possibleDependency.getRightAttributes());
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
