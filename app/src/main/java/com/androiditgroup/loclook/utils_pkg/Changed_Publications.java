package com.androiditgroup.loclook.utils_pkg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OS1 on 22.03.2016.
 */
public class Changed_Publications implements Serializable {

    public Map<Integer, Map<String, Integer>> publicationNumValues = new HashMap<>();

    public Map<Integer, Map<String, Boolean>> publicationBooleanValues = new HashMap<>();

    public Map<Integer, List<Integer>> publicationQuizValues = new HashMap<>();

    public Changed_Publications(Map<Integer, Map<String, Integer>> publicationNumValues, Map<Integer, Map<String, Boolean>> publicationBooleanValues, Map<Integer, List<Integer>> publicationQuizValues) {
        this.publicationNumValues       = publicationNumValues;
        this.publicationBooleanValues   = publicationBooleanValues;
        this.publicationQuizValues      = publicationQuizValues;
    }
}