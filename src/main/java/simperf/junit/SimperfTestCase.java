package simperf.junit;

import org.junit.runner.RunWith;

import simperf.Simperf;
import simperf.annotations.Inject;

/**
 * Simperf��JUnit���ɻ���
 * @author imbugs
 */
@RunWith(SimperfJUnit4Runner.class)
public class SimperfTestCase {
    @Inject
    protected Simperf simperf;
}
