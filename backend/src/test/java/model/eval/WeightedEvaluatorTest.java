package model.eval;

class WeightedEvaluatorTest extends EvaluatorTest {

    @Override
    protected Evaluator getEvaluator() {
        return new WeightedEvaluator();
    }
}