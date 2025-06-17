package model.eval;

import static org.junit.jupiter.api.Assertions.*;

class TrivialEvaluatorTest extends EvaluatorTest {

    @Override
    protected Evaluator getEvaluator() {
        return new TrivialEvaluator();
    }
}