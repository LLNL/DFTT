/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.classify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.FeatureDAO;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author dodge1
 */
public class RandomForestTwoStageDetectorClassifier implements DetectorClassifier {

    private final RandomForest artifactModel;
    private final RandomForest usabilityModel;
    private final ArrayList<Attribute> attributes;
    private final Instances template;
    private final boolean classifierUsable;
    private static final int MIN_INSTANCES_TO_TRAIN = 100;

    public RandomForestTwoStageDetectorClassifier(int streamid) throws Exception {
        List<LabeledFeature> artifactExamples = DetectionDAOFactory.getInstance().getFeatureDAO().getTrainingDataForStream(streamid, FeatureDAO.ClassificationType.ARTIFACT_CLASSIFICATION);

        List<LabeledFeature> usabilityExamples = DetectionDAOFactory.getInstance().getFeatureDAO().getTrainingDataForStream(streamid, FeatureDAO.ClassificationType.USABILITY_CLASSIFICATION);

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

    private RandomForest createModel(ArrayList<Attribute> attributes, List<LabeledFeature> examples) throws Exception {
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

    private Instances createInstances(ArrayList<Attribute> attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

    private Instances createInstancesTemplate(ArrayList<Attribute> attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
            break;
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

    private Instance createInstance(List<Double> values, ArrayList<Attribute> attributes, LabeledFeature.Status label) {
        Instance instance = new DenseInstance(attributes.size());

        int i = 0;
        for (double value : values) {
            instance.setValue(attributes.get(i++), value);
        }
        if (label != null) {
            instance.setValue(attributes.get(i++), label.ordinal());
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
