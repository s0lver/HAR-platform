package tamps.cinvestav.s0lver.HAR_platform.classifiers;

/***
 * Notifies when a pattern has been classified
 */
public interface NaiveBayesListener {
    /***
     * Notifies when a pattern has been classified in the specified type
     * @param predictedActivityType The type of the activity on which the pattern has been predicted
     */
    void onClassifiedPattern(byte predictedActivityType);
}
