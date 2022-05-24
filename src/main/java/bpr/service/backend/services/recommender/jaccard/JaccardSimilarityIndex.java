package bpr.service.backend.services.recommender.jaccard;

import bpr.service.backend.models.entities.TagEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardSimilarityIndex {


    public static double findJaccardSimilarityIndex(List<TagEntity> a, List<TagEntity> b) {
        //J(A,B) = |A∩B| / |A∪B|
        Set<TagEntity> s1 = new HashSet<>(a);
        Set<TagEntity> s2 = new HashSet<>(b);
        final int sa = s1.size();
        final int sb = s2.size();
        s1.retainAll(s2);
        final int intersection = s1.size();
        return 1d / (sa + sb - intersection) * intersection;
    }

    public static double findJaccardSimilarityIndexDivideByTwo(List<TagEntity> a, List<TagEntity> b) {
        //J(A,B) = |A∩B| / |A∪B|
        Set<TagEntity> s1 = new HashSet<>(a);
        Set<TagEntity> s2 = new HashSet<>(b);
        final int sa = s1.size();
        final int sb = s2.size();
        s1.retainAll(s2);
        final int intersection = s1.size();
        return 1d / (sa + sb - intersection) * intersection / 2;
    }
}
