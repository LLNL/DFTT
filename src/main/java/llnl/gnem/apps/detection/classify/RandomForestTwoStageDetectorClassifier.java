/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.util.Collections;
import java.util.List;
import llnl.gnem.apps.detection.database.FeatureDAO;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author dodge1
 */
public class RandomForestTwoStageDetectorClassifier implements DetectorClassifier {

    private final RandomForest artifactModel;
    private final RandomForest usabilityModel;
    private final FastVector attributes;
    private final Instances template;
    private final boolean classifierUsable;
    private static final int MIN_INSTANCES_TO_TRAIN = 100;

    public RandomForestTwoStageDetectorClassifier(int streamid) throws Exception {
        List<LabeledFeature> artifactExamples = FeatureDAO.getInstance().getTrainingDataForStream(streamid,
                FeatureDAO.ClassificationType.ARTIFACT_CLASSIFICATION);

        List<LabeledFeature> usabilityExamples = FeatureDAO.getInstance().getTrainingDataForStream(streamid,
                FeatureDAO.ClassificationType.USABILITY_CLASSIFICATION);

        classifierUsable = (artifactExamples.size() >= MIN_INSTANCES_TO_TRAIN && usabilityExamples.size() >= MIN_INSTANCES_TO_TRAIN);
        if (classifierUsable) {
            attributes = LabeledFeature.getAttributes();
            template = createInstancesTemplate(attributes, artifactExamples);
            artifactModel = createModel(attributes, artifactExamples);
            usabilityModel = createModel(attributes, usabilityExamples);
        } else {
            attributes = null;
            template = null;
            artifactModel = null;
            usabilityModel = null;
        }

    }

    @Override
    public boolean isClassifierUsable() {
        return classifierUsable;
    }

    @Override
    public TriggerClassification classifyTrigger(LabeledFeature feature) throws Exception {
        Instance instance = createInstance(feature.getValues(), attributes, feature.getLabel());
        instance.setDataset(template);

        double classLabel = artifactModel.classifyInstance(instance);
        int ordinal = (int) Math.round(classLabel);
        LabeledFeature.Status status = LabeledFeature.Status.values()[ordinal];

        if (status == LabeledFeature.Status.valid) {
            classLabel = usabilityModel.classifyInstance(instance);
            ordinal = (int) Math.round(classLabel);
            LabeledFeature.Status usabilityStatus = LabeledFeature.Status.values()[ordinal];
            switch (usabilityStatus) {
                case valid:
                    return TriggerClassification.GOOD;
                case invalid:
                    return TriggerClassification.UNUSABLE;
                default:
                    return TriggerClassification.INCOMPLETE;
            }
        } else {
            return TriggerClassification.ARTIFACT;
        }

    }

    private RandomForest createModel(FastVector attributes, List<LabeledFeature> examples) throws Exception {
        Collections.shuffle(examples);
        System.out.println("Full dataset: " + examples.size());

        int split = (int) (examples.size() * 0.7);
        Instances training = createInstances(attributes, examples.subList(0, split));
        Instances test = createInstances(attributes, examples.subList(split, examples.size()));
        RandomForest model = new RandomForest();
        Evaluation eval = train(model, training);
        System.out.println(model.toString());
        evaluate(model, eval, training);
        evaluate(model, eval, test);
        return model;
    }

    private Instances createInstances(FastVector attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

    private Instances createInstancesTemplate(FastVector attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
            break;
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

    private Instance createInstance(List<Double> values, FastVector attributes, LabeledFeature.Status label) {
        Instance instance = new Instance(attributes.size());

        int i = 0;
        for (double value : values) {
            instance.setValue((Attribute) attributes.elementAt(i++), value);
        }
        if (label != null) {
            instance.setValue((Attribute) attributes.elementAt(i++), label.ordinal());
        }

        return instance;
    }

    private Evaluation train(Classifier model, Instances training) throws Exception {
        model.buildClassifier(training);

        // Test the artifactModel
        Evaluation eval = new Evaluation(training);
        return eval;
    }

    private void evaluate(Classifier model, Evaluation eval, Instances test) throws Exception {
        eval.evaluateModel(model, test);

        // Print out the result
        String strSummary = eval.toSummaryString();
        System.out.println(strSummary);
        System.out.println("Precision 0: " + eval.precision(0));
        System.out.println("Recall 0: " + eval.recall(0));
        System.out.println("Precision 1: " + eval.precision(1));
        System.out.println("Recall 1: " + eval.recall(1));
    }

}
