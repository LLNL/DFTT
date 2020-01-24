package llnl.gnem.core.correlation.clustering;



import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dodge1
 * Date: Apr 7, 2009
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


public class ClusterResult {
    private final Collection<GroupData> groups;

    public ClusterResult(Collection<GroupData> groups )
    {
        this.groups = groups;
      
    }

    public Collection<GroupData> getGroups()
    {
        Collection<GroupData> result = new ArrayList<GroupData>();
        while(!groups.isEmpty()){
            GroupData gd = getLargestGroup();
            result.add(gd);
            groups.remove(gd);
        }
        return result;
    }

    public GroupData getLargestGroup() {
        GroupData best = null;
        int bestSize = -1;
        for( GroupData gd : groups){
            if( gd.size() > bestSize){
                bestSize = gd.size();
                best = gd;
            }
        }
        return best;
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

}
