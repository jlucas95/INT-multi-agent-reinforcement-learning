package main;

import policy.DeterministicPolicy;
import policy.Policy;
import policy.ProbabilisticPolicy;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Jan on 29-3-2017.
 */
public class PolicyAnalyzer {

    public static void analyzePolicy(Policy p, Map<String, Predicate<State>> filter){
        try{
            analyzeProbPolicy((ProbabilisticPolicy) p, filter);
        }
        catch (ClassCastException e){
            try{
                analyzeDetPolicy((DeterministicPolicy) p, filter);
            }
            catch (ClassCastException x){
                System.out.print("no analyzer available for this object");
                throw x;
            }
        }
    }

    private static void analyzeDetPolicy(DeterministicPolicy p, Map<String, Predicate<State>> filters){
        // get all states
        Set<State> keys = p.keySet();
        Set<String> filterKeys = filters.keySet();
        for(String filterName : filterKeys){
            Predicate<State> filter = filters.get(filterName);
            // filter states based on elements: possesion and locations of the players
            Set<State> filteredStates = keys.stream().filter(filter).collect(Collectors.toSet());

            // average the action probabilities per set of states
            Map<Action, Integer> counts = computeCount(p, filteredStates);
            // print the result
            System.out.println("probability of choosing action for filter: " + filterName);
            print(counts);
        }
    }

    private static Map<Action, Integer> computeCount(DeterministicPolicy p , Set<State> keys){

        Map<Action, Integer> counts = new HashMap<>();
        for(Action a : Action.values()){
            counts.put(a, 0);
        }
        for(State s : keys){
            Action a = p.get(s);
            int count = counts.get(a);
            count += 1;
            counts.put(a, count);

        }
        return counts;
    }


    private static void analyzeProbPolicy(ProbabilisticPolicy p, Map<String, Predicate<State>> filters){
        // get all states
        Set<State> keys = p.keySet();
        Set<String> filterKeys = filters.keySet();
        for(String filterName : filterKeys){
            Predicate<State> filter = filters.get(filterName);
            // filter states based on elements: possesion and locations of the players
            Set filteredStates = keys.stream().filter(filter).collect(Collectors.toSet());

            // average the action probabilities per set of states
            double[] average = ComputeAverage(p, filteredStates);
            // print the result
            System.out.println("probability of choosing action for filter: " + filterName);
            print(average);
        }
    }

    private static void print(double[] averages){
        Action[] actions = Action.values();

        for(Action a : actions){
            System.out.print(a.toString() + "\t");
        }
        System.out.println();
        for(double d : averages){
            System.out.print(d);
            System.out.print("\t");
        }
        System.out.println();
    }

    private static void print(Map<Action, Integer> counts){
        Action[] actions = Action.values();

        for(Action a : actions){
            System.out.print(a.toString() + "\t");
        }
        System.out.println();
        for(Action a : actions){
            System.out.print(counts.get(a));
            System.out.print("\t");
        }
        System.out.println();
    }

    private static double[] ComputeAverage(ProbabilisticPolicy p, Set<State> keys){
        double[] average = new double[]{0.0,0.0,0.0,0.0,0.0};
        ArrayList<double[]> values = new ArrayList<>();
        keys.forEach(k->values.add(p.get(k)));
        for(double[] value : values){
            for(int i = 0; i < value.length; i++){
                average[i] += value[i];
            }
        }
        for(int i = 0; i < average.length; i++){
            average[i] = average[i]/values.size();
        }
        return average;

    }

}
