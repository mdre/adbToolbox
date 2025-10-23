/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.mdre.adbSequences;

import com.arcadedb.database.DatabaseInternal;
import com.arcadedb.database.Identifiable;
import com.arcadedb.graph.Vertex;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.function.SQLFunctionAbstract;

/**
 *
 * @author mdre
 */
public class CurrentSequence extends SQLFunctionAbstract {
    final String DEFAULTSEQUENCETYPENAME = "___sequences";
    
    /**
     * return the next value that will be returned without incremented it
     */
    public CurrentSequence() {
        super("currentSequence");
    }

    @Override
    public Object execute(Object self, Identifiable currentRecord, Object currentResult, Object[] params, CommandContext context) {
        // check if the ___sequence vertex exists or create ir
        DatabaseInternal db = context.getDatabase();
        
        if (!db.getSchema().existsType(DEFAULTSEQUENCETYPENAME)) {
            throw new SequenceException();
        }
        
        // verify the parameters.
        if (params.length < 1)
            throw new SequenceException("missing parameters!");
        
        final Object sequenceName = params[0];
        
        Integer result = null;
        
        if (sequenceName == null )
            throw new SequenceException("sequence name must not be null!");
        else if (!(sequenceName instanceof String))
            throw new SequenceException("sequence name must be a String!");
        
        try {
            Vertex sq = db.lookupByKey(DEFAULTSEQUENCETYPENAME, "sequenceName", sequenceName)
                                .next()
                                .asVertex()
                                ;
            
            result = sq.getInteger("sequenceValue");
        } catch(Exception e) {
            System.out.println("Squence Exception!!! sequenceName "+sequenceName);
            e.printStackTrace();
            db.rollback();
        }
        return result;
    }

    @Override
    public String getSyntax() {
        return "currentSequence(<sequenceName>)";
    }
    
}
