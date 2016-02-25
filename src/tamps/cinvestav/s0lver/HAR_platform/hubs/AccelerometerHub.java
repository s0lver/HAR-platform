package tamps.cinvestav.s0lver.HAR_platform.hubs;

public class AccelerometerHub {

    // This dude will only trigger the collection of data:
    // A) Read from accelerometer
    // B) Preprocess data (magnitude vector and stuff)
    // C) Store records file
    // D) Stores magVectors File
    // E) Stores pattern files (type, stdDev, mean)
    public void startDataCollection() {

    }

    // nThis dude will only stop the process created by the previous method
    public void stopDataCollection() {

    }

    // This dude will trigger the classification of a naive bayes classifier
    // It will read the pattern files
    // It will store the configuration information in another file
    public void trainClassifier() {

    }

    // This dude will start the process of classification in a permanent loop
    // A) Will read, as in startDataCollection, the accelerometer data
    // B) Will load the learning information stored by trainClassifier
    // C) Will classify each pattern (preprocessed acc data) acquired in A)
    public void startClassification() {

    }

    // This dude will stop the task initiated by previous element.
    public void stopClassification() {

    }
}
