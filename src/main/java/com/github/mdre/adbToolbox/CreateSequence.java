/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.mdre.adbToolbox;

import com.arcadedb.database.DatabaseInternal;
import com.arcadedb.database.Identifiable;
import com.arcadedb.function.sql.SQLFunctionAbstract;
import com.arcadedb.graph.MutableVertex;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.Schema;
import com.arcadedb.schema.Type;

/**
 *
 * @author mdre
 */
public class CreateSequence extends SQLFunctionAbstract {
    final String DEFAULTSEQUENCETYPENAME = "___sequences";
    
    public CreateSequence() {
        super("createSequence");
    }

    @Override
    public Object execute(Object self, Identifiable currentRecord, Object currentResult, Object[] params, CommandContext context) {
        // check if the ___sequence vertex exists or create ir
        DatabaseInternal db = context.getDatabase();
        
        if (!db.getSchema().existsType(DEFAULTSEQUENCETYPENAME)) {
            db.transaction(() -> {
                DocumentType sequenceType = db.getSchema().createVertexType(DEFAULTSEQUENCETYPENAME);
                sequenceType.createProperty("sequenceName", Type.STRING);
                sequenceType.createProperty("sequenceValue", Type.INTEGER);
                sequenceType.createProperty("sequenceInitValue", Type.INTEGER);
                db.getSchema().createTypeIndex(Schema.INDEX_TYPE.LSM_TREE, true, DEFAULTSEQUENCETYPENAME, "sequenceName");
            });
        }
        
        // verify the parameters.
        if (params.length < 2)
            return null;
        
        final Object sequenceName = params[0];
        final Object sequenceInitValue = params[1];
        Object result = null;
        
        if (sequenceName == null )
            return null;
        else if (!(sequenceName instanceof String))
            return null;
        if (sequenceInitValue == null )
            return null;
        else if (!(sequenceInitValue instanceof Integer))
            return null;
        
        db.transaction(()-> {
            MutableVertex v = db.newVertex(DEFAULTSEQUENCETYPENAME);
            v.set("sequenceName", sequenceName)
             .set("sequenceInitValue",sequenceInitValue)
             .set("sequenceValue",sequenceInitValue)
             .save();
            
        });
        return true;
    }

    @Override
    public String getSyntax() {
        return "createSequence('<sequenceName>', <int initValue>)";
    }
    
}
