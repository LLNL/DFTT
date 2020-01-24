/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.database.FeatureDAO;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
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
public class ClassifyAllInstancesRunner {

    private static Instance createInstance(List<Double> values, FastVector attributes, LabeledFeature.Status label) {
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

    private static Evaluation train(Classifier model, Instances training) throws Exception {
        model.buildClassifier(training);

        // Test the artifactModel
        Evaluation eval = new Evaluation(training);
        return eval;
    }

    private static void evaluate(Classifier model, Evaluation eval, Instances test) throws Exception {
        eval.evaluateModel(model, test);

        // Print out the result
        String strSummary = eval.toSummaryString();
        ApplicationLogger.getInstance().log(Level.INFO, strSummary);
        ApplicationLogger.getInstance().log(Level.INFO, "Precision 0: " + eval.precision(0));
        ApplicationLogger.getInstance().log(Level.INFO, "Recall 0: " + eval.recall(0));
        ApplicationLogger.getInstance().log(Level.INFO, "Precision 1: " + eval.precision(1));
        ApplicationLogger.getInstance().log(Level.INFO, "Recall 1: " + eval.recall(1));
    }

    public static void classifyInstances(int runid) {
        try {
            ApplicationLogger.getInstance().log(Level.INFO, "Retrieving unlabeled features...");
            ProgressDialog.getInstance().setTitle("Retrieving unlabeled features...");
            List<DbLabeledFeature> toBeClassified = FeatureDAO.getInstance().getAllUnLabeledFeatures(runid);

            ApplicationLogger.getInstance().log(Level.INFO, "Retrieving labeled artifact features...");
            ProgressDialog.getInstance().setTitle("Retrieving labeled artifact features...");
            List<LabeledFeature> artifactExamples = FeatureDAO.getInstance().getAllLabeledFeatures(runid,
                    FeatureDAO.ClassificationType.ARTIFACT_CLASSIFICATION);

            ApplicationLogger.getInstance().log(Level.INFO, "Retrieving labeled usability features...");
            ProgressDialog.getInstance().setTitle("Retrieving labeled usability features...");
            List<LabeledFeature> usabilityExamples = FeatureDAO.getInstance().getAllLabeledFeatures(runid,
                    FeatureDAO.ClassificationType.USABILITY_CLASSIFICATION);
            FastVector attributes = LabeledFeature.getAttributes();

            Map<Integer, TriggerClassification> triggeridClassificationMap
                    = FeatureDAO.getInstance().getClassificationFromTrainingData(runid);

            Instances template = createInstancesTemplate(attributes, artifactExamples);
            ApplicationLogger.getInstance().log(Level.INFO, "Training artifact model...");
            ProgressDialog.getInstance().setTitle("Training artifact model...");
            RandomForest artifactModel = createModel(attributes, artifactExamples);
            ApplicationLogger.getInstance().log(Level.INFO, "Training usability model...");
            ProgressDialog.getInstance().setTitle("Training usability model...");
            RandomForest usabilityModel = createModel(attributes, usabilityExamples);

            int good = 0;
            int unusable = 0;
            int artifact = 0;
            ProgressDialog.getInstance().setTitle("Labeling triggers...");
            ApplicationLogger.getInstance().log(Level.INFO, "Labeling triggers...");
            int done = 0;
            int size = toBeClassified.size();
            ProgressDialog.getInstance().setProgressBarIndeterminate(false);
            ProgressDialog.getInstance().setMinMax(done, size);
            for (DbLabeledFeature dbFeature : toBeClassified) {
                LabeledFeature feature = (LabeledFeature) dbFeature.getFeature();
                int triggerid = dbFeature.getTriggerid();
                Instance instance = createInstance(feature.getValues(), attributes, feature.getLabel());
                instance.setDataset(template);

                double classLabel = artifactModel.classifyInstance(instance);
                int ordinal = (int) Math.round(classLabel);
                LabeledFeature.Status status = LabeledFeature.Status.values()[ordinal];
                LabeledFeature.Status usabilityStatus = LabeledFeature.Status.unset;
                if (status == LabeledFeature.Status.valid) {
                    classLabel = usabilityModel.classifyInstance(instance);
                    ordinal = (int) Math.round(classLabel);
                    usabilityStatus = LabeledFeature.Status.values()[ordinal];
                    if (usabilityStatus == LabeledFeature.Status.valid) {
                        good++;
                    } else {
                        unusable++;
                    }
                } else {
                    artifact++;
                }
                FeatureDAO.getInstance().writeClassification(triggerid, status, usabilityStatus);
                ProgressDialog.getInstance().setValue(++done);
            }

            // Force measured classification to override result from classifier.
            ApplicationLogger.getInstance().log(Level.INFO, "Applying override values from training data...");
            ProgressDialog.getInstance().setTitle("Applying override values from training data...");
            done = 0;
            size = triggeridClassificationMap.size();
            ProgressDialog.getInstance().setMinMax(done, size);
            for (int triggerid : triggeridClassificationMap.keySet()) {
                LabeledFeature.Status status = LabeledFeature.Status.unset;
                LabeledFeature.Status usabilityStatus = LabeledFeature.Status.unset;
                //valid, invalid,unset
                TriggerClassification tc = triggeridClassificationMap.get(triggerid);
                switch (tc) {
                    case UNSET:
                        status = LabeledFeature.Status.unset;
                        usabilityStatus = LabeledFeature.Status.unset;
                        break;
                    case ARTIFACT:
                        status = LabeledFeature.Status.invalid;
                        usabilityStatus = LabeledFeature.Status.unset;
                        break;
                    case UNUSABLE:
                        status = LabeledFeature.Status.valid;
                        usabilityStatus = LabeledFeature.Status.invalid;
                        break;
                    case GOOD:
                        status = LabeledFeature.Status.valid;
                        usabilityStatus = LabeledFeature.Status.valid;
                        break;

                }
                FeatureDAO.getInstance().writeClassification(triggerid, status, usabilityStatus);
                ProgressDialog.getInstance().setValue(++done);
            }
            ApplicationLogger.getInstance().log(Level.INFO, String.format("%d artifact, %d unusable, %d good", artifact, unusable, good));
            ApplicationLogger.getInstance().log(Level.INFO, "Done.");

        } catch (Exception ex) {
            Logger.getLogger(ClassifyAllInstancesRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static RandomForest createModel(FastVector attributes, List<LabeledFeature> examples) throws Exception {
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

    private static Instances createInstances(FastVector attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

    private static Instances createInstancesTemplate(FastVector attributes, List<LabeledFeature> examples) {
        Instances instances = new Instances("Data", attributes, examples.size());
        for (LabeledFeature example : examples) {
            instances.add(createInstance(example.getValues(), attributes, example.getLabel()));
            break;
        }
        instances.setClassIndex(attributes.size() - 1);
        return instances;
    }

}
